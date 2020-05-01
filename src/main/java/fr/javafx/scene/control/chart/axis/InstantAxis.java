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
package fr.javafx.scene.control.chart.axis;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.chart.Axis;
import javafx.util.StringConverter;

import fr.java.time.Time;
import fr.java.time.TimeUnit;

import fr.javafx.scene.control.chart.XY;

public final class InstantAxis extends Axis<Instant> {

	public static Axis<Number> forLong() {
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		format.setTimeZone( TimeZone.getTimeZone( "GMT" ) );

		NumericAxis xAxis = new NumericAxis();
//		xAxis.setAxisTickFormatter(XY.Axis.Ticks.newNumberFormat( d -> format.format(d) ) );
		return xAxis;
	}
	
	enum Interval {
		DECADE		(Period.ofYears(10),     TimeUnit.DECADE),
        YEAR		(Period.ofYears(1),      TimeUnit.YEAR),
        MONTH_6		(Period.ofMonths(6),     TimeUnit.MONTH),
        MONTH_3		(Period.ofMonths(3),     TimeUnit.MONTH),
        MONTH_1		(Period.ofMonths(1),     TimeUnit.MONTH),
        WEEK		(Period.ofDays(7),       TimeUnit.WEEK),
        DAY			(Period.ofDays(1),       TimeUnit.DAY),
        HOUR_12		(Duration.ofHours(12),   TimeUnit.HOUR),
        HOUR_6		(Duration.ofHours(6),    TimeUnit.HOUR),
        HOUR_3		(Duration.ofHours(3),    TimeUnit.HOUR),
        HOUR_1		(Duration.ofHours(1),    TimeUnit.HOUR),
        MINUTE_30	(Duration.ofHours(1),    TimeUnit.MINUTE),
        MINUTE_15	(Duration.ofHours(1),    TimeUnit.MINUTE),
        MINUTE_5	(Duration.ofHours(1),    TimeUnit.MINUTE),
        MINUTE_1	(Duration.ofHours(1),    TimeUnit.MINUTE),
        SECOND_30	(Duration.ofSeconds(30), TimeUnit.SECOND),
        SECOND_15	(Duration.ofSeconds(15), TimeUnit.SECOND),
        SECOND_5	(Duration.ofSeconds(5),  TimeUnit.SECOND),
        SECOND_1	(Duration.ofSeconds(1),  TimeUnit.SECOND),
        MILLISECOND	(Duration.ofMillis(1),   TimeUnit.MINUTE);

		Duration duration;
		Period   period;
		TimeUnit unit;

		private Interval(Duration _duration, TimeUnit _tu) {
			duration = _duration;
			period   = null;
			unit     = _tu;
		}
		private Interval(Period _period, TimeUnit _tu) {
			duration = null;
			period   = _period;
			unit     = _tu;
		}

	    public Instant 			addTo(Instant _instant) {
			return period != null ?
								Time.add(_instant, period)
								:
								Time.add(_instant, duration);
	    }

		
	}
	
    private final LongProperty      currentLowerBound = new SimpleLongProperty(this, "currentLowerBound");
    private ObjectProperty<Instant> lowerBound        = new ObjectPropertyBase<Instant>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return InstantAxis.this;
        }

        @Override
        public String getName() {
            return "lowerBound";
        }
    };
    private final LongProperty      currentUpperBound = new SimpleLongProperty(this, "currentUpperBound");
    private ObjectProperty<Instant> upperBound        = new ObjectPropertyBase<Instant>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return InstantAxis.this;
        }

        @Override
        public String getName() {
            return "upperBound";
        }
    };

    private Interval             	actualInterval;
    private Instant              	minDate, maxDate;

    private final ObjectProperty<StringConverter<Instant>> tickLabelFormatter = new ObjectPropertyBase<StringConverter<Instant>>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return InstantAxis.this;
        }

        @Override
        public String getName() {
            return "tickLabelFormatter";
        }
    };


    public InstantAxis() {
    	super();
    }
    public InstantAxis(Instant lowerBound, Instant upperBound) {
    	super();
        setAutoRanging(false);
        setLowerBound(lowerBound);
        setUpperBound(upperBound);
    }
    public InstantAxis(String axisLabel, Instant lowerBound, Instant upperBound) {
        this(lowerBound, upperBound);
        setLabel(axisLabel);
    }

    public final void 						setLowerBound(Instant date) {
        lowerBound.set(date);
    }
    public final Instant 					getLowerBound() {
        return lowerBound.get();
    }
    public final ObjectProperty<Instant> 	lowerBoundProperty() {
        return lowerBound;
    }

    public final void 						setUpperBound(Instant date) {
        upperBound.set(date);
    }
    public final Instant 					getUpperBound() {
        return upperBound.get();
    }
    public final ObjectProperty<Instant> 	upperBoundProperty() {
        return upperBound;
    }

    public final void 						setTickLabelFormatter(StringConverter<Instant> value) {
        tickLabelFormatter.setValue(value);
    }
    public final StringConverter<Instant> 	getTickLabelFormatter() {
        return tickLabelFormatter.getValue();
    }
    public final ObjectProperty<StringConverter<Instant>> tickLabelFormatterProperty() {
        return tickLabelFormatter;
    }

    public final TimeUnit					getCurrentTimeUnit() {
    	return actualInterval.unit;
    }

    @Override
    public void 							invalidateRange(List<Instant> list) {
        super.invalidateRange(list);

        Collections.sort(list);
        if(list.isEmpty()) {
            minDate = maxDate = Instant.EPOCH;
        } else if (list.size() == 1) {
            minDate = maxDate = list.get(0);
        } else if (list.size() > 1) {
            minDate = list.get(0);
            maxDate = list.get(list.size() - 1);
        }
    }

    @Override
    protected void 							setRange(Object range, boolean animating) {
        Object[] r     = (Object[]) range;
        Instant  lower = (Instant) r[0];
        Instant  upper = (Instant) r[1];

        setLowerBound(lower);
        setUpperBound(upper);

        currentLowerBound.set(getLowerBound().toEpochMilli());
        currentUpperBound.set(getUpperBound().toEpochMilli());
    }
    @Override
    protected Instant[] 					getRange() {
        return new Instant[] { getLowerBound(), getUpperBound() };
    }
    @Override
    protected Object 						autoRange(double length) {
        if (isAutoRanging()) {
            return new Object[]{minDate, maxDate};
        } else {
            if (getLowerBound() == null || getUpperBound() == null) {
                throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");
            }
            return getRange();
        }
    }

    @Override
    public double 							getZeroPosition() {
        return 0; //Instant.now().toEpochMilli();
    }

    @Override
    public double 							getDisplayPosition(Instant date) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();

        // Then get the difference from the actual date to the min date and divide it by the total difference.
        // We get a value between 0 and 1, if the date is within the min and max date.
        double d = (date.toEpochMilli() - currentLowerBound.get()) / diff;

        // Multiply this percent value with the range and add the zero offset.
        if (getSide().isVertical()) {
            return getHeight() - d * range + getZeroPosition();
        } else {
            return d * range + getZeroPosition();
        }
    }
    @Override
    public Instant 							getValueForDisplay(double displayPosition) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();

        if (getSide().isVertical()) {
            // displayPosition = getHeight() - ((date - lowerBound) / diff) * range + getZero
            // date = displayPosition - getZero - getHeight())/range * diff + lowerBound
            return Instant.ofEpochMilli((long) ((displayPosition - getZeroPosition() - getHeight()) / -range * diff + currentLowerBound.get()));
        } else {
            // displayPosition = ((date - lowerBound) / diff) * range + getZero
            // date = displayPosition - getZero)/range * diff + lowerBound
            return Instant.ofEpochMilli((long) ((displayPosition - getZeroPosition()) / range * diff + currentLowerBound.get()));
        }
    }

    @Override
    protected List<Instant> 				calculateTickValues(double _length, Object _range) {
    	Object[] range = (Object[]) _range;
        Instant  lower = (Instant) range[0];
        Instant  upper = (Instant) range[1];

        List<Instant> dateList = new ArrayList<Instant>();

        double averageTickGap = 100;
        double averageTicks   = _length / averageTickGap;

        List<Instant> previousDateList = new ArrayList<Instant>();
        Interval      previousInterval = Interval.values()[0];

        Instant value = null;
        for (Interval interval : Interval.values()) {
        	value = lower;

            dateList.clear();
            previousDateList.clear();
            actualInterval = interval;

            while(value.isBefore(upper) || value.equals(upper)) {
                dateList.add(value);
                value = interval.addTo(value);
            }

            if (dateList.size() > averageTicks) {
            	value = lower;
                // Recheck if the previous interval is better suited.
                while (value.isBefore(upper) || value.equals(upper)) {
                    previousDateList.add(value);
                    value = previousInterval.addTo(value);
                }
                break;
            }

            previousInterval = interval;
        }
        if (previousDateList.size() - averageTicks > averageTicks - dateList.size()) {
            dateList = previousDateList;
            actualInterval = previousInterval;
        }

        // At last add the upper bound.
        dateList.add(upper);

        List<Instant> evenDateList = makeDatesEven(dateList, value);
        // If there are at least three dates, check if the gap between the lower date and the second date is at least half the gap of the second and third date.
        // Do the same for the upper bound.
        // If gaps between dates are to small, remove one of them.
        // This can occur, e.g. if the lower bound is 25.12.2013 and years are shown. Then the next year shown would be 2014 (01.01.2014) which would be too narrow to 25.12.2013.
        if (evenDateList.size() > 2) {

        	Instant secondDate       = evenDateList.get(1);
            Instant thirdDate        = evenDateList.get(2);
            Instant lastDate         = evenDateList.get(dateList.size() - 2);
            Instant previousLastDate = evenDateList.get(dateList.size() - 3);

            // If the second date is too near by the lower bound, remove it.
            if (secondDate.toEpochMilli() - lower.toEpochMilli() < (thirdDate.toEpochMilli() - secondDate.toEpochMilli()) / 2) {
                evenDateList.remove(secondDate);
            }

            // If difference from the upper bound to the last date is less than the half of the difference of the previous two dates,
            // we better remove the last date, as it comes to close to the upper bound.
            if (upper.toEpochMilli() - lastDate.toEpochMilli() < (lastDate.toEpochMilli() - previousLastDate.toEpochMilli()) / 2) {
                evenDateList.remove(lastDate);
            }
        }

        return evenDateList;
    }

    @Override
    protected String 						getTickMarkLabel(Instant date) {
        StringConverter<Instant> converter = getTickLabelFormatter();
        if (converter != null)
            return converter.toString(date);

        DateTimeFormatter dateFormat;
        LocalDateTime ldt = Time.of(date);

        if (actualInterval.unit == TimeUnit.YEAR && ldt.getMonthValue() == 1 && ldt.getDayOfMonth() == 1) {
            dateFormat = DateTimeFormatter.ofPattern("uuuu");
        } else if (actualInterval.unit == TimeUnit.MONTH && ldt.getDayOfMonth() == 1) {
            dateFormat = DateTimeFormatter.ofPattern("uuuu-MMM");
        } else {
            switch (actualInterval.unit) {
            case WEEK        :
            case DAY         :
            default          :	dateFormat = DateTimeFormatter.ofPattern("d::MMM::uuuu"); break;
            case HOUR        :
            case MINUTE      :	dateFormat = DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm");  break;
            case SECOND      :	dateFormat = DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss"); break;
            case MILLISECOND :	dateFormat = DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss");   break;
            }
        }
        return dateFormat.format(ldt);
    }

    @Override
    public double 							toNumericValue(Instant date) {
        return date.toEpochMilli();
    }
    @Override
    public Instant 							toRealValue(double v) {
        return Instant.ofEpochMilli((long) v);
    }

    @Override
    public boolean 							isValueOnAxis(Instant date) {
        return date.toEpochMilli() > currentLowerBound.get() && date.toEpochMilli() < currentUpperBound.get();
    }

    @Override
    protected void 							layoutChildren() {
        if (!isAutoRanging()) {
            currentLowerBound.set(getLowerBound().toEpochMilli());
            currentUpperBound.set(getUpperBound().toEpochMilli());
        }
        super.layoutChildren();
    }

    private List<Instant> makeDatesEven(List<Instant> dates, Instant calendar) {
        if (dates.size() > 2) {
            List<Instant> evenDates = new ArrayList<Instant>();

            for (int i = 0; i < dates.size(); i++) {
            	Instant date = dates.get(i);

                switch (actualInterval.unit) {
                    case YEAR:
                        if (i != 0 && i != dates.size() - 1)
                        	date = Time.asStartOfYear(date);
                        break;
                    case MONTH:
                        if (i != 0 && i != dates.size() - 1)
                        	date = Time.asStartOfMonth(date);
                        break;
                    case WEEK:
                    	date = Time.asStartOfWeek(date);
                        break;
                    case DAY:
                    	date = Time.asStartOfDay(date);
                        break;
                    case HOUR:
                    	date = Time.asStartOfHour(date);
                        break;
                    case MINUTE:
                        if (i != 0 && i != dates.size() - 1)
                        	date = Time.asStartOfMinute(date);
                        break;
                    case SECOND:
                    	date = Time.asStartOfSecond(date);
                        break;
	                case MILLENIUM:
	                case CENTURY:
	                case DECADE:
	                    break;

                }
                evenDates.add(date);
            }

            return evenDates;
        } else {
            return dates;
        }
    }

}
