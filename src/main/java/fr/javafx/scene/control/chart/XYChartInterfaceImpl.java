package fr.javafx.scene.control.chart;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import fr.javafx.utils.FxUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

class XYChartInterfaceImpl<X, Y> implements XYChartInterface<X, Y> {

	private Pane 					chartPane;
	private XYChart<X, Y> chart;

	public XYChartInterfaceImpl(Mode _mode, Axis<X> _xAxis, Axis<Y> _yAxis) {
		super();
		chart = switch(_mode) {
					case Scatter-> 		new ScatterChart<X, Y>(_xAxis, _yAxis);
					case Line->    		new LineChart<X, Y>(_xAxis, _yAxis);
					case Area-> 		new AreaChart<X, Y>(_xAxis, _yAxis);
					case StackedArea-> 	new StackedAreaChart<X, Y>(_xAxis, _yAxis);
					case Bar-> 			new BarChart<X, Y>(_xAxis, _yAxis);
				};
		chart.setAnimated(false);
	}

	@Override
	public XYChart<X, Y> 	getXYChart() {
		return chart;
	}
	@Override
	public Region 					getNode() {
		return chartPane != null ? chartPane : chart;
	}

	public XYChartInterface<X, Y> enablePanning(boolean _enabled) {
		return enablePanning(_enabled, me -> { if( ! ( me.getButton() == MouseButton.SECONDARY || ( me.getButton() == MouseButton.PRIMARY && me.isShortcutDown() ) ) ) me.consume(); });
	}
	public XYChartInterface<X, Y> enablePanning(boolean _enabled, EventHandler<? super MouseEvent> mouseFilter) {
		XYChartPanManager panner = new XYChartPanManager( chart );
		panner.setMouseFilter(mouseFilter);
		panner.start();

		return this;
	}

	public XYChartInterface<X, Y> enableZooming(boolean _enabled ) {
		return enableZooming(_enabled, XYChartZoomManager.DEFAULT_FILTER);
	}
	public XYChartInterface<X, Y> enableZooming(boolean _enabled, EventHandler<? super MouseEvent> mouseFilter) {
		if(chartPane != null && _enabled)
			return this;

		chartPane = new StackPane();
		
		if ( chart.getParent() != null )
			FxUtils.replaceComponent( chart, chartPane );
		
		Rectangle selectRect = new Rectangle( 0, 0, 0, 0 );
		selectRect.setFill( Color.DODGERBLUE );
		selectRect.setMouseTransparent( true );
		selectRect.setOpacity( 0.3 );
		selectRect.setStroke( Color.rgb( 0, 0x29, 0x66 ) );
		selectRect.setStrokeType( StrokeType.INSIDE );
		selectRect.setStrokeWidth( 3.0 );
		StackPane.setAlignment( selectRect, Pos.TOP_LEFT );
		
		chartPane.getChildren().addAll( chart, selectRect );
		
		XYChartZoomManager zoomManager = new XYChartZoomManager( chartPane, selectRect, chart );
		zoomManager.setMouseFilter( mouseFilter );
		zoomManager.start();

		return this;
	}

	public XYChartInterface<X, Y> enableAutoRange(boolean _enabled) {
		XYChartInfo chartInfo = new XYChartInfo( chart, chart );
		EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent event ) {
				if ( event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY ) {
					double x = event.getX();
					double y = event.getY();
					if ( !chartInfo.getXAxisArea().contains( x, y ) )
						chartInfo.getChart().getYAxis().setAutoRanging( true );
					if ( !chartInfo.getYAxisArea().contains( x, y ) )
						chartInfo.getChart().getXAxis().setAutoRanging( true );
				}
			}
		};
		chart.addEventHandler( MouseEvent.MOUSE_CLICKED, handler );

		return this;
	}
	public XYChartInterface<X, Y> enableAutoRange(boolean _enabled, EventHandler<? super MouseEvent> mouseFilter) {
		return this;
	}

	protected Line 					addHLine(Y _y) {
		Region plotArea     = (Region) chart.lookup(".chart-plot-background");
		Pane   chartContent = (Pane)   chart.lookup(".chart-content");

		Line hLine = new Line();

		Runnable updater = () -> {
	        double location = chart.getYAxis().getDisplayPosition(_y);

	        Point2D a = plotArea.localToScene(new Point2D(0, location));
	        Point2D b = plotArea.localToScene(new Point2D(plotArea.getWidth(), location));

	        Point2D aTrans = chartContent.sceneToLocal(a);
	        Point2D bTrans = chartContent.sceneToLocal(b);

	        hLine.setStartX(aTrans.getX());
	        hLine.setStartY(aTrans.getY());
	        hLine.setEndX(bTrans.getX());
	        hLine.setEndY(bTrans.getY());
		};

        chartContent.getChildren().add(hLine);

        chart.boundsInParentProperty()   . addListener((obs, oldValue, newValue) -> updater.run());
        plotArea.boundsInLocalProperty() . addListener((obs, oldValue, newValue) -> updater.run());
        updater.run();
        
        return hLine;
	}
	protected void 					addNode(Node _n, double _x, double _y) {
//		Region plotArea     = (Region) chart.lookup(".chart-plot-background");
		Pane   chartContent = (Pane)   chart.lookup(".chart-content");

        chartContent.getChildren().add(_n);
	}
	
	public void  setStyle(XYChart.Series<?,?> _series, 
											Color _lineColor, Integer _lineWidth, 
											String _shape, Color _inColor, Color _outColor,
											Color _fillColor) {
		Function<Color, String> colorConverter = c -> _lineColor != null ? String.format(Locale.ROOT, "rgba(%d, %d, %d, %.2f)", (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255), c.getOpacity()) : null;

		String line_color = "-fx-stroke: %s; ";
		String line_width = "-fx-stroke-width: %spt; ";
		String pin_color  = "-fx-background-color: %s, %s; ";
		String pin_shape  = "-fx-shape: \"%s\"; ";
		String area_color = "-fx-fill: %s; ";
		String legend_color = "-fx-background-color: %s, %s; ";

        StringBuilder lineStyle = new StringBuilder();
        if(_lineColor != null)
        	lineStyle.append( String.format(line_color, colorConverter.apply(_lineColor)) );
        if(_lineWidth != null)
        	lineStyle.append( String.format(line_width, _lineWidth) );

        StringBuilder pinStyle = new StringBuilder();
        if(_shape != null)
        	pinStyle.append( String.format(pin_shape, _shape) );
        if(_inColor != null && _outColor != null)
        	pinStyle.append( String.format(pin_color, colorConverter.apply(_inColor), colorConverter.apply(_outColor)) );

        StringBuilder areaStyle = new StringBuilder();
        if(_lineColor != null)
        	areaStyle.append( String.format(area_color, colorConverter.apply(_fillColor)) );

        StringBuilder legendStyle = new StringBuilder();
        if(_lineColor != null)
        	legendStyle.append( String.format(legend_color, colorConverter.apply(_inColor), colorConverter.apply(_outColor)) );
        else if(_inColor != null && _outColor != null)
        	legendStyle.append( String.format(legend_color, colorConverter.apply(_inColor), colorConverter.apply(_outColor)) );

		String chartClass = chart.getClass().getSimpleName();
		switch(chartClass) {
		case "ScatterChart" : 		setSymbolStyle (_series, ".chart-symbol",             pinStyle.toString());
									setLegendStyle (_series, ".chart-legend-item",        legendStyle.toString());
									break;
		case "LineChart" : 			setSymbolStyle (_series, ".chart-line-symbol",        pinStyle.toString());
									setLineStyle   (_series, ".chart-series-line",        lineStyle.toString());
									setLegendStyle (_series, ".chart-legend-item",        legendStyle.toString());
									break;
		case "StackedAreaChart" :
		case "AreaChart" : 			setSymbolStyle (_series, ".chart-area-symbol",        pinStyle.toString());
									setLineStyle   (_series, ".chart-series-area-line",   lineStyle.toString());
									setAreaStyle   (_series, ".chart-series-area-fill",   areaStyle.toString());
									break;
		default :
									break;
		}

	}
    private void setSymbolStyle(XYChart.Series<?, ?> _series, String _lookup, String _style) {
    	_series.getData().stream()
    					 .map(data -> data.getNode().lookup(_lookup))
    					 .forEach(symbol -> symbol.setStyle(_style));
    }
    private void setLineStyle(XYChart.Series<?, ?> _series, String _lookup, String _style) {
    	_series.getNode().lookup(_lookup) . setStyle(_style.toString());
    }
    private void setAreaStyle(XYChart.Series<?, ?> _series, String _lookup, String _style) {
    	_series.getNode().lookup(_lookup) . setStyle(_style.toString());
    }
    private void setLegendStyle(XYChart.Series<?, ?> _series, String _lookup, String _style) {
    	Set<Node> items = chart.lookupAll("Label.chart-legend-item");
    	
    	for(int i = 0; i < items.size(); ++i) {
    		;
		}
    	
//    	_series.getNode().lookup(_lookup) . setStyle(_style.toString());
    }

    @Override public String 										getTitle() { return chart.getTitle(); }
	@Override public void 											setTitle(String value) { chart.setTitle(value); }
	@Override public StringProperty 								titleProperty() { return chart.titleProperty(); }
	
	@Override public Side 											getTitleSide() { return chart.getTitleSide(); }
    @Override public void 											setTitleSide(Side value) { chart.setTitleSide(value); }
    @Override public ObjectProperty<Side> 							titleSideProperty() { return chart.titleSideProperty(); }

    @Override public boolean 										isLegendVisible() { return chart.isLegendVisible(); }
    @Override public void 											setLegendVisible(boolean value) { chart.setLegendVisible(value); }
    @Override public BooleanProperty 								legendVisibleProperty() { return chart.legendVisibleProperty(); }

    @Override public Side 											getLegendSide() { return chart.getLegendSide(); }
    @Override public void 											setLegendSide(Side value) { chart.setLegendSide(value); }
    @Override public ObjectProperty<Side> 							legendSideProperty() { return chart.legendSideProperty(); }

    @Override public boolean 										getAnimated() { return chart.getAnimated(); }
    @Override public void 											setAnimated(boolean value) { chart.setAnimated(value); }
    @Override public BooleanProperty 								animatedProperty() { return chart.animatedProperty(); }

    @Override public ObservableList<Series<X, Y>> 					getData() { return chart.getData(); }
    @Override public void 											setData(ObservableList<Series<X, Y>> value) { chart.setData(value); }
    @Override public ObjectProperty<ObservableList<Series<X, Y>>> 	dataProperty() { return chart.dataProperty(); }

    @Override public boolean 										getVerticalGridLinesVisible() { return chart.getVerticalGridLinesVisible(); }
    @Override public void 											setVerticalGridLinesVisible(boolean value) { chart.setVerticalGridLinesVisible(value); }
    @Override public BooleanProperty 								verticalGridLinesVisibleProperty() { return chart.verticalGridLinesVisibleProperty(); }

    @Override public boolean 										isHorizontalGridLinesVisible() { return chart.isHorizontalGridLinesVisible(); }
    @Override public void 											setHorizontalGridLinesVisible(boolean value) { chart.setHorizontalGridLinesVisible(value); }
    @Override public BooleanProperty 								horizontalGridLinesVisibleProperty() { return chart.horizontalGridLinesVisibleProperty(); }

    @Override public boolean 										isAlternativeColumnFillVisible() { return chart.isAlternativeColumnFillVisible(); }
    @Override public void 											setAlternativeColumnFillVisible(boolean value) { chart.setAlternativeColumnFillVisible(value); }
    @Override public BooleanProperty 								alternativeColumnFillVisibleProperty() { return chart.alternativeColumnFillVisibleProperty(); }

    @Override public boolean 										isAlternativeRowFillVisible() { return chart.isAlternativeRowFillVisible(); }
    @Override public void 											setAlternativeRowFillVisible(boolean value) { chart.setAlternativeRowFillVisible(value); }
    @Override public BooleanProperty 								alternativeRowFillVisibleProperty() { return chart.alternativeRowFillVisibleProperty(); }

    @Override public boolean 										isVerticalZeroLineVisible() { return chart.isVerticalZeroLineVisible(); }
    @Override public void 											setVerticalZeroLineVisible(boolean value) { chart.setVerticalZeroLineVisible(value); }
    @Override public BooleanProperty 								verticalZeroLineVisibleProperty() { return chart.verticalZeroLineVisibleProperty(); }

    @Override public boolean 										isHorizontalZeroLineVisible() { return chart.isHorizontalZeroLineVisible(); }
    @Override public void 											setHorizontalZeroLineVisible(boolean value) { chart.setHorizontalZeroLineVisible(value); }
    @Override public BooleanProperty 								horizontalZeroLineVisibleProperty() { return chart.horizontalZeroLineVisibleProperty(); }
	
    @Override public List<CssMetaData<? extends Styleable, ?>> 		getCssMetaData() { return chart.getCssMetaData(); }
	
    @Override public Axis<X> 										getXAxis() { return chart.getXAxis(); }
	@Override public Axis<Y> 										getYAxis() { return chart.getYAxis(); }


}