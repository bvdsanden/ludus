package org.ludus.backend.graph.simpleSingle;

/**
 * @author Bram van der Sanden
 */
public class SSEdge {
    Double _w;
    SSVertex src;
    SSVertex tgt;

    public SSEdge(SSVertex source, SSVertex target, Double weight) {
        _w = weight;
        src = source;
        tgt = target;
    }

    public Double getWeight() {
        return _w;
    }

    public SSVertex getSource() {
        return src;
    }

    public SSVertex getTarget() {
        return tgt;
    }

}
