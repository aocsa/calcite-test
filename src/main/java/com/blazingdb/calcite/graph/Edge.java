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
package com.blazingdb.calcite.graph;


class Edge<N> implements Comparable<Edge<N>> {

  final N from, to;
  final int weight;

  public Edge(final N argFrom, final N argTo, final int argWeight) {
    from = argFrom;
    to = argTo;
    weight = argWeight;
  }

  public int compareTo(final Edge<N> argEdge) {
    return weight - argEdge.weight;
  }

  @Override
  public String toString() {
    return "Edge [from=" + from + ", to=" + to + "]";
  }


}
