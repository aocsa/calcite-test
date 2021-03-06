/*
 * This file is a copy with some modifications of the ProjectJoinTransposeRule from
 * the Apache Calcite project. The original code can be found at:
 * https://github.com/apache/calcite/blob/branch-1.23/core/src/main/java/org/apache/calcite/rel/rules/ProjectJoinTransposeRule.java
 * The changes are about using a customized version from the PushProjector class.
 */

package com.blazingdb.calcite.rules;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.*;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.rules.TransformationRule;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.*;
import org.apache.calcite.tools.RelBuilderFactory;
import org.apache.calcite.util.mapping.Mappings;

import java.util.ArrayList;
import java.util.List;

/**
 * Planner rule that pushes a {@link Project}
 * past a {@link Join}
 * by splitting the projection into a projection on top of each child of
 * the join.
 */
public class ProjectJoinTransposeRule extends RelOptRule implements TransformationRule {
  /**
   * A instance for ProjectJoinTransposeRule that pushes a
   * {@link LogicalProject}
   * past a {@link LogicalJoin}
   * by splitting the projection into a projection on top of each child of
   * the join.
   */
  public static final ProjectJoinTransposeRule INSTANCE =
      new ProjectJoinTransposeRule(
          LogicalProject.class, LogicalJoin.class,
          expr -> !(expr instanceof RexOver),
          RelFactories.LOGICAL_BUILDER);

  //~ Instance fields --------------------------------------------------------

  /**
   * Condition for expressions that should be preserved in the projection.
   */
  private final PushProjector.ExprCondition preserveExprCondition;

  //~ Constructors -----------------------------------------------------------

  /**
   * Creates a ProjectJoinTransposeRule with an explicit condition.
   *
   * @param preserveExprCondition Condition for expressions that should be
   *                             preserved in the projection
   */
  public ProjectJoinTransposeRule(
      Class<? extends Project> projectClass,
      Class<? extends Join> joinClass,
      PushProjector.ExprCondition preserveExprCondition,
      RelBuilderFactory relFactory) {
    super(operand(projectClass, operand(joinClass, any())), relFactory, null);
    this.preserveExprCondition = preserveExprCondition;
  }

  //~ Methods ----------------------------------------------------------------

  // implement RelOptRule
  public void onMatch(RelOptRuleCall call) {
    Project origProj = call.rel(0);
    final Join join = call.rel(1);

    if (!join.getJoinType().projectsRight()) {
      return; // TODO: support SemiJoin / AntiJoin
    }

    // Normalize the join condition so we don't end up misidentified expanded
    // form of IS NOT DISTINCT FROM as PushProject also visit the filter condition
    // and push down expressions.
    RexNode joinFilter = join.getCondition().accept(new RexShuttle() {
      @Override public RexNode visitCall(RexCall rexCall) {
        final RexNode node = super.visitCall(rexCall);
        if (!(node instanceof RexCall)) {
          return node;
        }
        return RelOptUtil.collapseExpandedIsNotDistinctFromExpr((RexCall) node,
            call.builder().getRexBuilder());
      }
    });

    // locate all fields referenced in the projection and join condition;
    // determine which inputs are referenced in the projection and
    // join condition; if all fields are being referenced and there are no
    // special expressions, no point in proceeding any further
    PushProjector pushProject =
        new PushProjector(
            origProj,
            joinFilter,
            join,
            preserveExprCondition,
            call.builder());
    if (pushProject.locateAllRefs()) {
      return;
    }

    // create left and right projections, projecting only those
    // fields referenced on each side
    // Let's call another function
    RelNode leftProjRel =
        pushProject.createProjectRefsAndExprsCustomized(
            join.getLeft(),
            true,
            false,
            origProj);

    RelNode rightProjRel =
        pushProject.createProjectRefsAndExprsCustomized(
            join.getRight(),
            true,
            true,
            origProj);

    // convert the join condition to reference the projected columns
    RexNode newJoinFilter = null;
    int[] adjustments = pushProject.getAdjustments();
    if (joinFilter != null) {
      List<RelDataTypeField> projJoinFieldList = new ArrayList<>();
      projJoinFieldList.addAll(
          join.getSystemFieldList());
      projJoinFieldList.addAll(
          leftProjRel.getRowType().getFieldList());
      projJoinFieldList.addAll(
          rightProjRel.getRowType().getFieldList());
      newJoinFilter =
          pushProject.convertRefsAndExprs(
              joinFilter,
              projJoinFieldList,
              adjustments);
    }
    RelTraitSet traits = join.getTraitSet();
    final List<RelCollation> originCollations = traits.getTraits(RelCollationTraitDef.INSTANCE);

    if (originCollations != null && !originCollations.isEmpty()) {
      List<RelCollation> newCollations = new ArrayList<>();
      final int originLeftCnt = join.getLeft().getRowType().getFieldCount();
      final Mappings.TargetMapping leftMapping = RelOptUtil.permutationPushDownProject(
              ((Project) leftProjRel).getProjects(), join.getLeft().getRowType(),
              0, 0);
      final Mappings.TargetMapping rightMapping = RelOptUtil.permutationPushDownProject(
              ((Project) rightProjRel).getProjects(), join.getRight().getRowType(),
              originLeftCnt, leftProjRel.getRowType().getFieldCount());
      for (RelCollation collation: originCollations) {
        List<RelFieldCollation> fc = new ArrayList<>();
        final List<RelFieldCollation> fieldCollations = collation.getFieldCollations();
        for (RelFieldCollation relFieldCollation: fieldCollations) {
          final int fieldIndex = relFieldCollation.getFieldIndex();
          Mappings.TargetMapping mapping = fieldIndex < originLeftCnt ? leftMapping : rightMapping;
          RelFieldCollation newFieldCollation = RexUtil.apply(mapping, relFieldCollation);
          if (newFieldCollation == null) {
            break;
          }
          fc.add(newFieldCollation);
        }
        newCollations.add(RelCollations.of(fc));
      }
      if (!newCollations.isEmpty()) {
        traits = traits.replace(newCollations);
      }
    }
    // create a new join with the projected children
    Join newJoinRel =
        join.copy(
            traits,
            newJoinFilter,
            leftProjRel,
            rightProjRel,
            join.getJoinType(),
            join.isSemiJoinDone());

    // put the original project on top of the join, converting it to
    // reference the modified projection list
    RelNode topProject =
        pushProject.createNewProject(newJoinRel, adjustments);

    call.transformTo(topProject);
  }
}
