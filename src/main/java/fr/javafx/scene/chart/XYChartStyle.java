package fr.javafx.scene.chart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class XYChartStyle<X, Y> {
	record Entry(String name, int index, XY.Series.Style style) { };

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
		updateCss();
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
		
		updateCss();
	}
	void handleStylesChange(MapChangeListener.Change<? extends Series<X, Y>, ? extends XY.Series.Style> changes) {
		System.out.println("Styles updated...");
		updateCss();
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
	
	int    toDefaultColorIndex(Series<X, Y> _series) {
		String defaultColor = _series.getNode().getStyleClass()
											   .stream()
											   .filter(cls -> cls.contains("default-color"))
											   .findAny().orElse(null);

		if( defaultColor != null )
			return Integer.parseInt( defaultColor.substring("default-color".length()) );
		return -1;
	}
	String toCssContent(XY.Series.Style[] _styles) {
		StringBuilder sb = new StringBuilder();

    	for(int i = 0; i < _styles.length; ++i) {

    		if(_styles[i] != null) {
	    		for(String symbol : Arrays.asList("chart-symbol", "chart-line-symbol", "chart-area-symbol", "chart-legend-item")) {
	
	        		sb.append( ".default-color" + i)
	    					.append("." + symbol + " { ");
	        		if(_styles[i].shape() != null)
									sb.append(String.format("-fx-shape: \"%s\"; ", _styles[i].shape()));
	        		if(_styles[i].inColor() != null && _styles[i].outColor() != null)
									sb.append(String.format("-fx-background-color: %s, %s; ", _styles[i].inColorHex(), _styles[i].outColorHex()));
	    					sb.append(" }\n" );
	
	    		}
	
	    		for(String line : Arrays.asList("chart-series-line", "chart-series-area-line")) {

	        		sb.append( ".default-color" + i)
	    					.append("." + line + " { ");
	    	        if(_styles[i].lineWidth() != null)
									sb.append(String.format("-fx-stroke-width: %spt; ", _styles[i].lineWidth().intValue()));
	    	        if(_styles[i].inColor() != null)
									sb.append(String.format("-fx-stroke: %s; ", _styles[i].inColorHex()));
	    					sb.append(" }\n" );

	    		}

	    		for(String area : Arrays.asList("chart-series-area-fill")) {

	        		sb.append( ".default-color" + i)
	    					.append("." + area + " { ")
	    							.append("-fx-fill: #f9d900;")
	    					.append(" }\n" );

	    		}

	    		sb.append( ".default-color" + i)
						.append(".chart-bubble { ")
								.append("-fx-bubble-fill: #f9d900aa;")
						.append(" }\n" );
	    		sb.append( ".default-color" + i)
						.append(".chart-bar { ")
								.append("-fx-bar-fill: #f9d900;")
						.append(" }\n" );
	    		sb.append( ".default-color" + i)
						.append(".chart-pie { ")
								.append("-fx-pie-color: #f9d900;")
						.append(" }\n" );
    		}

    	}

    	return sb.toString();
	}
    String toTemporaryCssFile(String _tempCssFile, XY.Series.Style[] _styles) {
    	String cssFile = null;

    	try {
    		File 
    		tempFile = _tempCssFile == null ? File.createTempFile("inlineCSS", ".css") : new File(_tempCssFile);
    		tempFile.delete();
    		tempFile.createNewFile();
    		cssFile  = tempFile.getAbsolutePath();

            BufferedWriter 
            bw = new BufferedWriter(new FileWriter(tempFile));
            bw . write( toCssContent(_styles) );
            bw . close();

    	} catch(Exception e) { }
System.out.println(cssFile);
        return cssFile;
    }

}
