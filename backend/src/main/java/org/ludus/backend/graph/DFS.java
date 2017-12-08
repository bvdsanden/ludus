package org.ludus.backend.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic depth-first search traversal of a graph.
 *
 * @author Bram van der Sanden
 */
public class DFS<V, E, R> {

    Graph<V, E> graph;
    V startVertex;
    R visitedResult;
    Map<Object, Boolean> statusMap = new HashMap<>();

    /**
     * Execute a depth first search traversal on graph g, starting
     * from a start vertex s.
     */
    public R execute(Graph<V, E> graph, V startVertex) {
        this.graph = graph;
        this.startVertex = startVertex;

        // Mark vertices as unvisited.
        for (V v : graph.getVertices()) {
            unvisit(v); // mark vertices as unvisited
        }

        // Mark edges as unvisited.
        for (E e : graph.getEdges()) {
            unvisit(e);
        }

        // Perform any necessary setup prior to the DFS traversal.
        setup();
        return finalResult(dfsTraversal(startVertex));
    }

    /**
     * Recursive template method for a generic DFS traversal.
     *
     * @param v current vertex
     * @return return result after completing the DFS-tree starting from v.
     */
    protected R dfsTraversal(V v) {
        initResult();
        if (!isDone())
            startVisit(v);
        if (!isDone()) {
            visit(v);
            for (E e : graph.outgoingEdgesOf(v)) {
                if (!isVisited(e)) {
                    // found an unexplored edge, explore it
                    visit(e);
                    V w = graph.getEdgeTarget(e);
                    if (!isVisited(w)) {
                        // Vertex w is unexplored, this is a discovery edge.
                        traverseDiscovery(e, v);
                        if (isDone()) break;
                        // Get result from DFS-tree child.
                        visitedResult = dfsTraversal(w);
                        if (isDone()) break;
                    } else {
                        // Vertex w is explored, this is a back edge.
                        traverseBack(e, v);
                        if (isDone()) break;
                    }
                }
            }
        }
        if (!isDone())
            finishVisit(v);
        return result();
    }

    /**
     * Mark a vertex or edge as visited.
     */
    protected void visit(Object o) {
        statusMap.put(o, true);
    }

    /**
     * Mark a vertex or edge as unvisited.
     */
    protected void unvisit(Object o) {
        statusMap.put(o, false);
    }

    /**
     * Test if a vertex or edge has been visited.
     */
    protected boolean isVisited(Object o) {
        return statusMap.get(o);
    }

    // Auxiliary methods (all initially null) for specializing a generic DFS

    /**
     * Setup method that is called prior to the DFS execution.
     */
    protected void setup() {
    }

    /**
     * Initializes result (called first, once per vertex visited).
     */
    protected void initResult() {
    }

    /**
     * Called when we encounter a vertex (v).
     */
    protected void startVisit(V v) {
    }

    /**
     * Called after we finish the visit for a vertex (v).
     */
    protected void finishVisit(V v) {
    }

    /**
     * Called when we traverse a discovery edge (e) from a vertex (from).
     */
    protected void traverseDiscovery(E e, V from) {
    }

    /**
     * Called when we traverse a back edge (e) from a vertex (from).
     */
    protected void traverseBack(E e, V from) {
    }

    /**
     * Determines whether the traversal is done early.
     */
    protected boolean isDone() {
        return false; /* default value */
    }

    /**
     * Returns a result of a visit (if needed).
     */
    protected R result() {
        return null; /* default value */
    }

    /**
     * Returns the final result of the DFS execute method.
     */
    protected R finalResult(R r) {
        return r; /* default value */
    }
}
