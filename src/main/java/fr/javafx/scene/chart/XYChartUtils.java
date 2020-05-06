package fr.javafx.scene.chart;

import static fr.javafx.utils.FxUtils.getXShift;
import static fr.javafx.utils.FxUtils.getYShift;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public final class XYChartUtils {
	
	public static final class Axes {
		private Axes() {}
	
		public static boolean isValueAxis(Axis<?> axis) {
			return axis instanceof ValueAxis<?>;
		}
	
		public static boolean isCategoryAxis(Axis<?> axis) {
			return axis instanceof CategoryAxis;
		}
	
		@SuppressWarnings("unchecked")
		public static <T extends Number> ValueAxis<T> toValueAxis(Axis<?> axis) {
			if (isValueAxis(axis)) {
				return (ValueAxis<T>) axis;
			}
			throw new IllegalArgumentException("Expected an instance of ValueAxis");
		}
	
		public static boolean hasBoundedRange(ValueAxis<?> axis) {
			return axis.lowerBoundProperty().isBound() || axis.upperBoundProperty().isBound();
		}
	
		public static void bindBounds(ValueAxis<?> axis, ValueAxis<?> observable) {
			axis.lowerBoundProperty().bind(observable.lowerBoundProperty());
			axis.upperBoundProperty().bind(observable.upperBoundProperty());
		}
	
		public static void unbindBounds(ValueAxis<?> axis) {
			axis.lowerBoundProperty().unbind();
			axis.upperBoundProperty().unbind();
		}
		
		
		
		
		

		public static <T> void 								setLowerBound( Axis<T> axis, double _value) {
			if(axis instanceof ValueAxis<?> valueAxis)
				valueAxis.setLowerBound(_value);
			else
				toDoubleProperty(axis, XYChartUtils.Axes.<T>getProperty(axis, "lowerBoundProperty") ).set(_value);
		}
		public static <T> double 							getLowerBound( Axis<T> axis ) {
			return axis instanceof ValueAxis ?
					((ValueAxis<?>) axis).getLowerBound() :
					toDoubleProperty(axis, XYChartUtils.Axes.<T>getProperty(axis, "lowerBoundProperty") ).get();
		}
		public static <T> DoubleProperty 					getLowerBoundProperty( Axis<T> axis ) {
			return axis instanceof ValueAxis ?
					((ValueAxis<?>) axis).lowerBoundProperty() :
					toDoubleProperty(axis, XYChartUtils.Axes.<T>getProperty(axis, "lowerBoundProperty") );
		}

		public static <T> void 								setUpperBound( Axis<T> axis, double _value) {
			if(axis instanceof ValueAxis<?> valueAxis)
				valueAxis.upperBoundProperty().set(_value);
			else
				toDoubleProperty(axis, XYChartUtils.Axes.<T>getProperty(axis, "upperBoundProperty") ).set(_value);
		}
		public static <T> double		 					getUpperBound( Axis<T> axis ) {
			return axis instanceof ValueAxis ?
					((ValueAxis<?>) axis).getUpperBound() :
					toDoubleProperty(axis, XYChartUtils.Axes.<T>getProperty(axis, "upperBoundProperty") ).get();
		}
		public static <T> DoubleProperty 					getUpperBoundProperty( Axis<T> axis ) {
			return axis instanceof ValueAxis ?
					((ValueAxis<?>) axis).upperBoundProperty() :
					toDoubleProperty(axis, XYChartUtils.Axes.<T>getProperty(axis, "upperBoundProperty") );
		}

		private static <T> DoubleProperty 					toDoubleProperty( final Axis<T> axis, final Property<T> property ) {
			final ChangeListener<Number>[] doubleChangeListenerAry = new ChangeListener[1];
			final ChangeListener<T>[]      realValListenerAry      = new ChangeListener[1];

			final DoubleProperty result = new SimpleDoubleProperty() {
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

    private XYChartUtils() { }

	public static XY.Context 		getContext(XYChartPane<?,?> _pane, double _x, double _y) {
		if(getXAxisArea(_pane).contains( _x, _y ))
			return XY.Context.onXAxis;
		if(getYAxisArea(_pane).contains( _x, _y ))
			return XY.Context.onYAxis;
		if(getPlotArea(_pane).contains( _x, _y ))
			return XY.Context.inPlotArea;
		return XY.Context.outsideChart;
	}
	public static boolean 			isInPlotArea( XYChartPane<?,?> _pane, double x, double y ) {
		return getPlotArea(_pane).contains( x, y );
	}

	public static Rectangle2D 		getPlotArea(XYChartPane<?,?> _pane) {
		Axis<?> xAxis = _pane.getXAxis();
		Axis<?> yAxis = _pane.getYAxis();

		double xStart = getXShift( xAxis, _pane );
		double yStart = getYShift( yAxis, _pane );

		double width  = xAxis.getWidth();
		double height = yAxis.getHeight();

		return new Rectangle2D( xStart, yStart, width, height );
	}
	public static Rectangle2D 		getXAxisArea(XYChartPane<?,?> _pane) {
		return getComponentArea( _pane, _pane.getXAxis() );
	}
	public static Rectangle2D 		getYAxisArea(XYChartPane<?,?> _pane) {
		return getComponentArea( _pane, _pane.getYAxis() );
	}
	public static Rectangle2D 		getYAxisArea(XYChartPane<?,?> _pane, XYChart<?,?> _chart) {
		return getComponentArea( _pane, _chart.getYAxis() );
	}

	private static Rectangle2D 		getComponentArea(XYChartPane<?,?> _pane, Region childRegion) {
		double xStart = getXShift( childRegion, _pane );
		double yStart = getYShift( childRegion, _pane );

		return new Rectangle2D( xStart, yStart, childRegion.getWidth(), childRegion.getHeight() );
	}



	@SuppressWarnings( "unchecked" )
	public static Point2D 			getDataCoordinates( XYChartPane<?,?> _pane, double x, double y ) {
		Axis xAxis = _pane.getXAxis();
		Axis yAxis = _pane.getYAxis();

		double xStart = getXShift( xAxis, _pane );
		double yStart = getYShift( yAxis, _pane );

		return new Point2D(
				xAxis.toNumericValue( xAxis.getValueForDisplay( x - xStart ) ),
		    yAxis.toNumericValue( yAxis.getValueForDisplay( y - yStart ) )
		);
	}
	@SuppressWarnings( "unchecked" )
	public static Point2D 			getDataCoordinates( XYChartPane<?,?> _pane, XYChart<?,?> _chart, double x, double y ) {
		Axis xAxis = _chart.getXAxis();
		Axis yAxis = _chart.getYAxis();

		double xStart = getXShift( xAxis, _pane );
		double yStart = getYShift( yAxis, _pane );

		return new Point2D(
				xAxis.toNumericValue( xAxis.getValueForDisplay( x - xStart ) ),
		    yAxis.toNumericValue( yAxis.getValueForDisplay( y - yStart ) )
		);
	}
	@SuppressWarnings( "unchecked" )
	public static Rectangle2D 		getDataCoordinates( XYChartPane<?,?> _pane, double minX, double minY, double maxX, double maxY ) {
		if ( minX > maxX || minY > maxY ) {
			throw new IllegalArgumentException( "min > max for X and/or Y" );
		}

		Axis xAxis = _pane.getXAxis();
		Axis yAxis = _pane.getYAxis();

		double xStart = getXShift( xAxis, _pane );
		double yStart = getYShift( yAxis, _pane );

		double minDataX = xAxis.toNumericValue( xAxis.getValueForDisplay( minX - xStart ) );
		double maxDataX = xAxis.toNumericValue( xAxis.getValueForDisplay( maxX - xStart ) );

		//The "low" Y data value is actually at the maxY graphical location as Y graphical axis gets
		//larger as you go down on the screen.
		double minDataY = yAxis.toNumericValue( yAxis.getValueForDisplay( maxY - yStart ) );
		double maxDataY = yAxis.toNumericValue( yAxis.getValueForDisplay( minY - yStart ) );

		return new Rectangle2D( minDataX,
		                        minDataY,
		                        maxDataX - minDataX,
		                        maxDataY - minDataY );
	}

	
	
    public static Point2D 									limitToPlotArea(XYChartPane<?,?> _pane, MouseEvent event) {
    	Bounds plotBounds = _pane.getPlotAreaBounds();
        double limitedX = Math.max(Math.min(event.getX(), plotBounds.getMaxX()), plotBounds.getMinX());
        double limitedY = Math.max(Math.min(event.getY(), plotBounds.getMaxY()), plotBounds.getMinY());
        return new Point2D(limitedX, limitedY);
    }

    
    
    
    static double getLocationX(Node node) {
        return node.getLayoutX() + node.getTranslateX();
    }

    static double getLocationY(Node node) {
        return node.getLayoutY() + node.getTranslateY();
    }

    static Region getChartContent(Chart chart) {
        return (Region) chart.lookup(".chart-content");
    }

    static Node getPlotContent(XYChart<?, ?> chart) {
        return chart.lookup(".plot-content");
    }

    static Pane getLegend(XYChart<?, ?> chart) {
        return (Pane) chart.lookup(".chart-legend");
    }

    static double getHorizontalInsets(Insets insets) {
        return insets.getLeft() + insets.getRight();
    }

    static double getVerticalInsets(Insets insets) {
        return insets.getTop() + insets.getBottom();
    }

    /**
     * Returns Chart instance containing given child node.
     * 
     * @param chartChildNode the node contained within the chart
     * @return chart or {@code null} if the node does not belong to chart
     */
    static Chart getChart(final Node chartChildNode) {
        Node node = chartChildNode;
        while (node != null && !(node instanceof Chart)) {
            node = node.getParent();
        }
        return (Chart) node;
    }

    static List<Label> getChildLabels(List<? extends Parent> parents) {
        List<Label> labels = new LinkedList<>();
        for (Parent parent : parents) {
            for (Node node : parent.getChildrenUnmodifiable()) {
                if (node instanceof Label) {
                    labels.add((Label) node);
                }
            }
        }
        return labels;
    }
}
