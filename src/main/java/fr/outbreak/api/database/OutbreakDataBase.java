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
import java.util.Collection;
import java.util.SortedSet;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.LocalizedReport;
import fr.outbreak.api.records.OutbreakPeriod;

public interface OutbreakDataBase {

	public OutbreakPeriod 							getPeriod();

	public SortedSet<Outbreak.LocalizedReport> 		getGlobalReports	(Outbreak.KpiType _type);
	public Outbreak.LocalizedReport				 	getGlobalReport		(Outbreak.KpiType _type, LocalDate _date);

	public Collection<Outbreak.LocalizedReport> 	getReports 			(Outbreak.KpiType _type);
	public SortedSet<LocalizedReport> 				getReports 			(Outbreak.KpiType _type, LocalDate _date);

	

//	public static interface Localized {

	
	public SortedSet<Country> 						getCountries();
//	public Collection<Location> 					getLocations();
//	public Collection<GeoCoordinate> 				getCoordinates();

//	public long							 			getPopulation(Country _country);

	public SortedSet<Outbreak.LocalizedReport> 		getReports (Outbreak.KpiType _type, Country _country);

	public Outbreak.LocalizedReport 				getReport  (Outbreak.KpiType _type, LocalDate _date, Country _country);

//	}

	
//	public Map<LocalDate, Long>						get(Outbreak.KpiType _type, Outbreak.Population _population);
	
}
