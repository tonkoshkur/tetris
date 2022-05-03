package ua.tonkoshkur.tetris.viewmodel;

import static ua.tonkoshkur.tetris.utils.Constants.MOVEMENT_PERIOD;
import static ua.tonkoshkur.tetris.utils.Constants.QUICK_MOVEMENT_PERIOD;
import static ua.tonkoshkur.tetris.utils.Constants.SPEED_INCREASING_STEP;
import static ua.tonkoshkur.tetris.utils.Constants.START_GAME_DELAY;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Timer;
import java.util.TimerTask;

public class GameViewModel extends AndroidViewModel {

    private final String TAG = GameViewModel.class.getSimpleName();
    private final MutableLiveData<Action> actionLiveData;
    private final MutableLiveData<GameStatus> gameStatusLiveData;
    private long movementPeriod;
    private Timer timer;

    public enum Action {
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
        this.actionLiveData = new MutableLiveData<>(Action.HOLD);
        this.gameStatusLiveData = new MutableLiveData<>(GameStatus.WAITING);
    }

    public MutableLiveData<Action> getActionLiveData() {
        return actionLiveData;
    }

    public MutableLiveData<GameStatus> getGameStatusLiveData() {
        return gameStatusLiveData;
    }

    public void startGame() {
        gameStatusLiveData.setValue(GameStatus.RUNNING);
        movementPeriod = MOVEMENT_PERIOD;
        timer = new Timer();
        timer.scheduleAtFixedRate(new MovementTimerTask(), START_GAME_DELAY, movementPeriod);
    }

    public void endGame() {
        resetTimer();
        gameStatusLiveData.postValue(GameStatus.FINISHED);
        actionLiveData.postValue(Action.HOLD);
    }

    public void moveLeft() {
        actionLiveData.postValue(Action.MOVE_LEFT);
    }

    public void moveRight() {
        actionLiveData.postValue(Action.MOVE_RIGHT);
    }

    public void rotateToLeft() {
        actionLiveData.postValue(Action.ROTATE_LEFT);
    }

    public void rotateToRight() {
        actionLiveData.postValue(Action.ROTATE_RIGHT);
    }

    public void turnOnQuickMoving() {
        if (isRunning()) {
            resetTimer();
            timer.schedule(new MovementTimerTask(), 0, QUICK_MOVEMENT_PERIOD);
        }
    }

    public void turnOffQuickMoving() {
        resetTimer();
        timer.schedule(new MovementTimerTask(), 0, movementPeriod);
    }

    public void increaseSpeedPermanent() {
        movementPeriod -= SPEED_INCREASING_STEP;
        resetTimer();
        timer.schedule(new MovementTimerTask(), 0, movementPeriod);
    }

    public void pause() {
        if (isRunning()) {
            resetTimer();
            gameStatusLiveData.postValue(GameStatus.PAUSED);
        }
    }

    public void resume() {
        if (isPaused()) {
            resetTimer();
            timer.schedule(new MovementTimerTask(), START_GAME_DELAY, movementPeriod);
            gameStatusLiveData.postValue(GameStatus.RUNNING);
        }
    }

    public boolean isRunning() {
        GameViewModel.GameStatus gameStatus = gameStatusLiveData.getValue();
        return gameStatus != null
                && gameStatus.equals(GameStatus.RUNNING);
    }

    public boolean isPaused() {
        GameViewModel.GameStatus gameStatus = gameStatusLiveData.getValue();
        return gameStatus != null
                && gameStatus.equals(GameStatus.PAUSED);
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
    }

    private class MovementTimerTask extends TimerTask {

        @Override
        public void run() {
            actionLiveData.postValue(Action.MOVE_BOTTOM);
        }
    }

}
