package com.chrnie.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class GraphNode<T extends GraphNode<T>> {

  private final Set<T> parent = new HashSet<>();
  private final Set<T> children = new HashSet<>();

  final void addChild(GraphNode<T> child) {
    if (child == null) {
      throw new NullPointerException("child == null");
    }

    child.parent.add((T) this);
    children.add((T) child);
  }

  final Set<T> getParent() {
    return parent;
  }

  final Set<T> getChildren() {
    return children;
  }

  final List<T> findCycle() {
    Map<GraphNode<T>, State> nodeState = new HashMap<>();
    List<T> cyclicNode = new ArrayList<>();
    deepFirstTraverse(nodeState, cyclicNode);
    return cyclicNode.isEmpty() ? null : cyclicNode;
  }

  private boolean deepFirstTraverse(Map<GraphNode<T>, State> nodeState, List<T> cyclicNode) {
    nodeState.put(this, State.IN_STACK);

    for (GraphNode<T> child : children) {
      State state = nodeState.get(child);

      if (state == null /* unvisited */) {
        boolean inCycle = child.deepFirstTraverse(nodeState, cyclicNode);

        if (cyclicNode.isEmpty()) {
          continue;
        }

        if (!inCycle) {
          return false;
        }

        cyclicNode.add((T) this);
        return nodeState.get(this) != State.CYCLE_ORIGIN;
      }

      if (state == State.VISITED) {
        continue;
      }

      if (state == State.IN_STACK) {
        nodeState.put(child, State.CYCLE_ORIGIN);
        cyclicNode.add((T) this);
        return true;
      }

      throw new IllegalStateException("illegal node state: " + state);
    }

    nodeState.put(this, State.VISITED);
    return false;
  }

  private enum State {
    VISITED,
    IN_STACK,
    CYCLE_ORIGIN
  }
}
