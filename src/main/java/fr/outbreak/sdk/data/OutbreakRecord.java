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

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.Population;
import fr.reporting.api.Report;

public record OutbreakRecord(Report.Type type, 
							 LocalDate date, Country country, 
							 Long susceptible, Long infected, Long recovered, Long immuned, Long dead) implements Outbreak.Report {

	public OutbreakRecord(long _susceptible, long _infected) {
		this(null, null, null, _susceptible, _infected, null, null, null);
	}
	public OutbreakRecord(long _susceptible, long _infected, long _recovered) {
		this(null, null, null, _susceptible, _infected, _recovered, null, null);
	}
	public OutbreakRecord(long _susceptible, long _infected, long _recovered, long _dead) {
		this(null, null, null, _susceptible, _infected, _recovered, null, _dead);
	}
	public OutbreakRecord(long _susceptible, long _infected, long _recovered, long _immuned, long _dead) {
		this(null, null, null, _susceptible, _infected, _recovered, _immuned, _dead);
	}
	
	public OutbreakRecord(Report.Type _type, Long _susceptible, Long _infected) {
		this(_type, null, null, _susceptible, _infected, null, null, null);
	}
	public OutbreakRecord(Report.Type _type, Long _susceptible, Long _infected, Long _recovered) {
		this(_type, null, null, _susceptible, _infected, _recovered, null, null);
	}
	public OutbreakRecord(Report.Type _type, Long _susceptible, Long _infected, Long _recovered, Long _dead) {
		this(_type, null, null, _susceptible, _infected, _recovered, null, _dead);
	}
	public OutbreakRecord(Report.Type _type, Long _susceptible, Long _infected, Long _recovered, Long _immuned, Long _dead) {
		this(_type, null, null, _susceptible, _infected, _recovered, _immuned, _dead);
	}

	public OutbreakRecord(Report.Type _type, LocalDate date, Long _susceptible, Long _infected) {
		this(_type, date, null, _susceptible, _infected, null, null, null);
	}
	public OutbreakRecord(Report.Type _type, LocalDate date, Long _susceptible, Long _infected, Long _recovered) {
		this(_type, date, null, _susceptible, _infected, _recovered, null, null);
	}
	public OutbreakRecord(Report.Type _type, LocalDate date, Long _susceptible, Long _infected, Long _recovered, Long _dead) {
		this(_type, date, null, _susceptible, _infected, _recovered, null, _dead);
	}
	public OutbreakRecord(Report.Type _type, LocalDate date, Long _susceptible, Long _infected, Long _recovered, Long _immuned, Long _dead) {
		this(_type, date, null, _susceptible, _infected, _recovered, _immuned, _dead);
	}

	public OutbreakRecord(Report.Type _type, LocalDate date, Country country, Long _susceptible, Long _infected) {
		this(_type, date, country, _susceptible, _infected, null, null, null);
	}
	public OutbreakRecord(Report.Type _type, LocalDate date, Country country, Long _susceptible, Long _infected, Long _recovered) {
		this(_type, date, country, _susceptible, _infected, _recovered, null, null);
	}
	public OutbreakRecord(Report.Type _type, LocalDate date, Country country, Long _susceptible, Long _infected, Long _recovered, Long _dead) {
		this(_type, date, country, _susceptible, _infected, _recovered, null, _dead);
	}
	public OutbreakRecord(Report.Type _type, LocalDate date, Country country, Optional<Long> _susceptible, Optional<Long> _infected, Optional<Long> _recovered, Optional<Long> _dead) {
		this(_type, date, country, _susceptible.orElse(null), _infected.orElse(null), _recovered.orElse(null), null, _dead.orElse(null));
	}

	@Override public Report.Type 	getType()        { return type; }

	@Override public LocalDate 		getDate()        { return date; }
	@Override public Country 		getCountry()     { return country; }

	public Optional<Long> 			get(Population _population) {
		return switch(_population) {
		case Susceptible -> Optional.ofNullable(susceptible);
		case Infected    -> Optional.ofNullable(infected);
		case Recovered   -> Optional.ofNullable(recovered);
		case Immuned     -> Optional.empty();
		case Dead        -> Optional.ofNullable(dead);
		case Alive       -> Optional.ofNullable(susceptible + infected);
		case Total       -> Optional.ofNullable(susceptible + infected + dead);
		};
	}

}
