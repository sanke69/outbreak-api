package fr.javafx.scene.chart;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public interface XY {
	

	public enum      Type {
		Scatter, Line, Area, StackedArea, Bar;
	}

	public enum      Symbols {
		diamond		("M5,0 L10,9 L5,18 L0,9 Z"),
		cross		("M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z"),
		triangle	("M5,0 L10,8 L0,8 Z"),
		check		("M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z");
		
		String path;
		
		Symbols(String _path) {
			path = _path;
		}

		public String toString() { return path; }

	}

	public enum      Context {
		onXAxis, onYAxis, inPlotArea, outsideChart;
	}

	public enum      Constraint {
		NONE       (0),		// Do not allow the operation.
		HORIZONTAL (1),		// Allow the operation (such as pan / selection / zoom) only on horizontal (x) axis.
		VERTICAL   (2),		// Allow the operation (such as pan / selection / zoom) only on vertical (y) axis.
		BOTH       (3);		// Allow the operation (such as pan / selection / zoom) on either x or y axes.

		int bits;

		Constraint(int _value) {
			bits = _value;
		}

	    public boolean allowsHor() { return this == HORIZONTAL || this == BOTH; }
	    public boolean allowsVer() { return this == VERTICAL   || this == BOTH; }

	    public Constraint AND (Constraint _other) {
	    	return switch(bits & _other.bits) {
	    	case 0  -> NONE;
	    	case 1  -> HORIZONTAL;
	    	case 2  -> VERTICAL;
	    	default -> BOTH;
	    	};
	    }
	    public Constraint OR  (Constraint _other) {
	    	return switch(bits | _other.bits) {
	    	case 0  -> NONE;
	    	case 1  -> HORIZONTAL;
	    	case 2  -> VERTICAL;
	    	default -> BOTH;
	    	};
	    }
	    public Constraint XOR (Constraint _other) {
	    	return switch(bits ^ _other.bits) {
	    	case 0  -> NONE;
	    	case 1  -> HORIZONTAL;
	    	case 2  -> VERTICAL;
	    	default -> BOTH;
	    	};
	    }

	}

	@FunctionalInterface
	public interface ConstraintStrategy {

		public static XY.ConstraintStrategy normal() {
			return new XY.ConstraintStrategy() {
				@Override
				public XY.Constraint getConstraint( XY.Context context ) {
					return switch(context) {
					case onXAxis      -> XY.Constraint.HORIZONTAL;
					case onYAxis      -> XY.Constraint.VERTICAL;
					case inPlotArea   -> XY.Constraint.BOTH;
					case outsideChart -> XY.Constraint.BOTH;
					};
				}
			};
		}
		public static XY.ConstraintStrategy ignoreOutsideChart() {
			return new XY.ConstraintStrategy() {
				@Override
				public XY.Constraint getConstraint( XY.Context context ) {
					return switch(context) {
					case onXAxis      -> XY.Constraint.HORIZONTAL;
					case onYAxis      -> XY.Constraint.VERTICAL;
					case inPlotArea   -> XY.Constraint.BOTH;
					case outsideChart -> XY.Constraint.NONE;
					};
				}
			};
		}
		public static XY.ConstraintStrategy withConstraint(Constraint _format) {
			return new XY.ConstraintStrategy() {
				@Override
				public XY.Constraint getConstraint( XY.Context context ) {
					return _format;
				}
			};
		}

		Constraint getConstraint(XY.Context context);

	}

	public interface Axis<T> {
	
	    public interface Range {
	        public static record Record(double lowerBound, double upperBound, double scale, String tickFormat, double tickSpacing) implements Range {}
	
	        public static Range  of(double lowerBound, double upperBound, double scale, String tickFormat) {
	        	return new Record(lowerBound, upperBound, scale, tickFormat, 0);
	        }
	        public static Range  of(double lowerBound, double upperBound, double scale, double tickSpacing) {
	        	return new Record(lowerBound, upperBound, scale, "#.###", tickSpacing);
	        }
	
		    public double 		 	lowerBound();
		    public double 		 	upperBound();
		    public default double 	deltaBound() { return upperBound() - lowerBound(); }
		    public double 		 	scale();
		    public String 		 	tickFormat();
		    public double	 	 	tickSpacing();
		  
		}
	
		public interface Ticks {
		    static final int               DEFAULT_BASE        = 10;
		    static final SortedSet<Number> DEFAULT_MULTIPLIERS = new TreeSet<>(Arrays.asList(1d, 2.5, 5d));

			@FunctionalInterface
			public interface UnitSupplier {
			    double computeTickUnit(double referenceTickUnit);
			}

			public static abstract class Formatter<T> extends StringConverter<T> {
//				public abstract String toString(T number, String format);
//				public abstract String toString(T number, NumberFormat formatter);
			}

			public static XY.Axis.Ticks.UnitSupplier       		defaultUnitSupplier() {
				return defaultUnitSupplier(DEFAULT_MULTIPLIERS);
			}
			public static XY.Axis.Ticks.UnitSupplier       		defaultUnitSupplier(SortedSet<Number> _multipliers) {
				return new XY.Axis.Ticks.UnitSupplier() {
				    private final double[] multipliers;

				    {
				        checkMultiplier(_multipliers);
				        multipliers = _multipliers.stream().mapToDouble(multiplier -> multiplier.doubleValue()).toArray();
				    }

				    void checkMultiplier(SortedSet<? extends Number> multipliers) {
				        Objects.requireNonNull(_multipliers, "The multipliers must not be null");
				        if(_multipliers.isEmpty())
				            throw new IllegalArgumentException("The set of multipliers must not be empty");

				        for(Number mult : multipliers)
				            if(mult.doubleValue() < 1 || mult.doubleValue() >= DEFAULT_BASE)
				                throw new IllegalArgumentException("The multiplier values must be in range [1, 10)");
				    }

				    @Override
				    public double computeTickUnit(double referenceTickUnit) {
				        if (referenceTickUnit <= 0)
				            throw new IllegalArgumentException("The reference tick unit must be a positive number");

				        int    exp        = (int) Math.floor(Math.log10(referenceTickUnit));
				        double factor     = referenceTickUnit / Math.pow(DEFAULT_BASE, exp);
				        double multiplier = 0;

				        int lastIndex = multipliers.length - 1;
				        if(factor > multipliers[lastIndex]) {
				            multiplier = multipliers[0];
				            exp++;
				        } else {
				            for(int i = lastIndex; i >= 0; i--)
				                if(factor <= multipliers[i])
				                    multiplier = multipliers[i];
				                else
				                    break;
				        }

				        return multiplier * Math.pow(DEFAULT_BASE, exp);
				    }
				};
			}

			public static <T> Formatter<T> 						defaultFormatter() {
				return new Formatter<T>() {

					@Override 
					public String 	toString(T value) {
						return value.toString();
					}
					@Override
					public T      	fromString(String string) {
						return null;
					}

				};
			}
			public static <T extends Number> Formatter<T> 		defaultNumberFormatter() {
				return new Formatter<T>() {

					@Override 
					public String 	toString(T value) {
						return new DecimalFormat( adjustFormatFormat(value.doubleValue()) ).format( value );
					}

					@Override
					public T      	fromString(String string) {
						return null;
					}

				    private String adjustFormatFormat(double _value) {
				        int log10 = (int) Math.floor(Math.log10(_value));
				        boolean unitHasFraction = Math.rint(_value) != _value;
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

				};
			}

			public static <T> Formatter<T> 						newFormatter(Format _format) {
				return new Formatter<T>() {

					@Override 
					public String 	toString(T value) {
						return _format.format(value);
					}
					@Override
					public T      	fromString(String string) {
						return null;
					}

				};
			}

			public static <T extends Number> Formatter<T> 		newNumberFormat(String _format) {
				return new Formatter<T>() {
					@Override
					public String 	toString(T value) {
						return new DecimalFormat( _format ).format( value );
					}
					@Override
					public T      	fromString(String string) {
						return null;
					}
				};
			}
			public static <T extends Number> Formatter<T> 		newNumberFormat(StringProperty _formatProperty) {
				return new Formatter<T>() {
					private NumberFormat numberFormat = new DecimalFormat(_formatProperty.get());

					{ _formatProperty.addListener((observable, oldValue, newValue) -> numberFormat = new DecimalFormat(_formatProperty.get())); }
					
					@Override
					public String 	toString(T value) {
						return numberFormat.format( value );
					}

					@Override
					public T      	fromString(String string) {
						return null;
					}

				};
			}
			public static <T extends Number> Formatter<T> 		newNumberFormat(Function<T, String> _format) {
				return new Formatter<T>() {

					@Override
					public String 	toString(T value) {
						return _format.apply( value );
					}

					@Override
					public T      	fromString(String string) { return null; }
				};
			}
			public static <T extends Number> Formatter<T> 		newNumberFormat(NumberFormat _format) {
				return newNumberFormat(null, null, _format);
			}
			public static <T extends Number> Formatter<T> 		newNumberFormat(String _prefix, String _suffix, NumberFormat _format) {
				return new Formatter<T>() {
			        private NumberFormat format = _format;
			        private String       prefix = _prefix;
			        private String       suffix = _suffix;

			        @Override
			        public String 	toString(Number number) {
			            String prefix = this.prefix == null ? "" : this.prefix;
			            String suffix = this.suffix == null ? "" : this.suffix;

			            return prefix + format.format(number) + suffix;
			        }

					@Override
			        public T 		fromString(String string) {
			            try {
			                int prefixLength = (prefix == null) ? 0 : prefix.length();
			                int suffixLength = (suffix == null) ? 0 : suffix.length();

			                return (T) format.parse(string.substring(prefixLength, string.length() - suffixLength));

			            } catch (ParseException exc) { throw new IllegalArgumentException(exc); }
			        }

			    };
			}

		}

		public BooleanProperty 								autoRangeRoundingProperty();

		public ObjectProperty<XY.Axis.Ticks.UnitSupplier> 	tickUnitSupplierProperty();
		public ObjectProperty<XY.Axis.Ticks.Formatter<T>> 	tickFormatterProperty();
	
	}

	public interface Chart<X, Y> {

		public void 										setStyle(XYChart.Series<?,?> _series, 
																			Color _lineColor, Integer _lineWidth, 
																			String _shape, Color _inColor, Color _outColor,
																			Color _fillColor);

		// Chart Methods
		public String 										getTitle();
		public void 										setTitle(String value);
		public StringProperty 								titleProperty();
		
	    public Side 										getTitleSide();
	    public void 										setTitleSide(Side value);
	    public ObjectProperty<Side> 						titleSideProperty();
	
	    public boolean 										isLegendVisible();
	    public void 										setLegendVisible(boolean value);
	    public BooleanProperty 								legendVisibleProperty();
	
	    public Side 										getLegendSide();
	    public void 										setLegendSide(Side value);
	    public ObjectProperty<Side> 						legendSideProperty();
	
	    public boolean 										getAnimated();
	    public void 										setAnimated(boolean value);
	    public BooleanProperty 								animatedProperty();
	
	    public ObservableList<Series<X,Y>> 					getData();
	    public void 										setData(ObservableList<Series<X,Y>> value);
	    public ObjectProperty<ObservableList<Series<X,Y>>> 	dataProperty();
	
	    public boolean 										getVerticalGridLinesVisible();
	    public void 										setVerticalGridLinesVisible(boolean value);
	    public BooleanProperty 								verticalGridLinesVisibleProperty();
	
	    public boolean 										isHorizontalGridLinesVisible();
	    public void 										setHorizontalGridLinesVisible(boolean value);
	    public BooleanProperty 								horizontalGridLinesVisibleProperty();
	
	    public boolean 										isAlternativeColumnFillVisible();
	    public void 										setAlternativeColumnFillVisible(boolean value);
	    public BooleanProperty 								alternativeColumnFillVisibleProperty();
	
	    public boolean 										isAlternativeRowFillVisible();
	    public void 										setAlternativeRowFillVisible(boolean value);
	    public BooleanProperty 								alternativeRowFillVisibleProperty();
	
	    public boolean 										isVerticalZeroLineVisible();
	    public void 										setVerticalZeroLineVisible(boolean value);
	    public BooleanProperty 								verticalZeroLineVisibleProperty();
	
	    public boolean 										isHorizontalZeroLineVisible();
	    public void 										setHorizontalZeroLineVisible(boolean value);
	    public BooleanProperty 								horizontalZeroLineVisibleProperty();
	
	    public List<CssMetaData<? extends Styleable, ?>> 	getCssMetaData();
	
		// XYChart Methods
	    public javafx.scene.chart.Axis<X> 					getXAxis();
	    public javafx.scene.chart.Axis<Y> 					getYAxis();
	
	}

	public interface ChartPlugin<X, Y> {

		public void 				setChartPane(XYChartPane<X, Y> xyChartPane);

		public default void 		layoutChildren() {}

		public ObservableList<Node> getChartChildren();

	}

    public static class SelectionEvent extends Event {
        private static final long serialVersionUID = 20200427055500L;

        XYChart<Number, Number> chart;
        Rectangle2D             selection;

        public SelectionEvent(XYChart<Number, Number> _chart, Rectangle2D _selection) {
            super(ON_SELECTION);
            chart     = _chart;
            selection = _selection;
        }
        public SelectionEvent(XYChart<Number, Number> _chart, Rectangle2D _selection, Object source, EventTarget target) {
            super(source, target, ON_SELECTION);
            chart     = _chart;
            selection = _selection;
        }

        public XYChart<Number, Number> 	getChart() {
        	return chart;
        }
        public Rectangle2D             	getSelection() {
        	return selection;
        }

        @Override
        public SelectionEvent 			copyFor(Object newSource, EventTarget newTarget) {
            return (SelectionEvent) super.copyFor(newSource, newTarget);
        }

    }

    public static final EventType<SelectionEvent> ON_SELECTION = new EventType<>("ON_SELECTION");

}
