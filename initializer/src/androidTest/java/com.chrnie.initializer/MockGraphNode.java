package com.chrnie.initializer;

public class MockGraphNode extends GraphNode<MockGraphNode> {

  private final int id;

  public MockGraphNode(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MockGraphNode that = (MockGraphNode) o;

    return id == that.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
