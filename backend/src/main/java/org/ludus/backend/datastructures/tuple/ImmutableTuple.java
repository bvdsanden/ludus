package org.ludus.backend.datastructures.tuple;

import java.util.Objects;

/**
 * @author Bram van der Sanden
 */
public final class ImmutableTuple<L, R> extends Tuple<L, R> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Left object
     */
    public final L left;
    /**
     * Right object
     */
    public final R right;

    /**
     * <p>Obtains an immutable triple of from three objects inferring the generic types.</p>
     * <p>
     * <p>This factory allows the triple to be created using inference to
     * obtain the generic types.</p>
     *
     * @param <L>   the left element type
     * @param <R>   the right element type
     * @param left  the left element, may be null
     * @param right the right element, may be null
     * @return a triple formed from the three parameters, not null
     */
    public static <L, R> ImmutableTuple<L, R> of(final L left, final R right) {
        return new ImmutableTuple<>(left, right);
    }

    /**
     * Create a new triple instance.
     *
     * @param left  the left value, may be null
     * @param right the right value, may be null
     */
    public ImmutableTuple(final L left, final R right) {
        super();
        this.left = left;
        this.right = right;
    }

    //-----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public L getLeft() {
        return left;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.left);
        hash = 61 * hash + Objects.hashCode(this.right);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImmutableTuple<?, ?> other = (ImmutableTuple<?, ?>) obj;
        if (!Objects.equals(this.left, other.left)) {
            return false;
        }
        return Objects.equals(this.right, other.right);
    }
}
