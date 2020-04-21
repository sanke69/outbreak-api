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
import java.util.function.Predicate;
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

	public <OLR extends Outbreak.LocalizedReport>
	SimpleOutbreakDataBase(Collection<OLR> _reports) {
		super();

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
											totalReports.add( new OutbreakRecord( KpiType.Value,
																				  r.getDate(), r.getCountry(), 
																				  nbSusceptible . addAndGet( r.get(Population.Susceptible) . orElse(0L) ), 
																				  nbInfected    . addAndGet( r.get(Population.Infected)    . orElse(0L) ), 
																				  nbDead        . addAndGet( r.get(Population.Dead)        . orElse(0L) ), 
																				  nbRecovered   . addAndGet( r.get(Population.Recovered)   . orElse(0L) )  ) );
										});
		});

		new SimpleOutbreakDataBaseDebug(this);
	}

	@Override
	public OutbreakPeriod 							getPeriod() {
		LocalDate firstDate = dailyReports.stream()
									      .min(Comparator.comparing( Outbreak.LocalizedReport::getDate ))
									      .map( Outbreak.LocalizedReport::getDate )
									      .orElseThrow(NoSuchElementException::new);
		LocalDate lastDate  = dailyReports.stream()
									      .max(Comparator.comparing( Outbreak.LocalizedReport::getDate ))
									      .map( Outbreak.LocalizedReport::getDate )
									      .orElseThrow(NoSuchElementException::new);
		
		return new OutbreakPeriod(firstDate, lastDate);
	}
	@Override
	public <T> Collection<T> 						getIndicators		(KpiType _type, Function<Outbreak.LocalizedReport, T> _mapper, boolean _distinct) {
		return switch(_type) {
		case Variation -> _distinct ?
								dailyReports.stream().map(_mapper).distinct().collect(Collectors.toList())
								:
								dailyReports.stream().map(_mapper).collect(Collectors.toList());
		case Value     -> _distinct ?
								totalReports.stream().map(_mapper).distinct().collect(Collectors.toList())
								:
								totalReports.stream().map(_mapper).collect(Collectors.toList());
		};
	}
	@Override
	public <T> SortedSet<T> 						getIndicators		(KpiType _type, Function<LocalizedReport, T> _mapper, Comparator<T> _comparator) {
		final boolean _distinct = true;
		return switch(_type) {
		case Variation -> _distinct ?
								dailyReports.stream().map(_mapper).distinct().collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)))
								:
								dailyReports.stream().map(_mapper).collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		case Value     -> _distinct ?
								totalReports.stream().map(_mapper).distinct().collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)))
								:
								totalReports.stream().map(_mapper).collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		};
	}

	@Override
	public Collection<Outbreak.LocalizedReport>		getReports 			(KpiType _type) {
		return switch(_type) {
		case Variation -> dailyReports;
		case Value     -> totalReports;
		};
	}
	@Override
	public Collection<Outbreak.LocalizedReport> 	getReports 			(KpiType _type, Predicate<Outbreak.LocalizedReport> _filter) {
		return switch(_type) {
		case Variation -> dailyReports.stream().filter(_filter).collect(Collectors.toList());
		case Value     -> totalReports.stream().filter(_filter).collect(Collectors.toList());
		};
	}
	@Override
	public SortedSet<LocalizedReport> 				getReports			(KpiType _type, Comparator<LocalizedReport> _comparator) {
		return switch(_type) {
		case Variation -> dailyReports.stream().collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		case Value     -> totalReports.stream().collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		};
	}
	@Override
	public SortedSet<Outbreak.LocalizedReport> 		getReports 			(KpiType _type, Predicate<Outbreak.LocalizedReport> _filter, Comparator<Outbreak.LocalizedReport> _comparator) {
		return switch(_type) {
		case Variation -> dailyReports.stream().filter(_filter).collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		case Value     -> totalReports.stream().filter(_filter).collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
		};
	}

	@Override
	public Collection<LocalizedReport> 				getGlobalReports	(KpiType _type) {
		return switch(_type) {
		case Variation -> new TreeSet<>(Outbreak.LocalizedReport.comparatorByDate()) {
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
		case Value     -> new TreeSet<>(Outbreak.LocalizedReport.comparatorByDate()) {
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
		};
	}
	@Override
	public Collection<LocalizedReport> 				getGlobalReports	(KpiType _type, Predicate<LocalizedReport> _filter) {
		return getGlobalReports(_type).stream().filter(_filter).collect(Collectors.toList());
	}
	@Override
	public SortedSet<LocalizedReport> 				getGlobalReports	(KpiType _type, Comparator<LocalizedReport> _comparator) {
		return getGlobalReports(_type).stream().collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
	}
	@Override
	public SortedSet<LocalizedReport> 				getGlobalReports	(KpiType _type, Predicate<LocalizedReport> _filter, Comparator<LocalizedReport> _comparator) {
		return getGlobalReports(_type).stream().filter(_filter).collect(Collectors.toCollection(() -> new TreeSet<>(_comparator)));
	}

}
