package fr.javafx.scene.control.chart;

import fr.java.time.Time;
import fr.javafx.scene.control.chart.axis.InstantAxis;
import fr.javafx.utils.EventHandlerManager;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

class XYChartPanManager {

	public static final EventHandler<MouseEvent> DEFAULT_FILTER = new EventHandler<MouseEvent>() {
		@Override
		public void handle( MouseEvent mouseEvent ) {
			if ( mouseEvent.getButton() != MouseButton.PRIMARY )
				mouseEvent.consume();
		}
	};

	private final Axis<?>      							xAxis;
	private final ValueAxis<?> 							yAxis;
	private final XYChartInfo  							chartInfo;

	private XYAxis.Constraint 							panMode = XYAxis.Constraint.NONE;
	private XYAxis.ConstraintStrategy 					axisConstraintStrategy = XYAxis.ConstraintStrategy.normal();

	private final EventHandlerManager 					handlerManager;
	private EventHandler<? super MouseEvent> 			mouseFilter = DEFAULT_FILTER;

	private boolean 									dragging = false;
	private boolean 									wasXAnimated;
	private boolean 									wasYAnimated;
	private double 										lastX;
	private double 										lastY;

	public XYChartPanManager(XYChart<?, ?> chart ) {
		xAxis     = (Axis<?>)        chart.getXAxis();
		yAxis     = (ValueAxis<?>)   chart.getYAxis();
		chartInfo = new XYChartInfo( chart, chart );

		handlerManager = new EventHandlerManager( chart );
		handlerManager . addEventHandler( MouseEvent.DRAG_DETECTED,  me -> { if ( passesFilter( me ) ) startDrag( me ); }, false );
		handlerManager . addEventHandler( MouseEvent.MOUSE_DRAGGED,  me -> drag(me),                                       false );
		handlerManager . addEventHandler( MouseEvent.MOUSE_RELEASED, me -> release(),                                       false);
	}

	public void 								setAxisConstraintStrategy( XYAxis.ConstraintStrategy _axisConstraintStrategy ) {
		axisConstraintStrategy = _axisConstraintStrategy;
	}
	public XYAxis.ConstraintStrategy 			getAxisConstraintStrategy() {
		return axisConstraintStrategy;
	}

	public void 								setMouseFilter( EventHandler<? super MouseEvent> _mouseFilter ) {
		mouseFilter = _mouseFilter;
	}
	public EventHandler<? super MouseEvent> 	getMouseFilter() {
		return mouseFilter;
	}

	public void 								start() {
		handlerManager.addAllHandlers();
	}
	public void 								stop() {
		handlerManager.removeAllHandlers();
		release();
	}

	private void 								startDrag( MouseEvent event ) {
		panMode = axisConstraintStrategy.getConstraint( chartInfo.getContext(event.getX(), event.getY()) );

		if (panMode != XYAxis.Constraint.NONE) {
			lastX = event.getX();
			lastY = event.getY();

			wasXAnimated = xAxis.getAnimated();
			wasYAnimated = yAxis.getAnimated();

			xAxis.setAnimated( false );
			xAxis.setAutoRanging( false );
			yAxis.setAnimated( false );
			yAxis.setAutoRanging( false );

			dragging = true;
		}
	}
	private void 								drag( MouseEvent event ) {
		if ( !dragging )
			return;

		if ( panMode == XYAxis.Constraint.BOTH || panMode == XYAxis.Constraint.HORIZONTAL ) {
			if(xAxis instanceof ValueAxis) {
				ValueAxis<?> xAxis_ = (ValueAxis<?>) xAxis;
				double       dX     = ( event.getX() - lastX ) / - xAxis_.getScale();
				lastX = event.getX();

				dragOnValueAxis(xAxis_, dX);
			}
			if(xAxis instanceof InstantAxis) {
				InstantAxis xAxis_ = (InstantAxis) xAxis;
				double      dX     = ( event.getX() - lastX ) / - xAxis_.getScaleX();
				lastX = event.getX();

				dragOnInstantAxis(xAxis_, dX);
			}
		}

		if ( panMode == XYAxis.Constraint.BOTH || panMode == XYAxis.Constraint.VERTICAL ) {
			double dY = ( event.getY() - lastY ) / -yAxis.getScale();
			lastY = event.getY();
			yAxis.setAutoRanging( false );
			yAxis.setLowerBound( yAxis.getLowerBound() + dY );
			yAxis.setUpperBound( yAxis.getUpperBound() + dY );
		}
	}
	private void 								release() {
		if ( !dragging )
			return;

		dragging = false;

		xAxis.setAnimated( wasXAnimated );
		yAxis.setAnimated( wasYAnimated );
	}

	private boolean 							passesFilter( MouseEvent event ) {
		if ( mouseFilter != null ) {
			MouseEvent cloned = (MouseEvent) event.clone();
			mouseFilter.handle( cloned );
			if ( cloned.isConsumed() )
				return false;
		}

		return true;
	}

	private void dragOnValueAxis(ValueAxis<?> _xAxis, double _delta) {
		_xAxis.setAutoRanging( false );
		_xAxis.setLowerBound( _xAxis.getLowerBound() + _delta );
		_xAxis.setUpperBound( _xAxis.getUpperBound() + _delta );
	}
	private void dragOnInstantAxis(InstantAxis _xAxis, double _delta) {
		System.out.println(_delta + "\t" + _xAxis.getCurrentTimeUnit().lower());
		_xAxis.setAutoRanging( false );
		_xAxis.setLowerBound( Time.add(_xAxis.getLowerBound(), (long) _delta, _xAxis.getCurrentTimeUnit().lower()) );
		_xAxis.setUpperBound( Time.add(_xAxis.getUpperBound(), (long) _delta, _xAxis.getCurrentTimeUnit().lower()) );
	}
}
