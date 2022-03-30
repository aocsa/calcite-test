/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querifylabs.blog.optimizer.cost;

import com.querifylabs.blog.optimizer.join.DrillJoinRelBase;
import com.querifylabs.blog.optimizer.join.DrillRelOptUtil;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.volcano.RelSubset;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.metadata.*;
import org.apache.calcite.rex.*;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.util.BuiltInMethod;
import org.apache.calcite.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DrillRelMdSelectivity extends RelMdSelectivity {
  private static final Logger logger = LoggerFactory.getLogger(DrillRelMdSelectivity.class);

  private static final DrillRelMdSelectivity INSTANCE = new DrillRelMdSelectivity();
  public static final RelMetadataProvider SOURCE = ReflectiveRelMetadataProvider.reflectiveSource(BuiltInMethod.SELECTIVITY.method, INSTANCE);
  /*
   * For now, we are treating all LIKE predicates to have the same selectivity irrespective of the number or position
   * of wildcard characters (%). This is no different than the present Drill/Calcite behaviour w.r.t to LIKE predicates.
   * The difference being Calcite keeps the selectivity 25% whereas we keep it at 5%
   * TODO: Differentiate leading/trailing wildcard characters(%) or explore different estimation techniques e.g. LSH-based
   */
  private static final double LIKE_PREDICATE_SELECTIVITY = 0.05;

  public static final Set<SqlKind> RANGE_PREDICATE =
    EnumSet.of(
      SqlKind.LESS_THAN, SqlKind.GREATER_THAN,
      SqlKind.LESS_THAN_OR_EQUAL, SqlKind.GREATER_THAN_OR_EQUAL);

  @Override
  public Double getSelectivity(RelNode rel, RelMetadataQuery mq, RexNode predicate) {
    if (rel instanceof RelSubset && !DrillRelOptUtil.guessRows(rel)) {
      return getSubsetSelectivity((RelSubset) rel, mq, predicate);
    } /*else if (rel instanceof TableScan) {
      return getScanSelectivity(rel, mq, predicate);
    } */else if (rel instanceof DrillJoinRelBase) {
      return getJoinSelectivity(((DrillJoinRelBase) rel), mq, predicate);
    } /*else if (rel instanceof SingleRel && !DrillRelOptUtil.guessRows(rel)) {
      return getSelectivity(((SingleRel)rel).getInput(), mq, predicate);
    }*/ else {
      return super.getSelectivity(rel, mq, predicate);
    }
  }

  private Double getJoinSelectivity(DrillJoinRelBase rel, RelMetadataQuery mq, RexNode predicate) {
    System.err.println("getJoinSelectivity");
    double sel = 1.0;
    // determine which filters apply to the left vs right
    RexNode leftPred, rightPred;
    JoinRelType joinType = rel.getJoinType();
    final RexBuilder rexBuilder = rel.getCluster().getRexBuilder();
    int[] adjustments = new int[rel.getRowType().getFieldCount()];

    if (DrillRelOptUtil.guessRows(rel)) {
      return super.getSelectivity(rel, mq, predicate);
    }

    if (predicate != null) {
      RexNode pred;
      List<RexNode> leftFilters = new ArrayList<>();
      List<RexNode> rightFilters = new ArrayList<>();
      List<RexNode> joinFilters = new ArrayList<>();
      List<RexNode> predList = RelOptUtil.conjunctions(predicate);

      RelOptUtil.classifyFilters(
              rel,
              predList,
              joinType,
              joinType == JoinRelType.INNER,
              !joinType.generatesNullsOnLeft(),
              !joinType.generatesNullsOnRight(),
              joinFilters,
              leftFilters,
              rightFilters);
      leftPred =
              RexUtil.composeConjunction(rexBuilder, leftFilters, true);
      rightPred =
              RexUtil.composeConjunction(rexBuilder, rightFilters, true);
      for (RelNode child : rel.getInputs()) {
        RexNode modifiedPred = null;

        if (child == rel.getLeft()) {
          pred = leftPred;
        } else {
          pred = rightPred;
        }
        if (pred != null) {
          // convert the predicate to reference the types of the children
          modifiedPred =
                  pred.accept(new RelOptUtil.RexInputConverter(
                          rexBuilder,
                          null,
                          child.getRowType().getFieldList(),
                          adjustments));
        }
        sel *= mq.getSelectivity(child, modifiedPred);
      }
      sel *= RelMdUtil.guessSelectivity(RexUtil.composeConjunction(rexBuilder, joinFilters, true));
    }
    return sel;
  }

  private Double getSubsetSelectivity(RelSubset rel, RelMetadataQuery mq, RexNode predicate) {
    if (rel.getBest() != null) {
      return getSelectivity(rel.getBest(), mq, predicate);
    } else {
      List<RelNode> list = rel.getRelList();
      if (list != null && list.size() > 0) {
        return getSelectivity(list.get(0), mq, predicate);
      }
    }
    //TODO: Not required? return mq.getSelectivity(((RelSubset)rel).getOriginal(), predicate);
    return RelMdUtil.guessSelectivity(predicate);
  }

}
