package com.querifylabs.blog.optimizer.join;

import com.blazingdb.calcite.metastore.metadata.TableMetadata;
import com.blazingdb.calcite.metastore.statistics.TableStatisticsKind;
import com.blazingdb.calcite.util.Utilities;
import com.querifylabs.blog.optimizer.DrillTable;
import org.apache.calcite.plan.hep.HepRelVertex;
import org.apache.calcite.plan.volcano.RelSubset;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rex.*;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.util.Pair;
import org.apache.calcite.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrillRelOptUtil {
    public static List<Pair<Integer, Integer>> analyzeSimpleEquiJoin(Join join) {
        List<Pair<Integer, Integer>> joinConditions = new ArrayList<>();
        try {
            RexVisitor<Void> visitor =
                    new RexVisitorImpl<Void>(true) {
                        @Override
                        public Void visitCall(RexCall call) {
                            if (call.getKind() == SqlKind.AND || call.getKind() == SqlKind.OR) {
                                super.visitCall(call);
                            } else {
                                if (call.getKind() == SqlKind.EQUALS) {
                                    RexNode leftComparand = call.operands.get(0);
                                    RexNode rightComparand = call.operands.get(1);
                                    // If a join condition predicate has something more complicated than a RexInputRef
                                    // we bail out!
                                    if (!(leftComparand instanceof RexInputRef && rightComparand instanceof RexInputRef)) {
                                        joinConditions.clear();
                                        throw new Util.FoundOne(call);
                                    }
                                    int leftFieldCount = join.getLeft().getRowType().getFieldCount();
                                    int rightFieldCount = join.getRight().getRowType().getFieldCount();
                                    RexInputRef leftFieldAccess = (RexInputRef) leftComparand;
                                    RexInputRef rightFieldAccess = (RexInputRef) rightComparand;
                                    if (leftFieldAccess.getIndex() >= leftFieldCount + rightFieldCount ||
                                            rightFieldAccess.getIndex() >= leftFieldCount + rightFieldCount) {
                                        joinConditions.clear();
                                        throw new Util.FoundOne(call);
                                    }
                                    /* Both columns reference same table */
                                    if ((leftFieldAccess.getIndex() >= leftFieldCount &&
                                            rightFieldAccess.getIndex() >= leftFieldCount) ||
                                            (leftFieldAccess.getIndex() < leftFieldCount &&
                                                    rightFieldAccess.getIndex() < leftFieldCount)) {
                                        joinConditions.clear();
                                        throw new Util.FoundOne(call);
                                    } else {
                                        if (leftFieldAccess.getIndex() < leftFieldCount) {
                                            joinConditions.add(Pair.of(leftFieldAccess.getIndex(),
                                                    rightFieldAccess.getIndex() - leftFieldCount));
                                        } else {
                                            joinConditions.add(Pair.of(rightFieldAccess.getIndex(),
                                                    leftFieldAccess.getIndex() - leftFieldCount));
                                        }
                                    }
                                }
                            }
                            return null;
                        }
                    };
            join.getCondition().accept(visitor);
        } catch (Util.FoundOne ex) {
            Util.swallow(ex, null);
        }
        return joinConditions;
    }

    /**
     * Returns whether the join condition is a simple equi-join or not. A simple equi-join is
     * defined as an two-table equality join (no self-join)
     * @param join input join
     * @param joinFieldOrdinals join field ordinal w.r.t. the underlying inputs to the join
     * @return TRUE if the join is a simple equi-join (not a self-join), FALSE otherwise
     * */
    public static boolean analyzeSimpleEquiJoin(Join join, int[] joinFieldOrdinals) {
        RexNode joinExp = join.getCondition();
        if (joinExp.getKind() != SqlKind.EQUALS) {
            return false;
        } else {
            RexCall binaryExpression = (RexCall) joinExp;
            RexNode leftComparand = binaryExpression.operands.get(0);
            RexNode rightComparand = binaryExpression.operands.get(1);
            if (!(leftComparand instanceof RexInputRef)) {
                return false;
            } else if (!(rightComparand instanceof RexInputRef)) {
                return false;
            } else {
                int leftFieldCount = join.getLeft().getRowType().getFieldCount();
                int rightFieldCount = join.getRight().getRowType().getFieldCount();
                RexInputRef leftFieldAccess = (RexInputRef) leftComparand;
                RexInputRef rightFieldAccess = (RexInputRef) rightComparand;
                if (leftFieldAccess.getIndex() >= leftFieldCount + rightFieldCount ||
                        rightFieldAccess.getIndex() >= leftFieldCount + rightFieldCount) {
                    return false;
                }
                /* Both columns reference same table */
                if ((leftFieldAccess.getIndex() >= leftFieldCount &&
                        rightFieldAccess.getIndex() >= leftFieldCount) ||
                        (leftFieldAccess.getIndex() < leftFieldCount &&
                                rightFieldAccess.getIndex() < leftFieldCount)) {
                    return false;
                } else {
                    if (leftFieldAccess.getIndex() < leftFieldCount) {
                        joinFieldOrdinals[0] = leftFieldAccess.getIndex();
                        joinFieldOrdinals[1] = rightFieldAccess.getIndex() - leftFieldCount;
                    } else {
                        joinFieldOrdinals[0] = rightFieldAccess.getIndex();
                        joinFieldOrdinals[1] = leftFieldAccess.getIndex() - leftFieldCount;
                    }
                    return true;
                }
            }
        }
    }

    /**
     * Returns whether statistics-based estimates or guesses are used by the optimizer
     * for the {@link RelNode} rel.
     * @param rel : input
     * @return TRUE if the estimate is a guess, FALSE otherwise
     * */
    public static boolean guessRows(RelNode rel) {
        return false;
//
//        if (rel instanceof RelSubset) {
//            if (((RelSubset) rel).getBest() != null) {
//                return guessRows(((RelSubset) rel).getBest());
//            } else if (((RelSubset) rel).getOriginal() != null) {
//                return guessRows(((RelSubset) rel).getOriginal());
//            }
//        } else if (rel instanceof HepRelVertex) {
//            if (((HepRelVertex) rel).getCurrentRel() != null) {
//                return guessRows(((HepRelVertex) rel).getCurrentRel());
//            }
//        } else if (rel instanceof TableScan) {
//            DrillTable table = Utilities.getDrillTable(rel.getTable());
//            try {
//                TableMetadata tableMetadata;
//                return table == null
//                        || (tableMetadata = table.getTableMetadata()) == null
//                        || !TableStatisticsKind.HAS_DESCRIPTIVE_STATISTICS.getValue(tableMetadata);
//            } catch (Exception e) {
//                 return true;
//            }
//        } else {
//            for (RelNode child : rel.getInputs()) {
//                if (guessRows(child)) { // at least one child is a guess
//                    return true;
//                }
//            }
//        }
//        return false;
    }
}
