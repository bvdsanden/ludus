package org.ludus.backend.por;

import java.util.Set;

/**
 * Dependency interface that allows to check whether two events are dependent, given some notion of dependency.
 */
public interface DependencyInterface {

    boolean hasDependency(String eventA, String eventB);

    Set<String> getDependencies(String event);

}
