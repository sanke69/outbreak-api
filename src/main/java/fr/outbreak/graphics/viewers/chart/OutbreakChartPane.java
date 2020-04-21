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
 */package fr.outbreak.graphics.viewers.chart;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.TimeZone;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;

import fr.javafx.scene.control.chart.XYAxis;
import fr.javafx.scene.control.chart.XYChartInterface;
import fr.javafx.scene.control.chart.axis.NumberAxis;

import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.graphics.OutbreakViewerBase;
import fr.outbreak.graphics.OutbreakViewerChart;
import fr.outbreak.graphics.charts.OutbreakSeries;

public class OutbreakChartPane extends OutbreakViewerBase implements OutbreakViewerChart<Number,Number> {
	private final BorderPane                      	container;
	private final XYChartInterface<Number,Number> 	chart;

	private final boolean 							useDateAxis;
	public EnumSet<Population> 						defaultPopulations;

	public OutbreakChartPane() {
		this("Chart View", true);
	}
	public OutbreakChartPane(String _title) {
		this(_title, true);
	}
	public OutbreakChartPane(boolean _useDateAxis) {
		this("Chart View", _useDateAxis);
	}
	public OutbreakChartPane(String _title, boolean _useDateAxis) {
		super(_title);

		chart              = createChart();
		container          = createContainer();

		useDateAxis        = _useDateAxis;
		defaultPopulations = EnumSet.allOf(Population.class);

		databaseProperty().addListener( (_obs, _old, _new) -> {
			if(_new == null)
				return ;

			OutbreakSeries world = new OutbreakSeries( "WLD", _new.getGlobalReports(KpiType.Value) );

			chart.getData().clear();
			chart.getData().add(world.getData(Population.Infected));
		});
	}

	public XYChart<Number,Number> 									getXYChart()											{ return chart.getXYChart(); }

    public ObservableList<Series<Number,Number>> 					getData() 												{ return chart.dataProperty().get(); }
    public void 													setData(ObservableList<Series<Number, Number>> value) 	{ chart.setData(value); }
    public ObjectProperty<ObservableList<Series<Number,Number>>> 	dataProperty() 											{ return chart.dataProperty(); }

	@Override
	protected Skin<OutbreakChartPane> 								createDefaultSkin() {
		return new Skin<OutbreakChartPane>() {
			@Override public OutbreakChartPane getSkinnable() 	{ return OutbreakChartPane.this; }
			@Override public Node getNode() 			{ return container; }
			@Override public void dispose() 			{  }
		};
	}

	private final BorderPane										createContainer() {
		BorderPane container = new BorderPane(chart.getNode(),null,null,null,null);
		container.setStyle("-fx-background-color: gray;");

		return container;
	}
	private final XYChartInterface<Number,Number>
																	createChart() {
		XYChartInterface<Number,Number> chart = XYChartInterface.newInstance(XYChartInterface.Mode.Line, getXAxis(), getYAxis());
		chart . enablePanning(true)
			  . enableZooming(true)
			  . enableAutoRange(true);

		return chart;
	}
	private final Axis<Number>										getXAxis() {
		Format format = null;
		if(useDateAxis) {
			format = new SimpleDateFormat( "yyyy-MM-dd" );

			((SimpleDateFormat) format) . setTimeZone( TimeZone.getTimeZone( "GMT" ) );
		} else {
			format = new DecimalFormat("j ###,###");
		}

		NumberAxis<Number> xAxis = new NumberAxis<Number>();
		xAxis.setAxisTickFormatter(XYAxis.TickFormatter.withFormat( format ) );
		xAxis.setAnimated(false);
		xAxis.setSide(Side.BOTTOM);

		return xAxis;
	}
	private final Axis<Number>										getYAxis() {
		NumberAxis<Number> 
		yAxis = new NumberAxis<Number>();
		yAxis . setSide(Side.LEFT);

		return yAxis;
	}

}
