package ua.tonkoshkur.tetris.viewmodel;

import static ua.tonkoshkur.tetris.utils.Constants.AFTER_LAST_TAP_MOVEMENT_DELAY;
import static ua.tonkoshkur.tetris.utils.Constants.MAX_NUMBER_OF_MOVEMENT_DELAYS;
import static ua.tonkoshkur.tetris.utils.Constants.MOVEMENT_PERIOD;
import static ua.tonkoshkur.tetris.utils.Constants.QUICK_MOVEMENT_PERIOD;
import static ua.tonkoshkur.tetris.utils.Constants.SPEED_INCREASING_STEP;
import static ua.tonkoshkur.tetris.utils.Constants.START_GAME_DELAY;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Timer;
import java.util.TimerTask;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.factory.BlockFactory;
import ua.tonkoshkur.tetris.model.Block;
import ua.tonkoshkur.tetris.utils.SharedPrefs;

public class GameViewModel extends AndroidViewModel {

    private final String TAG = GameViewModel.class.getSimpleName();
    private final MutableLiveData<Action> actionLiveData;
    private final MutableLiveData<GameStatus> gameStatusLiveData;
    private final MutableLiveData<Block.BlockShape> runningBlockShapeLiveData;
    private final MutableLiveData<Block.BlockShape> nextBlockShapeLiveData;
    private final MutableLiveData<Integer> scoreLiveData;
    private int mMovementDelaysCounter;
    private long mMovementPeriod;
    private boolean mIsQuickMovingTurnedOn;
    private Timer mTimer;

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
        actionLiveData = new MutableLiveData<>(Action.HOLD);
        gameStatusLiveData = new MutableLiveData<>(GameStatus.WAITING);
        runningBlockShapeLiveData = new MutableLiveData<>();
        nextBlockShapeLiveData = new MutableLiveData<>();
        scoreLiveData = new MutableLiveData<>(0);
        mIsQuickMovingTurnedOn = false;
    }

    public MutableLiveData<Action> getActionLiveData() {
        return actionLiveData;
    }

    public MutableLiveData<GameStatus> getGameStatusLiveData() {
        return gameStatusLiveData;
    }

    public MutableLiveData<Block.BlockShape> getRunningBlockShapeLiveData() {
        return runningBlockShapeLiveData;
    }

    public MutableLiveData<Block.BlockShape> getNextBlockShapeLiveData() {
        return nextBlockShapeLiveData;
    }

    public MutableLiveData<Integer> getScoreLiveData() {
        return scoreLiveData;
    }

    private void resetTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
    }

    private void updateBestScore() {
        Integer score = scoreLiveData.getValue();
        if (score != null
                && score > SharedPrefs.getInt(getApplication(), R.string.best_score_key)) {
            SharedPrefs.putInt(getApplication(), R.string.best_score_key, score);
        }
    }

    public boolean isStarted() {
        GameViewModel.GameStatus gameStatus = gameStatusLiveData.getValue();
        return gameStatus != null
                && !gameStatus.equals(GameStatus.WAITING)
                && !gameStatus.equals(GameStatus.FINISHED);
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

    public void startGame() {
        if (!isStarted()) {
            reset();
            gameStatusLiveData.setValue(GameStatus.RUNNING);
            updateRunningAndNextBlocks();
            mMovementPeriod = MOVEMENT_PERIOD;
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new MovementTimerTask(), START_GAME_DELAY, mMovementPeriod);
        }
    }

    public void pause() {
        if (isRunning()) {
            resetTimer();
            gameStatusLiveData.postValue(GameStatus.PAUSED);
        }
    }

    public void resume() {
        if (isPaused()) {
            gameStatusLiveData.postValue(GameStatus.RUNNING);
            resetTimer();
            mTimer.schedule(new MovementTimerTask(), START_GAME_DELAY, mMovementPeriod);
        }
    }

    public void endGame() {
        gameStatusLiveData.setValue(GameStatus.FINISHED);
        resetTimer();
        updateBestScore();
    }

    public void reset() {
        scoreLiveData.postValue(0);
        runningBlockShapeLiveData.setValue(null);
        nextBlockShapeLiveData.setValue(null);
        gameStatusLiveData.setValue(GameStatus.WAITING);
        actionLiveData.postValue(Action.HOLD);
    }

    public void updateRunningAndNextBlocks() {
        Block.BlockShape newBlockShape = nextBlockShapeLiveData.getValue();
        if (newBlockShape == null) newBlockShape = BlockFactory.getRandomBlockShape();
        runningBlockShapeLiveData.setValue(newBlockShape);
        Block.BlockShape newNextBlockShape = BlockFactory.getRandomBlockShape();
        while (newNextBlockShape == newBlockShape) {
            newNextBlockShape = BlockFactory.getRandomBlockShape();
        }
        nextBlockShapeLiveData.setValue(newNextBlockShape);
    }

    public void moveLeft() {
        actionLiveData.postValue(Action.MOVE_LEFT);
    }

    public void moveRight() {
        actionLiveData.postValue(Action.MOVE_RIGHT);
    }

    public void turnOnQuickMoving() {
        if (isRunning()
                && !mIsQuickMovingTurnedOn) {
            resetTimer();
            mTimer.schedule(new MovementTimerTask(), 0, QUICK_MOVEMENT_PERIOD);
            mIsQuickMovingTurnedOn = true;
        }
    }

    public void turnOffQuickMoving() {
        if (isRunning()
                && mIsQuickMovingTurnedOn) {
            resetTimer();
            mTimer.schedule(new MovementTimerTask(), 0, mMovementPeriod);
            mIsQuickMovingTurnedOn = false;
        }
    }

    public void rotateToLeft() {
        actionLiveData.postValue(Action.ROTATE_LEFT);
    }

    public void rotateToRight() {
        actionLiveData.postValue(Action.ROTATE_RIGHT);
    }

    public void incrementScore() {
        Integer score = scoreLiveData.getValue();
        if (score == null) score = 0;
        scoreLiveData.postValue(++score);
    }

    public void increaseSpeedPermanent() {
        mMovementPeriod -= SPEED_INCREASING_STEP;
        resetTimer();
        mTimer.schedule(new MovementTimerTask(), 0, mMovementPeriod);
    }

    public void delayNextMovement() {
        Log.e(TAG, "delayNextMovement");
        /*if (mMovementDelaysCounter <= MAX_NUMBER_OF_MOVEMENT_DELAYS) {
            resetTimer();
            mTimer.schedule(new MovementTimerTask(), mMovementPeriod, mMovementPeriod);
            mMovementDelaysCounter++;
        }*/
    }

    public void resetMovementDelaysCounter() {
        mMovementDelaysCounter = 0;
    }

    private class MovementTimerTask extends TimerTask {

        @Override
        public void run() {
            actionLiveData.postValue(Action.MOVE_BOTTOM);
        }
    }

}
