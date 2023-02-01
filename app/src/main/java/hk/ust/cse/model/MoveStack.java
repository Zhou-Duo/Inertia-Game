package hk.ust.cse.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@link java.util.Stack}-like data structure to track all valid moves made by a player.
 */
public class MoveStack {

    @NotNull
    private final List<MoveResult> moves = new ArrayList<>();

    private int popCount = 0;

    /**
     * Pushes a move to this stack.
     *
     * @param move The move to push into this stack.
     */
    public void push(@NotNull final MoveResult move) {
        Objects.requireNonNull(move);
        if (!(move instanceof MoveResult.Valid.Alive)) {
            throw new IllegalArgumentException();
        }

        moves.add(move);
    }

    /**
     * @return Whether the stack is currently empty.
     */
    public boolean isEmpty() {
        return moves.isEmpty();
    }

    /**
     * Pops a move from this stack.
     *
     * @return The instance of {@link MoveResult} last performed by the player.
     */
    @NotNull
    public MoveResult pop() {
        assert peek() instanceof MoveResult.Valid.Alive;

        ++popCount;
        return moves.remove(moves.size() - 1);
    }

    /**
     * @return The number of {@link MoveStack#pop} calls invoked.
     */
    public int getPopCount() {
        return popCount;
    }

    /**
     * Peeks the topmost of the element of the stack.
     *
     * @return The instance of {@link MoveResult} at the top of the stack, corresponding to the last move performed by
     * the player.
     */
    @NotNull
    public MoveResult peek() {
        final var topmostMove = moves.get(moves.size() - 1);
        assert topmostMove instanceof MoveResult.Valid.Alive;

        return topmostMove;
    }
}
