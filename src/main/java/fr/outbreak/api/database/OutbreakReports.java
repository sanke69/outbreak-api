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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.api.records.OutbreakRecord;

public interface OutbreakReports {

	public static <ORReport extends Outbreak.ReferencedReport> 
	SortedMap<LocalDate, SortedSet<ORReport>> 					groupByDate				(Collection<ORReport> _reports) {
	    return _reports .stream()
//						.filter(report -> report.getType() == Outbreak.KpiType.Variation)
			            .collect(Collectors.groupingBy(
				            		r -> r.getDate(), 
				            		TreeMap::new, 
				            		Collectors.toCollection(() -> new TreeSet<ORReport>(Outbreak.ReferencedReport.comparatorByDate()))));
	}

	public static <ORReport extends Outbreak.ReferencedReport> 
	SortedSet<Outbreak.ReferencedReport> 						computeCumulation		(Collection<ORReport> _reports) {
		SortedSet<Outbreak.ReferencedReport> results = new TreeSet<Outbreak.ReferencedReport>(Outbreak.ReferencedReport.comparatorByDate);

		List<ORReport> sortedReports = new ArrayList<ORReport>(_reports);
		Collections.sort(sortedReports, Outbreak.ReferencedReport.comparatorByDate());

		long nbDead        = 0;
		long nbInfected    = 0;
		long nbRecovered   = 0;
		long nbSusceptible = 0;

		for(ORReport report : _reports) {
			nbSusceptible += report.get(Population.Susceptible) . orElse(0L); // must be 0 as variation
			nbInfected    += report.get(Population.Infected)    . orElse(0L);
			nbRecovered   += report.get(Population.Recovered)   . orElse(0L);
			nbDead        += report.get(Population.Dead)        . orElse(0L);

			results.add( new OutbreakRecord(KpiType.Value,  report.getDate(), 
													Country.GROUP, 
													nbSusceptible, nbInfected, nbDead, nbRecovered) );
		}

		return results;
	}
	public static <ORReport extends Outbreak.ReferencedReport> 
	SortedSet<Outbreak.ReferencedReport> 						computeCumulationByDate	(Collection<ORReport> _reports) {
		SortedMap<LocalDate, SortedSet<ORReport>> reports = groupByDate(_reports);
		SortedSet<Outbreak.ReferencedReport>      results = new TreeSet<Outbreak.ReferencedReport>(Outbreak.ReferencedReport.comparatorByDate());

		for(LocalDate date : reports.keySet()) {
			long nbDead        = 0;
			long nbInfected    = 0;
			long nbRecovered   = 0;
			long nbSusceptible = 0;

			for(ORReport report : reports.get(date)) {
				nbSusceptible += report.get(Population.Susceptible) . orElse(0L); // must be 0 as variation
				nbInfected    += report.get(Population.Infected)    . orElse(0L);
				nbRecovered   += report.get(Population.Recovered)   . orElse(0L);
				nbDead        += report.get(Population.Dead)        . orElse(0L);
			}

			results.add( new OutbreakRecord(KpiType.Value, date, 
													  Country.GROUP, nbSusceptible, 
													  nbInfected, nbDead, nbRecovered) );
		}

		return results;
	}

	public static class Localized {

		public static <OLReport extends Outbreak.LocalizedReport> 
		SortedSet<Country> 											getCountries				(Collection<OLReport> _reports) {
			return _reports .stream()
							.map(Outbreak.LocalizedReport::getCountry)
							.distinct()
							.collect(Collectors.toCollection(() -> new TreeSet<Country>(Country.nameComparator)));
		}

		public static <OLReport extends Outbreak.LocalizedReport> 
		Map<Country, SortedSet<OLReport>> 						groupByCountry				(Collection<OLReport> _reports) {
			return _reports .stream()
//							.filter(report -> report.getType() == Outbreak.KpiType.Variation)
							.collect(Collectors.groupingBy(
										Outbreak.LocalizedReport::getCountry,                      
										Collectors.mapping(
											Function.identity(),
											Collectors.toCollection(() -> new TreeSet<OLReport>(Outbreak.ReferencedReport.comparatorByDate())))));

		}

		public static <OLReport extends Outbreak.LocalizedReport> 
		SortedSet<Outbreak.LocalizedReport> 					computeCumulation			(SortedSet<OLReport> _reports) {
			SortedSet<Outbreak.LocalizedReport> results = new TreeSet<Outbreak.LocalizedReport>(Outbreak.ReferencedReport.comparatorByDate());

			long nbDead        = 0;
			long nbInfected    = 0;
			long nbRecovered   = 0;
			long nbSusceptible = 0;
			
			Country country = null;

			for(OLReport report : _reports) {
				nbSusceptible += report.get(Population.Susceptible) . orElse(0L); // must be 0 as variation
				nbInfected    += report.get(Population.Infected)    . orElse(0L);
				nbRecovered   += report.get(Population.Recovered)   . orElse(0L);
				nbDead        += report.get(Population.Dead)        . orElse(0L);
				
				if(country == null)
					country = report.getCountry();
				else if(country == Country.GROUP)
					;
				else if(country != report.getCountry())
					country = Country.GROUP;

				results.add( new OutbreakRecord(KpiType.Value,  report.getDate(), 
														country, 
														nbSusceptible, nbInfected, nbDead, nbRecovered) );
			}
			
			if(country == Country.GROUP) {
				SortedSet<Outbreak.LocalizedReport> 
				temp    = results;
				results = new TreeSet<Outbreak.LocalizedReport>(Outbreak.ReferencedReport.comparatorByDate());

				for(Outbreak.LocalizedReport report : temp) {
					results.add( new OutbreakRecord(KpiType.Value, report.getDate(), 
															Country.GROUP, 
															report.get(Population.Susceptible), report.get(Population.Infected), report.get(Population.Dead), report.get(Population.Recovered)) );
				}
			}

			return results;
		}
		public static <OLReport extends Outbreak.ReferencedReport> 
		SortedSet<Outbreak.LocalizedReport> 					computeCumulationByDate		(Collection<OLReport> _reports) {
			SortedMap<LocalDate, SortedSet<OLReport>> reports = groupByDate(_reports);
			SortedSet<Outbreak.LocalizedReport>       results = new TreeSet<Outbreak.LocalizedReport>(Outbreak.LocalizedReport.comparatorByDate);

			for(LocalDate date : reports.keySet()) {
				long nbDead        = 0;
				long nbInfected    = 0;
				long nbRecovered   = 0;
				long nbSusceptible = 0;

				for(OLReport report : reports.get(date)) {
					nbSusceptible += report.get(Population.Susceptible) . orElse(0L); // must be 0 as variation
					nbInfected    += report.get(Population.Infected)    . orElse(0L);
					nbRecovered   += report.get(Population.Recovered)   . orElse(0L);
					nbDead        += report.get(Population.Dead)        . orElse(0L);
				}

				results.add( new OutbreakRecord(KpiType.Value, date, 
															  Country.GROUP, nbSusceptible, 
															  nbInfected, nbDead, nbRecovered) );
			}

			return results;
		}
		public static <OLReport extends Outbreak.LocalizedReport> 
		Map<Country, SortedSet<Outbreak.LocalizedReport>> 		computeCumulationByCountry	(Collection<OLReport> _reports) {
			SortedMap<Country, SortedSet<Outbreak.LocalizedReport>> results = new TreeMap<Country, SortedSet<Outbreak.LocalizedReport>>(Country.nameComparator);

			Map<Country, SortedSet<OLReport>> reports = groupByCountry(_reports);

			for(Country country : reports.keySet()) {
				SortedSet<Outbreak.LocalizedReport> cResults = new TreeSet<Outbreak.LocalizedReport>(Outbreak.LocalizedReport.comparatorByDate());

				long nbDead        = 0;
				long nbInfected    = 0;
				long nbRecovered   = 0;
				long nbSusceptible = 0;

				for(OLReport report : reports.get(country)) {
					nbSusceptible += report.get(Population.Susceptible) . orElse(0L); // must be 0 as variation
					nbInfected    += report.get(Population.Infected)    . orElse(0L);
					nbRecovered   += report.get(Population.Recovered)   . orElse(0L);
					nbDead        += report.get(Population.Dead)        . orElse(0L);
	
					cResults.add( new OutbreakRecord(KpiType.Value, report.getDate(), country, 
															nbSusceptible, nbInfected, nbDead, nbRecovered) );
				}
				
				results.put(country, cResults);
			}

			return results;
		}

	}

}
