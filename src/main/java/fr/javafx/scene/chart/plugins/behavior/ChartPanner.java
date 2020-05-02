package fr.javafx.scene.chart.plugins.behavior;

import java.util.function.Predicate;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseEvent;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;
import fr.javafx.utils.MouseEvents;

public class ChartPanner extends AbstractBehaviorPlugin<Number, Number> {

	private Predicate<MouseEvent> 	mouseFilter           = me -> MouseEvents.isOnlyPrimaryButtonDown(me);
    private Point2D 				previousMouseLocation = null;

    public ChartPanner() {
        this(XY.Constraint.BOTH);
    }
    public ChartPanner(XY.Constraint _panMode) {
    	super(_panMode, Cursor.CLOSED_HAND);
    	
    	interactionModeProperty().addListener((_mode) -> {
        	if(getInteractionMode() == null || getInteractionMode() == XY.Constraint.NONE) {
        		setInteractionMode( XY.Constraint.BOTH );
        		throw new RuntimeException("The 'InteractionMode' can't not be null or set to NONE with ChartPanner");
        	}});

        registerMouseEventHandler(MouseEvent.MOUSE_PRESSED,  this::onMousePressed);
        registerMouseEventHandler(MouseEvent.MOUSE_DRAGGED,  this::onMouseDragged);
        registerMouseEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
    }

    private void 	onMousePressed(MouseEvent _me) {
    	if(mouseFilter != null && !mouseFilter.test(_me))
    		return ;

        installCursor();

        previousMouseLocation = getLocationInPlotArea(_me);

        _me.consume();
    }
    private void 	onMouseDragged(MouseEvent _me) {
        if(previousMouseLocation == null)
        	return ;

        XY.Constraint mode   = getInteractionMode().AND( getInteractionModeInContext(_me) );
        Point2D       cursor = getLocationInPlotArea(_me);

        for(XYChart<Number, Number> chart : getCharts())
            panChart(chart, cursor, mode);

        previousMouseLocation = cursor;

        _me.consume();
    }
    private void 	onMouseReleased(MouseEvent _me) {
        if(previousMouseLocation == null)
        	return ;

        uninstallCursor();

        previousMouseLocation = null;

        _me.consume();
    }

    private void 	panChart(XYChart<Number, Number> chart, Point2D mouseLocation, XY.Constraint _mode) {
        Data<Number, Number> prevData = toDataPoint(chart.getYAxis(), previousMouseLocation);
        Data<Number, Number> data     = toDataPoint(chart.getYAxis(), mouseLocation);

        double xOffset = prevData.getXValue().doubleValue() - data.getXValue().doubleValue();
        double yOffset = prevData.getYValue().doubleValue() - data.getYValue().doubleValue();

        ValueAxis<?> xAxis = XYChartUtils.Axes.toValueAxis(chart.getXAxis());
        if (!XYChartUtils.Axes.hasBoundedRange(xAxis) && _mode.allowsHor()) {
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(xAxis.getLowerBound() + xOffset);
            xAxis.setUpperBound(xAxis.getUpperBound() + xOffset);
        }
        ValueAxis<?> yAxis = XYChartUtils.Axes.toValueAxis(chart.getYAxis());
        if (!XYChartUtils.Axes.hasBoundedRange(yAxis) && _mode.allowsVer()) {
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(yAxis.getLowerBound() + yOffset);
            yAxis.setUpperBound(yAxis.getUpperBound() + yOffset);
        }
    }

}
