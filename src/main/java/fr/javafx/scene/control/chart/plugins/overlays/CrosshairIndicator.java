package fr.javafx.scene.control.chart.plugins.overlays;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import fr.javafx.scene.control.chart.plugins.AbstractDataFormattingPlugin;

public class CrosshairIndicator<X, Y> extends AbstractDataFormattingPlugin<X, Y> {
    public static final String 				STYLE_CLASS_PATH  = "chart-crosshair-path";
    public static final String 				STYLE_CLASS_LABEL = "chart-crosshair-label";

    private static final int 				LABEL_X_OFFSET    = 15;
    private static final int 				LABEL_Y_OFFSET    = 5;

    private final Path 						crosshairPath     = new Path();
    private final Label 					coordinatesLabel  = new Label();

    private final EventHandler<MouseEvent> 	mouseMoveHandler = (MouseEvent event) -> {
        Bounds plotAreaBounds = getChartPane().getPlotAreaBounds();
        if(!plotAreaBounds.contains(event.getX(), event.getY())) {
            getChartChildren().clear();
            return;
        }

        updatePath(event, plotAreaBounds);
        updateLabel(event, plotAreaBounds);

        if(!getChartChildren().contains(crosshairPath))
            getChartChildren().addAll(crosshairPath, coordinatesLabel);
    };

    public CrosshairIndicator() {
    	super();

        crosshairPath    . getStyleClass().add(STYLE_CLASS_PATH);
        crosshairPath    . setManaged(false);
        coordinatesLabel . getStyleClass().add(STYLE_CLASS_LABEL);
        coordinatesLabel . setManaged(false);

        registerMouseEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
    }

    private void 	updatePath(MouseEvent _event, Bounds _plotArea) {
        ObservableList<PathElement> 
        path = crosshairPath.getElements();
        path . clear();
        path . add(new MoveTo(_plotArea.getMinX() + 1, _event.getY()));
        path . add(new LineTo(_plotArea.getMaxX(),     _event.getY()));
        path . add(new MoveTo(_event.getX(), _plotArea.getMinY() + 1));
        path . add(new LineTo(_event.getX(), _plotArea.getMaxY()));
    }
    private void 	updateLabel(MouseEvent _event, Bounds _plotArea) {
        coordinatesLabel.setText(formatLabelText(getLocationInPlotArea(_event)));

        double width  = coordinatesLabel.prefWidth(-1);
        double height = coordinatesLabel.prefHeight(width);

        double xLocation = _event.getX() + LABEL_X_OFFSET;
        double yLocation = _event.getY() + LABEL_Y_OFFSET;

        if (xLocation + width > _plotArea.getMaxX())
            xLocation = _event.getX() - LABEL_X_OFFSET - width;
        if (yLocation + height > _plotArea.getMaxY())
            yLocation = _event.getY() - LABEL_Y_OFFSET - height;

        coordinatesLabel.resizeRelocate(xLocation, yLocation, width, height);
    }

    private String 	formatLabelText(Point2D _displayPointInPlotArea) {
        // Uses default axis
        Axis<Y> yAxis = getChartPane().getXYChart().getYAxis();

        return formatData(yAxis, toDataPoint(yAxis, _displayPointInPlotArea));
    }

}
