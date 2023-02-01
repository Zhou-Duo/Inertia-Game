package hk.ust.cse.util;

import hk.ust.cse.model.Direction;
import hk.ust.cse.model.Player;
import hk.ust.cse.view.panes.GameControlPane;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a delegate that delegate the movement control of {@link Player} instances
 * from {@link GameControlPane} GUI component to an automated worker.
 */
public interface MoveDelegate {

    /**
     * This functional interface represents a lambda expression describing the process to make movements according to a {@link Direction}.
     * This functional interface is designated to be used in {@link MoveDelegate#startDelegation(MoveProcessor)}.
     */
    @FunctionalInterface
    interface MoveProcessor {
        /**
         * Perform the movement according to the provided {@link Direction}.
         *
         * @param direction The direction to move.
         */
        void move(@NotNull Direction direction);
    }

    /**
     * Start the delegation.
     *
     * @param processor The processor to make movements.
     */
    void startDelegation(@NotNull MoveProcessor processor);

    /**
     * Stop the current delegation.
     * If not delegation is running, do nothing.
     */
    void stopDelegation();
}
