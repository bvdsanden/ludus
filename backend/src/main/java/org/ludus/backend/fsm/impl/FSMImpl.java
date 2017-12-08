package org.ludus.backend.fsm.impl;

import org.ludus.backend.fsm.FSM;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FSM implementation using an adjacency list representation.
 * Note that the incoming edges are not stored explicitly.
 *
 * @author Bram van der Sanden
 */
public class FSMImpl implements FSM<Location, Edge> {

    private final Set<Location> locations;
    private final HashMap<String, Location> locMap;
    private Location initial;

    Map<Location, Set<Edge>> outgoingMap;

    private final Set<String> controllable;
    private final Set<String> uncontrollable;

    public FSMImpl() {
        locations = new LinkedHashSet<>();
        locMap = new HashMap<>();
        controllable = new HashSet<>();
        uncontrollable = new HashSet<>();
        outgoingMap = new HashMap<>();
    }

    public void addLocation(Location... locations) {
        for (Location loc : locations) {
            locMap.put(loc.getName(), loc);
            this.locations.add(loc);
        }
    }

    public Location getLocation(String name) {
        return locMap.get(name);
    }

    public void setInitial(Location initial) {
        this.initial = initial;
    }

    public Edge addEdge(Location source, Location target, String event) {
        Edge edge = new Edge(source, event, target);
        addEdge(edge);
        return edge;
    }

    public void addEdge(Edge edge) {
        Location src = edge.getSource();
        outgoingMap.putIfAbsent(src, new HashSet<>());
        Set<Edge> out = outgoingMap.get(src);
        out.add(edge);
    }

    public void addControllable(String... events) {
        Collections.addAll(controllable, events);
    }

    public void addUncontrollable(String... events) {
        Collections.addAll(uncontrollable, events);
    }

    @Override
    public Location getInitial() {
        return initial;
    }

    @Override
    public Set<String> getControllable() {
        return controllable;
    }

    @Override
    public Set<String> getUncontrollable() {
        return uncontrollable;
    }

    @Override
    public Set<Edge> incomingEdgesOf(Location location) {
        Set<Edge> incoming = new HashSet<>();
        for (Location l : getVertices()) {
            for (Edge e : outgoingEdgesOf(l)) {
                if (e.getTarget().equals(location)) {
                    incoming.add(e);
                }
            }
        }
        return incoming;
    }

    @Override
    public Set<Edge> outgoingEdgesOf(Location location) {
        return outgoingMap.getOrDefault(location, Collections.EMPTY_SET);
    }

    @Override
    public Set<Location> getVertices() {
        return locations;
    }

    @Override
    public Set<Edge> getEdges() {
        Set<Edge> edges = new HashSet<>();
        for (Location l : getVertices()) {
            edges.addAll(outgoingEdgesOf(l));
        }
        return edges;
    }

    @Override
    public Location getEdgeSource(Edge edge) {
        return edge.getSource();
    }

    @Override
    public Location getEdgeTarget(Edge edge) {
        return edge.getTarget();
    }

    @Override
    public Edge getEdge(Location source, Location target) {
        for (Edge e : outgoingEdgesOf(source)) {
            if (e.getTarget().equals(target)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public Collection<Edge> getEdges(Location source, Location target) {
        return outgoingEdgesOf(source).stream().filter(e -> e.getTarget().equals(target)).collect(Collectors.toSet());
    }

    @Override
    public Edge getEdge(Location source, Location target, String event) {
        for (Edge e : outgoingEdgesOf(source)) {
            if (e.getTarget().equals(target) && e.getEvent().equals(event)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String getEvent(Edge e) {
        return e.getEvent();
    }

    /**
     * Return the union of the controllable and uncontrollable events.
     *
     * @return alphabet of the FSM
     */
    @Override
    public Set<String> getAlphabet() {
        Set<String> alphabet = new HashSet<>();
        alphabet.addAll(getControllable());
        alphabet.addAll(getUncontrollable());
        return alphabet;
    }

    @Override
    public boolean hasEdge(Location source, Location target, String event) {
        return outgoingEdgesOf(source).stream().anyMatch(e -> e.getTarget().equals(target) && e.getEvent().equals(event));
    }
}
