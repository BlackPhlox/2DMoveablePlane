package Controller.InputAction;

import Controller.UIAction.WindowAction.PreferenceAction;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.awt.event.KeyListener;

/**
 * Connects the users inputs from the keyboard to the action on the UI.
 */
public class KeyboardController{
    private SwingView canvas;
    private boolean up,down,left,right,shift, ctrl;
    private static float regSpeed = 2, shiftSpeed = 2;
    private AnimationTimer movementTimer =new AnimationTimer()
    {
        @Override
        public void handle(long now) {
            if(PreferenceAction.isAaWhenMoving()) MouseController.temporarilyDisableAA();
            int dx = 0,dy = 0;
            if(up) dy += regSpeed;
            if(down) dy -= regSpeed;
            if(right) dx -= regSpeed;
            if(left) dx += regSpeed;
            if(shift) canvas.pan(dx*shiftSpeed,dy*shiftSpeed);
            else canvas.pan(dx,dy);
            if((!up && !down && !left && !right) || ctrl) {
                movementTimer.stop();
                if(PreferenceAction.isAa())canvas.setUseAntiAliasing(true);
                up = false; down = false; left = false; right = false;
            }
        }
    };
    private AnimationTimer aniTimerZoomIn = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(PreferenceAction.isAaWhenMoving()) MouseController.temporarilyDisableAA();
            canvas.smoothZoomToCenter(1.1);
        }
    };
    private AnimationTimer aniTimerZoomOut = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(PreferenceAction.isAaWhenMoving()) MouseController.temporarilyDisableAA();
            canvas.smoothZoomToCenter(-1/1.1);
        }
    };

    /**
     * Constructor, creates the appropriate action for the button pressed on the keyboard.
     * @param c Get a canvas.
     */
    public KeyboardController(SwingView c) {
        canvas = c;

        MapView.getSwingNode().setOnKeyPressed(e ->{
            movementTimer.start();

            if(e.isControlDown()) ctrl = true;
            if(e.isShiftDown()) shift = true;
            char key = ' ';
            if(e.getText().length() > 0)  key = e.getText().toCharArray()[0];
            switch (key) {
                case 'x':
                    canvas.toggleAntiAliasing();
                    break;
                case 'w':
                    up = true;
                    break;
                case 'a':
                    left = true;
                    break;
                case 's':
                    down = true;
                    break;
                case 'd':
                    right = true;
                    break;
                case '+':
                    if(PreferenceAction.isSmoothZoom()){
                        aniTimerZoomIn.start();
                        Timeline zoomInTimer = new Timeline(new KeyFrame(
                                Duration.millis(200),
                                ae -> {aniTimerZoomIn.stop();
                                    if(PreferenceAction.isAa())canvas.setUseAntiAliasing(true);
                                }));
                        zoomInTimer.play();
                    } else {
                        canvas.zoomToCenter(1.1);
                    }
                    break;
                case '-':
                    if(PreferenceAction.isSmoothZoom()){
                        aniTimerZoomOut.start();
                        Timeline zoomOutTimer = new Timeline(new KeyFrame(
                                Duration.millis(200),
                                ae -> {aniTimerZoomOut.stop();
                                    if(PreferenceAction.isAa())canvas.setUseAntiAliasing(true);
                                }));
                        zoomOutTimer.play();
                    } else {
                        canvas.zoomToCenter(1/1.1);
                    }
                    break;
                default:
                    break;
            }
        });

        MapView.getSwingNode().setOnKeyReleased(e ->{
            ctrl = false;
            shift = false;
            char key = ' ';
            if(e.getText().length() > 0)  key = e.getText().toCharArray()[0];
            switch (key) {
                case 'w':
                    up = false;
                    break;
                case 'a':
                    left = false;
                    break;
                case 's':
                    down = false;
                    break;
                case 'd':
                    right = false;
                    break;
            }
        });
    }

    /**
     * Sets the regulation speed.
     * @param regSpeed Set the regulation speed for the keyBoardController.
     */
    public static void setRegSpeed(float regSpeed) {
        KeyboardController.regSpeed = regSpeed;
    }

    /**
     * Sets the shift speed.
     * @param shiftSpeed Set the speed for when shift is used.
     */
    public static void setShiftSpeed(float shiftSpeed) {
        KeyboardController.shiftSpeed = shiftSpeed;
    }
}
