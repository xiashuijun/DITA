/*
 *  Copyright 2017 by DITA Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.spark.sql.execution.dita.sql

import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.expressions.dita.{TrajectorySimilarityFunction, TrajectorySimilarityWithKNNExpression, TrajectorySimilarityWithThresholdExpression}
import org.apache.spark.sql.catalyst.expressions.{Expression, PredicateHelper}
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.catalyst.plans.{JoinType, logical}

/**
  * A pattern that finds joins with trajectory similarity conditions.
  */
object ExtractTrajectorySimilarityWithThresholdJoin extends Logging with PredicateHelper {
  type ReturnType =
    (JoinType, Expression, Expression, TrajectorySimilarityFunction, Double, LogicalPlan, LogicalPlan)

  def unapply(plan: LogicalPlan): Option[ReturnType] = plan match {
    case logical.Join(left, right, joinType, condition) =>
      logDebug(s"Considering join on: $condition")
      if (condition.isDefined) {
        condition.get match {
          case TrajectorySimilarityWithThresholdExpression(similarity, threshold) =>
            Some((joinType, similarity.traj1, similarity.traj2, similarity.function,
              threshold, left, right))
          case _ => None
        }
      } else {
        None
      }
    case _ => None
  }
}

object ExtractTrajectorySimilarityWithKNNJoin extends Logging with PredicateHelper {
  type ReturnType =
    (JoinType, Expression, Expression, TrajectorySimilarityFunction, Int, LogicalPlan, LogicalPlan)

  def unapply(plan: LogicalPlan): Option[ReturnType] = plan match {
    case logical.Join(left, right, joinType, condition) =>
      logDebug(s"Considering join on: $condition")
      if (condition.isDefined) {
        condition.get match {
          case TrajectorySimilarityWithKNNExpression(similarity, count) =>
            Some((joinType, similarity.traj1, similarity.traj2, similarity.function,
              count, left, right))
          case _ => None
        }
      } else {
        None
      }
    case _ => None
  }
}
