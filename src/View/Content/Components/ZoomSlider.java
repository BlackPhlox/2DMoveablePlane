package View.Content.Components;

import Controller.InputAction.MouseController;
import Controller.UIAction.WindowAction.PreferenceAction;
import View.Content.MapContext.MapView;
import javafx.animation.AnimationTimer;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;

/**
 * The zoomSlider is the configuration of the zoomSlider so that it works, when using it.
 */
public class ZoomSlider extends Slider {
    static ZoomSlider zoomSlider;

    /**
     * Get the instance of zoom slider.
     * @return Gives the instance of the zoom slider.
     */
    public static ZoomSlider getInstance(){
        if(zoomSlider==null){
            zoomSlider = new ZoomSlider();
        }
        return zoomSlider;
    }

    /**
     * Set placement, orientation, animation timer and event when moused pressed and released.
     */
    private ZoomSlider(){
        setOrientation(Orientation.VERTICAL);
        setValue(0);
        setMin(-1);
        setMax(1);
        setBlockIncrement(1);
        setMinorTickCount(0);
        setMajorTickUnit(0.1);
        //slider.setShowTickMarks(true);
        setSnapToTicks(true);
        setTooltip(new Tooltip("Click or Drag to zoom"));
        setMaxHeight(150);

        setOnMousePressed(event -> {
            if(PreferenceAction.isAaWhenMoving()) MouseController.temporarilyDisableAA();
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {

                    MapView.getSwingView().smoothZoomToCenter(getValue()*0.8);
                }
            };
            timer.start();
        });

        setOnMouseReleased(event -> {
            setValue(0);
            if(PreferenceAction.isAa()) MapView.getSwingView().setUseAntiAliasing(true);
        });
    }
}
