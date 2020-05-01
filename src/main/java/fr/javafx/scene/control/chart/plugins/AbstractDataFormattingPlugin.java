package fr.javafx.scene.control.chart.plugins;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.util.StringConverter;

import fr.javafx.scene.control.chart.XY;
import fr.javafx.scene.control.chart.XYChartUtils;
import fr.javafx.scene.control.chart.axis.XYValueAxis;

public abstract class AbstractDataFormattingPlugin<X, Y> extends AbstractChartPlugin<X, Y> {
    private final ObjectProperty<StringConverter<X>> xValueFormatter;
    private final ObjectProperty<StringConverter<Y>> yValueFormatter;

    private StringConverter<X> defaultXValueFormatter;
    private StringConverter<Y> defaultYValueFormatter;

    protected AbstractDataFormattingPlugin() {
    	super();
    	xValueFormatter = new SimpleObjectProperty<>();
    	yValueFormatter = new SimpleObjectProperty<>();

        chartPaneProperty().addListener((obs, oldChartPane, newChartPane) -> {
            if (newChartPane != null) {
                defaultXValueFormatter = createDefaultFormatter(newChartPane.getXYChart().getXAxis());
                defaultYValueFormatter = createDefaultFormatter(newChartPane.getXYChart().getYAxis());
            }
        });
    }

    protected String 								formatData(Axis<Y> yAxis, Data<X, Y> data) {
        return getXValueFormatter(getChartPane().getXYChart().getXAxis())
        		.toString(data.getXValue()) + ", " + getYValueFormatter(yAxis).toString(data.getYValue());
    }

    public final void 								setXValueFormatter(StringConverter<X> formatter) {
    	xValueFormatter.set(formatter);
    }
    public final StringConverter<X> 				getXValueFormatter() {
        return xValueFormatter.get();
    }
    public final ObjectProperty<StringConverter<X>> xValueFormatterProperty() {
        return xValueFormatter;
    }

    public final void 								setYValueFormatter(StringConverter<Y> formatter) {
    	yValueFormatter.set(formatter);
    }
    public final StringConverter<Y> 				getYValueFormatter() {
        return yValueFormatter.get();
    }
    public final ObjectProperty<StringConverter<Y>> yValueFormatterProperty() {
        return yValueFormatter;
    }

    private StringConverter<X> 						getXValueFormatter(Axis<X> xAxis) {
        return getValueFormatter(xAxis, getXValueFormatter(), defaultXValueFormatter);
    }
    private StringConverter<Y> 						getYValueFormatter(Axis<Y> yAxis) {
        return getValueFormatter(yAxis, getYValueFormatter(), defaultYValueFormatter);
    }
    @SuppressWarnings("unchecked")
    private <T> StringConverter<T> 					getValueFormatter(Axis<T> axis, StringConverter<T> formatter, StringConverter<T> defaultFormatter) {
        StringConverter<T> valueFormatter = formatter;

        if (valueFormatter == null && XYChartUtils.Axes.isValueAxis(axis))
            valueFormatter = (StringConverter<T>) XYChartUtils.Axes.toValueAxis(axis).getTickLabelFormatter();

        if (valueFormatter == null)
            valueFormatter = defaultFormatter;

        return valueFormatter;
    }

    @SuppressWarnings("unchecked")
    private static <T> StringConverter<T> 			createDefaultFormatter(Axis<T> axis) {
        if (axis instanceof XYValueAxis)
            return XY.Axis.Ticks.defaultFormatter();

        if (axis instanceof NumberAxis)
            return (StringConverter<T>) XY.Axis.Ticks.defaultNumberFormatter();

        return (StringConverter<T>) XY.Axis.Ticks.defaultFormatter();
    }

}
