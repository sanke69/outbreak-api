package fr.javafx.scene.chart;

import static java.util.stream.Collectors.toList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class XYChartStyle<X, Y> {
	public static final String SCATTER_SYMBOL = ".chart-symbol";
	public static final String LINE_SYMBOL    = ".chart-line-symbol";
	public static final String LINE_SEGMENT   = ".chart-series-line";
	public static final String AREA_SYMBOL    = ".chart-area-symbol";
	public static final String AREA_SEGMENT   = ".chart-series-area-line";
	public static final String AREA_REGION    = ".chart-series-area-fill";
	public static final String LEGEND_SYMBOL  = ".chart-legend-item";
	public static final String BUBBLE_CHART   = ".chart-bubble";
	public static final String BAR_CHART      = ".chart-bar";
	public static final String PIE_CHART      = ".chart-pie";
	public static final String DEFAULT_COLOR  = ".default-color";

	record Entry(String name, int index, XY.Series.Style style) { };
	
	static final String defaultColor(int _integer) {
		return DEFAULT_COLOR + _integer;
	}

	XYChart<X, Y> 								chart;
	String 										attachedCSS;
	MapProperty<Series<X, Y>, Integer>  		indexes;
	MapProperty<Series<X, Y>, XY.Series.Style>  styles;

	XYChartStyle(XYChart<X, Y> _chart) {
		super();
		chart   = _chart;
		indexes = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>()));
		styles  = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>()));

		chart.getStylesheets().add( attachedCSS = toTemporaryCssFile(attachedCSS, new XY.Series.Style[10]) ); 

		chart.getData() . addListener(this::handleSeriesChange);
		styles			. addListener(this::handleStylesChange);
		update();
	}

	void handleSeriesChange(ListChangeListener.Change<? extends Series<X, Y>> _changes) {
		while(_changes.next()) {
			if(_changes.wasAdded()) {
				for(Series<X, Y> newSeries : _changes.getAddedSubList()) {

					String          name  = newSeries.getName();
					int             index = toDefaultColorIndex(newSeries);
					
					indexes.put(newSeries, index);
					System.out.println("added index: " + index);
				}
			} else if(_changes.wasRemoved()) {
				for(Series<X, Y> oldSeries : _changes.getRemoved()) {

					String          name  = oldSeries.getName();
					int             index = toDefaultColorIndex(oldSeries);

					indexes.remove(oldSeries);
					styles.remove(oldSeries);
					System.out.println("remove index: " + index);
				}
			}
		}
		
		update();
	}
	void handleStylesChange(MapChangeListener.Change<? extends Series<X, Y>, ? extends XY.Series.Style> changes) {
		System.out.println("Styles updated...");
		update();
	}

	void update() {
//		updateCss();
		updateStyles();
	}

	void updateCss() {
		chart.getStylesheets().remove(attachedCSS);

		XY.Series.Style   defaultStyle = null;
		XY.Series.Style[] arrayStyle   = new XY.Series.Style[10];

		for(Map.Entry<Series<X, Y>, XY.Series.Style> e : styles.entrySet()) {
			int index = toDefaultColorIndex ( e.getKey() );

			System.out.println("used index: " + index);
			if(index >= 0)
				arrayStyle[index] = e.getValue();
		}
		
		for(int i = 0; i < arrayStyle.length; ++i)
			if( arrayStyle[i] == null )
				arrayStyle[i] = defaultStyle;

		attachedCSS = toTemporaryCssFile(null/*attachedCSS*/, arrayStyle);
		chart.getStylesheets().add( "file://" + attachedCSS );
	}
	void updateStyles() {
		for(Map.Entry<Series<X, Y>, XY.Series.Style> e : styles.entrySet()) {
			updateStyle(e.getKey(), e.getValue());
		}
	}

    // To Update Node styles
	void updateStyle(Series<X,Y> _series, XY.Series.Style _style) {
		int position = toDefaultColorIndex ( _series );

		for(String symbol : Arrays.asList(SCATTER_SYMBOL, LINE_SYMBOL, AREA_SYMBOL, LEGEND_SYMBOL)) {
			Node nl  = chart.lookup(DEFAULT_COLOR + position + symbol);

			if(nl != null)
				nl.setStyle(symbolStyle(_style));
		}
		for(String line   : Arrays.asList(LINE_SEGMENT, AREA_SEGMENT)) {
			Node nl  = chart.lookup(DEFAULT_COLOR + position + line);

			if(nl != null)
				nl.setStyle(lineStyle(_style));
		}
		for(String area   : Arrays.asList(AREA_REGION)) {
			Node nl  = chart.lookup(DEFAULT_COLOR + position + area);

			if(nl != null)
				nl.setStyle(areaStyle(_style));
		}

		Node 
		specials  = chart.lookup(DEFAULT_COLOR + position + BUBBLE_CHART);
		if(specials != null)
			specials.setStyle(bubbleStyle(_style));
		specials  = chart.lookup(DEFAULT_COLOR + position + BAR_CHART);
		if(specials != null)
			specials.setStyle(barStyle(_style));
		specials  = chart.lookup(DEFAULT_COLOR + position + PIE_CHART);
		if(specials != null)
			specials.setStyle(pieStyle(_style));

		// Update each Node on Line...
		for(int index = 0; index<_series.getData().size(); index++){
    	    XYChart.Data<X, Y> dataPoint = _series.getData().get(index);

    		for(String symbol : Arrays.asList(SCATTER_SYMBOL, LINE_SYMBOL, AREA_SYMBOL, LEGEND_SYMBOL)) {
        	    Node nodeSymbol = dataPoint.getNode().lookup(symbol);

    			if(nodeSymbol != null)
    				nodeSymbol.setStyle(symbolStyle(_style));
    		}
    	}
    	_series.getNode().setStyle(symbolStyle(_style) + lineStyle(_style));
    	
    	// Update each Node on Legend...
    	Pane legend = XYChartUtils.getLegend(_series.getChart());
    	
    	
		legend.getChildrenUnmodifiable()
			    .stream()
			    .filter(n -> n instanceof Label)
			    .map(n -> (Label) n)
			    .forEach(l -> {
			    	System.out.println("update legends");
			    	l.setStyle(symbolStyle(_style));
			    	l.getGraphic().setStyle(symbolStyle(_style));
			    });
				
				
				//XYChartUtils.getChildLabels(allLegends);
    	
    	
	}

    // To write CSS
    String toTemporaryCssFile(String _tempCssFile, XY.Series.Style[] _styles) {
    	String cssFile = null;

    	try {
    		File 
    		tempFile = _tempCssFile == null ? File.createTempFile("inlineCSS-", ".css") : new File(_tempCssFile);
    		tempFile.delete();
    		tempFile.createNewFile();
    		cssFile  = tempFile.getAbsolutePath();

            BufferedWriter 
            bw = new BufferedWriter(new FileWriter(tempFile));
            bw . write( toCssContent(_styles) );
            bw . close();

    	} catch(Exception e) { }

        return cssFile;
    }

	String toCssContent(XY.Series.Style[] _styles) {
		StringBuilder sb = new StringBuilder();

    	for(int i = 0; i < _styles.length; ++i) 
    		sb.append( defaultColorStyle(i, _styles[i]) );

    	return sb.toString();
	}

    String defaultColorStyle(int _index, XY.Series.Style _style) {
		if(_style == null)
			return "";

		StringBuilder sb = new StringBuilder();

		for(String symbol : Arrays.asList(SCATTER_SYMBOL, LINE_SYMBOL, AREA_SYMBOL, LEGEND_SYMBOL))
    		sb.append( defaultColor(_index) ).append( symbol  + " { " + symbolStyle(_style) + " }\n" );
		for(String line : Arrays.asList(LINE_SEGMENT, AREA_SEGMENT))
    		sb.append( defaultColor(_index) ).append( line    + " { " + lineStyle(_style)   + " }\n" );
		for(String area : Arrays.asList(AREA_REGION))
    		sb.append( defaultColor(_index) ).append( area    + " { " + areaStyle(_style)   + " }\n" );

		sb.append( defaultColor(_index) ).append(BUBBLE_CHART + " { " + bubbleStyle(_style) + " }\n" );
		sb.append( defaultColor(_index) ).append(BAR_CHART    + " { " + barStyle(_style)    + " }\n" );
		sb.append( defaultColor(_index) ).append(PIE_CHART    + " { " + pieStyle(_style)    + " }\n" );

    	return sb.toString();
    }
    
    // Common Methods
	int    toDefaultColorIndex(Series<X, Y> _series) {
		String defaultColor = _series.getNode().getStyleClass()
											   .stream()
											   .filter(cls -> cls.contains("default-color"))
											   .findAny().orElse(null);

		if( defaultColor != null )
			return Integer.parseInt( defaultColor.substring("default-color".length()) );
		return -1;
	}

    String symbolStyle (XY.Series.Style _style) {
    	StringBuilder sb = new StringBuilder();

		if(_style.shape() != null)
			sb.append(String.format("-fx-shape: \"%s\"; ", _style.shape()));
		if(_style.inColor() != null && _style.outColor() != null)
			sb.append(String.format("-fx-background-color: %s, %s; ", _style.inColorHex(), _style.outColorHex()));

        return sb.toString();
    }
    String lineStyle   (XY.Series.Style _style) {
    	StringBuilder sb = new StringBuilder();

        if(_style.lineWidth() != null)
			sb.append(String.format("-fx-stroke-width: %spt; ", _style.lineWidth().intValue()));
        if(_style.inColor() != null)
			sb.append(String.format("-fx-stroke: %s; ", _style.inColorHex()));

        return sb.toString();
    }
    String areaStyle   (XY.Series.Style _style) {
    	StringBuilder sb = new StringBuilder();

        if(_style.lineWidth() != null)
			sb.append("-fx-fill: #f9d900;");

        return sb.toString();
    }
    String bubbleStyle (XY.Series.Style _style) {
    	StringBuilder sb = new StringBuilder();

        if(_style.lineWidth() != null)
			sb.append("-fx-bubble-fill: #f9d900aa;");

        return sb.toString();
    }
    String barStyle    (XY.Series.Style _style) {
    	StringBuilder sb = new StringBuilder();

        if(_style.lineWidth() != null)
			sb.append("-fx-bar-fill: #f9d900;");

        return sb.toString();
    }
    String pieStyle    (XY.Series.Style _style) {
    	StringBuilder sb = new StringBuilder();

        if(_style.lineWidth() != null)
			sb.append("-fx-pie-color: #f9d900;");

        return sb.toString();
    }

}
