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
package fr.outbreak.sdk.data;

import java.time.LocalDate;
import java.util.Optional;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.Population;
import fr.reporting.api.Report;

public class OutbreakReportBeanFx implements Outbreak.Report {
	private ObjectProperty<Report.Type>   type			= new SimpleObjectProperty<Report.Type>();

	private ObjectProperty<LocalDate>  	  date			= new SimpleObjectProperty<LocalDate>();
	private ObjectProperty<Country>    	  country		= new SimpleObjectProperty<Country>();
//	private ObjectProperty<Location>      location		= new SimpleObjectProperty<Location>();
//	private ObjectProperty<GeoCoordinate> coordinate	= new SimpleObjectProperty<GeoCoordinate>();

	private LongProperty    			  susceptible	= new SimpleLongProperty();
	private LongProperty    			  infected		= new SimpleLongProperty();
	private LongProperty    			  dead			= new SimpleLongProperty();
	private LongProperty    			  recovered		= new SimpleLongProperty();

	public OutbreakReportBeanFx() {
		super();
	}
	public OutbreakReportBeanFx(Outbreak.Report _record) {
		this();
		setDate			(_record.getDate());
		setCountry		(_record.getCountry());
		setSusceptible	(_record.get(Population.Susceptible) .orElse(null));
		setInfected		(_record.get(Population.Infected)    .orElse(null));
		setRecovered	(_record.get(Population.Recovered)   .orElse(null));
		setDead			(_record.get(Population.Dead)        .orElse(null));
	}

	public void 								setType(Report.Type _type) 		{ type.set(_type); }
	@Override public Report.Type 				getType() 						{ return type.get(); }
	public ReadOnlyObjectProperty<Report.Type>  typeProperty() 					{ return type; }

	public Optional<Long> 						get(Population _population) {
		return switch(_population) {
		case Susceptible -> Optional.ofNullable(getSusceptible());
		case Infected    -> Optional.ofNullable(getInfected());
		case Dead        -> Optional.ofNullable(getDead());
		case Immuned     -> Optional.empty();
		case Recovered   -> Optional.ofNullable(getRecovered());
		case Total       -> Optional.ofNullable(getSusceptible() + getInfected());
		case Alive       -> throw new UnsupportedOperationException("Unimplemented case: " + _population);
		default          -> throw new IllegalArgumentException("Unexpected value: " + _population);
		};
	}

	public void 								setDate(LocalDate _date) 		{ date.set(_date); }
	@Override public LocalDate 					getDate() 						{ return date.get(); }
	public ReadOnlyObjectProperty<LocalDate>  	dateProperty() 					{ return date; }

	public void 								setCountry(Country _country) 	{ country.set(_country); }
	@Override public Country 					getCountry() 					{ return country.get(); }
	public ReadOnlyObjectProperty<Country>  	countryProperty() 				{ return country; }

	public void 								setSusceptible(long _pop) 		{ susceptible.set(_pop); }
	public Long 								getSusceptible() 				{ return susceptible.get(); }
	public LongProperty 						susceptibleProperty() 			{ return susceptible; }

	public void 								setInfected(long _infected) 	{ infected.set(_infected); }
	public Long 								getInfected() 					{ return infected.get(); }
	public LongProperty 						infectedProperty() 				{ return infected; }

	public void 								setRecovered(long _recovered) 	{ recovered.set(_recovered); }
	public Long 								getRecovered() 					{ return recovered.get(); }
	public LongProperty 						recoveredProperty() 			{ return recovered; }

	public void 								setDead(long _deaths) 			{ dead.set(_deaths); }
	public Long 								getDead() 						{ return dead.get(); }
	public LongProperty 						deadProperty() 					{ return dead; }

}