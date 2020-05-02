package fr.javafx.scene.chart.axis;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Dimension2D;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;
import javafx.util.StringConverter;

import fr.javafx.scene.chart.XY;

public abstract class XYValueAxis extends ValueAxis<Number> implements XY.Axis<Number> {
    private static final int RANGE_ANIMATION_DURATION_MS = 700;
    
    private final Timeline         					animator           = new Timeline();

    ObjectProperty<XY.Axis.Ticks.UnitSupplier> 		tickUnitSupplierProperty = new SimpleObjectProperty<XY.Axis.Ticks.UnitSupplier>();
    ObjectProperty<XY.Axis.Ticks.Formatter<Number>> tickFormatterProperty    = new SimpleObjectProperty<XY.Axis.Ticks.Formatter<Number>>();
    private final XY.Axis.Ticks.Formatter<Number> 	defaultFormatter         = XY.Axis.Ticks.defaultFormatter();

    private final DoubleProperty   					scaleBinding       = new SimpleDoubleProperty(this, "scaleBinding", getScale()) { @Override protected void invalidated() { setScale(get()); } };
    private final BooleanProperty  					autoRangeRounding  = new SimpleBooleanProperty(true);

    protected XYValueAxis() {
        bindToBounds();
    }
    protected XYValueAxis(double lowerBound, double upperBound) {
        this(null, lowerBound, upperBound);
    }
    protected XYValueAxis(String axisLabel, double lowerBound, double upperBound) {
        super(lowerBound, upperBound);
        setLabel(axisLabel);
        bindToBounds();
    }

    public void 											setAutoRangeRounding(boolean round) {
    	autoRangeRounding.set(round);
    }
    public boolean 											isAutoRangeRounding() {
        return autoRangeRounding.get();
    }
    public BooleanProperty 									autoRangeRoundingProperty() {
        return autoRangeRounding;
    }

	public void 											setTickUnitSupplier(XY.Axis.Ticks.UnitSupplier _axisTickUnitSupplier) {
		tickUnitSupplierProperty.set( _axisTickUnitSupplier );
	}
	public XY.Axis.Ticks.UnitSupplier 						getTickUnitSupplier() {
		return tickUnitSupplierProperty.get();
	}
	@Override
	public ObjectProperty<XY.Axis.Ticks.UnitSupplier> 		tickUnitSupplierProperty() {
		return tickUnitSupplierProperty;
	}
	
	public void 											setTickFormatter(XY.Axis.Ticks.Formatter<Number> _axisTickFormatter) {
		tickFormatterProperty.set( _axisTickFormatter );
	}
	public XY.Axis.Ticks.Formatter<Number> 					getTickFormatter() {
		return tickFormatterProperty.get();
	}
	@Override
	public ObjectProperty<XY.Axis.Ticks.Formatter<Number>> 	tickFormatterProperty() {
		return tickFormatterProperty;
	}

    @Override
    protected void 											setRange(Object rangeObj, boolean animate) {
        XY.Axis.Range range = (XY.Axis.Range) rangeObj;
//        currentTickFormat.set(range.tickFormat());
        double oldLowerBound = getLowerBound();
        if (getLowerBound() != range.lowerBound()) {
            setLowerBound(range.lowerBound());
        }
        if (getUpperBound() != range.upperBound()) {
            setUpperBound(range.upperBound());
        }

        if (animate) {
            animator.stop();
            animator.getKeyFrames()
                    .setAll(new KeyFrame(Duration.ZERO, new KeyValue(currentLowerBound, oldLowerBound),
                            new KeyValue(scaleBinding, getScale())),
                            new KeyFrame(Duration.millis(RANGE_ANIMATION_DURATION_MS),
                                    new KeyValue(currentLowerBound, range.lowerBound()),
                                    new KeyValue(scaleBinding, range.scale())));
            animator.play();
        } else {
            currentLowerBound.set(range.lowerBound());
            setScale(range.scale());
        }
    }
    @Override
    protected XY.Axis.Range 								getRange() {
        return XY.Axis.Range.of(getLowerBound(), getUpperBound(), getScale(), ""/*urrentTickFormat.get()*/);
    }

    @Override
    protected String 										getTickMarkLabel(Number value) {
        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null) {
            formatter = defaultFormatter;
        }
        return formatter.toString(value);
    }

    @Override
    protected Dimension2D 									measureTickMarkSize(Number value, Object _range) {
        String labelText;

    	if(_range instanceof XY.Axis.Range range) {
    		String tickFormat = range.tickFormat();
    		
    		// TBC ...
    	}

        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null)
            formatter = defaultFormatter;

    	if(formatter instanceof XY.Axis.Ticks.Formatter ) {
        	XY.Axis.Ticks.Formatter<Number>
        	ticksFormatter = (XY.Axis.Ticks.Formatter<Number>) formatter;

            labelText = ticksFormatter.toString(value);
        } else
            labelText = formatter.toString(value);

        return measureTickMarkLabelSize(labelText, getTickLabelRotation());
    }

    private void 											bindToBounds() {
        ChangeListener<Number> rangeUpdater = (obj, oldValue, newValue) -> {
            if (!isAutoRanging()) {
                if (getLowerBound() <= getUpperBound()) {
                    setRange(computeRange(), false);
                } else {
                    throw new IllegalArgumentException("lowerBound [" + getLowerBound() + "] must not be greater than upperBound [" + getUpperBound() + "]");
                }
            }
        };

        lowerBoundProperty().addListener(rangeUpdater);
        upperBoundProperty().addListener(rangeUpdater);
    }

    private XY.Axis.Range 									computeRange() {
        if (getSide() == null) {
            return getRange();
        }
        double length = getSide().isVertical() ? getHeight() : getWidth();
        double labelSize = getTickLabelFont().getSize() * 2;
        return computeRange(getLowerBound(), getUpperBound(), length, labelSize);
    }
    protected abstract XY.Axis.Range 						computeRange(double minValue, double maxValue, double axisLength, double labelSize);

}