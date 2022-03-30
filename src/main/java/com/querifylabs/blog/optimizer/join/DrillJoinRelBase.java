package com.querifylabs.blog.optimizer.join;


 import com.blazingdb.calcite.drill.DrillStatsTable;
 import com.blazingdb.calcite.drill.NumericEquiDepthHistogram;
 import com.blazingdb.calcite.expression.SchemaPath;
 import com.blazingdb.calcite.util.Utilities;
 import com.google.common.collect.Lists;
 import com.querifylabs.blog.optimizer.DrillTable;
 import org.apache.calcite.adapter.enumerable.EnumerableConvention;
 import org.apache.calcite.adapter.enumerable.EnumerableHashJoin;
 import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTraitSet;
 import org.apache.calcite.rel.RelCollationTraitDef;
 import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.CorrelationId;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.hint.RelHint;
import org.apache.calcite.rel.logical.LogicalJoin;
 import org.apache.calcite.rel.metadata.RelMdCollation;
 import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexNode;
 import org.apache.calcite.rex.RexTableInputRef;
 import org.apache.calcite.util.ImmutableBitSet;
import org.apache.calcite.util.Pair;
 import org.checkerframework.checker.nullness.qual.Nullable;

 import java.util.*;

/**
 * Base class for logical and physical Joins implemented in Drill.
 */
public  class DrillJoinRelBase extends EnumerableHashJoin implements DrillRel {

    protected List<Integer> leftKeys = Lists.newArrayList();
    protected List<Integer> rightKeys = Lists.newArrayList();

    /**
     * The join key positions for which null values will not match.
     */
    protected List<Boolean> filterNulls = Lists.newArrayList();

    private  double joinRowFactor = 1.0;

    public DrillJoinRelBase(RelOptCluster cluster, RelTraitSet traits, RelNode left, RelNode right, RexNode condition,
                            Set<CorrelationId> variablesSet, JoinRelType joinType) {
        super(cluster, traits, left, right, condition,
                CorrelationId.setOf(Collections.<String> emptySet()), joinType);

        //        planner.join.row_count_estimate_factor: 1.0,
        this.joinRowFactor = 1.0;
    }

    public DrillJoinRelBase(RelOptCluster cluster, RelTraitSet traits, RelNode left, RelNode right, RexNode condition, JoinRelType joinType, List<Integer> leftKeys, List<Integer> rightKeys) {
        super(cluster, traits, left, right, condition, CorrelationId.setOf(Collections.<String> emptySet()), joinType);

        assert (leftKeys != null && rightKeys != null);
        this.leftKeys = leftKeys;
        this.rightKeys = rightKeys;
    }

    public static DrillJoinRelBase create(
            RelNode left,
            RelNode right,
            RexNode condition,
            Set<CorrelationId> variablesSet,
            JoinRelType joinType) {
        final RelOptCluster cluster = left.getCluster();
        final RelMetadataQuery mq = cluster.getMetadataQuery();
        final RelTraitSet traitSet =
                cluster.traitSetOf(EnumerableConvention.INSTANCE)
                        .replaceIfs(RelCollationTraitDef.INSTANCE,
                                () -> RelMdCollation.enumerableHashJoin(mq, left, right, joinType));
        return new DrillJoinRelBase(cluster, traitSet, left, right, condition,
                variablesSet, joinType);
    }

//    @Nullable
//    @Override
//    public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
//        if (this.isSemiJoin()) {
//            return planner.getCostFactory().makeTinyCost();
//        } else {
//            double rowCount = this.estimateRowCount(mq);
//            return planner.getCostFactory().makeCost(rowCount, 0.0D, 0.0D);
//        }
//    }


    private double estimateJoinCardinality(Double[] left, Double[] right) {
        Double cnt = 0.0;
        for (int i=1; i<left.length; i++) {
            for (int j=1; j<right.length; j++) {
                if (right[j-1] > left[i] || left[i-1] > right[j]) continue;
                cnt += 1;
            }
        }
        return cnt;
    }



    private DrillStatsTable getTable(Set<RexTableInputRef.RelTableRef> tables, String col_name) {
        for (RexTableInputRef.RelTableRef table:tables) {
            DrillTable drillTable = Utilities.getDrillTable(table.getTable());
            if (drillTable == null) {
                continue;
            }
            DrillStatsTable drillStatsTable = drillTable.getStatsProvider();
            if (drillStatsTable == null) {
                continue;
            }
            Set<SchemaPath> columns = drillStatsTable.getColumns();
            if (columns.contains(SchemaPath.parseFromString(col_name))) {
                return drillStatsTable;
            }
        }
        return null;
    }

    @Override
    public double estimateRowCount(RelMetadataQuery mq) {
        if (this.condition.isAlwaysTrue()) {
            return joinRowFactor * this.getLeft().estimateRowCount(mq) * this.getRight().estimateRowCount(mq);
        }

//        LogicalJoin jr = LogicalJoin.create(this.getLeft(), this.getRight(), this.getCondition(),
//                this.getVariablesSet(), this.getJoinType());

        LogicalJoin jr = LogicalJoin.create(this.getLeft(), this.getRight(), hints, this.getCondition(),
                this.getVariablesSet(), this.getJoinType());

        if (!DrillRelOptUtil.guessRows(this)         //Statistics present for left and right side of the join
                && jr.getJoinType() == JoinRelType.INNER) {
            // System.out.println("Estimate row count join cost");
            List<Pair<Integer, Integer>> joinConditions = DrillRelOptUtil.analyzeSimpleEquiJoin((Join)jr);
            if (joinConditions.size() > 0) {
                List<Integer> leftSide =  new ArrayList<>();
                List<Integer> rightSide = new ArrayList<>();
                for (Pair<Integer, Integer> condition : joinConditions) {
                    leftSide.add(condition.left);
                    rightSide.add(condition.right);
                }
                ImmutableBitSet leq = ImmutableBitSet.of(leftSide);
                ImmutableBitSet req = ImmutableBitSet.of(rightSide);

                Double ldrc = mq.getDistinctRowCount(this.getLeft(), leq, null);
                Double rdrc = mq.getDistinctRowCount(this.getRight(), req, null);

                Double lrc = mq.getRowCount(this.getLeft());
                Double rrc = mq.getRowCount(this.getRight());

                System.out.println(ldrc + ", " + rdrc + ", " + lrc + ", " + rrc);

                Set<RexTableInputRef.RelTableRef> tables_left = mq.getTableReferences(this.getLeft());
                Set<RexTableInputRef.RelTableRef> tables_right = mq.getTableReferences(this.getRight());
                String name_left = this.getLeft().getRowType().getFieldNames().get(leq.nth(0));
                String name_right = this.getRight().getRowType().getFieldNames().get(req.nth(0));

//                System.out.println("TableNames:" + this.left + "|" + this.right);
                System.out.println(name_left + ":" + name_right);

                if (tables_left != null && tables_right != null && ldrc != null && rdrc != null && lrc != null && rrc != null) {
                    DrillStatsTable stats_left = getTable(tables_left, name_left);
                    DrillStatsTable stats_right = getTable(tables_right, name_right);
                    SchemaPath col_name_left = SchemaPath.parseFromString(name_left);
                    SchemaPath col_name_right = SchemaPath.parseFromString(name_right);
                    // System.out.println(stats_left.getTableName() + " " + stats_right.getTableName());

                    NumericEquiDepthHistogram hist_left = (NumericEquiDepthHistogram) stats_left.getHistogram(col_name_left);
                    NumericEquiDepthHistogram hist_right = (NumericEquiDepthHistogram) stats_right.getHistogram(col_name_right);
                    double factor = lrc * rrc;
                    double card = estimateJoinCardinality(hist_left.getBuckets(), hist_right.getBuckets()) * Math.max(hist_left.getNumRowsPerBucket(), hist_right.getNumRowsPerBucket());
          /*System.out.printf("Cardinality %f\n", card);
          System.out.printf("Hist: %f -- %f\n", hist_left.getNumRowsPerBucket(), hist_right.getNumRowsPerBucket());
          System.out.printf("Estimate row count join cost %f\n", (card==0) ? 0 : (lrc/card)*rrc);
          System.out.printf("Prev Estimate row count join cost %f\n", (lrc / Math.max(ldrc, rdrc)) * rrc);
          System.out.printf("RowCnt: %f -- %f\n", lrc, rrc);*/
                    return (card==0) ? 0 : (lrc/card)*rrc*1000;
                }

                if (ldrc != null && rdrc != null && lrc != null && rrc != null) {
                    // Join cardinality = (lrc * rrc) / Math.max(ldrc, rdrc). Avoid overflow by dividing earlier
                    return (lrc / Math.max(ldrc, rdrc)) * rrc;
                }
            }
        }

        return joinRowFactor * Math.max(
                mq.getRowCount(this.getLeft()),
                mq.getRowCount(this.getRight()));
    }


    public double estimateRowCount2(RelMetadataQuery mq) {
        System.err.println("Join:estimateRowCount");
        if (this.condition.isAlwaysTrue()) {
            return joinRowFactor * this.getLeft().estimateRowCount(mq) * this.getRight().estimateRowCount(mq);
        }
        List<RelHint> hints = new ArrayList<>();
        LogicalJoin jr = LogicalJoin.create(this.getLeft(), this.getRight(), hints, this.getCondition(),
                this.getVariablesSet(), this.getJoinType());

        if (!DrillRelOptUtil.guessRows(this)         //Statistics present for left and right side of the join
                && jr.getJoinType() == JoinRelType.INNER) {
            List<Pair<Integer, Integer>> joinConditions = DrillRelOptUtil.analyzeSimpleEquiJoin((Join)jr);
            if (joinConditions.size() > 0) {
                List<Integer> leftSide =  new ArrayList<>();
                List<Integer> rightSide = new ArrayList<>();
                for (Pair<Integer, Integer> condition : joinConditions) {
                    leftSide.add(condition.left);
                    rightSide.add(condition.right);
                }
                ImmutableBitSet leq = ImmutableBitSet.of(leftSide);
                ImmutableBitSet req = ImmutableBitSet.of(rightSide);

                Double rdrc = mq.getDistinctRowCount(this.getRight(), req, null);
                Double ldrc = mq.getDistinctRowCount(this.getLeft(), leq, null);

                Double lrc = mq.getRowCount(this.getLeft());
                Double rrc = mq.getRowCount(this.getRight());


                Set<RexTableInputRef.RelTableRef> tables_left = mq.getTableReferences(this.getLeft());
                Set<RexTableInputRef.RelTableRef> tables_right = mq.getTableReferences(this.getRight());
                String name_left = this.getLeft().getRowType().getFieldNames().get(leq.nth(0));
                String name_right = this.getRight().getRowType().getFieldNames().get(req.nth(0));

                System.out.println(name_left + ":" + name_right);
                System.out.println(ldrc + ", " + rdrc + ", " + lrc + ", " + rrc);

                if (ldrc != null && rdrc != null && lrc != null && rrc != null) {
                    // Join cardinality = (lrc * rrc) / Math.max(ldrc, rdrc). Avoid overflow by dividing earlier
                    return (lrc / Math.max(ldrc, rdrc)) * rrc;
                }
            }
        }

        return joinRowFactor * Math.max(
                mq.getRowCount(this.getLeft()),
                mq.getRowCount(this.getRight()));
    }



    /**
     * Returns whether there are any elements in common between left and right.
     */
    private static <T> boolean intersects(List<T> left, List<T> right) {
        return new HashSet<>(left).removeAll(right);
    }

    public static boolean uniqueFieldNames(RelDataType rowType) {
        return isUnique(rowType.getFieldNames());
    }

    public static <T> boolean isUnique(List<T> list) {
        return new HashSet<>(list).size() == list.size();
    }

    public List<Integer> getLeftKeys() {
        return this.leftKeys;
    }

    public List<Integer> getRightKeys() {
        return this.rightKeys;
    }


}
