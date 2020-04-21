package fr.javafx.scene.control.chart.axis;

import java.util.ArrayList;
import java.util.List;

import fr.javafx.scene.control.chart.XYAxis;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;

public class NumberAxis<T extends Number> extends ValueAxis<T> implements XYAxis<T> {

	private static final double[] dividers = new double[] { 1.0, 2.5, 5.0 };

	private static final int numMinorTicks = 3;

	private final Timeline animationTimeline = new Timeline();
	private final WritableValue<Double> scaleValue = new WritableValue<Double>() {
		@Override
		public Double getValue() {
			return getScale();
		}

		@Override
		public void setValue( Double value ) {
			setScale( value );
		}
	};

	private TickFormatter<T> axisTickFormatter = XYAxis.TickFormatter.defaultNumberFormat();
	private List<Number>     minorTicks;

	private DoubleProperty   autoRangePadding  = new SimpleDoubleProperty( 0.1 );
	private BooleanProperty  forceZeroInRange  = new SimpleBooleanProperty( false );

	public NumberAxis() {
		super();
	}
	public NumberAxis( double lowerBound, double upperBound ) {
		super( lowerBound, upperBound );
	}

	public TickFormatter<?> getAxisTickFormatter() {
		return axisTickFormatter;
	}

	public void 			setAxisTickFormatter( TickFormatter<T> _axisTickFormatter ) {
		axisTickFormatter = _axisTickFormatter;
	}

	public void 			setAutoRangePadding( double _autoRangePadding ) {
		autoRangePadding.set( _autoRangePadding );
	}
	public double 			getAutoRangePadding() {
		return autoRangePadding.get();
	}
	public DoubleProperty 	autoRangePaddingProperty() {
		return autoRangePadding;
	}

	public void 			setForceZeroInRange( boolean forceZeroInRange ) {
		this.forceZeroInRange.set( forceZeroInRange );
	}
	public boolean 			isForceZeroInRange() {
		return forceZeroInRange.get();
	}
	public BooleanProperty 	forceZeroInRangeProperty() {
		return forceZeroInRange;
	}

	@Override
	protected void 			setRange( Object range, boolean animate ) {
		Range rangeVal = (Range) range;
//		System.out.format( "StableTicksAxis.setRange (%s, %s)%n", range, animate );
		if ( animate ) {
			animationTimeline.stop();
			ObservableList<KeyFrame> keyFrames = animationTimeline.getKeyFrames();
			keyFrames.setAll(
					new KeyFrame( Duration.ZERO,
					              new KeyValue( currentLowerBound, getLowerBound() ),
					              new KeyValue( scaleValue, getScale() ) ),
					new KeyFrame( Duration.millis( 750 ),
					              new KeyValue( currentLowerBound, rangeVal.low ),
					              new KeyValue( scaleValue, rangeVal.scale ) ) );
			animationTimeline.play();

		} else {
			currentLowerBound.set( rangeVal.low );
			setScale( rangeVal.scale );
		}
		setLowerBound( rangeVal.low );
		setUpperBound( rangeVal.high );

		axisTickFormatter.setRange( rangeVal.low, rangeVal.high, rangeVal.tickSpacing );
	}
	@Override
	protected Range 		getRange() {
		Range ret = getRange( getLowerBound(), getUpperBound() );
//		System.out.println( "StableTicksAxis.getRange = " + ret );
		return ret;
	}
	@Override
	protected Range 		autoRange( double minValue, double maxValue, double length, double labelSize ) {
//		System.out.printf( "autoRange(%f, %f, %f, %f)\n", minValue, maxValue, length, labelSize );
		//By dweil: if the range is very small, display it like a flat line, the scaling doesn't work very well at these
		//values. 1e-300 was chosen arbitrarily.
		if ( Math.abs(minValue - maxValue) < 1e-300) {
			//Normally this is the case for all points with the same value
			minValue = minValue - 1;
			maxValue = maxValue + 1;

		} else {
			//Add padding
			double delta = maxValue - minValue;
			double paddedMin = minValue - delta * autoRangePadding.get();
			//If we've crossed the 0 line, clamp to 0.
			//noinspection FloatingPointEquality
			if ( Math.signum( paddedMin ) != Math.signum( minValue ) )
				paddedMin = 0.0;

			double paddedMax = maxValue + delta * autoRangePadding.get();
			//If we've crossed the 0 line, clamp to 0.
			//noinspection FloatingPointEquality
			if ( Math.signum( paddedMax ) != Math.signum( maxValue ) )
				paddedMax = 0.0;

			minValue = paddedMin;
			maxValue = paddedMax;
		}

		//Handle forcing zero into the range
		if ( forceZeroInRange.get() ) {
			if ( minValue < 0 && maxValue < 0 ) {
				maxValue = 0;
				minValue -= -minValue * autoRangePadding.get();
			} else if ( minValue > 0 && maxValue > 0 ) {
				minValue = 0;
				maxValue += maxValue * autoRangePadding.get();
			}
		}

		Range ret = getRange( minValue, maxValue );
//		System.out.printf( " = %s%n", ret );
		return ret;
	}

	private Range 			getRange( double minValue, double maxValue ) {
		double length = getLength();
		double delta = maxValue - minValue;
		double scale = calculateNewScale( length, minValue, maxValue );

		int maxTicks = Math.max( 1, (int) ( length / getLabelSize() ) );

		Range ret;
		ret = new Range( minValue, maxValue, calculateTickSpacing( delta, maxTicks ), scale );
		return ret;
	}

	@Override
	protected List<T> 		calculateTickValues( double length, Object range ) {
		Range rangeVal = (Range) range;
//		System.out.format( "StableTicksAxis.calculateTickValues (length=%f, range=%s)",
//		                   length, rangeVal );
		//Use floor so we start generating ticks before the axis starts -- this is really only relevant
		//because of the minor ticks before the first visible major tick. We'll generate a first
		//invisible major tick but the ValueAxis seems to filter it out.
		double firstTick = Math.floor( rangeVal.low / rangeVal.tickSpacing ) * rangeVal.tickSpacing;
		//Generate one more tick than we expect, for "overlap" to get minor ticks on both sides of the
		//first and last major tick.
		int numTicks = (int) (rangeVal.getDelta() / rangeVal.tickSpacing) + 1;
		List<Number> ret = new ArrayList<Number>( numTicks + 1 );
		minorTicks = new ArrayList<Number>( ( numTicks + 2 ) * numMinorTicks );
		double minorTickSpacing = rangeVal.tickSpacing / ( numMinorTicks + 1 );
		for ( int i = 0; i <= numTicks; ++i ) {
			double majorTick = firstTick + rangeVal.tickSpacing * i;
			ret.add( majorTick );
			for ( int j = 1; j <= numMinorTicks; ++j ) {
				minorTicks.add( majorTick + minorTickSpacing * j );
			}
		}
//		System.out.printf( " = %s%n", ret );
		return (List<T>) ret;
	}
	@Override
	protected List<T> 		calculateMinorTickMarks() {
//		System.out.println( "StableTicksAxis.calculateMinorTickMarks" );
		return (List<T>) minorTicks;
	}

	@Override
	protected String 		getTickMarkLabel( Number number ) {
		return axisTickFormatter.format( number );
	}

	private double 			getLength() {
		if ( getSide().isHorizontal() )
			return getWidth();
		else
			return getHeight();
	}
	private double 			getLabelSize() {
		Dimension2D dim = measureTickMarkLabelSize( "-888.88E-88", getTickLabelRotation() );
		if ( getSide().isHorizontal() ) {
			return dim.getWidth();
		} else {
			return dim.getHeight();
		}
	}

	public static double 	calculateTickSpacing( double delta, int maxTicks ) {
		if ( delta == 0.0 )
			return 0.0;
		if ( delta <= 0.0 )
			throw new IllegalArgumentException( "delta must be positive" );
		if ( maxTicks < 1 )
			throw new IllegalArgumentException( "must be at least one tick" );

		//The factor will be close to the log10, this just optimizes the search
		int factor = (int) Math.log10( delta );
		int divider = 0;
		double numTicks = delta / ( dividers[divider] * Math.pow( 10, factor ) );

		//We don't have enough ticks, so increase ticks until we're over the limit, then back off once.
		if ( numTicks < maxTicks ) {
			while ( numTicks < maxTicks ) {
				//Move up
				--divider;
				if ( divider < 0 ) {
					--factor;
					divider = dividers.length - 1;
				}

				numTicks = delta / ( dividers[divider] * Math.pow( 10, factor ) );
			}

			//Now back off once unless we hit exactly
			//noinspection FloatingPointEquality
			if ( numTicks != maxTicks ) {
				++divider;
				if ( divider >= dividers.length ) {
					++factor;
					divider = 0;
				}
			}
		} else {
			//We have too many ticks or exactly max, so decrease until we're just under (or at) the limit.
			while ( numTicks > maxTicks ) {
				++divider;
				if ( divider >= dividers.length ) {
					++factor;
					divider = 0;
				}

				numTicks = delta / ( dividers[divider] * Math.pow( 10, factor ) );
			}
		}

//		System.out.printf( "calculateTickSpacing( %f, %d ) = %f%n", delta, maxTicks, dividers[divider] * Math.pow( 10, factor ) );

		return dividers[divider] * Math.pow( 10, factor );
	}

	private static class Range {
		public final double low;
		public final double high;
		public final double tickSpacing;
		public final double scale;

		private Range( double low, double high, double tickSpacing, double scale ) {
			this.low = low;
			this.high = high;
			this.tickSpacing = tickSpacing;
			this.scale = scale;
		}

		public double getDelta() {
			return high - low;
		}

		@Override
		public String toString() {
			return "Range{" +
			       "low=" + low +
			       ", high=" + high +
			       ", tickSpacing=" + tickSpacing +
			       ", scale=" + scale +
			       '}';
		}
	}

}
