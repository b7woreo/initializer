package com.chrnie.initializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class GraphNode<T extends GraphNode<T>> {

  private final Set<T> parent = new HashSet<>();
  private final Set<T> children = new HashSet<>();

  @SuppressWarnings("unchecked")
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
    Set<GraphNode<T>> visited = new HashSet<>();
    Set<GraphNode<T>> inStack = new HashSet<>();
    List<T> cyclicNode = new ArrayList<>();
    deepFirstTraverse(visited, inStack, cyclicNode);
    return cyclicNode.isEmpty() ? null : cyclicNode;
  }

  @SuppressWarnings("unchecked")
  private GraphNode<T> deepFirstTraverse(
      Set<GraphNode<T>> visited,
      Set<GraphNode<T>> inStack,
      List<T> cyclicNode) {

    if (visited.contains(this)) {
      return null;
    }

    if (inStack.contains(this)) {
      cyclicNode.add((T) this);
      return this;
    }

    inStack.add(this);

    for (GraphNode<T> child : children) {
      GraphNode<T> cycleOrigin = child.deepFirstTraverse(visited, inStack, cyclicNode);

      if (cyclicNode.isEmpty()) {
        continue;
      }

      if (cycleOrigin == null) {
        return null;
      }

      cyclicNode.add((T) this);
      return cycleOrigin.equals(this) ? null : cycleOrigin;
    }

    inStack.remove(this);
    visited.add(this);
    return null;
  }
}
