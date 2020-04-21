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
package fr.outbreak.api;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

import fr.geodesic.referential.api.countries.Country;

public interface Outbreak {

	// Population
	public enum Population  	{ Susceptible, Infected, Recovered, Immuned, Dead, Alive, Total; }

	// Indicator - Key-Point Indicator
	public enum KpiType        { Value,   Variation; }
	public enum KpiScope       { Summary, ByEvent,  ByDay; }
	public enum KpiUnit        { Count,   PerEvent, PerDay, PerMillion; }

	// Observation / Report
//	public interface Event {
//		public Instant 	getDate();
//		public long 		get(Population _population);
//	}
	public interface Report {
		public default KpiType		getType()			{ return KpiType.Variation; }
		public default KpiUnit		getUnit() 			{ return KpiUnit.Count; }

		public Optional<Long> 		get(Population _population);

	}

	/**
	 * Based on initial retrieved reports format, i.e. only Susceptible (Population), Infected & Dead, by:
	 * 	  Date,
	 * 	  Country.
	 *
	 */
	public interface ReferencedReport extends Report {
		public static final Comparator<ReferencedReport>             		   comparatorByDate      = (r1, r2) -> r1.getDate().compareTo(r2.getDate());
		public static <ORReport extends ReferencedReport> Comparator<ORReport> comparatorByDate()    { return (r1, r2) -> r1.getDate().compareTo(r2.getDate()); }

		public LocalDate  			getDate();

	}

	public interface LocalizedReport  extends ReferencedReport {
		public static final Comparator<LocalizedReport>                        comparatorByDate      = (r1, r2) -> r1.getDate().compareTo(r2.getDate());
		public static <OLReport extends LocalizedReport> Comparator<OLReport>  comparatorByDate()    { return (r1, r2) -> r1.getDate().compareTo(r2.getDate()); }
		public static final Comparator<LocalizedReport>                        comparatorByCountry   = (r1, r2) -> r1.getCountry().compareTo(r2.getCountry());
		public static <OLReport extends LocalizedReport> Comparator<OLReport>  comparatorByCountry() { return (r1, r2) -> r1.getCountry().compareTo(r2.getCountry()); }

		public static enum Resolution { World, /* CountryGroup, */ Country, /* CountryRegion, City, */ Location, GeoCoordinate; } // Must be refined...

		public default Resolution   getResolution() { return Resolution.Country; }

		public Country    			getCountry();
//		public Location   			getLocation();
//		public Coordinate 			getCoordinate();

	}

}
