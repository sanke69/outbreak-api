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
package fr.outbreak.graphics.timeseries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.paint.Color;

import fr.java.time.Time;

import fr.outbreak.api.Outbreak;
import fr.reporting.api.Report;

public class OutbreakSeries {
	static final long dayDurationMs = 24 * 3600 * 1000;

	public static record Style(Color lineColor, Integer lineWidth, String shape, Color inColor, Color outColor, Color fillColor) {

		public Style(Color lineColor, Integer lineWidth) {
			this(lineColor, lineWidth, null, null, null, null);
		}
		public Style(String shape, Color inColor, Color outColor, Color fillColor) {
			this(null, null, shape, inColor, outColor, fillColor);
		}

	}

	protected final String										name;
	protected final SortedSet<Outbreak.Report> 	 				reports;
	protected final Map<String, XYChart.Series<Number,Number>> 	series;

	protected boolean 											defaultDayNormalization;
	protected int     											defaultDayShift;

	public OutbreakSeries(String _name, Collection<? extends Outbreak.Report> _reports) {
		this(_name, _reports, false, 0);
	}
	public OutbreakSeries(String _name, Collection<? extends Outbreak.Report> _reports, boolean _dayNormalization) {
		this(_name, _reports, _dayNormalization, 0);
	}
	public OutbreakSeries(String _name, Collection<? extends Outbreak.Report> _reports, boolean _dayNormalization, int _dayShift) {
		super();

		defaultDayNormalization = _dayNormalization;
		defaultDayShift         = _dayShift;

		name    = _name;
		reports = new TreeSet<Outbreak.Report>(Report.Daily.comparatorByDate);
		for(Outbreak.Report report : _reports) reports.add((Outbreak.Report) report);

		series  = new HashMap<String, XYChart.Series<Number,Number>>();
	}

	public XYChart.Series<Number,Number> getData			(String _name) {
		return series.get(_name);
	}
	public XYChart.Series<Number,Number> getData			(String _name, Outbreak.Population _population) {
		return getData(_name, _population, defaultDayNormalization, defaultDayShift);
	}
	public XYChart.Series<Number,Number> getData			(String _name, Outbreak.Population _population, boolean _dayNormalization) {
		return getData(_name, _population, _dayNormalization, defaultDayShift);
	}
	public XYChart.Series<Number,Number> getData			(String _name, Outbreak.Population _population, int     _dayShift) {
		return getData(_name, _population, defaultDayNormalization, _dayShift);
	}
	public XYChart.Series<Number,Number> getData			(String _name, Outbreak.Population _population, boolean _dayNormalization, int _dayShift) {
		if(series.containsKey(_name))
			return series.get(_name);

		XYChart.Series<Number,Number>
		serie = new XYChart.Series<Number,Number>();
		serie . setName(_name);

		long t0 = _dayNormalization ? reports.first().getDate().atStartOfDay().toInstant(Time.DEFAULT_ZONEOFFSET).toEpochMilli() : 0;

		for(Outbreak.Report orr : reports) {
			long t = orr.getDate().atStartOfDay().toInstant(Time.DEFAULT_ZONEOFFSET).toEpochMilli() - t0;
			t      = _dayNormalization ? t / dayDurationMs : t;
			t     += _dayShift         * (_dayNormalization ? 1 : dayDurationMs);

			if(t >= 0) {
				long val = orr.get(_population) . orElse(0L);

				serie . getData().add(new Data<Number, Number>(t, val));
			}
		}

		series.put(_name, serie);
		return serie;
	}

	public XYChart.Series<Number,Number> removeSeries		(String _name) {
		XYChart.Series<Number,Number> serie = series.remove(_name);
		return serie;
	}

	public XYChart.Series<Number,Number> getData			(Outbreak.Population _population) {
		return getData(_population.name(), _population, defaultDayNormalization, defaultDayShift);
	}
	public XYChart.Series<Number,Number> getData			(Outbreak.Population _population, boolean _dayNormalization) {
		return getData(_population.name(), _population, _dayNormalization, defaultDayShift);
	}
	public XYChart.Series<Number,Number> getData			(Outbreak.Population _population, int _dayShift) {
		return getData(_population.name(), _population, defaultDayNormalization, _dayShift);
	}
	public XYChart.Series<Number,Number> getData			(Outbreak.Population _population, boolean _dayNormalization, int _dayShift) {
		return getData(_population.name(), _population, _dayNormalization, _dayShift);
	}

	public XYChart.Series<Number,Number> removeSeries		(Outbreak.Population _population) {
		return removeSeries(_population.name());
	}


}
