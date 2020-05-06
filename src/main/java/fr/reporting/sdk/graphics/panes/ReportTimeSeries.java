/**
 * OutBreak API
 * Copyright (C) 2020-?XYZ  Steve PECHBERTI <steve.pechberti@gmail.com>
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
 */package fr.reporting.sdk.graphics.panes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Skin;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartPane;
import fr.javafx.scene.chart.axis.NumericAxis;

import fr.outbreak.api.Outbreak.Population;
import fr.reporting.api.Report;
import fr.reporting.api.ReportViewer;
import fr.reporting.sdk.graphics.ReportViewerBase;

public class ReportTimeSeries<R extends Report, DB extends Report.DataBase<R>> 
					extends    ReportViewerBase<R, DB> 
					implements ReportViewer.TimeSeries<R, DB> {

	private final XYChartPane<Number,Number> 		chart;
	private NumericAxis								x_axis;
	private NumericAxis								y_axis;

	private final boolean 							useDateAxis;
	public EnumSet<Population> 						defaultPopulations;

	public ReportTimeSeries() {
		this("Chart View", true);
	}
	public ReportTimeSeries(String _title) {
		this(_title, true);
	}
	public ReportTimeSeries(boolean _useDateAxis) {
		this("Chart View", _useDateAxis);
	}
	public ReportTimeSeries(String _title, boolean _useDateAxis) {
		super(_title);
		useDateAxis        = _useDateAxis;
		defaultPopulations = EnumSet.allOf(Population.class);

		chart              = createChart();
	}

	@Override
	public XYChartPane<Number,Number> 								getChartPane()											{ return chart; }

	@Override
    public ObservableList<Series<Number,Number>> 					getData() 												{ return chart.dataProperty().get(); }
	@Override
    public void 													setData(ObservableList<Series<Number, Number>> value) 	{ chart.setData( value ); }
	@Override
    public ObjectProperty<ObservableList<Series<Number,Number>>> 	dataProperty() 											{ return chart.dataProperty(); }

	@Override
	protected Skin<? extends ReportTimeSeries<R, DB>> 				createDefaultSkin() {
		return new Skin<ReportTimeSeries<R, DB>>() {
			@Override public ReportTimeSeries<R, DB> getSkinnable() { return ReportTimeSeries.this; }
			@Override public Node getNode() 						{ return chart; }
			@Override public void dispose() 						{  }
		};
	}

	protected final NumericAxis					getXAxis() {
		if(x_axis != null)
			return x_axis;
		
		XY.Axis.Ticks.Formatter<Number> tickLabeller = null;
		if(useDateAxis) {
			tickLabeller = XY.newNumberFormat(t -> {
				LocalDate ld = Instant.ofEpochMilli(t.longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
				return    ld . format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			});
		} else {
			tickLabeller = XY.newNumberFormat("j. ", null);
		}

		x_axis = new NumericAxis();
		x_axis . setAnimated(false);
		x_axis . setSide(Side.BOTTOM);
		x_axis . setForceZeroInRange(false);
		x_axis . setTickLabelFormatter(tickLabeller);

		return x_axis;
	}
	protected final NumericAxis					getYAxis() {
		if(y_axis != null)
			return y_axis;

		y_axis = new NumericAxis();
		y_axis . setAnimated(false);
		y_axis . setSide(Side.LEFT);

		return y_axis;
	}

	private final XYChartPane<Number,Number>	createChart() {
		XYChartPane<Number,Number> chart = XYChartPane.of(XY.Type.Line, getXAxis(), getYAxis());

		return chart;
	}
}
