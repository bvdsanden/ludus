package org.ludus.backend.graph.weighted;

/**
 * @author Bram van der Sanden
 */
public class WDoubleEdge {
    Integer _w1;
    Integer _w2;
    WVertex src;
    WVertex tgt;

    public WDoubleEdge(WVertex source, WVertex target, Integer w1, Integer w2) {
        _w1 = w1;
        _w2 = w2;
        src = source;
        tgt = target;
    }

    public Integer getWeight1() {
        return _w1;
    }

    public Integer getWeight2() {
        return _w2;
    }

    public WVertex getSource() {
        return src;
    }

    public WVertex getTarget() {
        return tgt;
    }

}
