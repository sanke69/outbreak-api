package fr.javafx.scene.chart.plugins.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;
import fr.javafx.scene.chart.plugins.AbstractChartPlugin;
import fr.javafx.utils.MouseEvents;

public class SelectionRegion extends AbstractBehaviorPlugin<Number, Number> {
    public static final String 						STYLE_CLASS_SELECTION_RECT		= "chart-selection-rect";
    public static final Predicate<MouseEvent> 		DEFAULT_SELECTION_MOUSE_FILTER	= event -> MouseEvents.isOnlyPrimaryButtonDown(event) && MouseEvents.modifierKeysUp(event);
    public static final int 						DEFAULT_SELECTION_MIN_SIZE		= 3;

    private ObjectProperty<Predicate<MouseEvent>> 	selectionMouseFilter			= new SimpleObjectProperty<Predicate<MouseEvent>>(DEFAULT_SELECTION_MOUSE_FILTER);
    private final ObjectProperty<XY.Constraint> 	selectionMode 					= new SimpleObjectProperty<XY.Constraint>(XY.Constraint.BOTH) {
        @Override
        protected void invalidated() {
        	if(get() == null || get() == XY.Constraint.NONE) {
        		set( XY.Constraint.BOTH );
        		throw new RuntimeException("The " + getName() + " must not be null or set to NONE");
        	}
        }
    };
    private final BooleanProperty 					selectionRemaining 				= new SimpleBooleanProperty(true);

    private final Rectangle 						selectionRectangle 				= new Rectangle();
    private Point2D 								selectionStartPoint 			= null;
    private Point2D 								selectionEndPoint 				= null;

    private Paint 									originalPaint;

    public SelectionRegion() {
        this(XY.Constraint.BOTH);
    }
    public SelectionRegion(XY.Constraint _mode) {
    	super(_mode, Cursor.CROSSHAIR);

    	interactionModeProperty().addListener((im) -> {
        	if(getInteractionMode() == null || getInteractionMode() == XY.Constraint.NONE) {
        		setInteractionMode( XY.Constraint.BOTH );
        		throw new RuntimeException("The 'InteractionMode' can't not be null or set to NONE with ChartPanner");
        	}});

        selectionRectangle.setManaged(false);
        selectionRectangle.getStyleClass().add(STYLE_CLASS_SELECTION_RECT);
        getChartChildren().add(selectionRectangle);

        registerMouseEventHandler(MouseEvent.MOUSE_PRESSED,  this::onMousePressed);
        registerMouseEventHandler(MouseEvent.MOUSE_DRAGGED,  this::onMouseDragged);
        registerMouseEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);

        addEventHandler(XY.ON_SELECTION, se -> {
        	StringBuilder sb = new StringBuilder();

        	sb.append("Chart: " + se.getChart() + '\n');
        	sb.append("    R: ( " + se.getSelection().getMinX() + ", " + se.getSelection().getMinY() +  ", " + se.getSelection().getMaxX() +  ", " + se.getSelection().getMaxY() + " )\n");
        	
        	System.out.println(sb.toString());
        	
        	ChartZoomer.performZoom(se.getChart(), se.getSelection(), XY.Constraint.BOTH);
        });        
    }

    public final void 									setSelectionMode(XY.Constraint mode) {
    	selectionMode.set(mode);
    }
    public final XY.Constraint 							getSelectionMode() {
        return selectionMode.get();
    }
    public final ObjectProperty<XY.Constraint> 			selectionModeProperty() {
        return selectionMode;
    }

    public void 										setMouseFilter(Predicate<MouseEvent> zoomInMouseFilter) {
        selectionMouseFilter.set(zoomInMouseFilter);
    }
    public Predicate<MouseEvent> 						getMouseFilter() {
        return selectionMouseFilter.get();
    }
    public ObjectProperty<Predicate<MouseEvent>>		mouseFilterProperty() {
        return selectionMouseFilter;
    }

    public final <T extends Event> void 				addEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler) {
    	selectionRectangle.addEventHandler(eventType, eventHandler);
    }
    public final <T extends Event> void 				removeEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler) {
    	selectionRectangle.removeEventHandler(eventType, eventHandler);
    }
    public final <T extends Event> void 				addEventFilter(final EventType<T> eventType, final EventHandler<? super T> eventFilter) {
    	selectionRectangle.addEventFilter(eventType, eventFilter);
    }
    public final <T extends Event> void 				removeEventFilter(final EventType<T> eventType, final EventHandler<? super T> eventFilter) {
    	selectionRectangle.removeEventFilter(eventType, eventFilter);
    }
    
    private void 										onMousePressed(MouseEvent _me) {
        if (getMouseFilter() != null && !getMouseFilter().test(_me))
        	return ;
        
        if(originalPaint == null)
        	originalPaint = selectionRectangle.getFill();

        selectionStartPoint = new Point2D(_me.getX(), _me.getY());

        selectionRectangle.setX(selectionStartPoint.getX());
        selectionRectangle.setY(selectionStartPoint.getY());
        selectionRectangle.setWidth(0);
        selectionRectangle.setHeight(0);
        selectionRectangle.setVisible(true);
        selectionRectangle.setFill(originalPaint);

        installCursor();

        _me.consume();
    }
    private void 										onMouseDragged(MouseEvent _me) {
        if(selectionStartPoint == null)
        	return ;

        Bounds plotAreaBounds      = getChartPane().getPlotAreaBounds();
        double selectionRectX      = plotAreaBounds.getMinX();
        double selectionRectY      = plotAreaBounds.getMinY();
        double selectionRectWidth  = plotAreaBounds.getWidth();
        double selectionRectHeight = plotAreaBounds.getHeight();

        selectionEndPoint = limitToPlotArea(_me, plotAreaBounds);

        if(getSelectionMode().allowsHor()) {
            selectionRectX      = Math.min(selectionStartPoint.getX(), selectionEndPoint.getX());
            selectionRectWidth  = Math.abs(selectionEndPoint.getX() - selectionStartPoint.getX());
        }
        if(getSelectionMode().allowsVer()) {
        	selectionRectY      = Math.min(selectionStartPoint.getY(), selectionEndPoint.getY());
        	selectionRectHeight = Math.abs(selectionEndPoint.getY() - selectionStartPoint.getY());
        }

        selectionRectangle.setX(selectionRectX);
        selectionRectangle.setY(selectionRectY);
        selectionRectangle.setWidth(selectionRectWidth);
        selectionRectangle.setHeight(selectionRectHeight);

        _me.consume();
    }
    private void 										onMouseReleased(MouseEvent _me) {
        if(selectionStartPoint == null)
        	return ;

        if(selectionRectangle.getWidth() > DEFAULT_SELECTION_MIN_SIZE && selectionRectangle.getHeight() > DEFAULT_SELECTION_MIN_SIZE)
            performSelection();

        selectionStartPoint = selectionEndPoint = null;

        if(selectionRemaining.get())
        	selectionRectangle.setFill(Color.LIGHTGREEN); 
        else
        	selectionRectangle.setVisible(false);

        uninstallCursor();

        _me.consume();
    }

    private void 										performSelection() {
        Map<XYChart<Number, Number>, Rectangle2D> selectionWindows = new HashMap<>();
        for(XYChart<Number, Number> chart : getCharts())
            selectionWindows.put(chart, getSelectionWindow(chart));

        for(Entry<XYChart<Number, Number>, Rectangle2D> entry : selectionWindows.entrySet())
            performSelectionOn(entry.getKey(), entry.getValue());

        getChartPane().requestLayout();
    }
    private Rectangle2D 								getSelectionWindow(XYChart<Number, Number> chart) {
        double minX = selectionRectangle.getX();
        double minY = selectionRectangle.getY() + selectionRectangle.getHeight();
        double maxX = selectionRectangle.getX() + selectionRectangle.getWidth();
        double maxY = selectionRectangle.getY();

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
    private void 										performSelectionOn(XYChart<Number, Number> _chart, Rectangle2D _selection) {
    	selectionRectangle.fireEvent(new XY.SelectionEvent(_chart, _selection));
    }

   
    private Point2D 									limitToPlotArea(MouseEvent event, Bounds plotBounds) {
        double limitedX = Math.max(Math.min(event.getX(), plotBounds.getMaxX()), plotBounds.getMinX());
        double limitedY = Math.max(Math.min(event.getY(), plotBounds.getMaxY()), plotBounds.getMinY());
        return new Point2D(limitedX, limitedY);
    }

}
