package fr.javafx.scene.chart.plugins.behavior;

import java.util.Objects;
import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseEvent;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;
import fr.javafx.scene.chart.plugins.AbstractChartPlugin;
import fr.javafx.utils.MouseEvents;

public class XYChartPanner extends AbstractChartPlugin<Number, Number> {

    public static final Predicate<MouseEvent> DEFAULT_MOUSE_FILTER = (event) -> {
        return MouseEvents.isOnlyPrimaryButtonDown(event) && MouseEvents.isOnlyCtrlModifierDown(event);
    };

    private Predicate<MouseEvent> mouseFilter = DEFAULT_MOUSE_FILTER;
    private Point2D previousMouseLocation = null;

    private final ObjectProperty<XY.Constraint> axisMode = new SimpleObjectProperty<XY.Constraint>(this, "axisMode", XY.Constraint.BOTH) {
        @Override
        protected void invalidated() {
            Objects.requireNonNull(get(), "The " + getName() + " must not be null");
        }
    };

    public XYChartPanner() {
        this(XY.Constraint.BOTH);
    }

    public XYChartPanner(XY.Constraint panMode) {
        setAxisMode(panMode);
        setDragCursor(Cursor.CLOSED_HAND);
        registerMouseHandlers();
    }

    private void registerMouseHandlers() {
        registerMouseEventHandler(MouseEvent.MOUSE_PRESSED, panStartHandler);
        registerMouseEventHandler(MouseEvent.MOUSE_DRAGGED, panDragHandler);
        registerMouseEventHandler(MouseEvent.MOUSE_RELEASED, panEndHandler);
    }

    public Predicate<MouseEvent> getMouseFilter() {
        return mouseFilter;
    }
    public void setMouseFilter(Predicate<MouseEvent> mouseFilter) {
        this.mouseFilter = mouseFilter;
    }

    /**
     * The mode defining axis along which the pan operation is allowed. By default initialized to {@link Mode#XY}.
     * 
     * @return the axis mode property
     */
    public final ObjectProperty<XY.Constraint> axisModeProperty() {
        return axisMode;
    }

    /**
     * Sets the value of the {@link #axisModeProperty()}.
     * 
     * @param mode the mode to be used
     */
    public final void setAxisMode(XY.Constraint mode) {
        axisModeProperty().set(mode);
    }

    /**
     * Returns the value of the {@link #axisModeProperty()}.
     * 
     * @return current mode
     */
    public final XY.Constraint getAxisMode() {
        return axisModeProperty().get();
    }

    private Cursor originalCursor;
    private final ObjectProperty<Cursor> dragCursor = new SimpleObjectProperty<>(this, "dragCursor");

    /**
     * Mouse cursor to be used during drag operation.
     * 
     * @return the mouse cursor property
     */
    public final ObjectProperty<Cursor> dragCursorProperty() {
        return dragCursor;
    }

    /**
     * Sets value of the {@link #dragCursorProperty()}.
     * 
     * @param cursor the cursor to be used by the plugin
     */
    public final void setDragCursor(Cursor cursor) {
        dragCursorProperty().set(cursor);
    }

    /**
     * Returns the value of the {@link #dragCursorProperty()}
     * 
     * @return the current cursor
     */
    public final Cursor getDragCursor() {
        return dragCursorProperty().get();
    }

    private void installCursor() {
        originalCursor = getChartPane().getCursor();
        if (getDragCursor() != null) {
            getChartPane().setCursor(getDragCursor());
        }
    }

    private void uninstallCursor() {
        getChartPane().setCursor(originalCursor);
    }

    private final EventHandler<MouseEvent> panStartHandler = (event) -> {
        if (mouseFilter == null || mouseFilter.test(event)) {
            panStarted(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> panDragHandler = (event) -> {
        if (panOngoing()) {
            panDragged(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> panEndHandler = (event) -> {
        if (panOngoing()) {
            panEnded();
            event.consume();
        }
    };

    private boolean panOngoing() {
        return previousMouseLocation != null;
    }

    private void panStarted(MouseEvent event) {
        previousMouseLocation = getLocationInPlotArea(event);
        installCursor();
    }

    private void panDragged(MouseEvent event) {
        Point2D mouseLocation = getLocationInPlotArea(event);
        for (XYChart<Number, Number> chart : getCharts()) {
            panChart(chart, mouseLocation);
        }
        previousMouseLocation = mouseLocation;
    }

    private void panChart(XYChart<Number, Number> chart, Point2D mouseLocation) {
        Data<Number, Number> prevData = toDataPoint(chart.getYAxis(), previousMouseLocation);
        Data<Number, Number> data = toDataPoint(chart.getYAxis(), mouseLocation);

        double xOffset = prevData.getXValue().doubleValue() - data.getXValue().doubleValue();
        double yOffset = prevData.getYValue().doubleValue() - data.getYValue().doubleValue();

        ValueAxis<?> xAxis = XYChartUtils.Axes.toValueAxis(chart.getXAxis());
        if (!XYChartUtils.Axes.hasBoundedRange(xAxis) && getAxisMode().allowsHor()) {
            xAxis.setAutoRanging(false);
            shiftBounds(xAxis, xOffset);
        }
        ValueAxis<?> yAxis = XYChartUtils.Axes.toValueAxis(chart.getYAxis());
        if (!XYChartUtils.Axes.hasBoundedRange(yAxis) && getAxisMode().allowsVer()) {
            yAxis.setAutoRanging(false);
            shiftBounds(yAxis, yOffset);
        }
    }

    /**
     * Depending if the offset is positive or negative, change first upper or lower bound to not provoke
     * lowerBound >= upperBound when offset >= upperBound - lowerBound.
     */
    private void shiftBounds(ValueAxis<?> axis, double offset) {
        if (offset < 0) {
            axis.setLowerBound(axis.getLowerBound() + offset);
            axis.setUpperBound(axis.getUpperBound() + offset);
        } else {
            axis.setUpperBound(axis.getUpperBound() + offset);
            axis.setLowerBound(axis.getLowerBound() + offset);
        }
    }

    private void panEnded() {
        previousMouseLocation = null;
        uninstallCursor();
    }
}
