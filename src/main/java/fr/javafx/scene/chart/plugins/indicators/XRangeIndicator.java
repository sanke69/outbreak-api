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
