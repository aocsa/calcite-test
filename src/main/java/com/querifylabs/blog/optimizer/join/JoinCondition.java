package com.querifylabs.blog.optimizer.join;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinCondition {
    private final String relationship;
    private final LogicalExpression left;
    private final LogicalExpression right;

    @JsonCreator
    public JoinCondition(@JsonProperty("relationship") String relationship,
                         @JsonProperty("left") LogicalExpression left, @JsonProperty("right") LogicalExpression right) {
        super();
        this.relationship = relationship;
        this.left = left;
        this.right = right;
    }

    public String getRelationship() {
        return relationship;
    }

    public LogicalExpression getLeft() {
        return left;
    }

    public LogicalExpression getRight() {
        return right;
    }

    public JoinCondition flip(){
        return new JoinCondition(relationship, right, left);
    }

}