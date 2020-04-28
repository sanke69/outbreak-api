/**
 * OutBreak API
 * Copyright (C) 2020-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
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
package fr.reporting.api;

import java.util.Collection;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import fr.geodesic.referential.api.countries.Country;

// TODO:: R extends Report, DB extends Report.DataBase<? extends R>
public interface ReportViewer<R extends Report, DB extends Report.DataBase<R>> {

	public interface Table      <R extends Report, DB extends Report.DataBase<R>>         extends ReportViewer<R, DB> {
		public void setData(Collection<? extends R> _entries);
	}
	public interface Chart      <R extends Report, DB extends Report.DataBase<R>>         extends ReportViewer<R, DB> {

	}
	public interface TimeSeries <R extends Report, DB extends Report.DataBase<R>>         extends Chart<R, DB> {
		public XYChart<Number, Number> 									getXYChart();

	    public ObservableList<Series<Number,Number>> 					getData();
	    public void 													setData(ObservableList<Series<Number,Number>> _values);
	    public ObjectProperty<ObservableList<Series<Number,Number>>> 	dataProperty();
	}
	public interface Map        <R extends Report.Located, DB extends Report.DataBase<R>> extends ReportViewer<R, DB> {
		public void setCountryColor   (Country _country, Color _fill);
		public void setCountryInfos   (Country _country, R     _infos);
		public void setCountryOnClick (Country _country, EventHandler<MouseEvent> _clickHandler);
	}

	public interface Options <RV extends ReportViewer<?,?>> {
		public static final int width      = 320;
		public static final int labelWidth = 120;
		public static final int rowHeight  = 27;

		public static record GridPaneColumnProperty(double width, Color color) {}
		public static final  GridPaneColumnProperty left   = new GridPaneColumnProperty( labelWidth, Color.GRAY  ); 
		public static final  GridPaneColumnProperty right  = new GridPaneColumnProperty( width - labelWidth, Color.GRAY.brighter() );
		public static final  GridPaneColumnProperty unique = new GridPaneColumnProperty( width, left.color.interpolate(right.color, 0.5) );

		public abstract void initialize(RV _viewer);

		public 			void addEntry(Region _control);
		public 			void addEntry(String _name, Region _control);
		public 			void addEntry(Labeled _label, Region _control);

	}

	public StringProperty 		titleProperty();
	public ObjectProperty<Node>	graphicProperty();

	public DB					getDatabase();
	public ObjectProperty<DB>	databaseProperty();

}
