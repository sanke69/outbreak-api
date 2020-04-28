/**
 * Report API
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
package fr.reporting.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;

import fr.geodesic.referential.api.countries.Country;

public interface Report {
	public enum      Type { Variation, Value }

	public record    Period      (Instant from, Instant to) {}
	public record    DailyPeriod (LocalDate from, LocalDate to) {}

	public interface Unit extends Report {
		public Instant    getInstant();
	}
	public interface Daily extends Report {
		public static final Comparator<Report.Daily>              comparatorByDate      = (r1, r2) -> r1.getDate().compareTo(r2.getDate());
		public static <DR extends Report.Daily> Comparator<DR>    comparatorByDate()    { return (r1, r2) -> r1.getDate().compareTo(r2.getDate()); }

		public LocalDate  getDate();
	}
	public interface Located extends Report {
		public static final Comparator<Report.Located>            comparatorByCountry   = (r1, r2) -> r1.getCountry().compareTo(r2.getCountry());
		public static <LR extends Report.Located> Comparator<LR>  comparatorByCountry() { return (r1, r2) -> r1.getCountry().compareTo(r2.getCountry()); }

//		public Location             getLocation();

//		public default GeoPosition  getPosition() { return getLocation().getPosition(); }
//		public default Address      getAddress()  { return getLocation().getAddress(); }

//		public default String       getCity()     { return getLocation().getCity(); }
//		public default String       getRegion()   { return getLocation().getRegion(); }
//		public default Country      getCountry()  { return getLocation().getCountry(); }
		public Country 				getCountry();

//		public default Locale       getLocale()   { return getLocation().getLocale(); }

	}

	public interface Aggregator<T> {
		public void 				reset();
		public T    				aggregate(T _t);
	}

	public interface DataBase<R extends Report> {

		public <T> Collection<T> 	getIndicators		(Report.Type _type, Function<R, T> _mapper, boolean _distinct);
		public <T> SortedSet<T> 	getIndicators		(Report.Type _type, Function<R, T> _mapper, Comparator<T> _comparator);

		public Collection <R> 		getReports 			(Report.Type _type);
		public Collection <R> 		getReports 			(Report.Type _type, Predicate<R> _filter);
		public SortedSet  <R> 		getReports 			(Report.Type _type, Comparator<R> _comparator);
		public SortedSet  <R> 		getReports 			(Report.Type _type, Predicate<R> _filter, Comparator<R> _comparator);

		public Collection <R> 		getGlobalReports	(Report.Type _type);
		public Collection <R> 		getGlobalReports	(Report.Type _type, Predicate<R> _filter);
		public SortedSet  <R> 		getGlobalReports 	(Report.Type _type, Comparator<R> _comparator);
		public SortedSet  <R> 		getGlobalReports 	(Report.Type _type, Predicate<R> _filter, Comparator<R> _comparator);

	}
	public interface DailyDataBase<R extends Report.Daily & Report.Located> extends DataBase<R> {

		@Override
		public <T> Collection<T> 	getIndicators		(Report.Type _type, Function<R, T> _mapper, boolean _distinct);
		@Override
		public <T> SortedSet<T> 	getIndicators		(Report.Type _type, Function<R, T> _mapper, Comparator<T> _comparator);

		@Override
		public Collection <R> 		getReports 			(Report.Type _type);
		@Override
		public Collection <R> 		getReports 			(Report.Type _type, Predicate<R> _filter);
		@Override
		public SortedSet  <R> 		getReports 			(Report.Type _type, Comparator<R> _comparator);
		@Override
		public SortedSet  <R> 		getReports 			(Report.Type _type, Predicate<R> _filter, Comparator<R> _comparator);

		public DailyPeriod			getPeriod			();

		@Override
		public Collection <R> 		getGlobalReports	(Report.Type _type);
		@Override
		public Collection <R> 		getGlobalReports	(Report.Type _type, Predicate<R> _filter);
		@Override
		public SortedSet  <R> 		getGlobalReports 	(Report.Type _type, Comparator<R> _comparator);
		@Override
		public SortedSet  <R> 		getGlobalReports 	(Report.Type _type, Predicate<R> _filter, Comparator<R> _comparator);

	}

	public Type getType();

}
