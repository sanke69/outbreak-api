package fr.javafx.scene.chart.plugins.behavior;

import java.lang.reflect.InvocationTargetException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;
import fr.javafx.scene.chart.XYChartUtils.XYChartInfo;
import fr.javafx.scene.chart.plugins.AbstractChartPlugin;

public class ChartZoomer extends AbstractChartPlugin<Number, Number> {
	public static final EventHandler<MouseEvent> DEFAULT_FILTER = me -> { if ( me.getButton() != MouseButton.PRIMARY ) me.consume(); };

//	private XY.ConstraintStrategy 				axisConstraintStrategy           = XY.ConstraintStrategy.ignoreOutsideChart();
	private XY.ConstraintStrategy 				mouseWheelAxisConstraintStrategy = XY.ConstraintStrategy.normal();

	private EventHandler<? super MouseEvent> 	mouseFilter                      = me -> { if ( me.getButton() != MouseButton.PRIMARY ) me.consume(); };

	private Axis<?> 							xAxis;
	private DoubleProperty 						xAxisLowerBoundProperty;
	private DoubleProperty 						xAxisUpperBoundProperty;
	private Axis<?> 							yAxis;
	private DoubleProperty 						yAxisLowerBoundProperty;
	private DoubleProperty 						yAxisUpperBoundProperty;

	private XYChartUtils.XYChartInfo 			chartInfo;

	public ChartZoomer() {
		super();
		chartPaneProperty().addListener((_obs, _old, _new) -> {
    		xAxis     = (Axis<?>)        _new.getXAxis();
    		yAxis     = (ValueAxis<?>)   _new.getYAxis();
    		chartInfo = new XYChartInfo( _new.getXYChart(), _new );

    		xAxis                   = _new.getXYChart().getXAxis();
    		xAxisLowerBoundProperty = getLowerBoundProperty(xAxis);
    		xAxisUpperBoundProperty = getUpperBoundProperty(xAxis);
    		yAxis                   = _new.getXYChart().getYAxis();
    		yAxisLowerBoundProperty = getLowerBoundProperty(yAxis);
    		yAxisUpperBoundProperty = getUpperBoundProperty(yAxis);

    		if (
    			xAxisLowerBoundProperty == null || xAxisUpperBoundProperty == null ||
    			yAxisLowerBoundProperty == null || yAxisUpperBoundProperty == null
    		) {
    			throw new IllegalArgumentException("Axis type not supported");
    		}
        });

		registerScrollEventHandler ( ScrollEvent.ANY, this::onMouseScroll );
	}

	public EventHandler<? super MouseEvent> 			getMouseFilter() {
		return mouseFilter;
	}
	public void 										setMouseFilter( EventHandler<? super MouseEvent> _mouseFilter ) {
		mouseFilter = _mouseFilter;
	}

	public void onMouseScroll( ScrollEvent event ) {
		EventType<? extends Event> eventType = event.getEventType();
		if ( eventType == ScrollEvent.SCROLL_STARTED ) {
			//mouse wheel events never send SCROLL_STARTED

		} else if ( eventType == ScrollEvent.SCROLL_FINISHED ) {
			//end non-mouse wheel event

		} else if ( eventType == ScrollEvent.SCROLL && !event.isInertia() && event.getDeltaY() != 0 && event.getTouchCount() == 0 ) {
			double eventX = event.getX();
			double eventY = event.getY();

			XY.Constraint zoomMode = mouseWheelAxisConstraintStrategy.getConstraint( chartInfo.getContext(eventX, eventY ) );
			if( zoomMode == XY.Constraint.NONE )
				return;

			Point2D dataCoords  = chartInfo.getDataCoordinates( eventX, eventY );

			double xZoomBalance = getBalance( dataCoords.getX(), getXAxisLowerBound(), getXAxisUpperBound() );
			double yZoomBalance = getBalance( dataCoords.getY(), getYAxisLowerBound(), getYAxisUpperBound() );

			double direction   = -Math.signum( event.getDeltaY() );
			double zoomAmount  = 0.2 * direction;

			if( zoomMode.allowsHor() ) {
				double xZoomDelta = ( getXAxisUpperBound() - getXAxisLowerBound() ) * zoomAmount;

				xAxis.setAutoRanging( false );
				setXAxisLowerBound( getXAxisLowerBound() - xZoomDelta * xZoomBalance );
				setXAxisUpperBound( getXAxisUpperBound() + xZoomDelta * ( 1 - xZoomBalance ) );
			}

			if( zoomMode.allowsVer() ) {
				double yZoomDelta = ( getYAxisUpperBound() - getYAxisLowerBound() ) * zoomAmount;

				yAxis.setAutoRanging( false );
				setYAxisLowerBound( getYAxisLowerBound() - yZoomDelta * yZoomBalance );
				setYAxisUpperBound( getYAxisUpperBound() + yZoomDelta * ( 1 - yZoomBalance ) );
			}
		}
	}

	private void 										setXAxisLowerBound(double _value) {
		xAxisLowerBoundProperty.set(_value);
	}
	private double 										getXAxisLowerBound() {
		return xAxisLowerBoundProperty.get();
	}

	private void 										setXAxisUpperBound(double _value) {
		xAxisUpperBoundProperty.set(_value);
	}
	private double 										getXAxisUpperBound() {
		return xAxisUpperBoundProperty.get();
	}

	private void 										setYAxisLowerBound(double _value) {
		yAxisLowerBoundProperty.set(_value);
	}
	private double 										getYAxisLowerBound() {
		return yAxisLowerBoundProperty.get();
	}

	private void 										setYAxisUpperBound(double _value) {
		yAxisUpperBoundProperty.set(_value);
	}
	private double 										getYAxisUpperBound() {
		return yAxisUpperBoundProperty.get();
	}

	private static double 								getBalance( double val, double min, double max ) {
		if ( val <= min )
			return 0.0;
		else if ( val >= max )
			return 1.0;

		return (val - min) / (max - min);
	}

	private static <T> DoubleProperty 					getLowerBoundProperty( Axis<T> axis ) {
		return axis instanceof ValueAxis ?
				((ValueAxis<?>) axis).lowerBoundProperty() :
				toDoubleProperty(axis, ChartZoomer.<T>getProperty(axis, "lowerBoundProperty") );
	}
	private static <T> DoubleProperty 					getUpperBoundProperty( Axis<T> axis ) {
		return axis instanceof ValueAxis ?
				((ValueAxis<?>) axis).upperBoundProperty() :
				toDoubleProperty(axis, ChartZoomer.<T>getProperty(axis, "upperBoundProperty") );
	}
	private static <T> DoubleProperty 					toDoubleProperty( final Axis<T> axis, final Property<T> property ) {
		final ChangeListener<Number>[] doubleChangeListenerAry = new ChangeListener[1];
		final ChangeListener<T>[] realValListenerAry = new ChangeListener[1];

		final DoubleProperty result = new SimpleDoubleProperty() {
			/** Retain references so that they're not garbage collected. */
			private final Object[] listeners = new Object[] {
				doubleChangeListenerAry, realValListenerAry
			};
		};

		doubleChangeListenerAry[0] = new ChangeListener<Number>() {
			@Override
			public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue ) {
				property.removeListener( realValListenerAry[0] );
				property.setValue( axis.toRealValue(
						newValue == null ? null : newValue.doubleValue() )
				);
				property.addListener( realValListenerAry[0] );
			}
		};
		result.addListener(doubleChangeListenerAry[0]);

		realValListenerAry[0] = new ChangeListener<T>() {
			@Override
			public void changed( ObservableValue<? extends T> observable, T oldValue, T newValue ) {
				result.removeListener( doubleChangeListenerAry[0] );
				result.setValue( axis.toNumericValue( newValue ) );
				result.addListener( doubleChangeListenerAry[0] );
			}
		};
		property.addListener(realValListenerAry[0]);

		return result;
	}

	@SuppressWarnings( "unchecked" )
	private static <T> Property<T> 						getProperty( Object object, String method ) {
		try {
			Object result = object.getClass().getMethod(method).invoke(object);
			return result instanceof Property ? (Property<T>) result : null;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored ) {}

		return null;
	}

	
	
	
	
	

    public static void 									performZoom(XYChart<Number, Number> chart, Rectangle2D zoomWindow, XY.Constraint _mode) {
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
	
	
}
