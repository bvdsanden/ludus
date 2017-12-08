package org.ludus.backend.fsm;

import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.Location;

/**
 * Print a given finite-state machine to CIF syntax.
 * See also: http://cif.se.wtb.tue.nl/
 *
 * @author Bram van der Sanden
 */
public class PrintToCIF {

    public static String print(FSM<Location, Edge> fsm, String name) {
        StringBuilder sb = new StringBuilder();

        sb.append("supervisor automaton ");
        sb.append(name);
        sb.append(":\n");

        for (Location l : fsm.getVertices()) {
            sb.append("  location ");
            sb.append(l.getName());
            if (fsm.outgoingEdgesOf(l).isEmpty()) {
                sb.append(";\n");
            } else {
                sb.append(":\n");
            }
            if (l.equals(fsm.getInitial())) {
                sb.append("    initial; marked;\n");
            }

            for (Edge e : fsm.outgoingEdgesOf(l)) {
                sb.append("    edge ");
                sb.append(e.getEvent());
                sb.append(" goto ");
                sb.append(e.getTarget().getName());
                sb.append(";\n");
            }

        }

        sb.append("end");
        return sb.toString();
    }

}
