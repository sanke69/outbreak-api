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
package fr.outbreak.api.database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.api.Outbreak.LocalizedReport;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.api.records.OutbreakPeriod;
import fr.outbreak.api.records.OutbreakRecord;

public abstract class SimpleOutbreakDataBase implements OutbreakDataBase {
	final Collection<Outbreak.LocalizedReport>  dailyReports;
	final Collection<Outbreak.LocalizedReport>  totalReports;
	final OutbreakPeriod						period;

	public <OLR extends Outbreak.LocalizedReport>
	SimpleOutbreakDataBase(Collection<OLR> _reports) {
		super();

		LocalDate firstDate = _reports.stream()
									      .min(Comparator.comparing( Outbreak.LocalizedReport::getDate ))
									      .map( Outbreak.LocalizedReport::getDate )
									      .orElseThrow(NoSuchElementException::new);
		LocalDate lastDate  = _reports.stream()
									      .max(Comparator.comparing( Outbreak.LocalizedReport::getDate ))
									      .map( Outbreak.LocalizedReport::getDate )
									      .orElseThrow(NoSuchElementException::new);

		period       = new OutbreakPeriod(firstDate, lastDate);
		dailyReports = new ArrayList<Outbreak.LocalizedReport>(_reports);
		totalReports = new ArrayList<Outbreak.LocalizedReport>();
	
		dailyReports.stream().collect(Collectors.groupingBy(
										Outbreak.LocalizedReport::getCountry,                      
										Collectors.mapping(
											Function.identity(),
											Collectors.toCollection(() -> new TreeSet<Outbreak.LocalizedReport>(Outbreak.ReferencedReport.comparatorByDate())))))
							.forEach((country, c_reports) -> {
										AtomicLong nbSusceptible = new AtomicLong(0L);
										AtomicLong nbInfected    = new AtomicLong(0L);
										AtomicLong nbDead        = new AtomicLong(0L);
										AtomicLong nbRecovered   = new AtomicLong(0L);

										c_reports.forEach(r -> {
											OutbreakRecord totalCountryByDate = new OutbreakRecord(
																					KpiType.Value,
																					r.getDate(), r.getCountry(), 
																					nbSusceptible . addAndGet( r.get(Population.Susceptible) . orElse(0L) ), 
																					nbInfected    . addAndGet( r.get(Population.Infected)    . orElse(0L) ), 
																					nbDead        . addAndGet( r.get(Population.Dead)        . orElse(0L) ), 
																					nbRecovered   . addAndGet( r.get(Population.Recovered)   . orElse(0L) )  );
											totalReports.add( totalCountryByDate );
										});
		});
		
		
/*
//		period             = OutbreakReportProcessing.getPeriod( dailyReports );
		variation          = new TreeSet<>(Outbreak.LocalizedReport.comparatorByDate()) {
			private static final long serialVersionUID = 617128529407194447L;
			{
			Map<LocalDate, List<Outbreak.LocalizedReport>> sortedByDate = dailyReports.stream().collect(Collectors.groupingBy(Outbreak.LocalizedReport::getDate));
			for(LocalDate key:sortedByDate.keySet()){
			   Long nbSusceptible = sortedByDate.get(key).stream().map(r -> r.get(Population.Susceptible)) . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
			   Long nbInfected    = sortedByDate.get(key).stream().map(r -> r.get(Population.Infected))    . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
			   Long nbDead        = sortedByDate.get(key).stream().map(r -> r.get(Population.Dead))        . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
			   Long nbRecovered   = sortedByDate.get(key).stream().map(r -> r.get(Population.Recovered))   . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);

			   add(new OutbreakRecordDaily(key,Country.GROUP, nbSusceptible, nbInfected, nbDead, nbRecovered));
			}
		}};
		situation          = OutbreakReports.Localized.computeCumulationByDate( dailyReports );

		countries          = OutbreakReports.Localized.getCountries( dailyReports );
		variationByCountry = OutbreakReports.Localized.groupByCountry( dailyReports );
		situationByCountry = new HashMap<Country, SortedSet<Outbreak.LocalizedReport>>();

		for(Country c : countries) {
			situationByCountry . put(c, OutbreakReports.Localized.computeCumulation( variationByCountry.get(c) ));
		}
*/
		new SimpleOutbreakDataBaseDebug(this);
	}

	@Override
	public OutbreakPeriod 							getPeriod() {
		return period;
	}

	@Override
	public SortedSet<LocalizedReport> 				getGlobalReports(KpiType _type) {
		
		if(_type == KpiType.Variation)
			return new TreeSet<>(Outbreak.LocalizedReport.comparatorByDate()) {
				private static final long serialVersionUID = 1L;
				{
				Map<LocalDate, List<Outbreak.LocalizedReport>> sortedByDate = dailyReports.stream().collect(Collectors.groupingBy(Outbreak.LocalizedReport::getDate));
				for(LocalDate date : sortedByDate.keySet()){
				   Long nbSusceptible = sortedByDate.get(date).stream().map(r -> r.get(Population.Susceptible)) . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
				   Long nbInfected    = sortedByDate.get(date).stream().map(r -> r.get(Population.Infected))    . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
				   Long nbDead        = sortedByDate.get(date).stream().map(r -> r.get(Population.Dead))        . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
				   Long nbRecovered   = sortedByDate.get(date).stream().map(r -> r.get(Population.Recovered))   . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
	
				   add(new OutbreakRecord(_type, date, Country.GROUP, nbSusceptible, nbInfected, nbDead, nbRecovered));
				}
			}};

		if(_type == KpiType.Value)
			return new TreeSet<>(Outbreak.LocalizedReport.comparatorByDate()) {
				private static final long serialVersionUID = 1L;
				{
				Map<LocalDate, List<Outbreak.LocalizedReport>> sortedByDate = totalReports.stream().collect(Collectors.groupingBy(Outbreak.LocalizedReport::getDate));
				for(LocalDate key:sortedByDate.keySet()){
				   Long nbSusceptible = sortedByDate.get(key).stream().map(r -> r.get(Population.Susceptible)) . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
				   Long nbInfected    = sortedByDate.get(key).stream().map(r -> r.get(Population.Infected))    . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
				   Long nbDead        = sortedByDate.get(key).stream().map(r -> r.get(Population.Dead))        . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
				   Long nbRecovered   = sortedByDate.get(key).stream().map(r -> r.get(Population.Recovered))   . map(o -> o.orElse(0L)) . reduce(0L, (x, y) -> x + y);
	
				   add(new OutbreakRecord(_type, key,Country.GROUP, nbSusceptible, nbInfected, nbDead, nbRecovered));
				}
			}};

		return switch(_type) {
		case Variation -> null; //variation.stream().filter(r -> r.getDate().equals(_date)).findFirst().orElse(null);
		case Value     -> null; //situation.stream().filter(r -> r.getDate().equals(_date)).findFirst().orElse(null);
		};
	}
	public Outbreak.LocalizedReport 				getGlobalReport  (KpiType _type, LocalDate _date) {
		return switch(_type) {
		case Variation -> null; //variation.stream().filter(r -> r.getDate().equals(_date)).findFirst().orElse(null);
		case Value     -> null; //situation.stream().filter(r -> r.getDate().equals(_date)).findFirst().orElse(null);
		};
	}
	
	
	
	record KpiEvolution(LocalDate date, long value) {}
	public List<KpiEvolution> 						getCurve(KpiType _type, Population _kpi) {
		return	 totalReports	 .stream()
								 .collect(Collectors.groupingBy(Outbreak.LocalizedReport::getDate))
								 .entrySet().stream()
									        .sorted(Map.Entry.<LocalDate, List<Outbreak.LocalizedReport>>comparingByKey().reversed())
									        .peek(e -> {})
									        .map(e -> new KpiEvolution(e.getKey(), e.getValue().stream().mapToLong(s -> s.get(Population.Dead).orElse(0L)).sum()))
									        .collect(Collectors.toList());
	}
	
	
	
	
	@Override
	public Collection<Outbreak.LocalizedReport>		getReports (KpiType _type) {
		return switch(_type) {
		case Variation -> dailyReports;
		case Value     -> totalReports;
		};
	}
//	@Override
//	public Outbreak.LocalizedReport 				getReport  (KpiType _type, LocalDate _date) {
//		return switch(_type) {
//		case Variation -> dailyReports.stream().filter(r -> r.getDate().equals(_date)).findFirst().orElse(null);
//		case Value     -> totalReports.stream().filter(r -> r.getDate().equals(_date)).findFirst().orElse(null);
//		};
//	}

	/**
	 * Get all country reports for a specified date
	 */
	@Override
	public SortedSet<Outbreak.LocalizedReport> 		getReports (KpiType _type, LocalDate _date) {
		return switch(_type) {
		case Variation -> dailyReports.stream()
									  .filter(r -> r.getDate().equals(_date))
									  .collect(Collectors.toCollection(() -> new TreeSet<>(Outbreak.LocalizedReport.comparatorByCountry)));
		case Value     -> totalReports.stream()
									  .filter(r -> r.getDate().equals(_date))
									  .collect(Collectors.toCollection(() -> new TreeSet<>(Outbreak.LocalizedReport.comparatorByCountry)));
		};
	}
//	@Override
	public SortedSet<Outbreak.LocalizedReport> 		getReports (KpiType _type, LocalDate _date, Comparator<Outbreak.LocalizedReport> _comparator) {
		return switch(_type) {
		case Variation -> dailyReports.stream()
									  .filter(r -> r.getDate().equals(_date))
									  .collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		case Value     -> totalReports.stream()
									  .filter(r -> r.getDate().equals(_date))
									  .collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		};
	}
	/**
	 * Get all country reports for a specified date
	 */
	@Override
	public SortedSet<Outbreak.LocalizedReport> 		getReports (KpiType _type, Country _country) {
		return switch(_type) {
		case Variation -> dailyReports.stream()
									  .filter(r -> r.getCountry().equals(_country))
									  .collect(Collectors.toCollection(() -> new TreeSet<>(Outbreak.LocalizedReport.comparatorByDate)));
		case Value     -> totalReports.stream()
									  .filter(r -> r.getCountry().equals(_country))
									  .collect(Collectors.toCollection(() -> new TreeSet<>(Outbreak.LocalizedReport.comparatorByDate)));
		};
	}
	@Override
	public Outbreak.LocalizedReport 				getReport  (KpiType _type, LocalDate _date, Country _country) {
		return getReports(_type, _country).stream().filter(r -> r.getDate().equals(_date)).findFirst().orElse(null);
	}

	
	
	public SortedSet<Country> 	getCountries() {
		return dailyReports .stream()
							.map(Outbreak.LocalizedReport::getCountry)
							.distinct()
							.collect(Collectors.toCollection(() -> new TreeSet<Country>(Country.nameComparator)));
	}
	public OutbreakPeriod 		getPeriod(Country _country) {
		LocalDate firstDate = dailyReports.stream()
										  .filter(r -> r.getCountry() == _country)
									      .min(Comparator.comparing( Outbreak.LocalizedReport::getDate ))
									      .map( Outbreak.LocalizedReport::getDate )
									      .orElseThrow(NoSuchElementException::new);
		LocalDate lastDate  = dailyReports.stream()
				  						  .filter(r -> r.getCountry() == _country)
									      .max(Comparator.comparing( Outbreak.LocalizedReport::getDate ))
									      .map( Outbreak.LocalizedReport::getDate )
									      .orElseThrow(NoSuchElementException::new);
		
		return new OutbreakPeriod(firstDate, lastDate);
	}

}
