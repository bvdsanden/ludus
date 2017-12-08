package org.ludus.backend.datastructures.tuple;

/**
 * @author Bram van der Sanden
 */
public abstract class Quadruple<L, ML, MR, R> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;

    /**
     * <p>Obtains an immutable triple of from three objects inferring the generic types.</p>
     * <p>
     * <p>This factory allows the triple to be created using inference to
     * obtain the generic types.</p>
     *
     * @param <L>         the left element type
     * @param <ML>        the middle left element type
     * @param <MR>        the middle right element type
     * @param <R>         the right element type
     * @param left        the left element, may be null
     * @param middleLeft  the middle left element, may be null
     * @param middleRight the middle right element, may be null
     * @param right       the right element, may be null
     * @return a triple formed from the three parameters, not null
     */
    public static <L, ML, MR, R> Quadruple<L, ML, MR, R> of(final L left, final ML middleLeft, final MR middleRight, final R right) {
        return new ImmutableQuadruple<>(left, middleLeft, middleRight, right);
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Gets the left element from this triple.</p>
     *
     * @return the left element, may be null
     */
    public abstract L getLeft();

    /**
     * <p>Gets the middle left element from this triple.</p>
     *
     * @return the middle left element, may be null
     */
    public abstract ML getMiddleLeft();

    /**
     * <p>Gets the middle right element from this triple.</p>
     *
     * @return the middle right element, may be null
     */
    public abstract MR getMiddleRight();

    /**
     * <p>Gets the right element from this triple.</p>
     *
     * @return the right element, may be null
     */
    public abstract R getRight();

}
