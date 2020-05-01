package fr.javafx.scene.control.chart.plugins.behavior;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import fr.javafx.scene.control.chart.XY;
import fr.javafx.scene.control.chart.XYChartUtils;
import fr.javafx.scene.control.chart.plugins.AbstractChartPlugin;
import fr.javafx.utils.MouseEvents;

public class XYChartZoomer extends AbstractChartPlugin<Number, Number> {

    public static final String 					STYLE_CLASS_ZOOM_RECT = "chart-zoom-rect";
    private static final int 					ZOOM_RECT_MIN_SIZE    = 5;
    private static final Duration 				DEFAULT_ZOOM_DURATION = Duration.millis(500);

    public static final Predicate<MouseEvent> 	DEFAULT_ZOOM_IN_MOUSE_FILTER     = event -> MouseEvents.isOnlyPrimaryButtonDown(event) && MouseEvents.modifierKeysUp(event);
    public static final Predicate<MouseEvent> 	DEFAULT_ZOOM_OUT_MOUSE_FILTER    = event -> MouseEvents.isOnlySecondaryButtonDown(event) && MouseEvents.modifierKeysUp(event);
    public static final Predicate<MouseEvent> 	DEFAULT_ZOOM_ORIGIN_MOUSE_FILTER = event -> MouseEvents.isOnlySecondaryButtonDown(event) && MouseEvents.isOnlyCtrlModifierDown(event);

    private final BooleanProperty 				animated = new SimpleBooleanProperty(this, "animated", false);

    private final ObjectProperty<Duration> 		zoomDuration = new SimpleObjectProperty<Duration>(this, "zoomDuration",
            DEFAULT_ZOOM_DURATION) {
        @Override
        protected void invalidated() {
            Objects.requireNonNull(get(), "The " + getName() + " must not be null");
        }
    };

    private Predicate<MouseEvent> 				zoomInMouseFilter     	= DEFAULT_ZOOM_IN_MOUSE_FILTER;
    private Predicate<MouseEvent> 				zoomOutMouseFilter    	= DEFAULT_ZOOM_OUT_MOUSE_FILTER;
    private Predicate<MouseEvent> 				zoomOriginMouseFilter 	= DEFAULT_ZOOM_ORIGIN_MOUSE_FILTER;

    private final EventHandler<MouseEvent> 		zoomInStartHandler      = event -> {
        if (getZoomInMouseFilter() == null || getZoomInMouseFilter().test(event)) {
            zoomInStarted(event);
            event.consume();
        }
    };
    private final EventHandler<MouseEvent> 		zoomInDragHandler       = event -> {
        if (zoomOngoing()) {
            zoomInDragged(event);
            event.consume();
        }
    };
    private final EventHandler<MouseEvent> 		zoomInEndHandler        = event -> {
        if (zoomOngoing()) {
            zoomInEnded();
            event.consume();
        }
    };
    private final EventHandler<MouseEvent> 		zoomOutHandler          = event -> {
        if (getZoomOutMouseFilter() == null || getZoomOutMouseFilter().test(event)) {
            boolean zoomOutPerformed = zoomOut();
            if (zoomOutPerformed) {
                event.consume();
            }
        }
    };
    private final EventHandler<MouseEvent> 		zoomOriginHandler       = event -> {
        if (getZoomOriginMouseFilter() == null || getZoomOriginMouseFilter().test(event)) {
            boolean zoomOutPerformed = zoomOrigin();
            if (zoomOutPerformed) {
                event.consume();
            }
        }
    };

    private final Rectangle 					zoomRectangle 			= new Rectangle();
    private Point2D 							zoomStartPoint 			= null;
    private Point2D 							zoomEndPoint 			= null;
    private final Map<XYChart<Number, Number>, Deque<Rectangle2D>> 
    											zoomStacks 				= new HashMap<>();

    private Cursor 								originalCursor;
    private final ObjectProperty<Cursor> 		dragCursor = new SimpleObjectProperty<>(this, "dragCursor");

    private final ObjectProperty<XY.Mode> 		axisMode = new SimpleObjectProperty<XY.Mode>(this, "axisMode", XY.Mode.XY) {
        @Override
        protected void invalidated() {
            Objects.requireNonNull(get(), "The " + getName() + " must not be null");
        }
    };

    public XYChartZoomer() {
        this(XY.Mode.XY);
    }
    public XYChartZoomer(XY.Mode zoomMode) {
        this(zoomMode, false);
    }

    public XYChartZoomer(boolean animated) {
        this(XY.Mode.XY, animated);
    }
    public XYChartZoomer(XY.Mode zoomMode, boolean animated) {
        setAxisMode(zoomMode);
        setAnimated(animated);
        setDragCursor(Cursor.CROSSHAIR);

        zoomRectangle.setManaged(false);
        zoomRectangle.getStyleClass().add(STYLE_CLASS_ZOOM_RECT);
        getChartChildren().add(zoomRectangle);
        registerMouseHandlers();
    }

    public final void 									setAnimated(boolean value) {
        animated.set(value);
    }
    public final boolean 								isAnimated() {
        return animated.get();
    }
    public final BooleanProperty 						animatedProperty() {
        return animated;
    }

    public final void 									setZoomDuration(Duration duration) {
        zoomDuration.set(duration);
    }
    public final Duration 								getZoomDuration() {
        return zoomDuration.get();
    }
    public final ObjectProperty<Duration> 				zoomDurationProperty() {
        return zoomDuration;
    }

    public final void 									setAxisMode(XY.Mode mode) {
        axisModeProperty().set(mode);
    }
    public final XY.Mode 								getAxisMode() {
        return axisModeProperty().get();
    }
    public final ObjectProperty<XY.Mode> 				axisModeProperty() {
        return axisMode;
    }

    public final void 									setDragCursor(Cursor cursor) {
        dragCursorProperty().set(cursor);
    }
    public final Cursor 								getDragCursor() {
        return dragCursorProperty().get();
    }
    public final ObjectProperty<Cursor> 				dragCursorProperty() {
        return dragCursor;
    }

    public void 										setZoomInMouseFilter(Predicate<MouseEvent> zoomInMouseFilter) {
        this.zoomInMouseFilter = zoomInMouseFilter;
    }
    public Predicate<MouseEvent> 						getZoomInMouseFilter() {
        return zoomInMouseFilter;
    }

    public void 										setZoomOutMouseFilter(Predicate<MouseEvent> zoomOutMouseFilter) {
        this.zoomOutMouseFilter = zoomOutMouseFilter;
    }
    public Predicate<MouseEvent> 						getZoomOutMouseFilter() {
        return zoomOutMouseFilter;
    }

    public void 										setZoomOriginMouseFilter(Predicate<MouseEvent> zoomOriginMouseFilter) {
        this.zoomOriginMouseFilter = zoomOriginMouseFilter;
    }
    public Predicate<MouseEvent> 						getZoomOriginMouseFilter() {
        return zoomOriginMouseFilter;
    }

    public boolean 										zoomOut() {
        clearZoomStackIfAxisAutoRangingIsEnabled();
        Map<XYChart<Number, Number>, Rectangle2D> zoomWindows = getZoomWindows(Deque::pollFirst);

        if (zoomWindows.isEmpty()) {
            return false;
        }
        performZoom(zoomWindows);
        return true;
    }
   
    public void 										clear() {
        zoomStacks.clear();
    }

    private void 										registerMouseHandlers() {
        registerMouseEventHandler(MouseEvent.MOUSE_PRESSED,  zoomInStartHandler);
        registerMouseEventHandler(MouseEvent.MOUSE_DRAGGED,  zoomInDragHandler);
        registerMouseEventHandler(MouseEvent.MOUSE_RELEASED, zoomInEndHandler);
        registerMouseEventHandler(MouseEvent.MOUSE_CLICKED,  zoomOutHandler);
        registerMouseEventHandler(MouseEvent.MOUSE_CLICKED,  zoomOriginHandler);
    }

    private void 										installCursor() {
        originalCursor = getChartPane().getCursor();
        if (getDragCursor() != null) {
            getChartPane().setCursor(getDragCursor());
        }
    }
    private void 										uninstallCursor() {
        getChartPane().setCursor(originalCursor);
    }

    private void 										zoomInStarted(MouseEvent event) {
        zoomStartPoint = new Point2D(event.getX(), event.getY());
        zoomRectangle.setX(zoomStartPoint.getX());
        zoomRectangle.setY(zoomStartPoint.getY());
        zoomRectangle.setWidth(0);
        zoomRectangle.setHeight(0);
        zoomRectangle.setVisible(true);
        installCursor();
    }
    private void 										zoomInDragged(MouseEvent event) {
        Bounds plotAreaBounds = getChartPane().getPlotAreaBounds();
        zoomEndPoint = limitToPlotArea(event, plotAreaBounds);

        double zoomRectX      = plotAreaBounds.getMinX();
        double zoomRectY      = plotAreaBounds.getMinY();
        double zoomRectWidth  = plotAreaBounds.getWidth();
        double zoomRectHeight = plotAreaBounds.getHeight();

        if (getAxisMode().allowsX()) {
            zoomRectX      = Math.min(zoomStartPoint.getX(), zoomEndPoint.getX());
            zoomRectWidth  = Math.abs(zoomEndPoint.getX() - zoomStartPoint.getX());
        }
        if (getAxisMode().allowsY()) {
            zoomRectY      = Math.min(zoomStartPoint.getY(), zoomEndPoint.getY());
            zoomRectHeight = Math.abs(zoomEndPoint.getY() - zoomStartPoint.getY());
        }

        zoomRectangle.setX(zoomRectX);
        zoomRectangle.setY(zoomRectY);
        zoomRectangle.setWidth(zoomRectWidth);
        zoomRectangle.setHeight(zoomRectHeight);
    }
    private void 										zoomInEnded() {
        zoomRectangle.setVisible(false);
        if (zoomRectangle.getWidth() > ZOOM_RECT_MIN_SIZE && zoomRectangle.getHeight() > ZOOM_RECT_MIN_SIZE)
            performZoomIn();

        zoomStartPoint = zoomEndPoint = null;
        uninstallCursor();
    }

    private boolean 									zoomOrigin() {
        clearZoomStackIfAxisAutoRangingIsEnabled();
        Map<XYChart<Number, Number>, Rectangle2D> zoomWindows = getZoomWindows(Deque::peekLast);
        if (zoomWindows.isEmpty())
            return false;

        clear();
        performZoom(zoomWindows);
        return true;
    }
    private boolean 									zoomOngoing() {
        return zoomStartPoint != null;
    }

    private void 										pushCurrentZoomWindows() {
        for (XYChart<Number, Number> chart : getCharts())
            pushCurrentZoomWindow(chart);
    }
    private void 										pushCurrentZoomWindow(XYChart<Number, Number> chart) {
        ValueAxis<Number> xAxis = XYChartUtils.Axes.toValueAxis(chart.getXAxis());
        ValueAxis<Number> yAxis = XYChartUtils.Axes.toValueAxis(chart.getYAxis());

        Deque<Rectangle2D> zoomStack = zoomStacks.get(chart);
        if (zoomStack == null) {
            zoomStack = new ArrayDeque<>();
            zoomStacks.put(chart, zoomStack);
        }

        zoomStack.addFirst(
        		new Rectangle2D(xAxis.getLowerBound(), 
        						yAxis.getLowerBound(),
        						xAxis.getUpperBound() - xAxis.getLowerBound(), 
        						yAxis.getUpperBound() - yAxis.getLowerBound()));
    }

    private Map<XYChart<Number, Number>, Rectangle2D> 	getZoomDataWindows() {
        Map<XYChart<Number, Number>, Rectangle2D> zoomWindows = new HashMap<>();
        for (XYChart<Number, Number> chart : getCharts()) {
            zoomWindows.put(chart, getZoomDataWindow(chart));
        }
        return zoomWindows;
    }
    private Rectangle2D 								getZoomDataWindow(XYChart<Number, Number> chart) {
        double minX = zoomRectangle.getX();
        double minY = zoomRectangle.getY() + zoomRectangle.getHeight();
        double maxX = zoomRectangle.getX() + zoomRectangle.getWidth();
        double maxY = zoomRectangle.getY();

        Data<Number, Number> dataMin = toDataPoint(chart.getYAxis(), getChartPane().toPlotArea(minX, minY));
        Data<Number, Number> dataMax = toDataPoint(chart.getYAxis(), getChartPane().toPlotArea(maxX, maxY));

        double dataMinX = dataMin.getXValue().doubleValue();
        double dataMinY = dataMin.getYValue().doubleValue();
        double dataMaxX = dataMax.getXValue().doubleValue();
        double dataMaxY = dataMax.getYValue().doubleValue();

        double dataRectWidth = dataMaxX - dataMinX;
        double dataRectHeight = dataMaxY - dataMinY;

        return new Rectangle2D(dataMinX, dataMinY, dataRectWidth, dataRectHeight);
    }

    private void 										performZoomIn() {
        clearZoomStackIfAxisAutoRangingIsEnabled();
        pushCurrentZoomWindows();
        performZoom(getZoomDataWindows());
    }
    private void 										performZoom(Map<XYChart<Number, Number>, Rectangle2D> zoomWindows) {
        for(Entry<XYChart<Number, Number>, Rectangle2D> entry : zoomWindows.entrySet())
            performZoom(entry.getKey(), entry.getValue());

        getChartPane().requestLayout();
    }
    private void 										performZoom(XYChart<Number, Number> chart, Rectangle2D zoomWindow) {
        ValueAxis<Number> xAxis = XYChartUtils.Axes.toValueAxis(chart.getXAxis());
        ValueAxis<Number> yAxis = XYChartUtils.Axes.toValueAxis(chart.getYAxis());

        if (getAxisMode().allowsX())
            xAxis.setAutoRanging(false);

        if (getAxisMode().allowsY())
            yAxis.setAutoRanging(false);

        if (isAnimated()) {
            if (!XYChartUtils.Axes.hasBoundedRange(xAxis)) {
                Timeline xZoomAnimation = new Timeline();
                xZoomAnimation.getKeyFrames().setAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(xAxis.lowerBoundProperty(), xAxis.getLowerBound()),
                                new KeyValue(xAxis.upperBoundProperty(), xAxis.getUpperBound())),
                        new KeyFrame(getZoomDuration(), new KeyValue(xAxis.lowerBoundProperty(), zoomWindow.getMinX()),
                                new KeyValue(xAxis.upperBoundProperty(), zoomWindow.getMaxX())));
                xZoomAnimation.play();
            }
            if (!XYChartUtils.Axes.hasBoundedRange(yAxis)) {
                Timeline yZoomAnimation = new Timeline();
                yZoomAnimation.getKeyFrames().setAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(yAxis.lowerBoundProperty(), yAxis.getLowerBound()),
                                new KeyValue(yAxis.upperBoundProperty(), yAxis.getUpperBound())),
                        new KeyFrame(getZoomDuration(), new KeyValue(yAxis.lowerBoundProperty(), zoomWindow.getMinY()),
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

    private void 										clearZoomStackIfAxisAutoRangingIsEnabled() {
        for (XYChart<Number, Number> chart : getCharts()) {
            if ((getAxisMode().allowsX() && chart.getXAxis().isAutoRanging())
                    || (getAxisMode().allowsY() && chart.getYAxis().isAutoRanging())) {
                clear();
                return;
            }
        }
    }

    private Map<XYChart<Number, Number>, Rectangle2D> getZoomWindows(
            Function<Deque<Rectangle2D>, Rectangle2D> extractor) {
        Map<XYChart<Number, Number>, Rectangle2D> zoomWindows = new HashMap<>();
        for (XYChart<Number, Number> chart : getCharts()) {
            Deque<Rectangle2D> deque = zoomStacks.get(chart);
            if (deque == null || deque.isEmpty()) {
                return Collections.emptyMap();
            }
            zoomWindows.put(chart, extractor.apply(deque));
        }
        return zoomWindows;
    }


    private Point2D 									limitToPlotArea(MouseEvent event, Bounds plotBounds) {
        double limitedX = Math.max(Math.min(event.getX(), plotBounds.getMaxX()), plotBounds.getMinX());
        double limitedY = Math.max(Math.min(event.getY(), plotBounds.getMaxY()), plotBounds.getMinY());
        return new Point2D(limitedX, limitedY);
    }

}
