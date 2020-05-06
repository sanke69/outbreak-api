package fr.javafx.scene.chart.plugins;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Pair;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartPane;

public abstract class AbstractChartPlugin<X, Y> implements XY.ChartPlugin<X, Y> {
    private final ObjectProperty<XYChartPane<X, Y>> chartPane          = new SimpleObjectProperty<>();
    private final ObservableList<Node> 				chartChildren      = FXCollections.observableArrayList();

    private final List<Pair<EventType<KeyEvent>,    EventHandler<KeyEvent>>>     keyEventHandlers    = new LinkedList<>();
    private final List<Pair<EventType<MouseEvent>,  EventHandler<MouseEvent>>>   mouseEventHandlers  = new LinkedList<>();
    private final List<Pair<EventType<ScrollEvent>, EventHandler<ScrollEvent>>>  scrollEventHandlers = new LinkedList<>();

    protected AbstractChartPlugin() {
    	super();

        chartPaneProperty().addListener((obs, oldChartPane, newChartPane) -> {
            removeEventHanlders(oldChartPane);
            addEventHandlers(newChartPane);
        });
    }

    public final void 								setChartPane(XYChartPane<X, Y> chartPane) {
        chartPaneProperty().set(chartPane);
    }
    public final XYChartPane<X, Y> 					getChartPane() {
        return chartPaneProperty().get();
    }
    public final ObjectProperty<XYChartPane<X, Y>> 	chartPaneProperty() {
        return chartPane;
    }

    protected final List<XYChart<X, Y>> 			getCharts() {
        if (getChartPane() == null)
            return Collections.emptyList();

        List<XYChart<X, Y>>
        charts = new LinkedList<>();
        charts . add(getChartPane().getXYChart());
        charts . addAll(getChartPane().getOverlayCharts());
        return charts;
    }
    public final ObservableList<Node> 				getChartChildren() {
        return chartChildren;
    }

    public void 									layoutChildren() {
        // empty by default
    }

    protected final void 							registerKeyEventHandler(EventType<KeyEvent> eventType, EventHandler<KeyEvent> handler) {
    	keyEventHandlers.add(new Pair<>(eventType, handler));
    }
    protected final void 							registerMouseEventHandler(EventType<MouseEvent> eventType, EventHandler<MouseEvent> handler) {
        mouseEventHandlers.add(new Pair<>(eventType, handler));
    }
    protected final void 							registerScrollEventHandler(EventType<ScrollEvent> eventType, EventHandler<ScrollEvent> handler) {
    	scrollEventHandlers.add(new Pair<>(eventType, handler));
    }

    protected final Point2D 						getLocationInPlotArea(MouseEvent event) {
        Point2D mouseLocationInScene = new Point2D(event.getSceneX(), event.getSceneY());
        double xInAxis = getChartPane().getXYChart().getXAxis().sceneToLocal(mouseLocationInScene).getX();
        double yInAxis = getChartPane().getXYChart().getYAxis().sceneToLocal(mouseLocationInScene).getY();
        return new Point2D(xInAxis, yInAxis);
    }

    protected final Point2D 						toDisplayPoint	(Axis<Y> yAxis, Data<X, Y> dataPoint) {
        return new Point2D(
        			getChartPane().getXYChart()
        						  .getXAxis()
        						  .getDisplayPosition( dataPoint.getXValue()), yAxis.getDisplayPosition(dataPoint.getYValue()) );
    }
    protected final Data<X, Y> 						toDataPoint		(Axis<Y> yAxis, Point2D displayPoint) {
        return new Data<>(
        			getChartPane().getXYChart()
        						  .getXAxis()
        						  .getValueForDisplay( displayPoint.getX()), yAxis.getValueForDisplay(displayPoint.getY()) );
    }

    private void 									addEventHandlers(Node node) {
        if(node == null)
            return;

        for(Pair<EventType<KeyEvent>, EventHandler<KeyEvent>> pair : keyEventHandlers)
            node.addEventHandler(pair.getKey(), pair.getValue());
        for(Pair<EventType<MouseEvent>, EventHandler<MouseEvent>> pair : mouseEventHandlers)
            node.addEventHandler(pair.getKey(), pair.getValue());
        for(Pair<EventType<ScrollEvent>, EventHandler<ScrollEvent>> pair : scrollEventHandlers)
            node.addEventHandler(pair.getKey(), pair.getValue());
    }
    private void 									removeEventHanlders(Node node) {
        if(node == null)
            return;

        for(Pair<EventType<KeyEvent>, EventHandler<KeyEvent>> pair : keyEventHandlers)
            node.removeEventHandler(pair.getKey(), pair.getValue());
        for(Pair<EventType<MouseEvent>, EventHandler<MouseEvent>> pair : mouseEventHandlers)
            node.removeEventHandler(pair.getKey(), pair.getValue());
        for(Pair<EventType<ScrollEvent>, EventHandler<ScrollEvent>> pair : scrollEventHandlers)
            node.removeEventHandler(pair.getKey(), pair.getValue());
    }

}
