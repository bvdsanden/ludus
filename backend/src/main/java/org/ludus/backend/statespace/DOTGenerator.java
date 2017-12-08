package org.ludus.backend.statespace;

import org.ludus.backend.algebra.Vector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts a state space to dot output.
 * The dot2tex program transforms the dot graph to TikZ code, that can be embedded in LaTeX.
 *
 * @author Bram van der Sanden
 */
public class DOTGenerator {

    private boolean showReward = false;

    public void setReward(boolean showReward) {
        this.showReward = showReward;
    }


    public void generate(MaxPlusStateSpace graph, File outputFile) throws IOException {
        FileWriter fw = new FileWriter(outputFile);
        BufferedWriter output = new BufferedWriter(fw);

        // Output header.
        printHeader(output);

        Map<Configuration, Integer> cidMap = new HashMap<>();

        // Configurations.
        int cid = 0;
        for (Configuration c : graph.getVertices()) {
            printConfiguration(output, graph, c, cid);
            cidMap.put(c, cid);
            cid += 1;
        }

        // Transitions.
        for (Transition t : graph.getEdges()) {
            printTransition(output, graph, t, cidMap.get(t.getSource()), cidMap.get(t.getTarget()));
        }

        // Output footer.
        printFooter(output);


        output.close();
        fw.close();
    }

    private void printConfiguration(BufferedWriter output, MaxPlusStateSpace graph, Configuration configuration, Integer cid) throws IOException {
        StringBuilder sb = new StringBuilder();
        // Configuration id.
        sb.append("  c").append(cid);

        // Label.
        sb.append("[texlbl=\"$\\langle ").
                append(configuration.getLocation().toString()).append(", ").
                append("\\begin{bmatrix}");

        Vector vec = configuration.getVector();
        for (int col = 0; col < vec.size(); col++) {
            sb.append(vec.get(col));
            if (col != vec.size() - 1) {
                sb.append("\\\\");
            }
        }
        sb.append("\\end{bmatrix}\\rangle$\"];\n");

        output.write(sb.toString());
    }

    private void printTransition(BufferedWriter output, MaxPlusStateSpace graph, Transition t, Integer src, Integer tgt) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("  c").append(src).append(" -> ").append("c").append(tgt).append(" [label=\"").append(t.getEvent()).append(",");
        if (showReward) {
            sb.append(t.getReward()).append(",");
        }
        sb.append(t.getDuration()).append("\"];\n");

        output.write(sb.toString());
    }


    private void printHeader(BufferedWriter output) throws IOException {
        output.write("digraph statespace {\n  node [shape=none margin=0 width=0 height=0.55];\n");
    }

    private void printFooter(BufferedWriter output) throws IOException {
        output.write("}");
    }

}
