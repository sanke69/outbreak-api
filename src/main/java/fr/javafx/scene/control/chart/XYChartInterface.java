package fr.javafx.scene.control.chart;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

// https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/css-styles.htm
public interface XYChartInterface<X, Y> {

	public enum Symbols {
		diamond		("M5,0 L10,9 L5,18 L0,9 Z"),
		cross		("M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z"),
		triangle	("M5,0 L10,8 L0,8 Z"),
		check		("M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z");
		
		String path;
		
		Symbols(String _path) {
			path = _path;
		}

		public String toString() { return path; }

	}
	public enum Mode {
		Scatter, Line, Area, StackedArea, Bar;
	}

	public static <X, Y> XYChartInterface<X, Y> 		newInstance(Mode _mode, Axis<X> _xAxis, Axis<Y> _yAxis) {
		return new XYChartInterfaceImpl<X, Y>(_mode, _xAxis, _yAxis);
	}

	public XYChartInterface<X, Y> 						enablePanning(boolean _enabled);
	public XYChartInterface<X, Y> 						enablePanning(boolean _enabled, EventHandler<? super MouseEvent> mouseFilter);

	public XYChartInterface<X, Y> 						enableZooming(boolean _enabled);
	public XYChartInterface<X, Y> 						enableZooming(boolean _enabled, EventHandler<? super MouseEvent> mouseFilter);

	public XYChartInterface<X, Y> 						enableAutoRange(boolean _enabled);
	public XYChartInterface<X, Y> 						enableAutoRange(boolean _enabled, EventHandler<? super MouseEvent> mouseFilter);

	public default Chart	 							getChart() { return getXYChart(); }
	public XYChart<X, Y> 								getXYChart();

	public Node			 								getNode();
	
	public void 										setStyle(XYChart.Series<?,?> _series, 
																		Color _lineColor, Integer _lineWidth, 
																		String _shape, Color _inColor, Color _outColor,
																		Color _fillColor);

	// Chart Methods
	public String 										getTitle();
	public void 										setTitle(String value);
	public StringProperty 								titleProperty();
	
    public Side 										getTitleSide();
    public void 										setTitleSide(Side value);
    public ObjectProperty<Side> 						titleSideProperty();

    public boolean 										isLegendVisible();
    public void 										setLegendVisible(boolean value);
    public BooleanProperty 								legendVisibleProperty();

    public Side 										getLegendSide();
    public void 										setLegendSide(Side value);
    public ObjectProperty<Side> 						legendSideProperty();

    public boolean 										getAnimated();
    public void 										setAnimated(boolean value);
    public BooleanProperty 								animatedProperty();

    public ObservableList<Series<X,Y>> 					getData();
    public void 										setData(ObservableList<Series<X,Y>> value);
    public ObjectProperty<ObservableList<Series<X,Y>>> 	dataProperty();

    public boolean 										getVerticalGridLinesVisible();
    public void 										setVerticalGridLinesVisible(boolean value);
    public BooleanProperty 								verticalGridLinesVisibleProperty();

    public boolean 										isHorizontalGridLinesVisible();
    public void 										setHorizontalGridLinesVisible(boolean value);
    public BooleanProperty 								horizontalGridLinesVisibleProperty();

    public boolean 										isAlternativeColumnFillVisible();
    public void 										setAlternativeColumnFillVisible(boolean value);
    public BooleanProperty 								alternativeColumnFillVisibleProperty();

    public boolean 										isAlternativeRowFillVisible();
    public void 										setAlternativeRowFillVisible(boolean value);
    public BooleanProperty 								alternativeRowFillVisibleProperty();

    public boolean 										isVerticalZeroLineVisible();
    public void 										setVerticalZeroLineVisible(boolean value);
    public BooleanProperty 								verticalZeroLineVisibleProperty();

    public boolean 										isHorizontalZeroLineVisible();
    public void 										setHorizontalZeroLineVisible(boolean value);
    public BooleanProperty 								horizontalZeroLineVisibleProperty();

    public List<CssMetaData<? extends Styleable, ?>> 	getCssMetaData();

	// XYChart Methods
    public Axis<X> 										getXAxis();
    public Axis<Y> 										getYAxis();

}