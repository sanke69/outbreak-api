/**
 * JavaFR
 * Copyright (C) 2007-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.javafx.scene.chart.plugins;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.util.StringConverter;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;
import fr.javafx.scene.chart.axis.XYValueAxis;

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
            return (StringConverter<T>) XY.defaultNumberFormatter();

        if (axis instanceof NumberAxis)
            return (StringConverter<T>) XY.defaultNumberFormatter();

        return (StringConverter<T>) XY.defaultFormatter();
    }

}
