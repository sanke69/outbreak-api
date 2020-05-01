/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package fr.javafx.scene.control.chart;

import static fr.javafx.utils.FxUtils.getXShift;
import static fr.javafx.utils.FxUtils.getYShift;

import java.util.LinkedList;
import java.util.List;

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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public final class XYChartUtils {
	
	public static final class Axes {
	
		private Axes() {
			//
		}
	
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
	
	}

	public static final class XYChartInfo {
	
		private final XYChart<?,?> 	chart;
		private final Node 			referenceNode;
	
		public XYChartInfo( XYChart<?, ?> chart, Node referenceNode ) {
			this.chart = chart;
			this.referenceNode = referenceNode;
		}
		public XYChartInfo( XYChart<?, ?> chart ) {
			this( chart, chart );
		}
	
		public XYChart<?, ?> getChart() {
			return chart;
		}
	
		public Node 			getReferenceNode() {
			return referenceNode;
		}
	
		@SuppressWarnings( "unchecked" )
		public Point2D 			getDataCoordinates( double x, double y ) {
			Axis xAxis = chart.getXAxis();
			Axis yAxis = chart.getYAxis();
	
			double xStart = getXShift( xAxis, referenceNode );
			double yStart = getYShift( yAxis, referenceNode );
	
			return new Point2D(
					xAxis.toNumericValue( xAxis.getValueForDisplay( x - xStart ) ),
			    yAxis.toNumericValue( yAxis.getValueForDisplay( y - yStart ) )
			);
		}
		@SuppressWarnings( "unchecked" )
		public Rectangle2D 		getDataCoordinates( double minX, double minY, double maxX, double maxY ) {
			if ( minX > maxX || minY > maxY ) {
				throw new IllegalArgumentException( "min > max for X and/or Y" );
			}
	
			Axis xAxis = chart.getXAxis();
			Axis yAxis = chart.getYAxis();
	
			double xStart = getXShift( xAxis, referenceNode );
			double yStart = getYShift( yAxis, referenceNode );
	
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
	
		public boolean 			isInPlotArea( double x, double y ) {
			return getPlotArea().contains( x, y );
		}
	
		public Rectangle2D 		getPlotArea() {
			Axis<?> xAxis = chart.getXAxis();
			Axis<?> yAxis = chart.getYAxis();
	
			double xStart = getXShift( xAxis, referenceNode );
			double yStart = getYShift( yAxis, referenceNode );
	
			//If the direct method to get the width (which is based on its Node dimensions) is not found to
			//be appropriate, an alternative method is commented.
	//		double width = xAxis.getDisplayPosition( xAxis.toRealValue( xAxis.getUpperBound() ) );
			double width = xAxis.getWidth();
	//		double height = yAxis.getDisplayPosition( yAxis.toRealValue( yAxis.getLowerBound() ) );
			double height = yAxis.getHeight();
	
			return new Rectangle2D( xStart, yStart, width, height );
		}
	
		public Rectangle2D 		getXAxisArea() {
			return getComponentArea( chart.getXAxis() );
		}
	
		public Rectangle2D 		getYAxisArea() {
			return getComponentArea( chart.getYAxis() );
		}
	
		private Rectangle2D getComponentArea( Region childRegion ) {
			double xStart = getXShift( childRegion, referenceNode );
			double yStart = getYShift( childRegion, referenceNode );
	
			return new Rectangle2D( xStart, yStart, childRegion.getWidth(), childRegion.getHeight() );
		}
	
		public XY.Context getContext(double _x, double _y) {
			if(getXAxisArea().contains( _x, _y ))
				return XY.Context.onXAxis;
			if(getYAxisArea().contains( _x, _y ))
				return XY.Context.onYAxis;
			if(getPlotArea().contains( _x, _y ))
				return XY.Context.inPlotArea;
			return XY.Context.outsideChart;
		}
	
	}

    private XYChartUtils() {
        //
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
