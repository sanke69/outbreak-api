package fr.javafx.scene.chart.axis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Dimension2D;

import fr.javafx.scene.chart.XY;

public final class NumericAxis extends XYValueAxis {

    private static final int 						TICK_MARK_GAP = 6;
    private static final double 					NEXT_TICK_UNIT_FACTOR = 1.01;
    private static final int 						MAX_TICK_COUNT = 20;
    private static final XY.Axis.Ticks.UnitSupplier DEFAULT_TICK_UNIT_SUPPLIER = XY.Axis.Ticks.defaultUnitSupplier();
    private static final int 						DEFAULT_RANGE_LENGTH = 2;
    private static final double 					DEFAULT_RANGE_PADDING = 0.1;

    private final BooleanProperty 								forceZeroInRange = new SimpleBooleanProperty(this, "forceZeroInRange", true) {
        @Override
        protected void invalidated() {
            if (isAutoRanging()) {
                requestAxisLayout();
                invalidateRange();
            }
        }
    };
    private final DoubleProperty 								autoRangePadding = new SimpleDoubleProperty(0);

    private final DoubleProperty 								tickUnit         = new SimpleDoubleProperty(this, "tickUnit", 5d) {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }
    };

    private static record NumericAxisRange(double lowerBound, double upperBound, double scale, String tickFormat, double tickSpacing) implements XY.Axis.Range {
    	NumericAxisRange(XY.Axis.Range range, double tickSpacing) {
            this(range.lowerBound(), range.upperBound(), range.scale(), range.tickFormat(), tickSpacing);
        }
    	
    }

    public NumericAxis() {
        //
    }

    public NumericAxis(double lowerBound, double upperBound, double tickUnit) {
        this(null, lowerBound, upperBound, tickUnit);
    }
    public NumericAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
        super(axisLabel, lowerBound, upperBound);
        setTickUnit(tickUnit);
    }

    public void 										setForceZeroInRange(boolean value) {
        forceZeroInRange.setValue(value);
    }
    public boolean 										isForceZeroInRange() {
        return forceZeroInRange.getValue();
    }
    public BooleanProperty 								forceZeroInRangeProperty() {
        return forceZeroInRange;
    }
    public void 										setAutoRangePadding(double padding) {
        autoRangePaddingProperty().set(padding);
    }
    public double 										getAutoRangePadding() {
        return autoRangePaddingProperty().get();
    }
    public DoubleProperty 								autoRangePaddingProperty() {
        return autoRangePadding;
    }

    public void 										setTickUnit(double unit) {
        tickUnitProperty().set(unit);
    }
    public double 										getTickUnit() {
        return tickUnitProperty().get();
    }
    public DoubleProperty 								tickUnitProperty() {
        return tickUnit;
    }

    @Override
    protected void 										setRange(Object range, boolean animate) {
        super.setRange(range, animate);
        setTickUnit(((NumericAxisRange) range).tickSpacing());
    }
    @Override
    protected XY.Axis.Range 							getRange() {
        return new NumericAxisRange(super.getRange(), getTickUnit());
    }
    @Override
    protected Object 									autoRange(double minValue, double maxValue, double length, double labelSize) {
        double min = minValue > 0 && isForceZeroInRange() ? 0 : minValue;
        double max = maxValue < 0 && isForceZeroInRange() ? 0 : maxValue;
        double padding = getEffectiveRange(min, max) * getAutoRangePadding();
        double paddedMin = clampBoundToZero(min - padding, min);
        double paddedMax = clampBoundToZero(max + padding, max);

        return computeRange(paddedMin, paddedMax, length, labelSize);
    }
    @Override
    protected XY.Axis.Range 							computeRange(double min, double max, double axisLength, double labelSize) {
        double minValue = min;
        double maxValue = max;
        if (max - min == 0) {
            double padding = getAutoRangePadding() == 0 ? DEFAULT_RANGE_PADDING : getAutoRangePadding();
            double paddedRange = getEffectiveRange(min, max) * padding;
            minValue = min - paddedRange / 2;
            maxValue = max + paddedRange / 2;
        }
        return computeRangeImpl(minValue, maxValue, axisLength, labelSize);
    }
    private NumericAxisRange 							computeRangeImpl(double min, double max, double axisLength, double labelSize) {
        final int numOfFittingLabels = (int) Math.floor(axisLength / labelSize);
        final int numOfTickMarks = Math.max(Math.min(numOfFittingLabels, MAX_TICK_COUNT), 2);

        double rawTickUnit = (max - min) / numOfTickMarks;
        double prevTickUnitRounded;
        double tickUnitRounded = Double.MIN_VALUE;
        double minRounded = min;
        double maxRounded = max;
        int ticksCount;
        double reqLength;
        String tickNumFormat = "#.0#";

        do {
            if (Double.isNaN(rawTickUnit))
                throw new IllegalArgumentException( "Can't calculate axis range: data contains NaN value");

            prevTickUnitRounded = tickUnitRounded;
            tickUnitRounded = computeTickUnit(rawTickUnit);
            if (tickUnitRounded <= prevTickUnitRounded) {
                break;
            }
            tickNumFormat = computeTickNumFormat(tickUnitRounded);

            double firstMajorTick;
            if (isAutoRanging() && isAutoRangeRounding()) {
                minRounded = Math.floor(min / tickUnitRounded) * tickUnitRounded;
                maxRounded = Math.ceil(max / tickUnitRounded) * tickUnitRounded;
                firstMajorTick = minRounded;
            } else {
                firstMajorTick = Math.ceil(min / tickUnitRounded) * tickUnitRounded;
            }

            ticksCount = 0;
            double maxReqTickGap = 0;
            double halfOfLastTickSize = 0;
            for (double major = firstMajorTick; major <= maxRounded; major += tickUnitRounded, ticksCount++) {
                Dimension2D size         = super.measureTickMarkSize(major, null); // tickNumFormat
                double      tickMarkSize = getSide().isVertical() ? size.getHeight() : size.getWidth();

                if (major == firstMajorTick) {
                    halfOfLastTickSize = tickMarkSize / 2;
                } else {
                    maxReqTickGap = Math.max(maxReqTickGap, halfOfLastTickSize + TICK_MARK_GAP + (tickMarkSize / 2));
                }
            }
            reqLength = (ticksCount - 1) * maxReqTickGap;
            rawTickUnit = tickUnitRounded * NEXT_TICK_UNIT_FACTOR;
        } while (numOfTickMarks > 2 && (reqLength > axisLength || ticksCount > MAX_TICK_COUNT));

        double newScale = calculateNewScale(axisLength, minRounded, maxRounded);
        return new NumericAxisRange(minRounded, maxRounded, newScale, tickNumFormat, tickUnitRounded);
    }

    @Override
    protected List<Number> 								calculateTickValues(double axisLength, Object range) {
        NumericAxisRange rangeImpl = (NumericAxisRange) range;
        if (rangeImpl.lowerBound() == rangeImpl.upperBound() || rangeImpl.tickSpacing() <= 0) {
            return Arrays.asList(rangeImpl.lowerBound());
        }
        List<Number> tickValues = new ArrayList<>();
        double firstTick = computeFistMajorTick(rangeImpl.lowerBound(), rangeImpl.tickSpacing());
        for (double major = firstTick; major <= rangeImpl.upperBound(); major += rangeImpl.tickSpacing()) {
            tickValues.add(major);
        }
        return tickValues;
    }
    @Override
    protected List<Number> 								calculateMinorTickMarks() {
        if (getMinorTickCount() == 0 || getTickUnit() == 0) {
            return Collections.emptyList();
        }

        List<Number> minorTickMarks = new ArrayList<>();
        final double lowerBound = getLowerBound();
        final double upperBound = getUpperBound();
        final double majorUnit = getTickUnit();

        final double firstMajorTick = computeFistMajorTick(lowerBound, majorUnit);
        final double minorUnit = majorUnit / getMinorTickCount();

        for (double majorTick = firstMajorTick - majorUnit; majorTick < upperBound; majorTick += majorUnit) {
            double nextMajorTick = majorTick + majorUnit;
            for (double minorTick = majorTick + minorUnit; minorTick < nextMajorTick; minorTick += minorUnit) {
                if (minorTick >= lowerBound && minorTick <= upperBound) {
                    minorTickMarks.add(minorTick);
                }
            }
        }
        return minorTickMarks;
    }

    private static double 								computeFistMajorTick(double lowerBound, double tickUnit) {
        return Math.ceil(lowerBound / tickUnit) * tickUnit;
    }
    private double 										computeTickUnit(double rawTickUnit) {
    	XY.Axis.Ticks.UnitSupplier unitSupplier = getTickUnitSupplier();
        if (unitSupplier == null)
            unitSupplier = DEFAULT_TICK_UNIT_SUPPLIER;

        double majorUnit = unitSupplier.computeTickUnit(rawTickUnit);
        if (majorUnit <= 0)
            throw new IllegalArgumentException("The " + unitSupplier.getClass().getName() + " computed illegal unit value [" + majorUnit + "] for argument " + rawTickUnit);

        return majorUnit;
    }

    private static double 								getEffectiveRange(double min, double max) {
        double effectiveRange = max - min;
        if (effectiveRange == 0)
            effectiveRange = (min == 0) ? DEFAULT_RANGE_LENGTH : Math.abs(min);

        return effectiveRange;
    }
    private static double 								clampBoundToZero(double paddedBound, double bound) {
        if ((paddedBound < 0 && bound >= 0) || (paddedBound > 0 && bound <= 0))
            return 0;

        return paddedBound;
    }
    @Deprecated 
    private static String 								computeTickNumFormat(double tickUnit) {
        int log10 = (int) Math.floor(Math.log10(tickUnit));
        boolean unitHasFraction = Math.rint(tickUnit) != tickUnit;
        if (log10 >= 1 && !unitHasFraction) {
            return "#,##0";
        }
        int fractDigitsCount = unitHasFraction ? Math.abs(log10) + 1 : Math.abs(log10);
        StringBuilder format = new StringBuilder("0");
        if (fractDigitsCount > 0) {
            format.append('.');
        }
        for (int i = 0; i < fractDigitsCount; i++) {
            format.append('0');
        }
        return format.toString();
    }

}