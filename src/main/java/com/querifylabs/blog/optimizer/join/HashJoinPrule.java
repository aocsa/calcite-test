package com.querifylabs.blog.optimizer.join;

import com.google.common.collect.Lists;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableHashJoin;
import org.apache.calcite.adapter.enumerable.EnumerableNestedLoopJoin;
import org.apache.calcite.plan.*;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinInfo;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexUtil;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HashJoinPrule extends ConverterRule {

    private  boolean isDist;
    private boolean isSemi = false;

    public static final Config DEFAULT_CONFIG = Config.INSTANCE
            .withConversion(LogicalJoin.class, Convention.NONE,
                    EnumerableConvention.INSTANCE, "EnumerableJoinRule")
            .withRuleFactory(HashJoinPrule::new);

    /** Rule that matches Filter on TableScan. */
    //
   //  ,
    // RelFactories.LOGICAL_BUILDER,
    //
//    public static final HashJoinPrule INSTANCE_2 = new HashJoinPrule("Prel.HashJoinDistPrule", RelOptHelper.any(DrillJoinRelBase.class), true);
//
//    public static final HashJoinPrule INSTANCE = new HashJoinPrule();
//
//    public HashJoinPrule() {
//        super(operand(Join.class, operandJ(EnumerableHashJoin.class, null, JoinPruleBase::test, none())), RelFactories.LOGICAL_BUILDER, "JoinPruleBase");
//     }
//
//
//    public HashJoinPrule(String name, RelOptRuleOperand operand, boolean isDist) {
//        super(operand, name);
//        this.isDist = isDist;
//    }


    protected HashJoinPrule(Config config) {
        super(config);
    }


    @Override public RelNode convert(RelNode rel) {
        LogicalJoin join = (LogicalJoin) rel;
        List<RelNode> newInputs = new ArrayList<>();
        for (RelNode input : join.getInputs()) {
            if (!(input.getConvention() instanceof EnumerableConvention)) {
                input =
                        convert(
                                input,
                                input.getTraitSet()
                                        .replace(EnumerableConvention.INSTANCE));
            }
            newInputs.add(input);
        }
        final RexBuilder rexBuilder = join.getCluster().getRexBuilder();
        final RelNode left = newInputs.get(0);
        final RelNode right = newInputs.get(1);
        final JoinInfo info = join.analyzeCondition();

        // If the join has equiKeys (i.e. complete or partial equi-join),
        // create an EnumerableHashJoin, which supports all types of joins,
        // even if the join condition contains partial non-equi sub-conditions;
        // otherwise (complete non-equi-join), create an EnumerableNestedLoopJoin,
        // since a hash join strategy in this case would not be beneficial.
        final boolean hasEquiKeys = !info.leftKeys.isEmpty()
                && !info.rightKeys.isEmpty();
        if (hasEquiKeys) {
            // Re-arrange condition: first the equi-join elements, then the non-equi-join ones (if any);
            // this is not strictly necessary but it will be useful to avoid spurious errors in the
            // unit tests when verifying the plan.
            final RexNode equi = info.getEquiCondition(left, right, rexBuilder);
            final RexNode condition;
            if (info.isEqui()) {
                condition = equi;
            } else {
                final RexNode nonEqui = RexUtil.composeConjunction(rexBuilder, info.nonEquiConditions);
                condition = RexUtil.composeConjunction(rexBuilder, Arrays.asList(equi, nonEqui));
            }
            System.err.println("HashJoinPrule. EquiKeys");


            return DrillJoinRelBase.create(
                    left,
                    right,
                    condition,
                    join.getVariablesSet(),
                    join.getJoinType());
        }
        return EnumerableNestedLoopJoin.create(
                left,
                right,
                join.getCondition(),
                join.getVariablesSet(),
                join.getJoinType());
    }
//
//    @Override
//    public void onMatch(RelOptRuleCall call) {
//        final LogicalJoin join = call.rel(0);
//        final RelNode left = join.getLeft();
//        final RelNode right = join.getRight();
//        final RelTraitSet traits = join.getTraitSet().plus(DrillRel.DRILL_LOGICAL);
//
//        final RelNode convertedLeft = convert(left, left.getTraitSet().plus(DrillRel.DRILL_LOGICAL).simplify());
//        final RelNode convertedRight = convert(right, right.getTraitSet().plus(DrillRel.DRILL_LOGICAL).simplify());
//
//        List<Integer> leftKeys = Lists.newArrayList();
//        List<Integer> rightKeys = Lists.newArrayList();
//        List<Boolean> filterNulls = Lists.newArrayList();
//
//        boolean addFilter = false;
//        RexNode origJoinCondition = join.getCondition();
//        RexNode newJoinCondition = origJoinCondition;
//
//        RexNode remaining = RelOptUtil.splitJoinCondition(convertedLeft, convertedRight, origJoinCondition, leftKeys, rightKeys, filterNulls);
//        boolean hasEquijoins = leftKeys.size() == rightKeys.size() && leftKeys.size() > 0;
//
//        // If the join involves equijoins and non-equijoins, then we can process the non-equijoins through
//        // a filter right after the join
//        // DRILL-1337: We can only pull up a non-equivjoin filter for INNER join.
//        // For OUTER join, pulling up a non-eqivjoin filter will lead to incorrectly discarding qualified rows.
//        if (!remaining.isAlwaysTrue()) {
//            if (hasEquijoins && join.getJoinType() == JoinRelType.INNER) {
//                addFilter = true;
//                newJoinCondition = buildJoinCondition(convertedLeft, convertedRight, leftKeys, rightKeys, filterNulls, join.getCluster().getRexBuilder());
//            }
//        } else {
//            newJoinCondition = buildJoinCondition(convertedLeft, convertedRight, leftKeys, rightKeys, filterNulls, join.getCluster().getRexBuilder());
//        }
//
//        try {
////				if (join.isSemiJoin()) {
////					RelNode joinRel = new DrillSemiJoinRel(join.getCluster(), traits, convertedLeft, convertedRight, newJoinCondition,
////							leftKeys, rightKeys);
////					call.transformTo(joinRel);
////					return;
////				}
//            RelNode joinRel = new DrillJoinRelBase(join.getCluster(), traits, convertedLeft, convertedRight, newJoinCondition,
//                    join.getJoinType(), leftKeys, rightKeys) {
//            };
//            call.transformTo(joinRel);
//
//        } catch (Exception e) {
//            return;
//        }
//    }


//    @Override
//    public void onMatch(RelOptRuleCall call) {
//        final LogicalJoin join = call.rel(0);
//        final RelNode left = join.getLeft();
//        final RelNode right = join.getRight();
//        final RelTraitSet traits = join.getTraitSet().plus(DrillRel.DRILL_LOGICAL);
//
//        final RelNode convertedLeft = convert(left, left.getTraitSet().plus(DrillRel.DRILL_LOGICAL).simplify());
//        final RelNode convertedRight = convert(right, right.getTraitSet().plus(DrillRel.DRILL_LOGICAL).simplify());
//
//        List<Integer> leftKeys = Lists.newArrayList();
//        List<Integer> rightKeys = Lists.newArrayList();
//        List<Boolean> filterNulls = Lists.newArrayList();
//
//        boolean addFilter = false;
//        RexNode origJoinCondition = join.getCondition();
//        RexNode newJoinCondition = origJoinCondition;
//
//        RexNode remaining = RelOptUtil.splitJoinCondition(convertedLeft, convertedRight, origJoinCondition, leftKeys, rightKeys, filterNulls);
//        boolean hasEquijoins = leftKeys.size() == rightKeys.size() && leftKeys.size() > 0;
//
//        // If the join involves equijoins and non-equijoins, then we can process the non-equijoins through
//        // a filter right after the join
//        // DRILL-1337: We can only pull up a non-equivjoin filter for INNER join.
//        // For OUTER join, pulling up a non-eqivjoin filter will lead to incorrectly discarding qualified rows.
//        if (!remaining.isAlwaysTrue()) {
//            if (hasEquijoins && join.getJoinType() == JoinRelType.INNER) {
//                addFilter = true;
//                newJoinCondition = buildJoinCondition(convertedLeft, convertedRight, leftKeys, rightKeys, filterNulls, join.getCluster().getRexBuilder());
//            }
//        } else {
//            newJoinCondition = buildJoinCondition(convertedLeft, convertedRight, leftKeys, rightKeys, filterNulls, join.getCluster().getRexBuilder());
//        }
//
//        try {
////				if (join.isSemiJoin()) {
////					RelNode joinRel = new DrillSemiJoinRel(join.getCluster(), traits, convertedLeft, convertedRight, newJoinCondition,
////							leftKeys, rightKeys);
////					call.transformTo(joinRel);
////					return;
////				}
//            RelNode joinRel = new DrillJoinRelBase(join.getCluster(), traits, convertedLeft, convertedRight, newJoinCondition,
//                    join.getJoinType(), leftKeys, rightKeys) {
//            };
//            call.transformTo(joinRel);
//
//        } catch (Exception e) {
//            return;
//        }
//    }

    private RexNode buildJoinCondition(RelNode convertedLeft, RelNode convertedRight, List<Integer> leftKeys,
                                       List<Integer> rightKeys, List<Boolean> filterNulls, RexBuilder builder) {
        List<RexNode> equijoinList = Lists.newArrayList();
        final int numLeftFields = convertedLeft.getRowType().getFieldCount();
        List<RelDataTypeField> leftTypes = convertedLeft.getRowType().getFieldList();
        List<RelDataTypeField> rightTypes = convertedRight.getRowType().getFieldList();

        for (int i = 0; i < leftKeys.size(); i++) {
            int leftKeyOrdinal = leftKeys.get(i);
            int rightKeyOrdinal = rightKeys.get(i);

            equijoinList.add(builder.makeCall(
                    filterNulls.get(i) ? SqlStdOperatorTable.EQUALS : SqlStdOperatorTable.IS_NOT_DISTINCT_FROM,
                    builder.makeInputRef(leftTypes.get(leftKeyOrdinal).getType(), leftKeyOrdinal),
                    builder.makeInputRef(rightTypes.get(rightKeyOrdinal).getType(), rightKeyOrdinal + numLeftFields)
            ));
        }
        return RexUtil.composeConjunction(builder, equijoinList, false);
    }


}
