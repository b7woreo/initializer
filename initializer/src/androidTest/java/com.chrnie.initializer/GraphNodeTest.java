package com.chrnie.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GraphNodeTest {

  @Test
  public void defaultParent() {
    MockGraphNode node = new MockGraphNode(0);
    Set<MockGraphNode> parent = node.getParent();
    assertEquals(0, parent.size());
  }

  @Test
  public void defaultChildren() {
    MockGraphNode node = new MockGraphNode(0);
    Set<MockGraphNode> children = node.getChildren();
    assertEquals(0, children.size());
  }

  @Test
  public void addChild() {
    MockGraphNode node0 = new MockGraphNode(0);
    MockGraphNode node1 = new MockGraphNode(1);

    node0.addChild(node1);

    {
      Set<MockGraphNode> children = node0.getChildren();
      assertEquals(1, children.size());
      assertTrue(children.contains(node1));

      Set<MockGraphNode> parent = node1.getParent();
      assertEquals(1, parent.size());
      assertTrue(parent.contains(node0));
    }

    // test repeat add same child without change
    node0.addChild(node1);

    {
      Set<MockGraphNode> children = node0.getChildren();
      assertEquals(1, children.size());
      assertTrue(children.contains(node1));

      Set<MockGraphNode> parent = node1.getParent();
      assertEquals(1, parent.size());
      assertTrue(parent.contains(node0));
    }
  }

  @Test
  public void findCycle_graphWithoutCycle() {
    MockGraphNode node0 = new MockGraphNode(0);
    MockGraphNode node1 = new MockGraphNode(1);
    MockGraphNode node2 = new MockGraphNode(2);

    node0.addChild(node1);
    node1.addChild(node2);

    List<MockGraphNode> cycle = node0.findCycle();
    assertNull(cycle);
  }

  @Test
  public void findCycle_graphWithCycle() {
    MockGraphNode node0 = new MockGraphNode(0);
    MockGraphNode node1 = new MockGraphNode(1);
    MockGraphNode node2 = new MockGraphNode(2);

    node0.addChild(node1);
    node1.addChild(node2);
    node2.addChild(node0);

    List<MockGraphNode> cycle = node0.findCycle();
    assertNotNull(cycle);
    assertEquals(Arrays.asList(node0, node2, node1, node0), cycle);
  }
}
