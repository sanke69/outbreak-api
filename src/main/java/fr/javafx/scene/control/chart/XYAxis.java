package fr.javafx.scene.control.chart;

import java.text.Format;
import java.text.NumberFormat;

public interface XYAxis<K> {

	public enum      Constraint {
		HORIZONTAL,		// Allow the operation (such as pan or zoom) only on horizontal (x) axis.
		VERTICAL,		// Allow the operation (such as pan or zoom) only on vertical (y) axis.
		BOTH,			// Allow the operation (such as pan or zoom) on either x or y axes.
		NONE			// Do not allow the operation.
	}

	@FunctionalInterface
	public interface ConstraintStrategy {

		public static XYAxis.ConstraintStrategy normal() {
			return new XYAxis.ConstraintStrategy() {
				@Override
				public XYAxis.Constraint getConstraint( XYChartInputContext context ) {
					return switch(context) {
					case onXAxis      -> XYAxis.Constraint.HORIZONTAL;
					case onYAxis      -> XYAxis.Constraint.VERTICAL;
					case inPlotArea   -> XYAxis.Constraint.BOTH;
					case outsideChart -> XYAxis.Constraint.BOTH;
					};
				}
			};
		}
		public static XYAxis.ConstraintStrategy ignoreOutsideChart() {
			return new XYAxis.ConstraintStrategy() {
				@Override
				public XYAxis.Constraint getConstraint( XYChartInputContext context ) {
					return switch(context) {
					case onXAxis      -> XYAxis.Constraint.HORIZONTAL;
					case onYAxis      -> XYAxis.Constraint.VERTICAL;
					case inPlotArea   -> XYAxis.Constraint.BOTH;
					case outsideChart -> XYAxis.Constraint.NONE;
					};
				}
			};
		}
		public static XYAxis.ConstraintStrategy withConstraint(Constraint _format) {
			return new XYAxis.ConstraintStrategy() {
				@Override
				public XYAxis.Constraint getConstraint( XYChartInputContext context ) {
					return _format;
				}
			};
		}

		Constraint getConstraint(XYChartInputContext context);

	}

	public interface TickFormatter<K> {

		public static <T extends Number> TickFormatter<T> defaultNumberFormat() {
			return new XYAxis.TickFormatter<T>() {
				private final NumberFormat format = NumberFormat.getNumberInstance();

				@Override
				public void setRange( double low, double high, double tickSpacing ) { }
			
				@Override
				public String format( Number value ) { return format.format( value ); }

			};
		}
		public static TickFormatter<Number> withFormat(Format _format) {
			return new XYAxis.TickFormatter<Number>() {

				@Override
				public void setRange( double low, double high, double tickSpacing ) { }
			
				@Override
				public String format( Number value ) { return _format.format( value ); }

			};
		}
		
		public void   setRange(double low, double high, double tickSpacing);
		public String format(Number value);

	}

	public void setAxisTickFormatter(TickFormatter<K> axisTickFormatter);

}
