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
package fr.javafx.scene.chart.plugins.indicators;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;

import fr.javafx.scene.chart.XYChartPane;
import fr.javafx.scene.chart.XYChartUtils;

public class YRangeIndicator<X> extends AbstractValueIndicatorRange<X, Number> {

    private final ValueAxis<Number> axis;

    public YRangeIndicator(double _lower, double _upper) {
        this(_lower, _upper, null, null);
    }
    public YRangeIndicator(double _lower, double _upper, String _text) {
        this(_lower, _upper, _text, null);
    }
    public YRangeIndicator(double _lower, double _upper, ValueAxis<Number> _yAxis) {
        this(_lower, _upper, null, _yAxis);
    }
    private YRangeIndicator(double _lower, double _upper, String _text, ValueAxis<Number> _yAxis) {
        super(_lower, _upper, _text);
        this.axis = _yAxis;
    }

    @Override
    public void 					layoutChildren() {
        if (getChartPane() == null) {
            return;
        }

        Bounds plotAreaBounds = getChartPane().getPlotAreaBounds();
        double minX = plotAreaBounds.getMinX();
        double maxX = plotAreaBounds.getMaxX();
        double minY = plotAreaBounds.getMinY();
        double maxY = plotAreaBounds.getMaxY();

        Axis<Number> yAxis = getValueAxis(getChartPane());

        double startY = Math.max(minY, minY + yAxis.getDisplayPosition(getUpperBound()));
        double endY = Math.min(maxY, minY + yAxis.getDisplayPosition(getLowerBound()));

        layout(new BoundingBox(minX, startY, maxX - minX, endY - startY));
    }

    @Override
    protected ValueAxis<Number> 	getValueAxis(XYChartPane<X, Number> _chartPane) {
        return axis == null ? XYChartUtils.Axes.toValueAxis(_chartPane.getXYChart().getYAxis()) : axis;
    }

    @Override
    void 							updateStyleClass() {
        setStyleClasses(label, "y-", STYLE_CLASS_LABEL);
        setStyleClasses(rectangle, "y-", STYLE_CLASS_RECT);
    }

}
