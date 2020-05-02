package fr.javafx.scene.chart.plugins.behavior;

import java.util.function.Predicate;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;
import fr.javafx.scene.chart.XYChartUtils.XYChartInfo;
import fr.javafx.scene.chart.plugins.AbstractChartPlugin;

public class AbstractBehaviorPlugin<X, Y> extends AbstractChartPlugin<X, Y> {

	@Deprecated
    private final ObjectProperty<Predicate<MouseEvent>> activationPredicate;
    private final ObjectProperty<XY.Constraint> 		interactionMode;
	private final ObjectProperty<XY.ConstraintStrategy>	interactionStrategy;

    private final ObjectProperty<Cursor> 				dragCursor;
    private Cursor 										originalCursor;

    public AbstractBehaviorPlugin() {
        this(XY.Constraint.BOTH, Cursor.CLOSED_HAND);
    }
    public AbstractBehaviorPlugin(XY.Constraint _interactionMode) {
    	this(_interactionMode, Cursor.CLOSED_HAND);
    }
    protected AbstractBehaviorPlugin(XY.Constraint _interactionMode, Cursor _dragCursor) {
    	super();
    	activationPredicate = new SimpleObjectProperty<Predicate<MouseEvent>>();
    	interactionMode     = new SimpleObjectProperty<XY.Constraint>(_interactionMode);
    	interactionStrategy = new SimpleObjectProperty<XY.ConstraintStrategy>( XY.ConstraintStrategy.normal() );
    	dragCursor          = new SimpleObjectProperty<>(_dragCursor);
    }

	@Deprecated
    public final void 									setActivationPredicate(Predicate<MouseEvent> _predicate) {
    	activationPredicate.set( _predicate );
    }
	@Deprecated
	public final Predicate<MouseEvent> 					getActivationPredicate() {
        return activationPredicate.get();
    }
	@Deprecated
	public final ObjectProperty<Predicate<MouseEvent>>	activationPredicateProperty() {
        return activationPredicate;
    }

    public final void 									setInteractionMode(XY.Constraint mode) {
        interactionMode.set(mode);
    }
    public final XY.Constraint 							getInteractionMode() {
        return interactionMode.get();
    }
    public final ObjectProperty<XY.Constraint> 			interactionModeProperty() {
        return interactionMode;
    }

    public final void 									setInteractionStrategy(XY.ConstraintStrategy mode) {
    	interactionStrategy.set(mode);
    }
    public final XY.ConstraintStrategy 					getInteractionStrategy() {
        return interactionStrategy.get();
    }
    public final ObjectProperty<XY.ConstraintStrategy> 	interactionStrategyProperty() {
        return interactionStrategy;
    }

    public final void 									setDragCursor(Cursor cursor) {
        dragCursor.set(cursor);
    }
    public final Cursor 								getDragCursor() {
        return dragCursor.get();
    }
    public final ObjectProperty<Cursor> 				dragCursorProperty() {
        return dragCursor;
    }

    protected final XY.Constraint						getInteractionModeInContext(MouseEvent _me) {
    	return getInteractionStrategy().getConstraint( new XYChartInfo( getChartPane().getXYChart(), getChartPane() ).getContext(_me.getX(), _me.getY()) );
    }
    
    protected void 										installCursor() {
        originalCursor = getChartPane().getCursor();
        if(getDragCursor() != null)
            getChartPane().setCursor(getDragCursor());
    }
    protected void 										uninstallCursor() {
        getChartPane().setCursor(originalCursor);
    }

    public static void 									performZoom(XYChart<Number, Number> _chart, Rectangle2D _dataWindow, XY.Constraint _mode) {
    	boolean  isAnimated = false;
    	Duration getZoomDuration = Duration.millis(500);
   
        ValueAxis<Number> xAxis = XYChartUtils.Axes.toValueAxis(_chart.getXAxis());
        ValueAxis<Number> yAxis = XYChartUtils.Axes.toValueAxis(_chart.getYAxis());

        if (_mode.allowsHor())
            xAxis.setAutoRanging(false);

        if (_mode.allowsVer())
            yAxis.setAutoRanging(false);

        if (isAnimated) {
            if (!XYChartUtils.Axes.hasBoundedRange(xAxis)) {
                Timeline xZoomAnimation = new Timeline();
                xZoomAnimation.getKeyFrames().setAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(xAxis.lowerBoundProperty(), xAxis.getLowerBound()),
                                new KeyValue(xAxis.upperBoundProperty(), xAxis.getUpperBound())),
                        new KeyFrame(getZoomDuration, new KeyValue(xAxis.lowerBoundProperty(), _dataWindow.getMinX()),
                                new KeyValue(xAxis.upperBoundProperty(), _dataWindow.getMaxX())));
                xZoomAnimation.play();
            }
            if (!XYChartUtils.Axes.hasBoundedRange(yAxis)) {
                Timeline yZoomAnimation = new Timeline();
                yZoomAnimation.getKeyFrames().setAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(yAxis.lowerBoundProperty(), yAxis.getLowerBound()),
                                new KeyValue(yAxis.upperBoundProperty(), yAxis.getUpperBound())),
                        new KeyFrame(getZoomDuration, new KeyValue(yAxis.lowerBoundProperty(), _dataWindow.getMinY()),
                                new KeyValue(yAxis.upperBoundProperty(), _dataWindow.getMaxY())));
                yZoomAnimation.play();
            }
        } else {
            if (!XYChartUtils.Axes.hasBoundedRange(xAxis)) {
                xAxis.setLowerBound(_dataWindow.getMinX());
                xAxis.setUpperBound(_dataWindow.getMaxX());
            }
            if (!XYChartUtils.Axes.hasBoundedRange(yAxis)) {
                yAxis.setLowerBound(_dataWindow.getMinY());
                yAxis.setUpperBound(_dataWindow.getMaxY());
            }
        }
    }
	
}
