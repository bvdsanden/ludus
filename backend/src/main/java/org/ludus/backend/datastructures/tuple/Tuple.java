package org.ludus.backend.datastructures.tuple;

/**
 * @author Bram van der Sanden
 */
public abstract class Tuple<L, R> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;

    /**
     * <p>Obtains an immutable tuple of from two objects inferring the generic types.</p>
     * <p>
     * <p>This factory allows the tuple to be created using inference to
     * obtain the generic types.</p>
     *
     * @param <L>   the left element type
     * @param <R>   the right element type
     * @param left  the left element, may be null
     * @param right the right element, may be null
     * @return a tuple formed from the three parameters, not null
     */
    public static <L, R> Tuple<L, R> of(final L left, final R right) {
        return new ImmutableTuple<>(left, right);
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Gets the left element from this triple.</p>
     *
     * @return the left element, may be null
     */
    public abstract L getLeft();

    /**
     * <p>Gets the right element from this triple.</p>
     *
     * @return the right element, may be null
     */
    public abstract R getRight();

}
