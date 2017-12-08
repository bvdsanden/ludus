package org.ludus.backend.datastructures.tuple;

/**
 * @author Bram van der Sanden
 */
public abstract class Triple<L, M, R> {

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
     * @param <L>    the left element type
     * @param <M>    the middle element type
     * @param <R>    the right element type
     * @param left   the left element, may be null
     * @param middle the middle element, may be null
     * @param right  the right element, may be null
     * @return a triple formed from the three parameters, not null
     */
    public static <L, M, R> Triple<L, M, R> of(final L left, final M middle, final R right) {
        return new ImmutableTriple<>(left, middle, right);
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Gets the left element from this triple.</p>
     *
     * @return the left element, may be null
     */
    public abstract L getLeft();

    /**
     * <p>Gets the middle element from this triple.</p>
     *
     * @return the middle element, may be null
     */
    public abstract M getMiddle();

    /**
     * <p>Gets the right element from this triple.</p>
     *
     * @return the right element, may be null
     */
    public abstract R getRight();

}
