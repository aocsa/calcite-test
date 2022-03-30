package com.querifylabs.blog.optimizer.join;


import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.volcano.RelSubset;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableFunctionScan;
import org.apache.calcite.rel.logical.*;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class JoinUtils {

    public enum JoinCategory {
        EQUALITY,  // equality join
        INEQUALITY,  // inequality join: <>, <, >
        CARTESIAN   // no join condition
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JoinUtils.class);


    // Check the comparator is supported in join condition. Note that a similar check is also
    // done in JoinPrel; however we have to repeat it here because a physical plan
    // may be submitted directly to Drill.
    public static Comparator checkAndReturnSupportedJoinComparator(JoinCondition condition) {
        switch(condition.getRelationship().toUpperCase()) {
            case "EQUALS":
            case "==": /* older json plans still have '==' */
                return Comparator.EQUALS;
            case "IS_NOT_DISTINCT_FROM":
                return Comparator.IS_NOT_DISTINCT_FROM;
        }
        throw new RuntimeException("Invalid comparator supplied to this join: " + condition.getRelationship());
    }

    /**
     * Check if the given RelNode contains any Cartesian join.
     * Return true if find one. Otherwise, return false.
     *
     * @param relNode     the RelNode to be inspected.
     * @param leftKeys    a list used for the left input into the join which has
     *                    equi-join keys. It can be empty or not (but not null),
     *                    this method will clear this list before using it.
     * @param rightKeys   a list used for the right input into the join which has
     *                    equi-join keys. It can be empty or not (but not null),
     *                    this method will clear this list before using it.
     * @param filterNulls The join key positions for which null values will not
     *                    match.
     * @return            Return true if the given relNode contains Cartesian join.
     *                    Otherwise, return false
     */
    public static boolean checkCartesianJoin(RelNode relNode, List<Integer> leftKeys, List<Integer> rightKeys, List<Boolean> filterNulls) {
        if (relNode instanceof Join) {
            leftKeys.clear();
            rightKeys.clear();

            Join joinRel = (Join) relNode;
            RelNode left = joinRel.getLeft();
            RelNode right = joinRel.getRight();

            RexNode remaining = RelOptUtil.splitJoinCondition(left, right, joinRel.getCondition(), leftKeys, rightKeys, filterNulls);
            if (joinRel.getJoinType() == JoinRelType.INNER) {
                if (leftKeys.isEmpty() || rightKeys.isEmpty()) {
                    return true;
                }
            } else {
                if (!remaining.isAlwaysTrue() || leftKeys.isEmpty() || rightKeys.isEmpty()) {
                    return true;
                }
            }
        }

        for (RelNode child : relNode.getInputs()) {
            if (checkCartesianJoin(child, leftKeys, rightKeys, filterNulls)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the given RelNode contains any Cartesian join.
     * Return true if find one. Otherwise, return false.
     *
     * @param relNode     {@link RelNode} instance to be inspected
     * @return            Return true if the given relNode contains Cartesian join.
     *                    Otherwise, return false
     */
    public static boolean checkCartesianJoin(RelNode relNode) {
        return checkCartesianJoin(relNode, new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
    }


    public static JoinCategory getJoinCategory(RelNode left, RelNode right, RexNode condition,
                                               List<Integer> leftKeys, List<Integer> rightKeys, List<Boolean> filterNulls) {
        if (condition.isAlwaysTrue()) {
            return JoinCategory.CARTESIAN;
        }
        leftKeys.clear();
        rightKeys.clear();
        filterNulls.clear();
        RexNode remaining = RelOptUtil.splitJoinCondition(left, right, condition, leftKeys, rightKeys, filterNulls);

        if (!remaining.isAlwaysTrue() || (leftKeys.size() == 0 || rightKeys.size() == 0) ) {
            // for practical purposes these cases could be treated as inequality
            return JoinCategory.INEQUALITY;
        }
        return JoinCategory.EQUALITY;
    }

    /**
     * Collects expressions list from the input project.
     * For the case when input rel node has single input, its input is taken.
     */
    private static class ProjectExpressionsCollector extends RelShuttleImpl {
        private final List<RexNode> expressions = new ArrayList<>();

        @Override
        public RelNode visit(RelNode other) {
            // RelShuttleImpl doesn't have visit methods for Project and RelSubset.
            if (other instanceof RelSubset) {
                return visit((RelSubset) other);
            } else if (other instanceof Project) {
                return visit((Project) other);
            }
            return super.visit(other);
        }

        @Override
        public RelNode visit(TableFunctionScan scan) {
            return scan;
        }

        @Override
        public RelNode visit(LogicalJoin join) {
            return join;
        }

        @Override
        public RelNode visit(LogicalCorrelate correlate) {
            return correlate;
        }

        @Override
        public RelNode visit(LogicalUnion union) {
            return union;
        }

        @Override
        public RelNode visit(LogicalIntersect intersect) {
            return intersect;
        }

        @Override
        public RelNode visit(LogicalMinus minus) {
            return minus;
        }

        @Override
        public RelNode visit(LogicalSort sort) {
            return sort;
        }

        @Override
        public RelNode visit(LogicalExchange exchange) {
            return exchange;
        }

        private RelNode visit(Project project) {
            expressions.addAll(project.getProjects());
            return project;
        }

        private RelNode visit(RelSubset subset) {
            return Util.first(subset.getBest(), subset.getOriginal()).accept(this);
        }

        public List<RexNode> getProjectedExpressions() {
            return expressions;
        }
    }
}
