package com.blazingdb.calcite.rules;

import com.querifylabs.blog.optimizer.join.DrillJoinRelBase;
import com.querifylabs.blog.optimizer.join.HashJoinPrule;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.*;
import org.apache.calcite.plan.volcano.RelSubset;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.rules.JoinPushThroughJoinRule;
import org.apache.calcite.rel.rules.TransformationRule;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexPermuteInputsShuttle;
import org.apache.calcite.rex.RexUtil;
import org.apache.calcite.tools.RelBuilderFactory;
import org.apache.calcite.util.ImmutableBitSet;
import org.apache.calcite.util.mapping.Mappings;

import java.util.ArrayList;
import java.util.List;

public class EnumJoinAssociateRule   extends RelRule<EnumJoinAssociateRule.Config>
        implements TransformationRule  {

//    public static final EnumJoinAssociateRule INSTANCE =
//            new EnumJoinAssociateRule(RelFactories.LOGICAL_BUILDER);


    public static final EnumJoinAssociateRule.Config  DEFAULT_CONFIG = EnumJoinAssociateRule.Config.DEFAULT;


    public static final RelOptRule INSTANCE =
            EnumJoinAssociateRule.DEFAULT_CONFIG.toRule();

    /** Creates a EnumJoinAssociateRule. */
    public EnumJoinAssociateRule(EnumJoinAssociateRule.Config config) {
        super(config);
    }

    @Deprecated // to be removed before 2.0
    public EnumJoinAssociateRule(RelBuilderFactory relBuilderFactory) {
        this(EnumJoinAssociateRule.Config.DEFAULT.withRelBuilderFactory(relBuilderFactory)
                .as(EnumJoinAssociateRule.Config.class));
    }


    /**
     * Splits a condition into conjunctions that do or do not intersect with
     * a given bit set.
     */
    static void split(
            RexNode condition,
            ImmutableBitSet bitSet,
            List<RexNode> intersecting,
            List<RexNode> nonIntersecting) {
        for (RexNode node : RelOptUtil.conjunctions(condition)) {
            ImmutableBitSet inputBitSet = RelOptUtil.InputFinder.bits(node);
            if (bitSet.intersects(inputBitSet)) {
                intersecting.add(node);
            } else {
                nonIntersecting.add(node);
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override public void onMatch(final RelOptRuleCall call) {
        final Join topJoin = call.rel(0);
        final Join bottomJoin = call.rel(1);
        final RelNode relA = bottomJoin.getLeft();
        final RelNode relB = bottomJoin.getRight();
        final RelSubset relC = call.rel(2);
        final RelOptCluster cluster = topJoin.getCluster();
        final RexBuilder rexBuilder = cluster.getRexBuilder();

        if (relC.getConvention() != relA.getConvention()) {
            // relC could have any trait-set. But if we're matching say
            // EnumerableConvention, we're only interested in enumerable subsets.
            return;
        }

        //        topJoin
        //        /     \
        //   bottomJoin  C
        //    /    \
        //   A      B

        final int aCount = relA.getRowType().getFieldCount();
        final int bCount = relB.getRowType().getFieldCount();
        final int cCount = relC.getRowType().getFieldCount();
        final ImmutableBitSet aBitSet = ImmutableBitSet.range(0, aCount);
        final ImmutableBitSet bBitSet =
                ImmutableBitSet.range(aCount, aCount + bCount);

        if (!topJoin.getSystemFieldList().isEmpty()) {
            // FIXME Enable this rule for joins with system fields
            return;
        }

        // If either join is not inner, we cannot proceed.
        // (Is this too strict?)
        if (topJoin.getJoinType() != JoinRelType.INNER
                || bottomJoin.getJoinType() != JoinRelType.INNER) {
            return;
        }

        // Goal is to transform to
        //
        //       newTopJoin
        //        /     \
        //       A   newBottomJoin
        //               /    \
        //              B      C

        // Split the condition of topJoin and bottomJoin into a conjunctions. A
        // condition can be pushed down if it does not use columns from A.
        final List<RexNode> top = new ArrayList<>();
        final List<RexNode> bottom = new ArrayList<>();

        split(topJoin.getCondition(), aBitSet, top, bottom);
        split(bottomJoin.getCondition(), aBitSet, top,
                bottom);

        // Mapping for moving conditions from topJoin or bottomJoin to
        // newBottomJoin.
        // target: | B | C      |
        // source: | A       | B | C      |
        final Mappings.TargetMapping bottomMapping =
                Mappings.createShiftMapping(
                        aCount + bCount + cCount,
                        0, aCount, bCount,
                        bCount, aCount + bCount, cCount);
        final List<RexNode> newBottomList =
                new RexPermuteInputsShuttle(bottomMapping, relB, relC)
                        .visitList(bottom);
        RexNode newBottomCondition =
                RexUtil.composeConjunction(rexBuilder, newBottomList);

//        final Join newBottomJoin =
//                bottomJoin.copy(bottomJoin.getTraitSet(), newBottomCondition, relB,
//                        relC, JoinRelType.INNER, false);

        final Join newBottomJoin = DrillJoinRelBase.create(
                relB,
                relC,
                newBottomCondition,
                bottomJoin.getVariablesSet(),
                bottomJoin.getJoinType());

        // Condition for newTopJoin consists of pieces from bottomJoin and topJoin.
        // Field ordinals do not need to be changed.
        RexNode newTopCondition = RexUtil.composeConjunction(rexBuilder, top);
        @SuppressWarnings("SuspiciousNameCombination")
//        final Join newTopJoin =
//                topJoin.copy(topJoin.getTraitSet(), newTopCondition, relA,
//                        newBottomJoin, JoinRelType.INNER, false);

        final Join newTopJoin = DrillJoinRelBase.create(
                relA,
                newBottomJoin,
                newTopCondition,
                topJoin.getVariablesSet(),
                topJoin.getJoinType());
        call.transformTo(newTopJoin);
    }

    /** Rule configuration. */
    public interface Config extends RelRule.Config {
        EnumJoinAssociateRule.Config DEFAULT = EMPTY.as(EnumJoinAssociateRule.Config.class)
                .withOperandFor(Join.class, RelSubset.class);

        @Override default EnumJoinAssociateRule toRule() {
            return new EnumJoinAssociateRule(this);
        }

        /** Defines an operand tree for the given classes. */
        default EnumJoinAssociateRule.Config withOperandFor(Class<? extends Join> joinClass,
                                                        Class<? extends RelSubset> relSubsetClass) {
            return withOperandSupplier(b0 ->
                    b0.operand(joinClass).inputs(
                            b1 -> b1.operand(joinClass).anyInputs(),
                            b2 -> b2.operand(relSubsetClass).anyInputs()))
                    .as(EnumJoinAssociateRule.Config.class);
        }
    }
}
