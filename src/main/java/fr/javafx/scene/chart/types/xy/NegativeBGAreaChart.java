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
package fr.javafx.scene.chart.types.xy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

// https://stackoverflow.com/questions/33695027/javafx-area-chart-behavior-changed
public class NegativeBGAreaChart<X, Y> extends AreaChart<X, Y> {
    protected Map<Series<X, Y>, DoubleProperty> shadowSeriesYMultiplierMap = new HashMap<>();

    public NegativeBGAreaChart(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>> observableArrayList());
    }
    public NegativeBGAreaChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
    }

    @Override
    protected void seriesAdded(Series<X, Y> series, int seriesIndex) {
        DoubleProperty seriesYAnimMultiplier = new SimpleDoubleProperty(this, "seriesYMultiplier");
        shadowSeriesYMultiplierMap.put(series, seriesYAnimMultiplier);
        super.seriesAdded(series, seriesIndex);
    }

    @Override
    protected void seriesRemoved(final Series<X, Y> series) {
        shadowSeriesYMultiplierMap.remove(series);
        super.seriesRemoved(series);
    }

    @Override
    protected void layoutPlotChildren() {
//          super.layoutPlotChildren();
        try {
            List<LineTo> constructedPath = new ArrayList<>(getDataSize());
            for (int seriesIndex = 0; seriesIndex < getDataSize(); seriesIndex++) {
                Series<X, Y> series = getData().get(seriesIndex);
                DoubleProperty seriesYAnimMultiplier = shadowSeriesYMultiplierMap.get(series);
                double lastX = 0;
                final ObservableList<Node> children = ((Group) series.getNode()).getChildren();
                ObservableList<PathElement> seriesLine = ((Path) children.get(1)).getElements();
                ObservableList<PathElement> fillPath = ((Path) children.get(0)).getElements();
                seriesLine.clear();
                fillPath.clear();
                constructedPath.clear();
                for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext();) {
                    Data<X, Y> item = it.next();
                    double x = getXAxis().getDisplayPosition(item.getXValue());// FIXME: here should be used item.getCurrentX()
                    double y = getYAxis().getDisplayPosition(getYAxis().toRealValue(getYAxis().toNumericValue(item.getYValue())));// FIXME: here should be used item.getCurrentY()
                    constructedPath.add(new LineTo(x, y));
                    if (Double.isNaN(x) || Double.isNaN(y)) {
                        continue;
                    }
                    lastX = x;
                    Node symbol = item.getNode();
                    if (symbol != null) {
                        final double w = symbol.prefWidth(-1);
                        final double h = symbol.prefHeight(-1);
                        symbol.resizeRelocate(x - (w / 2), y - (h / 2), w, h);
                    }
                }

                if (!constructedPath.isEmpty()) {
                    Collections.sort(constructedPath, (e1, e2) -> Double.compare(e1.getX(), e2.getX()));
                    LineTo first = constructedPath.get(0);

                    seriesLine.add(new MoveTo(first.getX(), first.getY()));
                    fillPath.add(new MoveTo(first.getX(), getYAxis().getHeight()));

                    seriesLine.addAll(constructedPath);
                    fillPath.addAll(constructedPath);
                    fillPath.add(new LineTo(lastX, getYAxis().getHeight()));
                    fillPath.add(new ClosePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDataSize() {
        final ObservableList<Series<X, Y>> data = getData();
        return (data != null) ? data.size() : 0;
    }

}