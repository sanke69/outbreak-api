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
 */
package fr.outbreak.graphics.viewers;

import java.util.EnumSet;

import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.plugins.behavior.ChartPanner;
import fr.javafx.scene.chart.plugins.behavior.ChartSelecter;
import fr.javafx.scene.chart.plugins.behavior.ChartZoomer;
import fr.javafx.scene.chart.plugins.overlays.CrosshairIndicator;
import fr.javafx.scene.chart.plugins.overlays.DataPointTooltip;
import fr.javafx.utils.MouseEvents;

import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.api.OutbreakViewer;
import fr.outbreak.graphics.timeseries.OutbreakSeries;
import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.panes.ReportTimeSeries;

public class OutbreakTimeSeries extends ReportTimeSeries<Outbreak.Report, Outbreak.DataBase> implements OutbreakViewer.TimeSeries {
	public static final EnumSet<Population> defaultPopulations = EnumSet.allOf(Population.class);

	public static final XY.Series.Style infectedStyle = 
			new XY.Series.Style(Color.RED, 1d, "", Color.RED, Color.RED, Color.RED);
	
	public OutbreakTimeSeries(String _title, boolean _useDateAxis) {
		super(_title, _useDateAxis);

		getChartPane().getPlugins().addAll  (
											new ChartPanner(), 
											new ChartZoomer(), 
											new ChartSelecter(XY.Constraint.BOTH,
    												me -> MouseEvents.isOnlyPrimaryButtonDown(me) && MouseEvents.isOnlyCtrlModifierDown(me),
    												(chart, selection) -> ChartZoomer.performZoom(chart, selection, XY.Constraint.BOTH)),
											new DataPointTooltip(), 
											new CrosshairIndicator<>()
											);
		getChartPane().getXAxis().addEventHandler(MouseEvent.ANY, me -> { if (me.getClickCount() == 2) getChartPane().getXAxis().setAutoRanging(true); });
		getChartPane().getYAxis().addEventHandler(MouseEvent.ANY, me -> { if (me.getClickCount() == 2) getChartPane().getYAxis().setAutoRanging(true); });

		databaseProperty().addListener( (_obs, _old, _new) -> {
			if(_new == null)
				return ;

			OutbreakSeries 
			world = new OutbreakSeries( "WLD", _new.getGlobalReports(Report.Type.Value) );

			getChartPane() . getData() . clear();

			for(Population population : defaultPopulations) {
				Series<Number, Number> popSeries = world.getData(population);
				getChartPane() . getData() . add( popSeries );
// TODO::		getChartPane() . setStyle( popSeries, infectedStyle );
			}
		});
	}

}
