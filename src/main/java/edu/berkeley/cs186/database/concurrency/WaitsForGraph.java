package edu.berkeley.cs186.database.concurrency;

import java.util.*;

/**
 * A waits for graph for the lock manager (used to detect if
 * deadlock will occur and throw a DeadlockException if it does).
 */
public class WaitsForGraph {

  // We store the directed graph as an adjacency list where each node (transaction) is
  // mapped to a list of the nodes it has an edge to.
  private Map<Long, ArrayList<Long>> graph;

  public WaitsForGraph() {
    graph = new HashMap<Long, ArrayList<Long>>();
  }

  public boolean containsNode(long transNum) {
    return graph.containsKey(transNum);
  }

  protected void addNode(long transNum) {
    if (!graph.containsKey(transNum)) {
      graph.put(transNum, new ArrayList<Long>());
    }
  }

  protected void addEdge(long from, long to) {
    if (!this.edgeExists(from, to)) {
      ArrayList<Long> edges = graph.get(from);
      edges.add(to);
    }
  }

  protected void removeEdge(long from, long to) {
    if (this.edgeExists(from, to)) {
      ArrayList<Long> edges = graph.get(from);
      edges.remove(to);
    }
  }

  protected boolean edgeExists(long from, long to) {
    if (!graph.containsKey(from)) {
      return false;
    }
    ArrayList<Long> edges = graph.get(from);
    return edges.contains(to);
  }

  /**
   * Checks if adding the edge specified by to and from would cause a cycle in this
   * WaitsForGraph. Does not actually modify the graph in any way.
   *
   * @param from the transNum from which the edge points
   * @param to   the transNum to which the edge points
   * @return
   */
  protected boolean edgeCausesCycle(long from, long to) {
    //TODO: Implement Me!!
    boolean alteredFrom = false;
    boolean alteredTo = false;
    boolean alteredEdge = false;

    if (!this.containsNode(from)) {
      alteredFrom = true;
      this.addNode(from);
    }
    if (!this.containsNode(to)) {
      alteredTo = true;
      this.addNode(to);
    }
    if (!this.edgeExists(from, to)) {
      alteredEdge = true;
      this.addEdge(from, to);
    }

    boolean isCyclic = hasCycle();
    if (alteredEdge) {
      this.removeEdge(from, to);
    }
    if (alteredTo) {
      this.graph.remove(to);
    }
    if (alteredFrom) {
      this.graph.remove(from);
    }



    return isCyclic;
  }

  // DFS algorithm from http://www.geeksforgeeks.org/detect-cycle-direct-graph-using-colors/

  public enum Color {WHITE, GRAY, BLACK};
  Map<Long, Color> colorMap;

  protected boolean helperDFS(Long u)
  {

    colorMap.put(u, Color.GRAY);

    for (Long i : graph.get(u)) {
      if (colorMap.get(i) == Color.GRAY) {
        return true;
      }

      if (colorMap.get(i) == Color.WHITE && helperDFS(i)) {
        return true;
      }
    }
    colorMap.put(u, Color.BLACK);

    return false;
  }
  protected boolean hasCycle()
  {

    colorMap = new HashMap<Long, Color>();

    for (Long i : graph.keySet()) {
      colorMap.put(i, Color.WHITE);
    }
    for (Long i : colorMap.keySet()) {
      if (colorMap.get(i) == Color.WHITE) {
        if (helperDFS(i)) {
          return true;
        }
      }
    }

    return false;
  }

}