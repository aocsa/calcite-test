package com.querifylabs.blog.optimizer.join;


import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;

/**
 * Relational expression that is implemented in Drill.
 */
public interface DrillRel extends RelNode {
    /** Calling convention for relational expressions that are "implemented" by
     * generating Drill logical plans. */
    Convention DRILL_LOGICAL = new Convention.Impl("LOGICAL", DrillRel.class);

 }
