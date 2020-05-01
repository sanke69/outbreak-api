package fr.javafx.scene.control.chart.plugins.indicators;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.chart.ValueAxis;

import fr.javafx.scene.control.chart.XYChartUtils;
import fr.javafx.scene.control.chart.XYChartPane;

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
