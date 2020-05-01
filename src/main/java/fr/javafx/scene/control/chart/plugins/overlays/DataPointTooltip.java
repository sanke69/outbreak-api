package fr.javafx.scene.control.chart.plugins.overlays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

import fr.javafx.scene.control.chart.plugins.AbstractDataFormattingPlugin;

public class DataPointTooltip<X, Y> extends AbstractDataFormattingPlugin<X, Y> {
	public static final String STYLE_CLASS_LABEL = "chart-datapoint-tooltip-label";

	public static final int DEFAULT_PICKING_DISTANCE = 5;

	private static final int LABEL_X_OFFSET = 15;
	private static final int LABEL_Y_OFFSET = 5;

	private final Label label = new Label();

	private final DoubleProperty 			pickingDistance = new SimpleDoubleProperty(this, "pickingDistance", DEFAULT_PICKING_DISTANCE) {
		@Override
		protected void invalidated() {
			if (get() <= 0)
				throw new IllegalArgumentException("The " + getName() + " must be a positive value");
		}
	};

	private final EventHandler<MouseEvent> 	mouseMoveHandler = (MouseEvent event) -> updateToolTip(event);

	public DataPointTooltip() {
		label.getStyleClass().add(STYLE_CLASS_LABEL);
		registerMouseEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
	}
	public DataPointTooltip(double pickingDistance) {
		this();
		setPickingDistance(pickingDistance);
	}

	public final void 						setPickingDistance(double distance) {
		pickingDistance.set(distance);
	}
	public final double 					getPickingDistance() {
		return pickingDistance.get();
	}
	public final DoubleProperty 			pickingDistanceProperty() {
		return pickingDistance;
	}

	private void 							updateToolTip(MouseEvent event) {
		Bounds plotAreaBounds = getChartPane().getPlotAreaBounds();
		DataPoint dataPoint = findDataPoint(event, plotAreaBounds);

		if (dataPoint == null) {
			getChartChildren().remove(label);
			return;
		}
		updateLabel(event, plotAreaBounds, dataPoint);
		if (!getChartChildren().contains(label)) {
			getChartChildren().add(label);
			label.requestLayout();
		}
	}
	private void 							updateLabel(MouseEvent event, Bounds plotAreaBounds, DataPoint dataPoint) {
		label.setText(dataPoint.series.getName() + "\n" + formatDataPoint(dataPoint));

		double mouseX = event.getX();
		double mouseY = event.getY();
		double width  = label.prefWidth(-1);
		double height = label.prefHeight(width);

		double xLocation = mouseX + LABEL_X_OFFSET;
		double yLocation = mouseY - LABEL_Y_OFFSET - height;

		if (xLocation + width > plotAreaBounds.getMaxX())
			xLocation = mouseX - LABEL_X_OFFSET - width;

		if (yLocation < plotAreaBounds.getMinY())
			yLocation = mouseY + LABEL_Y_OFFSET;

		label.resizeRelocate(xLocation, yLocation, width, height);
	}

	private DataPoint 						findDataPoint(MouseEvent event, Bounds plotAreaBounds) {
		if (!plotAreaBounds.contains(event.getX(), event.getY()))
			return null;

		Point2D mouseLocation = getLocationInPlotArea(event);
		DataPoint nearestDataPoint = null;

		List<XYChart<X, Y>> charts = new ArrayList<>(getCharts());

		Collections.reverse(charts);
		for (XYChart<X, Y> chart : charts) {
			DataPoint point = findNearestDataPointWithinPickingDistance(chart, mouseLocation);
			if (nearestDataPoint == null
			|| (point != null && point.distanceFromMouse < nearestDataPoint.distanceFromMouse))
				nearestDataPoint = point;
		}
		return nearestDataPoint;
	}
	private DataPoint 						findNearestDataPointWithinPickingDistance(XYChart<X, Y> chart, Point2D mouseLocation) {
		DataPoint nearestDataPoint = null;

		X xValue = toDataPoint(chart.getYAxis(), mouseLocation).getXValue();
		for (DataPoint dataPoint : findPointsToCheck(chart, xValue)) {
			Node node = dataPoint.data.getNode();
			if (node != null && node.isVisible()) {
				if (node.getBoundsInParent().contains(mouseLocation)) {
					dataPoint.distanceFromMouse = 0;
					return dataPoint;
				}
			} else {
				Point2D displayPoint = toDisplayPoint(chart.getYAxis(), dataPoint.data);
				dataPoint.distanceFromMouse = displayPoint.distance(mouseLocation);

				if (displayPoint.distance(mouseLocation) <= getPickingDistance() && (nearestDataPoint == null
						|| dataPoint.distanceFromMouse < nearestDataPoint.distanceFromMouse))
					nearestDataPoint = dataPoint;
			}
		}
		return nearestDataPoint;
	}
	private List<DataPoint> 				findPointsToCheck(XYChart<X, Y> chart, X xValue) {
		if (xValue instanceof Number)
			return findNeighborPoints(chart, ((Number) xValue).doubleValue());

		List<DataPoint> points = new LinkedList<>();
		for (Series<X, Y> series : chart.getData())
			for (Data<X, Y> data : series.getData())
				points.add(new DataPoint(series, data));

		return points;
	}
	private List<DataPoint> 				findNeighborPoints(XYChart<X, Y> chart, double searchedX) {
		List<DataPoint> points = new LinkedList<>();
		for (Series<X, Y> series : chart.getData()) {
			Pair<DataPoint, DataPoint> neighborPoints = findNeighborPoints(series, searchedX);
			if (neighborPoints.getKey() != null)
				points.add(neighborPoints.getKey());

			if (neighborPoints.getValue() != null)
				points.add(neighborPoints.getValue());

		}

		return points;
	}
	private Pair<DataPoint, DataPoint> 		findNeighborPoints(Series<X, Y> series, double searchedX) {
		List<Data<? extends Number, Y>> seriesData = castXToNumber(series);

		int prevIndex = -1;
		int nextIndex = -1;
		double prevX = Double.MIN_VALUE;
		double nextX = Double.MAX_VALUE;

		for (int i = 0, size = seriesData.size(); i < size; i++) {
			double currentX = seriesData.get(i).getXValue().doubleValue();

			if (currentX < searchedX) {
				if (prevX < currentX) {
					prevIndex = i;
					prevX = currentX;
				}
			} else if (nextX > currentX) {
				nextIndex = i;
				nextX = currentX;
			}
		}
		DataPoint prevPoint = (prevIndex == -1) ? null : new DataPoint(series, series.getData().get(prevIndex));
		DataPoint nextPoint = (nextIndex == -1 || nextIndex == prevIndex) ? null : new DataPoint(series, series.getData().get(nextIndex));

		return new Pair<>(prevPoint, nextPoint);
	}

	private String 							formatDataPoint(DataPoint dataPoint) {
		return formatData(dataPoint.series.getChart().getYAxis(), dataPoint.data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Data<? extends Number, Y>> castXToNumber(Series series) {
		return series.getData();
	}

	private class DataPoint {
		final  Series<X, Y> series;
		final  Data<X, Y>   data;
		double distanceFromMouse;

		DataPoint(Series<X, Y> _series, Data<X, Y> _data) {
			super();

			series = _series;
			data   = _data;
		}

	}

}