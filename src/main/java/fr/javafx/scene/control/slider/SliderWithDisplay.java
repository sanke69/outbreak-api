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
package fr.javafx.scene.control.slider;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.java.time.Time;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class SliderWithDisplay<T> extends Control {

    public static SliderWithDisplay<LocalDate> 				forLocalDate(LocalDate _from, LocalDate _to) {
		double from = _from . atStartOfDay() . toInstant(Time.DEFAULT_ZONEOFFSET).toEpochMilli();
		double to   = _to    .atStartOfDay() . toInstant(Time.DEFAULT_ZONEOFFSET).toEpochMilli();
		double now  = Instant.now().toEpochMilli();
		double day  = 24 * 60 * 60 * 1000;

		Function<LocalDate, Double> objectToDouble = ld -> (double) ld.atStartOfDay().toInstant(Time.DEFAULT_ZONEOFFSET).toEpochMilli();
	    Function<Double, LocalDate> doubleToObject = d  -> LocalDate.ofInstant(Instant.ofEpochMilli(d.longValue()), Time.DEFAULT_ZONEID);
	    StringConverter<Double> 	toTicks        = new StringConverter<>() {

								        @Override
								        public String toString(Double timestamp) {
								        	Instant   instant = Instant.ofEpochMilli( timestamp.longValue() );
								        	LocalDate date    = LocalDate.ofInstant(instant, Time.DEFAULT_ZONEID);
							
								            return String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
								        }
							
								        @Override
								        public Double fromString(String string) {
								            return null;
								        }
								    },
									toDisplay      = new StringConverter<>() {
	
								        @Override
								        public String toString(Double timestamp) {
								        	Instant   instant = Instant.ofEpochMilli( timestamp.longValue() );
								        	LocalDate date    = LocalDate.ofInstant(instant, Time.DEFAULT_ZONEID);
							
								            return String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
								        }
							
								        @Override
								        public Double fromString(String string) {
								            return null;
								        }
								    };

    	return new SliderWithDisplay<LocalDate>(from, to, day, now, objectToDouble, doubleToObject, toTicks, toDisplay);
    }
    public static SliderWithDisplay<Integer> 				forDay(int _min, int _max) {
	    StringConverter<Double> toTicks   = new StringConverter<Double>() {

			@Override
			public String toString(Double _day) {
				return String.format("j. %02d", _day.intValue());
			}

			@Override
			public Double fromString(String string) {
				return null;
			}

		}, 
		toDisplay = new StringConverter<Double>() {

			@Override
			public String toString(Double _value) {
				return String.format(_value > 0 ? "jour +%2d" : "jour %2d", _value.intValue());
			}

			@Override
			public Double fromString(String string) {
				return null;
			}

		};

		return new SliderWithDisplay<Integer>(_min, _max, 1, 0,  i -> (double) i, d -> d.intValue(), toTicks, toDisplay);
    }
    public static <E extends Enum<E>> SliderWithDisplay<E> 	forEnum(EnumSet<E> _enum) {
    	List<E> indexes = new ArrayList<E>(_enum);

		double  from    = 0d;
		double  to      = _enum.size() - 1;
		double  step    = 1d;
		double  value   = 0d;

	    Function<E, Double>     objectToDouble = e -> (double) indexes.indexOf(e);
	    Function<Double, E>     doubleToObject = d -> indexes.get(d.intValue());
	    StringConverter<Double> toTicks        = new StringConverter<Double>() {

									@Override
									public String toString(Double d) {
										Enum<E> object = indexes.get(d.intValue());
										return object.name().substring(0, 3);
									}
						
									@Override
									public Double fromString(String s) {
										return (double) indexes.indexOf( _enum.stream()
																			  .filter(e -> e.name().substring(0, 3).compareTo(s) == 0)
																			  .findFirst().orElse(null) );
									}
							    	
							    }, 
	    						toDisplay      = new StringConverter<Double>() {

	    							@Override
	    							public String toString(Double d) {
	    								Enum<E> object = indexes.get(d.intValue());
	    								return object.name();
	    							}

	    							@Override
	    							public Double fromString(String s) {
	    								return (double) indexes.indexOf( _enum.stream()
	    																	  .filter(e -> e.name().compareTo(s) == 0)
	    																	  .findFirst().orElse(null) );
	    							}
	    					    	
	    					    };

	    return new SliderWithDisplay<E>(from, to, step, value, objectToDouble, doubleToObject, toTicks, toDisplay);
    }
    public static <T> SliderWithDisplay<T> 					forList(List<T> _enum, StringConverter<T> _itemToString) {

		double  from    = 0d;
		double  to      = _enum.size() - 1;
		double  step    = 1d;
		double  value   = 0d;

	    Function<T, Double>     objectToDouble = e -> (double) _enum.indexOf(e);
	    Function<Double, T>     doubleToObject = d -> _enum.get(d.intValue());
	    StringConverter<Double> toTicks        = new StringConverter<Double>() {

			@Override
			public String toString(Double d) {
				return "" + d.intValue();
			}

			@Override
			public Double fromString(String s) {
				return null;
			}
	    	
	    }, 
	    						toDisplay      = null;

	    return new SliderWithDisplay<T>(from, to, step, value, objectToDouble, doubleToObject, toTicks, toDisplay);
    }
    public static SliderWithDisplay<Double> 				forDouble(double _min, double _max, double _step, double _default) {

	    StringConverter<Double> toTicks   = new StringConverter<Double>() {

									@Override
									public String toString(Double _value) {
										return String.format("%02.3f", _value.doubleValue());
									}
						
									@Override
									public Double fromString(String string) {
										return null;
									}
						
								}, 
	    						toDisplay = new StringConverter<Double>() {

	    							@Override
	    							public String toString(Double _value) {
	    								return String.format("%02.3f", _value.doubleValue());
	    							}

	    							@Override
	    							public Double fromString(String string) {
	    								return null;
	    							}

	    						};
    	
    	return new SliderWithDisplay<Double>(_min, _max, _step, _default, null, null, toTicks, toDisplay);
    }
    public static SliderWithDisplay<Integer> 				forInteger(int _min, int _max, int _step, int _default) {
	    StringConverter<Double> toTicks   = new StringConverter<Double>() {

									@Override
									public String toString(Double _day) {
										return String.format("%02d", _day.intValue());
									}
						
									@Override
									public Double fromString(String string) {
										return null;
									}
						
								}, 
	    						toDisplay = new StringConverter<Double>() {

	    							@Override
	    							public String toString(Double _value) {
	    								return String.format("%02d", _value.intValue());
	    							}

	    							@Override
	    							public Double fromString(String string) {
	    								return null;
	    							}

	    						};
    	
    	return new SliderWithDisplay<Integer>(_min, _max, _step, _default, i -> (double) i, d -> d.intValue(), toTicks, toDisplay);
    }

    HBox                    hbox;
    Slider                  slider;
    Label                   display;

    ObjectProperty<T> 		value;

	protected SliderWithDisplay(double _min, double _max, double _step, double _value, 
								Function<T, Double>     _objectToDouble, Function<Double, T>     _doubleToObject,
								StringConverter<Double> _toTicks,        StringConverter<Double> _toDisplay) {
		super();

		double ticks = _step;
		if( (_max - _min ) / ticks > 100 )
			do {
				ticks *= 10;
			} while ( (_max - _min ) / ticks > 100 );

		hbox    = new HBox();
		slider  = new Slider(_min, _max, _value);
		display = new Label();
		value   = new SimpleObjectProperty<T>();

        slider.setMajorTickUnit(_step);
		slider.setShowTickLabels(true);
		if(_toTicks != null)
			slider.setLabelFormatter(_toTicks);

		Consumer<Double> validateValue = d -> {
			double v  = slider.getValue();

			double Q  = (int) ((v - _min) / _step);
			double R  = v % _step; // Math.IEEEremainder(v, _step);

			slider.setValue( (double) ( _min + (R < .5 * _step ? Q*_step : (Q+1)*_step) ) );
		};
		slider.setOnMouseReleased (me -> { if(!me.isPrimaryButtonDown()) { 
			validateValue.accept(slider.getValue()); 
		} });

        slider.valueProperty().addListener((_obs, _old, _new) -> display.setText(_toDisplay.toString(_new.doubleValue())));

        value.bind(new ObjectBinding<T>() {
        	{ bind(slider.valueProperty()); }

        	@SuppressWarnings("unchecked") // TODO:: Check it with TESTS !!!
			@Override protected T computeValue()
        	{ return _doubleToObject != null ? _doubleToObject.apply( slider.getValue() ) : (T) (Object) slider.getValue(); }

        });

        display.setPrefWidth(120);
        HBox.setHgrow(slider, Priority.ALWAYS);
        hbox.getChildren().addAll(slider, display);

	}

	public ObjectProperty<T> valueProperty() {
		return value;
	}

	protected Skin<SliderWithDisplay<T>> createDefaultSkin() {
		return new Skin<SliderWithDisplay<T>>() {

			@Override
			public SliderWithDisplay<T> getSkinnable() { return SliderWithDisplay.this; }

			@Override
			public Node getNode() { return hbox; }

			@Override
			public void dispose() { }
			
		};
	}

}
