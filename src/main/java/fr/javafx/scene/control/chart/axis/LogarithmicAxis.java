package fr.javafx.scene.control.chart.axis;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.ValueAxis;

public final class LogarithmicAxis extends ValueAxis<Number> {
    private final DoubleProperty currentUpperBound = new SimpleDoubleProperty();

    public LogarithmicAxis() {
        super();
    }
    public LogarithmicAxis(double lowerBound, double upperBound) {
        super(lowerBound, upperBound);
    }

    @Override
    protected void 			setRange(Object range, boolean animate) {
        double   lowerBound    = ((double[]) range)[0];
        double   upperBound    = ((double[]) range)[1];
        double[] r             = (double[]) range;
        double   oldLowerBound = getLowerBound();
        double   oldUpperBound = getUpperBound();
        double   lower         = r[0];
        double   upper         = r[1];

        setLowerBound(lower);
        setUpperBound(upper);

        currentLowerBound.set(lowerBound);
        currentUpperBound.set(upperBound);
    }
    @Override
    protected double[] 		getRange() {
        return new double[] { getLowerBound(), getUpperBound() };
    }
    @Override
    protected Object 		autoRange(double minValue, double maxValue, double length, double labelSize) {
        if (isAutoRanging()) {
            return new double[]{minValue, maxValue};
        } else {
            return getRange();
        }
    }

    @Override
    public double 			getDisplayPosition(Number value) {

        // Consider this axis, with lower bound 1 and upper bound 1000:
        // |1----------10---------100--------1000|
        //
        // Lets assume our value is 10. First, we want to get the relative position of the value between lower and upper bound.
        // Therefore we get the logarithmic value of 10, which is 1 and subtract the logarithmic value of 1 (lower bound), which is 0.
        // |1----------10---------100--------1000|
        //  ^^^^^^^^^^^^
        //
        // Then we divide this value by total by the total length of the axis, which is log(1000) (==3) minus log(1) (==0), which is 3.
        // |1----------10---------100--------1000|
        //  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        //
        // We now know, that the value 10 lies on 33% of the axis.
        // To get the actual value, we only need to multiply the percent value with the absolute length of the axis.
        //
        // log(a) - log(b) == log(a/b)
        // log(a/b) is faster in Java, so we use that.

        // Get the logarithmic difference between the value and the lower bound.
        double diffValue = Math.log10(value.doubleValue() / currentLowerBound.get());

        // Get the logarithmic difference between lower and upper bound.
        double diffTotal = Math.log10(currentUpperBound.get() / currentLowerBound.get());

        double percent = diffValue / diffTotal;

        if (getSide().isHorizontal()) {
            return percent * getWidth();
        } else {
            // Invert for the vertical axis.
            return (1 - percent) * getHeight();
        }
    }
    @Override
    public Number 			getValueForDisplay(double displayPosition) {
        if (getSide().isHorizontal()) {
            return Math.pow(10, displayPosition / getWidth() * Math.log10(currentUpperBound.get() / currentLowerBound.get())) * currentLowerBound.get();
        } else {
            return Math.pow(10, ((displayPosition / getHeight()) - 1) * -Math.log10(currentUpperBound.get() / currentLowerBound.get())) * currentLowerBound.get();
        }
    }

    @Override
    protected List<Number> 	calculateTickValues(double length, Object range) {
        List<Number> tickValues = new ArrayList<Number>();

        final double[] rangeProps = (double[]) range;
        final double lowerBound = rangeProps[0];
        final double upperBound = rangeProps[1];
        double logLowerBound = Math.log10(lowerBound);
        double logUpperBound = Math.log10(upperBound);

        // Always start with a "even" integer. That's why we floor the start value.
        // Otherwise the scale would contain odd values, rather then normal 1, 2, 3, 4, ... values.
        for (double major = Math.floor(logLowerBound); major < logUpperBound; major++) {
            double p = Math.pow(10, major);
            for (double j = 1; j < 10; j++) {
                tickValues.add(j * p);
            }
        }
        return tickValues;
    }
    @Override
    protected List<Number> 	calculateMinorTickMarks() {
        final List<Number> minorTickMarks = new ArrayList<Number>();
        double step = 1.0 / getMinorTickCount();
        double logLowerBound = Math.log10(getLowerBound());
        double logUpperBound = Math.log10(getUpperBound());

        for (double major = Math.floor(logLowerBound); major < logUpperBound; major++) {
            for (double j = 0; j < 10; j += step) {
                minorTickMarks.add(j * Math.pow(10, major));
            }
        }

        return minorTickMarks;
    }

    @Override
    protected String 		getTickMarkLabel(Number value) {
        return NumberFormat.getInstance().format(value);
    }

    @Override
    protected void 			layoutChildren() {
        if (!isAutoRanging()) {
            currentLowerBound.set(getLowerBound());
            currentUpperBound.set(getUpperBound());
        }
        super.layoutChildren();
    }

}