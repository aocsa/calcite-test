/*
 * This file is a copy with some modifications of the FilterTableScanRule from
 * the Apache Calcite project. The original code can be found at:
 * https://github.com/apache/calcite/blob/branch-1.23/core/src/main/java/org/apache/calcite/rel/rules/FilterTableScanRule.java
 * The changes are about passing the column aliases extracted from the Filter
 * to our customized BindableTableScan.
 */
package com.querifylabs.blog.optimizer.join;

 import com.google.common.collect.Lists;
 import org.apache.calcite.adapter.enumerable.EnumerableHashJoin;
 import org.apache.calcite.plan.*;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.*;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexUtil;
 import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.tools.RelBuilderFactory;

import java.util.List;

public abstract class JoinPruleBase extends RelOptRule {


	public static boolean
	test(Join join) {
	 return true;
	}


	//~ Constructors -----------------------------------------------------------

	@Deprecated  // to be removed before 2.0
	protected JoinPruleBase(RelOptRuleOperand operand, String description) {
		this(operand, RelFactories.LOGICAL_BUILDER, description);
	}

	/** Creates a FilterTableScanRule. */
	protected JoinPruleBase(RelOptRuleOperand operand, RelBuilderFactory relBuilderFactory, String description) {
		super(operand, relBuilderFactory, description);
	}
}

