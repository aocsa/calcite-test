/*
 * This file is a copy with some modifications of the FilterTableScanRule from
 * the Apache Calcite project. The original code can be found at:
 * https://github.com/apache/calcite/blob/branch-1.23/core/src/main/java/org/apache/calcite/rel/rules/FilterTableScanRule.java
 * The changes are about passing the column aliases extracted from the Filter
 * to our customized BindableTableScan.
 */
package com.blazingdb.calcite.rules;

import com.blazingdb.calcite.interpreter.BindableTableScan;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.enumerable.EnumerableInterpreter;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.FilterableTable;
import org.apache.calcite.schema.ProjectableFilterableTable;
import org.apache.calcite.tools.RelBuilderFactory;
import org.apache.calcite.util.ImmutableIntList;

import java.util.ArrayList;
import java.util.List;

/**
 * Planner rule that converts
 * a {@link Filter}
 * on a {@link TableScan}
 * of a {@link FilterableTable}
 * or a {@link ProjectableFilterableTable}
 * to a {@link BindableTableScan}.
 *
 * <p>The {@link #INTERPRETER} variant allows an intervening
 * {@link EnumerableInterpreter}.
 *
 * @see org.apache.calcite.rel.rules.ProjectTableScanRule
 */
public abstract class FilterTableScanRule extends RelOptRule {
	@SuppressWarnings("Guava")
	@Deprecated  // to be removed before 2.0
	public static final com.google.common.base.Predicate<TableScan> PREDICATE = FilterTableScanRule::test;

	/** Rule that matches Filter on TableScan. */
	public static final FilterTableScanRule INSTANCE = new FilterTableScanRule(
		operand(Filter.class, operandJ(TableScan.class, null, FilterTableScanRule::test, none())),
		RelFactories.LOGICAL_BUILDER,
		"FilterTableScanRule") {
		public void onMatch(RelOptRuleCall call) {
			final Filter filter = call.rel(0);
			final TableScan scan = call.rel(1);
			apply(call, filter, scan);
		}
	};

	/** Rule that matches Filter on EnumerableInterpreter on TableScan. */
	public static final FilterTableScanRule INTERPRETER = new FilterTableScanRule(
		operand(Filter.class,
			operand(EnumerableInterpreter.class, operandJ(TableScan.class, null, FilterTableScanRule::test, none()))),
		RelFactories.LOGICAL_BUILDER,
		"FilterTableScanRule:interpreter") {
		public void onMatch(RelOptRuleCall call) {
			final Filter filter = call.rel(0);
			final TableScan scan = call.rel(2);
			apply(call, filter, scan);
		}
	};

	//~ Constructors -----------------------------------------------------------

	@Deprecated  // to be removed before 2.0
	protected FilterTableScanRule(RelOptRuleOperand operand, String description) {
		this(operand, RelFactories.LOGICAL_BUILDER, description);
	}

	/** Creates a FilterTableScanRule. */
	protected FilterTableScanRule(RelOptRuleOperand operand, RelBuilderFactory relBuilderFactory, String description) {
		super(operand, relBuilderFactory, description);
	}

	//~ Methods ----------------------------------------------------------------

	public static boolean
	test(TableScan scan) {
		// We can only push filters into a FilterableTable or
		// ProjectableFilterableTable.
		final RelOptTable table = scan.getTable();
		return table.unwrap(FilterableTable.class) != null || table.unwrap(ProjectableFilterableTable.class) != null;
	}

	protected void
	apply(RelOptRuleCall call, Filter filter, TableScan scan) {
		final ImmutableIntList projects;
		final ImmutableList.Builder<RexNode> filters = ImmutableList.builder();
		final List<String> aliases;

		if(scan instanceof BindableTableScan) {
			final BindableTableScan bindableScan = (BindableTableScan) scan;
			filters.addAll(bindableScan.filters);
			projects = bindableScan.projects;
			aliases = bindableScan.aliases;
		} else {
			projects = scan.identity();
			aliases = new ArrayList<>();
		}

		filters.add(filter.getCondition());

		call.transformTo(
			BindableTableScan.create(scan.getCluster(), scan.getTable(), filters.build(), projects, aliases));
	}
}