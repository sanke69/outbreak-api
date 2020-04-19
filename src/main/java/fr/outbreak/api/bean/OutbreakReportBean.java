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
package fr.outbreak.api.bean;

import java.time.LocalDate;
import java.util.Optional;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.api.Outbreak.Population;

public class OutbreakReportBean implements Outbreak.LocalizedReport {
	private KpiType 		type;
	private Resolution  	resolution;

	private LocalDate   	date;
	private Country   		country;
//	private Location   		location;
//	private GeoCoordinate	coordinate;

	private Long   			susceptible;
	private Long   			infected;
	private Long   			recovered;
	private Long   			dead;

	public void 				setType(KpiType _type) 					{ type = _type; }
	@Override public KpiType	getType() 								{ return type; }

	public void 				setResolution(Resolution _resolution) 	{ resolution = _resolution; }
	@Override public Resolution getResolution() 						{ return resolution; }

	public void 				setDate(LocalDate _date) 				{ date = _date; }
	@Override public LocalDate 	getDate() 								{ return date; }

	public void 				setCountry(Country _country)			{ country = _country; }
	@Override public Country 	getCountry() 							{ return country; }

	public Optional<Long> 		get(Population _population) {
		return switch(_population) {
		case Susceptible -> Optional.ofNullable( getSusceptible() );
		case Infected    -> Optional.ofNullable( getInfected() );
		case Dead        -> Optional.ofNullable( getDead() );
		case Immuned     -> Optional.empty();
		case Recovered   -> Optional.ofNullable( getRecovered() );
		case Total       -> throw new UnsupportedOperationException("Unimplemented case: " + _population);
		case Alive       -> throw new UnsupportedOperationException("Unimplemented case: " + _population);
		default          -> throw new IllegalArgumentException("Unexpected value: " + _population);
		};
	}

	public void 				setSusceptible(long _susceptible) 		{ susceptible = _susceptible; }
	public Long 				getSusceptible() 						{ return susceptible; }

	public void 				setInfected(long _infected) 			{ infected = _infected; }
	public Long 				getInfected() 							{ return infected; }

	public void 				setDead(long _dead)	 					{ dead = _dead; }
	public Long					getDead() 								{ return dead; }

	public void 				setRecovered(long _recovered) 			{ recovered = _recovered; }
	public Long 				getRecovered() 							{ return recovered; }

}
