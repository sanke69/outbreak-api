/**
 * JavaFR
 * Copyright (C) 2007-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.javafx.scene.chart.plugins.behavior;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;

public class ChartZoomer extends AbstractBehaviorPlugin<Number, Number> {

	private XY.ConstraintStrategy constraintStrategy = XY.ConstraintStrategy.normal();

	public ChartZoomer() {
		super();
		registerScrollEventHandler ( ScrollEvent.ANY, this::onMouseScroll );
	}

	public void 			onMouseScroll( ScrollEvent event ) {
		EventType<? extends Event> eventType = event.getEventType();

		if ( eventType == ScrollEvent.SCROLL && !event.isInertia() && event.getDeltaY() != 0 && event.getTouchCount() == 0 ) {
			double eventX = event.getX();
			double eventY = event.getY();

			XY.Constraint zoomMode = constraintStrategy.getConstraint( XYChartUtils.getContext(getChartPane(), eventX, eventY) );
			if( zoomMode == XY.Constraint.NONE )
				return;

			if( zoomMode.allowsHor() )
				performZoomX(getChartPane().getXYChart(), eventX, eventY, event.getDeltaY());

			if( zoomMode.allowsVer() ) {
				performZoomY(getChartPane().getXYChart(), eventX, eventY, event.getDeltaY());
				if(!getChartPane().isCommonYAxis())
					getChartPane().getOverlayCharts().forEach(chart -> performZoomY(chart, eventX, eventY, event.getDeltaY()));
			}
		}
	}

    public static void 		performZoom(XYChart<?, ?> chart, Rectangle2D zoomWindow, XY.Constraint _mode) {
    	boolean  isAnimated = false;
    	Duration getZoomDuration = Duration.millis(500);
   
        ValueAxis<Number> xAxis = XYChartUtils.Axes.toValueAxis(chart.getXAxis());
        ValueAxis<Number> yAxis = XYChartUtils.Axes.toValueAxis(chart.getYAxis());

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
                        new KeyFrame(getZoomDuration, new KeyValue(xAxis.lowerBoundProperty(), zoomWindow.getMinX()),
                                new KeyValue(xAxis.upperBoundProperty(), zoomWindow.getMaxX())));
                xZoomAnimation.play();
            }
            if (!XYChartUtils.Axes.hasBoundedRange(yAxis)) {
                Timeline yZoomAnimation = new Timeline();
                yZoomAnimation.getKeyFrames().setAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(yAxis.lowerBoundProperty(), yAxis.getLowerBound()),
                                new KeyValue(yAxis.upperBoundProperty(), yAxis.getUpperBound())),
                        new KeyFrame(getZoomDuration, new KeyValue(yAxis.lowerBoundProperty(), zoomWindow.getMinY()),
                                new KeyValue(yAxis.upperBoundProperty(), zoomWindow.getMaxY())));
                yZoomAnimation.play();
            }
        } else {
            if (!XYChartUtils.Axes.hasBoundedRange(xAxis)) {
                xAxis.setLowerBound(zoomWindow.getMinX());
                xAxis.setUpperBound(zoomWindow.getMaxX());
            }
            if (!XYChartUtils.Axes.hasBoundedRange(yAxis)) {
                yAxis.setLowerBound(zoomWindow.getMinY());
                yAxis.setUpperBound(zoomWindow.getMaxY());
            }
        }
    }
    public final void		performZoomX(XYChart<Number, Number> _chart, double _x_anchor, double _y_anchor, double _factor) {
		Point2D dataCoords   = XYChartUtils.getDataCoordinates( getChartPane(), _chart, _x_anchor, _y_anchor );
		double  xMin         = XYChartUtils.Axes.getLowerBound( _chart.getXAxis() );
		double  xMax         = XYChartUtils.Axes.getUpperBound( _chart.getXAxis() );

		double  xZoomBalance = getBalance( dataCoords.getX(), xMin, xMax );
		double  zoomAmount   = - 0.2 * Math.signum( _factor );
		double  xZoomDelta   = ( xMax - xMin ) * zoomAmount;

		_chart.getXAxis().setAutoRanging( false );
		XYChartUtils.Axes.setLowerBound( _chart.getXAxis(), xMin - xZoomDelta * xZoomBalance );
		XYChartUtils.Axes.setUpperBound( _chart.getXAxis(), xMax + xZoomDelta * ( 1d - xZoomBalance ) );
    }
    public final void		performZoomY(XYChart<Number, Number> _chart, double _x_anchor, double _y_anchor, double _factor) {
		Point2D dataCoords   = XYChartUtils.getDataCoordinates( getChartPane(), _chart, _x_anchor, _y_anchor );
		double  yMin         = XYChartUtils.Axes.getLowerBound( _chart.getYAxis() );
		double  yMax         = XYChartUtils.Axes.getUpperBound( _chart.getYAxis() );

		double  yZoomBalance = getBalance( dataCoords.getY(), yMin, yMax );
		double  zoomAmount   = - 0.2 * Math.signum( _factor );
		double  yZoomDelta   = ( yMax - yMin ) * zoomAmount;

		_chart.getYAxis().setAutoRanging( false );
		XYChartUtils.Axes.setLowerBound( _chart.getYAxis(), yMin - yZoomDelta * yZoomBalance );
		XYChartUtils.Axes.setUpperBound( _chart.getYAxis(), yMax + yZoomDelta * ( 1d - yZoomBalance ) );
    }

	private static double 	getBalance( double val, double min, double max ) {
		if ( val <= min )
			return 0.0;
		else if ( val >= max )
			return 1.0;

		return (val - min) / (max - min);
	}

}
