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

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.TimeZone;

import fr.javafx.scene.control.chart.XY;

import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.api.OutbreakViewer;
import fr.outbreak.graphics.timeseries.OutbreakSeries;
import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.panes.ReportTimeSeries;

public class OutbreakTimeSeries extends ReportTimeSeries<Outbreak.Report, Outbreak.DataBase> implements OutbreakViewer.TimeSeries {
	public static final EnumSet<Population> defaultPopulations = EnumSet.allOf(Population.class);

	public OutbreakTimeSeries() {
		this("Chart View", true);
	}
	public OutbreakTimeSeries(String _title) {
		this(_title, true);
	}
	public OutbreakTimeSeries(boolean _useDateAxis) {
		this("Chart View", _useDateAxis);
	}
	public OutbreakTimeSeries(String _title, boolean _useDateAxis) {
		super(_title, _useDateAxis);
		
		if(_useDateAxis) {
			Format format = new SimpleDateFormat( "yyyy-MM-dd" );
			((SimpleDateFormat) format) . setTimeZone( TimeZone.getTimeZone( "GMT" ) );

//			getXAxis().setAxisTickFormatter(XY.Axis.Ticks.newFormatter( format ) );
		} else {
//			getXAxis().setAxisTickFormatter(XY.Axis.Ticks.newNumberFormat( new DecimalFormat("j ###,###") ) );
		}

		databaseProperty().addListener( (_obs, _old, _new) -> {
			if(_new == null)
				return ;

			OutbreakSeries 
			world = new OutbreakSeries( "WLD", _new.getGlobalReports(Report.Type.Value) );

			getXYChart() . getData() . clear();
			
			for(Population population : defaultPopulations)
				getXYChart() . getData() . add( world.getData(population) );
		});
	}

}
