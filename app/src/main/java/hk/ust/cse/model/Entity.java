package hk.ust.cse.model;

import org.jetbrains.annotations.Nullable;

/**
 * An entity on the game board.
 */
public abstract sealed class Entity implements BoardElement permits ExtraLife, Gem, Mine, Player {

    @Nullable
    private EntityCell owner;

    /**
     * Creates an instance of {@link Entity}, initially not present on any {@link EntityCell}.
     */
    protected Entity() {
        this(null);
    }

    /**
     * Creates an instance of {@link Entity}.
     *
     * @param owner The initial {@link EntityCell} the entity resides on.
     */
    protected Entity(@Nullable final EntityCell owner) {
        this.owner = owner;
    }

    /**
     * Sets the new owner of this entity.
     *
     * @param owner The new {@link EntityCell} owning this entity, or {@code null} if this entity is no longer owned by
     *              any cell.
     * @return The previous {@link EntityCell} owning this entity, or {@code null} if this entity was not previously
     * owned by any cell.
     */
    @Nullable
    public final EntityCell setOwner(@Nullable final EntityCell owner) {
        final var prevOwner = getOwner();
        this.owner = owner;
        return prevOwner;
    }

    /**
     * @return The {@link EntityCell} owning this entity, or {@code null} if this entity is not bound to a cell.
     */
    @Nullable
    public final EntityCell getOwner() {
        return owner;
    }
}
