package org.ludus.backend.por;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Undirected graph to store dependencies between names.
 *
 * @author Bram van der Sanden
 */
public class DependencyGraph implements DependencyInterface {

    private final Map<String, Set<String>> edges;
    private final Set<String> nodes;

    public DependencyGraph() {
        edges = new HashMap<>();
        nodes = new HashSet<>();
    }

    /**
     * Return the set of all nodes in the graph
     *
     * @return all nodes in dependency graph
     */
    public Set<String> getNodes() {
        return nodes;
    }

    /**
     * Return a set of all the dependencies in the graph for the given node
     *
     * @param node node in the graph
     * @return all outgoing dependencies from {@code node}
     */
    public Set<String> getDependencies(String node) {
        return edges.getOrDefault(node, new HashSet<>());
    }

    /**
     * Check whether there is a dependency between node1 and node2.
     *
     * @param node1 source node
     * @param node2 target node
     * @return true if and only if an edge exists between node1 and node2
     */
    public boolean hasDependency(String node1, String node2) {
        Set<String> node1deps = edges.getOrDefault(node1, new HashSet<>());
        return node1deps.contains(node2);
    }

    /**
     * Add a node to the dependency graph
     *
     * @param node node to add
     */
    public void addNode(String node) {
        nodes.add(node);
    }

    /**
     * Add a dependency between node1 and node2.
     *
     * @param node1 source node
     * @param node2 target node
     */
    public void addDependency(String node1, String node2) {
        addDirectedDependency(node1, node2);
        addDirectedDependency(node2, node1);
        nodes.add(node1);
        nodes.add(node2);
    }

    private void addDirectedDependency(String node1, String node2) {
        edges.putIfAbsent(node1, new HashSet<>());
        Set<String> node1Dependencies = edges.get(node1);
        node1Dependencies.add(node2);
    }

}
