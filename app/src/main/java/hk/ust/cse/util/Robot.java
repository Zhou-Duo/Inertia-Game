package hk.ust.cse.util;

import hk.ust.cse.model.Direction;
import hk.ust.cse.model.GameState;
import hk.ust.cse.model.MoveResult;
import hk.ust.cse.model.Position;
import hk.ust.cse.view.panes.GameControlPane;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The Robot is an automated worker that can delegate the movement control of a player.
 * <p>
 * It implements the {@link MoveDelegate} interface and
 * is used by {@link GameControlPane#delegateControl(MoveDelegate)}.
 */
public class Robot implements MoveDelegate {
    public enum Strategy {
        Random, Smart
    }

    /**
     * A generator to get the time interval before the robot makes the next move.
     */
    public static Generator<Long> timeIntervalGenerator = TimeIntervalGenerator.everySecond();

    /**
     * e.printStackTrace();
     * The game state of thee.printStackTrace(); player that the robot delegates.
     */
    private final GameState gameState;

    /**
     * The strategy of this instance of robot.
     */
    private final Strategy strategy;

    public Robot(GameState gameState) {
        this(gameState, Strategy.Random);
    }

    public Robot(GameState gameState, Strategy strategy) {
        this.strategy = strategy;
        this.gameState = gameState;
    }

    private ArrayList<Thread> threadsList =new ArrayList<>();
    /**
     * Start the delegation in a new thread.
     * The delegation should run in a separate thread.
     * This method should return immediately when the thread is started.
     * <p>
     * In the delegation of the control of the player,
     * the time interval between moves should be obtained from {@link Robot#timeIntervalGenerator}.
     * That is to say, the new thread should:
     * <ol>
     *   <li>Stop all existing threads by calling {@link Robot#stopDelegation()}</li>
     *   <li>Start a new thread. And inside the thread:</li>
     *   <ul>
     *      <li>Wait for some time (obtained from {@link TimeIntervalGenerator#next()}</li>
     *      <li>Make a move, call {@link Robot#makeMoveRandomly(MoveProcessor)} or
     *      {@link Robot#makeMoveSmartly(MoveProcessor)} according to {@link Robot#strategy}</li>
     *      <li>repeat</li>
     *   </ul>
     * </ol>
     * The started thread should be able to exit when {@link Robot#stopDelegation()} is called.
     * <p>
     *
     * @param processor The processor to make movements.
     */
    @Override
    public void startDelegation(@NotNull MoveProcessor processor) {
        stopDelegation();
        Thread thread = new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(timeIntervalGenerator.next());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    if (strategy==Strategy.Smart){
                        makeMoveSmartly(processor);
                    } else makeMoveRandomly(processor);
                });
            }
        });
        threadsList.add(thread);
        thread.start();
    }

    /**
     * Stop the delegations, i.e., stop the thread of this instance.
     * When this method returns, the thread must have exited already.
     */
    @Override
    public void stopDelegation() {
        for (Thread thread:threadsList) {
            if (!thread.isInterrupted()) {
                thread.stop();
            }
        }
    }

    private MoveResult tryMove(Direction direction) {
        var player = gameState.getPlayer();
        if (player.getOwner() == null) {
            return null;
        }
        return gameState.getGameBoardController().tryMove(player.getOwner().getPosition(), direction, player.getId());
    }

    /**
     * The robot moves randomly but rationally,
     * which means the robot will not move to a direction that will make the player die if there are other choices,
     * but for other non-dying directions, the robot just randomly chooses one.
     * If there is no choice but only have one dying direction to move, the robot will still choose it.
     * If there is no valid direction, i.e. can neither die nor move, the robot do not perform a move.
     * <p>
     * Thread synchronization: make move randomely
     *
     * @param processor The processor to make movements.
     */
    private synchronized void makeMoveRandomly(MoveProcessor processor) {
        var directions = new ArrayList<>(Arrays.asList(Direction.values()));
        Collections.shuffle(directions);
        Direction aliveDirection = null;
        Direction deadDirection = null;
        for (var direction :
                directions) {
            var result = tryMove(direction);
            if (result instanceof MoveResult.Valid.Alive) {
                aliveDirection = direction;
            } else if (result instanceof MoveResult.Valid.Dead) {
                deadDirection = direction;
            }
        }
        if (aliveDirection != null) {
            processor.move(aliveDirection);
        } else if (deadDirection != null) {
            processor.move(deadDirection);
        }
    }

    /**
     * The robot moves with a smarter strategy compared to random.
     * This strategy is expected to beat random strategy in most of the time.
     *
     * @param processor The processor to make movements.
     */
    private void makeMoveSmartly(MoveProcessor processor) {
        var player = gameState.getPlayer();
        if (player.getOwner()==null) return;
        DirectionWithScore directionWithScore = bestDirection(player.getOwner().getPosition(),10);
        processor.move(directionWithScore.direction);
    }

    private record DirectionWithScore(Direction direction, int score) {}

    private DirectionWithScore bestDirection(Position currentPosition, int numSteps){
        if (numSteps==0)
            return new DirectionWithScore(Direction.UP,0);

        var player = gameState.getPlayer();
        ArrayList<DirectionWithScore> directionWithScores = new ArrayList<>();

        var directions = new ArrayList<>(Arrays.asList(Direction.values()));
        Collections.shuffle(directions);

        for (var direction : directions) {
            var result = gameState.getGameBoardController().tryMove(currentPosition, direction, player.getId());
            switch (result) {
                case MoveResult.Valid.Alive aliveResult -> {
                    int expectedScoreLater = bestDirection(aliveResult.newPosition, numSteps - 1).score;
                    int resultScore = aliveResult.collectedGems.size() * 10 - 1 + expectedScoreLater;
                    directionWithScores.add(new DirectionWithScore(direction, resultScore));
                }
                case MoveResult.Invalid invalidResult -> directionWithScores.add(new DirectionWithScore(direction, 0));
                case MoveResult.Valid.Dead m -> directionWithScores.add(new DirectionWithScore(direction, -10));
                default -> {
                }
            }
        }

        int highestScore = Integer.MIN_VALUE;
        DirectionWithScore bestDirectionWithScore = null;
        for (DirectionWithScore directionWithScore:directionWithScores) {
            if (directionWithScore.score>highestScore) {
                highestScore = directionWithScore.score;
                bestDirectionWithScore = directionWithScore;
            }
        }
        return bestDirectionWithScore;
    }
}
