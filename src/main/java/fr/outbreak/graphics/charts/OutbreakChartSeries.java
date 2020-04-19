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
package fr.outbreak.graphics.charts;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.java.time.Time;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.Population;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.paint.Color;

public class OutbreakChartSeries {
	static final long day = 24 * 3600 * 1000;

	public static record Style(Color lineColor, Integer lineWidth, 
							   String shape, Color inColor, Color outColor, Color fillColor) {

		public Style(Color lineColor, Integer lineWidth) {
			this(lineColor, lineWidth, null, null, null, null);
		}
		public Style(String shape, Color inColor, Color outColor, Color fillColor) {
			this(null, null, shape, inColor, outColor, fillColor);
		}

	}
	
	public static interface ByCases {

		public static <ORReport extends Outbreak.ReferencedReport> 
		OutbreakChartSeries ByTimestamp		(Collection<ORReport> _reports) {
			return new OutbreakChartSeries(_reports, false, 0);
		}
		public static <ORReport extends Outbreak.ReferencedReport> 
		OutbreakChartSeries ByDay			(Collection<ORReport> _reports) {
			return new OutbreakChartSeries(_reports, true, 0);
		}
		public static <ORReport extends Outbreak.ReferencedReport> 
		OutbreakChartSeries ByDay			(Collection<ORReport> _reports, int _shift) {
			return new OutbreakChartSeries(_reports, true, _shift);
		}

	}
	public static interface ByPourcentage {
		
	}

	public XYChart.Series<Number,Number> 		  susceptible;
	public XYChart.Series<Number,Number> 		  infected;
	public XYChart.Series<Number,Number> 		  recovered;
	public XYChart.Series<Number,Number> 		  immuned;
	public XYChart.Series<Number,Number> 		  dead;

	protected final SortedSet<Outbreak.ReferencedReport> reports;

	protected OutbreakChartSeries(Collection<? extends Outbreak.ReferencedReport> _reports) {
		this(_reports, false, 0);
	}
	protected OutbreakChartSeries(Collection<? extends Outbreak.ReferencedReport> _reports, boolean _normalizeTimeline) {
		this(_reports, _normalizeTimeline, 0);
	}
	protected OutbreakChartSeries(Collection<? extends Outbreak.ReferencedReport> _reports, boolean _normalizeTimeline, int _shift) {
		super();

		reports = new TreeSet<Outbreak.ReferencedReport>(Outbreak.ReferencedReport.comparatorByDate);
		for(Outbreak.ReferencedReport report : _reports) reports . add( (Outbreak.ReferencedReport) report);

		initializeSeries();
		
		long t0 = _normalizeTimeline ? reports.first().getDate().atStartOfDay().toInstant(Time.DEFAULT_ZONEOFFSET).toEpochMilli() : 0;

		for(Outbreak.ReferencedReport orr : _reports) {
			long t = orr.getDate().atStartOfDay().toInstant(Time.DEFAULT_ZONEOFFSET).toEpochMilli() - t0;
			t      = _normalizeTimeline ? t / day : t;
			t     += _shift;

			if(t >= 0) {
//				long s = orr.get(Population.Susceptible) . orElse(0L);
				long i = orr.get(Population.Infected)    . orElse(0L);
				long r = orr.get(Population.Recovered)   . orElse(0L);
				long d = orr.get(Population.Dead)        . orElse(0L);

//				susceptible . getData().add(new Data<Number, Number>(t, s));
				infected    . getData().add(new Data<Number, Number>(t, i));
				recovered   . getData().add(new Data<Number, Number>(t, r));
				immuned     . getData().add(new Data<Number, Number>(t, r));
				dead        . getData().add(new Data<Number, Number>(t, d));
			}
		}
	}

	public void initializeSeries() {
		susceptible = new XYChart.Series<Number,Number>();
		susceptible . setName("Population");
		infected    = new XYChart.Series<Number,Number>();
		infected    . setName("Nb Cas");
		recovered   = new XYChart.Series<Number,Number>();
		recovered   . setName("Nb Guéris");
		immuned     = new XYChart.Series<Number,Number>();
		immuned     . setName("Nb Immunisés");
		dead        = new XYChart.Series<Number,Number>();
		dead        . setName("Nb Morts");
	}

}
