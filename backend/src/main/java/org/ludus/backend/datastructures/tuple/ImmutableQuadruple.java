package org.ludus.backend.datastructures.tuple;

import java.util.Objects;

/**
 * @author Bram van der Sanden
 */
public final class ImmutableQuadruple<L, ML, MR, R> extends Quadruple<L, ML, MR, R> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Left object
     */
    public final L left;
    /**
     * Middle object
     */
    public final ML middleLeft;
    public final MR middleRight;
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
    public static <L, ML, MR, R> ImmutableQuadruple<L, ML, MR, R> of(final L left, final ML middleLeft, final MR middleRight, final R right) {
        return new ImmutableQuadruple<>(left, middleLeft, middleRight, right);
    }

    /**
     * Create a new triple instance.
     *
     * @param left        the left value, may be null
     * @param middleLeft  the middle left element, may be null
     * @param middleRight the middle right element, may be null
     * @param right       the right value, may be null
     */
    public ImmutableQuadruple(final L left, final ML middleLeft, final MR middleRight, final R right) {
        super();
        this.left = left;
        this.middleLeft = middleLeft;
        this.middleRight = middleRight;
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
    public ML getMiddleLeft() {
        return middleLeft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MR getMiddleRight() {
        return middleRight;
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
        hash = 97 * hash + Objects.hashCode(this.left);
        hash = 97 * hash + Objects.hashCode(this.middleLeft);
        hash = 97 * hash + Objects.hashCode(this.middleRight);
        hash = 97 * hash + Objects.hashCode(this.right);
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
        final ImmutableQuadruple<?, ?, ?, ?> other = (ImmutableQuadruple<?, ?, ?, ?>) obj;
        if (!Objects.equals(this.left, other.left)) {
            return false;
        }
        if (!Objects.equals(this.middleLeft, other.middleLeft)) {
            return false;
        }
        if (!Objects.equals(this.middleRight, other.middleRight)) {
            return false;
        }
        return Objects.equals(this.right, other.right);
    }

}
