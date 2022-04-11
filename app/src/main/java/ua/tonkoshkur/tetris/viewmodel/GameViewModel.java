package ua.tonkoshkur.tetris.viewmodel;

import static ua.tonkoshkur.tetris.utils.Constants.PAUSE_BETWEEN_MOVEMENTS;
import static ua.tonkoshkur.tetris.utils.Constants.PAUSE_BETWEEN_QUICK_MOVEMENTS;
import static ua.tonkoshkur.tetris.utils.Constants.SPEED_INCREASING_STEP;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class GameViewModel extends AndroidViewModel {

    private final MutableLiveData<PuzzleAction> puzzleActionLiveData = new MutableLiveData<>(PuzzleAction.HOLD);
    private GameStatus gameStatus = GameStatus.WAITING;
    private long pauseBetweenMovements = PAUSE_BETWEEN_MOVEMENTS;
    private boolean isMoveQuickly = false;

    public enum PuzzleAction {
        HOLD,
        MOVE_LEFT,
        MOVE_RIGHT,
        MOVE_BOTTOM,
        ROTATE_LEFT,
        ROTATE_RIGHT
    }

    public enum GameStatus {
        WAITING,
        RUNNING,
        PAUSED,
        FINISHED
    }

    public GameViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PuzzleAction> getActionLiveData() {
        return puzzleActionLiveData;
    }

    public void startGame() {
        new Thread(() -> {
            try {
                Thread.sleep(1000L);
                run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void endGame() {
        gameStatus = GameStatus.FINISHED;
        puzzleActionLiveData.postValue(PuzzleAction.HOLD);
        pauseBetweenMovements = PAUSE_BETWEEN_MOVEMENTS;
    }

    public void moveToLeft() {
        puzzleActionLiveData.postValue(PuzzleAction.MOVE_LEFT);
    }

    public void moveToRight() {
        puzzleActionLiveData.postValue(PuzzleAction.MOVE_RIGHT);
    }

    public void turnOnQuickMoving() {
        isMoveQuickly = true;
    }

    public void turnOffQuickMoving() {
        isMoveQuickly = false;
    }

    public void increaseSpeedPermanent() {
        pauseBetweenMovements -= SPEED_INCREASING_STEP;
    }

    public void rotateToLeft() {
        puzzleActionLiveData.postValue(PuzzleAction.ROTATE_LEFT);
    }

    public void rotateToRight() {
        puzzleActionLiveData.postValue(PuzzleAction.ROTATE_RIGHT);
    }

    public void pause() {
        gameStatus = GameStatus.PAUSED;
    }

    public void resume() {
        gameStatus = GameStatus.RUNNING;
    }

    private void run() throws InterruptedException {
        gameStatus = GameStatus.RUNNING;
        while (!gameStatus.equals(GameStatus.FINISHED)) {
            while (gameStatus.equals(GameStatus.PAUSED)) {
                Thread.sleep(500);
            }

            puzzleActionLiveData.postValue(PuzzleAction.MOVE_BOTTOM);
            if (isMoveQuickly) {
                Thread.sleep(PAUSE_BETWEEN_QUICK_MOVEMENTS);
            } else {
                Thread.sleep(pauseBetweenMovements);
            }
        }
    }

}
