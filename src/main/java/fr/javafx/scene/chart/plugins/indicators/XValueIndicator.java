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
import javafx.scene.chart.ValueAxis;

import fr.javafx.scene.chart.XYChartPane;
import fr.javafx.scene.chart.XYChartUtils;

public class XValueIndicator<Y> extends AbstractValueIndicatorSingle<Number, Y> {

    public XValueIndicator(double _value) {
        this(_value, null);
    }
    public XValueIndicator(double _value, String _text) {
        super(_value, _text);
    }

    @Override
    public void 			layoutChildren() {
        if (getChartPane() == null) {
            return;
        }

        Bounds plotAreaBounds = getChartPane().getPlotAreaBounds();
        double minX = plotAreaBounds.getMinX();
        double maxX = plotAreaBounds.getMaxX();
        double minY = plotAreaBounds.getMinY();
        double maxY = plotAreaBounds.getMaxY();

        double xPos = minX + getChartPane().getXYChart().getXAxis().getDisplayPosition(getValue());

        if (xPos < minX || xPos > maxX) {
            getChartChildren().clear();
        } else {
            layoutLine(xPos, minY, xPos, maxY);
            layoutLabel(new BoundingBox(xPos, minY, 0, maxY - minY), MIDDLE_POSITION, getLabelPosition());
        }
    }

    @Override
    protected ValueAxis<?> 	getValueAxis(XYChartPane<Number, Y> _chartPane) {
        return XYChartUtils.Axes.toValueAxis(_chartPane.getXYChart().getXAxis());
    }

    @Override
    void 					updateStyleClass() {
        setStyleClasses(label, "x-", STYLE_CLASS_LABEL);
        setStyleClasses(line, "x-", STYLE_CLASS_LINE);
    }

}
