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

public class XRangeIndicator<Y> extends AbstractValueIndicatorRange<Number, Y> {

    public XRangeIndicator(double _lower, double _upper) {
        this(_lower, _upper, null);
    }
    public XRangeIndicator(double _lower, double _upper, String _text) {
        super(_lower, _upper, _text);
    }

    @Override
    public void 			layoutChildren() {
        if (getChartPane() == null)
            return;

        Bounds plotAreaBounds = getChartPane().getPlotAreaBounds();
        double minX = plotAreaBounds.getMinX();
        double maxX = plotAreaBounds.getMaxX();
        double minY = plotAreaBounds.getMinY();
        double maxY = plotAreaBounds.getMaxY();

        Axis<Number> xAxis = getChartPane().getXYChart().getXAxis();

        double startX = Math.max(minX, minX + xAxis.getDisplayPosition(getLowerBound()));
        double endX   = Math.min(maxX, minX + xAxis.getDisplayPosition(getUpperBound()));

        layout(new BoundingBox(startX, minY, endX - startX, maxY - minY));
    }

    @Override
    protected ValueAxis<?> 	getValueAxis(XYChartPane<Number, Y> _chartPane) {
        return XYChartUtils.Axes.toValueAxis(_chartPane.getXYChart().getXAxis());
    }

    @Override
    void 					updateStyleClass() {
        setStyleClasses(label, "x-", STYLE_CLASS_LABEL);
        setStyleClasses(rectangle, "x-", STYLE_CLASS_RECT);
    }

}
