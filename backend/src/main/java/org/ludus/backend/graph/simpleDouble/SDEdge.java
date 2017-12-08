package org.ludus.backend.graph.simpleDouble;

/**
 * @author Bram van der Sanden
 */
public class SDEdge {
    Double _w1;
    Double _w2;
    SDVertex src;
    SDVertex tgt;

    public SDEdge(SDVertex source, SDVertex target, Double w1, Double w2) {
        _w1 = w1;
        _w2 = w2;
        src = source;
        tgt = target;
    }

    public Double getWeight1() {
        return _w1;
    }

    public Double getWeight2() {
        return _w2;
    }

    public SDVertex getSource() {
        return src;
    }

    public SDVertex getTarget() {
        return tgt;
    }

}
