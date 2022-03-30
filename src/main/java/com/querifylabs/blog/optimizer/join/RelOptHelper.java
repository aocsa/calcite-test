package com.querifylabs.blog.optimizer.join;


import org.apache.calcite.rel.RelNode;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelTrait;

public class RelOptHelper {
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RelOptHelper.class);

    public static RelOptRuleOperand any(Class<? extends RelNode> first, RelTrait trait){
        return RelOptRule.operand(first, trait, RelOptRule.any());
    }

    public static RelOptRuleOperand any(Class<? extends RelNode> first){
        return RelOptRule.operand(first, RelOptRule.any());
    }

    public static RelOptRuleOperand any(Class<? extends RelNode> first, Class<? extends RelNode> second) {
        return RelOptRule.operand(first, RelOptRule.operand(second, RelOptRule.any()));
    }

    public static RelOptRuleOperand some(Class<? extends RelNode> rel, RelOptRuleOperand first, RelOptRuleOperand... rest){
        return RelOptRule.operand(rel, RelOptRule.some(first, rest));
    }

    public static RelOptRuleOperand some(Class<? extends RelNode> rel, RelTrait trait, RelOptRuleOperand first, RelOptRuleOperand... rest){
        return RelOptRule.operand(rel, trait, RelOptRule.some(first, rest));
    }

}
