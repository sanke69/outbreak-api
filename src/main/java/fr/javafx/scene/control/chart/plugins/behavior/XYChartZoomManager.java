package fr.javafx.scene.control.chart.plugins.behavior;

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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import fr.javafx.scene.control.chart.XYChartUtils;
import fr.javafx.scene.control.chart.XY;
import fr.javafx.scene.control.chart.XYChartUtils.XYChartInfo;
import fr.javafx.scene.control.chart.plugins.AbstractChartPlugin;
import fr.javafx.utils.EventHandlerManager;

public class XYChartZoomManager extends AbstractChartPlugin<Number, Number> {
	public static final EventHandler<MouseEvent> DEFAULT_FILTER = me -> { if ( me.getButton() != MouseButton.PRIMARY ) me.consume(); };

	private final SimpleBooleanProperty 		selecting                        = new SimpleBooleanProperty( false );
	private final SimpleDoubleProperty 			rectX                            = new SimpleDoubleProperty();
	private final SimpleDoubleProperty 			rectY                            = new SimpleDoubleProperty();

	private final Timeline 						zoomAnimation                    = new Timeline();
	private final DoubleProperty 				zoomDurationMillis               = new SimpleDoubleProperty( 750.0 );
	private final BooleanProperty 				zoomAnimated                     = new SimpleBooleanProperty( true );
	private final BooleanProperty 				mouseWheelZoomAllowed            = new SimpleBooleanProperty( true );

	private XY.Constraint 						zoomMode                         = XY.Constraint.NONE;
	private XY.ConstraintStrategy 				axisConstraintStrategy           = XY.ConstraintStrategy.ignoreOutsideChart();
	private XY.ConstraintStrategy 				mouseWheelAxisConstraintStrategy = XY.ConstraintStrategy.normal();

	private final EventHandlerManager 			handlerManager;
	private EventHandler<? super MouseEvent> 	mouseFilter                      = DEFAULT_FILTER;

	private Rectangle 							selectRect;
	private Axis<?> 							xAxis;
	private DoubleProperty 						xAxisLowerBoundProperty;
	private DoubleProperty 						xAxisUpperBoundProperty;
	private Axis<?> 							yAxis;
	private DoubleProperty 						yAxisLowerBoundProperty;
	private DoubleProperty 						yAxisUpperBoundProperty;

	private XYChartUtils.XYChartInfo 			chartInfo;

    private void registerMouseHandlers() {
		registerMouseEventHandler  ( MouseEvent.MOUSE_PRESSED,   me -> { if ( passesFilter( me ) ) onMousePressed( me ); } );
		registerMouseEventHandler  ( MouseEvent.DRAG_DETECTED,   me -> { if ( passesFilter( me ) ) onDragStart(); } );
		registerMouseEventHandler  ( MouseEvent.MOUSE_DRAGGED,   me -> onMouseDragged( me )  );
		registerMouseEventHandler  ( MouseEvent.MOUSE_RELEASED,  me -> onMouseReleased() );

		registerScrollEventHandler ( ScrollEvent.ANY, new MouseWheelZoomHandler() );
    }

	public XYChartZoomManager() {
		super();
		chartPaneProperty().addListener((_obs, _old, _new) -> {
//          removeEventHanlders(_old);

    		xAxis     = (Axis<?>)        _new.getXAxis();
    		yAxis     = (ValueAxis<?>)   _new.getYAxis();
    		chartInfo = new XYChartInfo( _new.getXYChart(), _new );

    		selectRect = null;
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

//            addEventHandlers(_new);
        });
		registerMouseHandlers();

		handlerManager = null;
	}

	public <X,Y> XYChartZoomManager( Pane chartPane, Rectangle selectRect, XYChart<X,Y> chart ) {
		this.selectRect = selectRect;
		this.xAxis = chart.getXAxis();
		this.xAxisLowerBoundProperty = getLowerBoundProperty(xAxis);
		this.xAxisUpperBoundProperty = getUpperBoundProperty(xAxis);
		this.yAxis = chart.getYAxis();
		this.yAxisLowerBoundProperty = getLowerBoundProperty(yAxis);
		this.yAxisUpperBoundProperty = getUpperBoundProperty(yAxis);

		if (
			xAxisLowerBoundProperty == null || xAxisUpperBoundProperty == null ||
			yAxisLowerBoundProperty == null || yAxisUpperBoundProperty == null
		) {
			throw new IllegalArgumentException("Axis type not supported");
		}

		chartInfo = new XYChartInfo( chart, chartPane );

		handlerManager = new EventHandlerManager( chartPane );

		handlerManager.addEventHandler( MouseEvent.MOUSE_PRESSED,   me -> { if ( passesFilter( me ) ) onMousePressed( me ); } );
		handlerManager.addEventHandler( MouseEvent.DRAG_DETECTED,   me -> { if ( passesFilter( me ) ) onDragStart(); } );
		handlerManager.addEventHandler( MouseEvent.MOUSE_DRAGGED,   me -> onMouseDragged( me )  );
		handlerManager.addEventHandler( MouseEvent.MOUSE_RELEASED,  me -> onMouseReleased() );

		handlerManager.addEventHandler( ScrollEvent.ANY, new MouseWheelZoomHandler() );
	}

	public XY.ConstraintStrategy 						getAxisConstraintStrategy() {
		return axisConstraintStrategy;
	}
	public void 										setAxisConstraintStrategy( XY.ConstraintStrategy _axisConstraintStrategy ) {
		axisConstraintStrategy = _axisConstraintStrategy;
	}

	public XY.ConstraintStrategy 						getMouseWheelAxisConstraintStrategy() {
		return mouseWheelAxisConstraintStrategy;
	}
	public void 										setMouseWheelAxisConstraintStrategy( XY.ConstraintStrategy _mouseWheelAxisConstraintStrategy ) {
		mouseWheelAxisConstraintStrategy = _mouseWheelAxisConstraintStrategy;
	}

	public void 										setZoomAnimated( boolean _zoomAnimated ) {
		zoomAnimated.set( _zoomAnimated );
	}
	public boolean 										isZoomAnimated() {
		return zoomAnimated.get();
	}
	public BooleanProperty 								zoomAnimatedProperty() {
		return zoomAnimated;
	}

	public void 										setZoomDurationMillis( double zoomDurationMillis ) {
		this.zoomDurationMillis.set( zoomDurationMillis );
	}
	public double 										getZoomDurationMillis() {
		return zoomDurationMillis.get();
	}
	public DoubleProperty 								zoomDurationMillisProperty() {
		return zoomDurationMillis;
	}

	public void 										setMouseWheelZoomAllowed( boolean _allowed ) {
		mouseWheelZoomAllowed.set( _allowed );
	}
	public boolean 										isMouseWheelZoomAllowed() {
		return mouseWheelZoomAllowed.get();
	}
	public BooleanProperty 								mouseWheelZoomAllowedProperty() {
		return mouseWheelZoomAllowed;
	}

	public EventHandler<? super MouseEvent> 			getMouseFilter() {
		return mouseFilter;
	}
	public void 										setMouseFilter( EventHandler<? super MouseEvent> _mouseFilter ) {
		mouseFilter = _mouseFilter;
	}

	public void 										start() {
		handlerManager.addAllHandlers();

		selectRect.widthProperty().bind( rectX.subtract( selectRect.translateXProperty() ) );
		selectRect.heightProperty().bind( rectY.subtract( selectRect.translateYProperty() ) );
		selectRect.visibleProperty().bind( selecting );
	}
	public void 										stop() {
		handlerManager.removeAllHandlers();

		selecting.set( false );
		selectRect.widthProperty().unbind();
		selectRect.heightProperty().unbind();
		selectRect.visibleProperty().unbind();
	}

	private boolean 									passesFilter( MouseEvent event ) {
		if ( mouseFilter != null ) {
			MouseEvent cloned = (MouseEvent) event.clone();
			mouseFilter.handle( cloned );
			if ( cloned.isConsumed() )
				return false;
		}

		return true;
	}

	private void 										onMousePressed( MouseEvent mouseEvent ) {
		double x = mouseEvent.getX();
		double y = mouseEvent.getY();

		Rectangle2D plotArea = chartInfo.getPlotArea();

		zoomMode = axisConstraintStrategy.getConstraint(chartInfo.getContext(x, y));
		switch(zoomMode) {
		case BOTH : 		selectRect.setTranslateX( x );
							selectRect.setTranslateY( y );
							rectX.set( x );
							rectY.set( y );
							break;
		case HORIZONTAL : 	selectRect.setTranslateX( x );
							selectRect.setTranslateY( plotArea.getMinY() );
							rectX.set( x );
							rectY.set( plotArea.getMaxY() );
							break;
		case VERTICAL : 	selectRect.setTranslateX( plotArea.getMinX() );
							selectRect.setTranslateY( y );
							rectX.set( plotArea.getMaxX() );
							rectY.set( y );
							break;
		}
	}
	private void 										onDragStart() {
		if ( zoomMode != XY.Constraint.NONE )
			selecting.set( true );
	}
	private void 										onMouseDragged( MouseEvent mouseEvent ) {
		if ( !selecting.get() )
			return;

		Rectangle2D plotArea = chartInfo.getPlotArea();

		if ( zoomMode == XY.Constraint.BOTH || zoomMode == XY.Constraint.HORIZONTAL ) {
			double x = mouseEvent.getX();
			//Clamp to the selection start
			x = Math.max( x, selectRect.getTranslateX() );
			//Clamp to plot area
			x = Math.min( x, plotArea.getMaxX() );
			rectX.set( x );
		}

		if ( zoomMode == XY.Constraint.BOTH || zoomMode == XY.Constraint.VERTICAL ) {
			double y = mouseEvent.getY();
			//Clamp to the selection start
			y = Math.max( y, selectRect.getTranslateY() );
			//Clamp to plot area
			y = Math.min( y, plotArea.getMaxY() );
			rectY.set( y );
		}
	}
	private void 										onMouseReleased() {
		if ( !selecting.get() )
			return;

		if ( selectRect.getWidth() == 0.0 || selectRect.getHeight() == 0.0 ) {
			selecting.set( false );
			return;
		}

		Rectangle2D zoomWindow = chartInfo.getDataCoordinates(
				selectRect.getTranslateX(), selectRect.getTranslateY(),
				rectX.get(), rectY.get()
		);

		xAxis.setAutoRanging( false );
		yAxis.setAutoRanging( false );
		if ( zoomAnimated.get() ) {
			zoomAnimation.stop();
			zoomAnimation.getKeyFrames().setAll(
					new KeyFrame( Duration.ZERO,
					              new KeyValue( xAxisLowerBoundProperty, getXAxisLowerBound() ),
					              new KeyValue( xAxisUpperBoundProperty, getXAxisUpperBound() ),
					              new KeyValue( yAxisLowerBoundProperty, getYAxisLowerBound() ),
					              new KeyValue( yAxisUpperBoundProperty, getYAxisUpperBound() )
					),
			    new KeyFrame( Duration.millis( zoomDurationMillis.get() ),
			                  new KeyValue( xAxisLowerBoundProperty, zoomWindow.getMinX() ),
			                  new KeyValue( xAxisUpperBoundProperty, zoomWindow.getMaxX() ),
			                  new KeyValue( yAxisLowerBoundProperty, zoomWindow.getMinY() ),
			                  new KeyValue( yAxisUpperBoundProperty, zoomWindow.getMaxY() )
			    )
			);
			zoomAnimation.play();
		} else {
			zoomAnimation.stop();
			setXAxisLowerBound( zoomWindow.getMinX() );
			setXAxisUpperBound( zoomWindow.getMaxX() );
			setYAxisLowerBound( zoomWindow.getMinY() );
			setYAxisUpperBound( zoomWindow.getMaxY() );
		}

		selecting.set( false );
	}

	private class MouseWheelZoomHandler implements EventHandler<ScrollEvent> {
		private boolean ignoring = false;

		@Override
		public void handle( ScrollEvent event ) {
			EventType<? extends Event> eventType = event.getEventType();
			if ( eventType == ScrollEvent.SCROLL_STARTED ) {
				//mouse wheel events never send SCROLL_STARTED
				ignoring = true;
			} else if ( eventType == ScrollEvent.SCROLL_FINISHED ) {
				//end non-mouse wheel event
				ignoring = false;

			} else if ( eventType == ScrollEvent.SCROLL &&
			            //If we are allowing mouse wheel zooming
			            mouseWheelZoomAllowed.get() &&
			            //If we aren't between SCROLL_STARTED and SCROLL_FINISHED
			            !ignoring &&
			            //inertia from non-wheel gestures might have touch count of 0
			            !event.isInertia() &&
			            //Only care about vertical wheel events
			            event.getDeltaY() != 0 &&
			            //mouse wheel always has touch count of 0
			            event.getTouchCount() == 0 ) {

				//Find out which axes to zoom based on the strategy
				double eventX = event.getX();
				double eventY = event.getY();

				XY.Constraint zoomMode = mouseWheelAxisConstraintStrategy.getConstraint( chartInfo.getContext(eventX, eventY ) );
				if ( zoomMode == XY.Constraint.NONE )
					return;

				zoomAnimation.stop();

				//At this point we are a mouse wheel event, based on everything I've read
				Point2D dataCoords = chartInfo.getDataCoordinates( eventX, eventY );

				//Determine the proportion of change to the lower and upper bounds based on how far the
				//cursor is along the axis.
				double xZoomBalance = getBalance( dataCoords.getX(),
				                                  getXAxisLowerBound(), getXAxisUpperBound() );
				double yZoomBalance = getBalance( dataCoords.getY(),
				                                  getYAxisLowerBound(), getYAxisUpperBound() );

				//Are we zooming in or out, based on the direction of the roll
				double direction = -Math.signum( event.getDeltaY() );

				//TODO: Do we need to handle "continuous" scroll wheels that don't work based on ticks?
				//If so, the 0.2 needs to be modified
				double zoomAmount = 0.2 * direction;

				if ( zoomMode == XY.Constraint.BOTH || zoomMode == XY.Constraint.HORIZONTAL ) {
					double xZoomDelta = ( getXAxisUpperBound() - getXAxisLowerBound() ) * zoomAmount;
					xAxis.setAutoRanging( false );
					setXAxisLowerBound( getXAxisLowerBound() - xZoomDelta * xZoomBalance );
					setXAxisUpperBound( getXAxisUpperBound() + xZoomDelta * ( 1 - xZoomBalance ) );
				}

				if ( zoomMode == XY.Constraint.BOTH || zoomMode == XY.Constraint.VERTICAL ) {
					double yZoomDelta = ( getYAxisUpperBound() - getYAxisLowerBound() ) * zoomAmount;
					yAxis.setAutoRanging( false );
					setYAxisLowerBound( getYAxisLowerBound() - yZoomDelta * yZoomBalance );
					setYAxisUpperBound( getYAxisUpperBound() + yZoomDelta * ( 1 - yZoomBalance ) );
				}
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
				toDoubleProperty(axis, XYChartZoomManager.<T>getProperty(axis, "lowerBoundProperty") );
	}
	private static <T> DoubleProperty 					getUpperBoundProperty( Axis<T> axis ) {
		return axis instanceof ValueAxis ?
				((ValueAxis<?>) axis).upperBoundProperty() :
				toDoubleProperty(axis, XYChartZoomManager.<T>getProperty(axis, "upperBoundProperty") );
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

}
