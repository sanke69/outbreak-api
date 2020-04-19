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
package fr.outbreak.api.records;

import java.time.LocalDate;
import java.util.Optional;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.api.Outbreak.Population;

public record OutbreakRecord(KpiType type, 
							 LocalDate date, Country country, 
							 Long susceptible, Long infected, Long dead, Long recovered) implements Outbreak.LocalizedReport {

	public OutbreakRecord(KpiType _type, 
							LocalDate _date, Country _country, 
							Optional<Long> _susceptible, Optional<Long> _infected, Optional<Long> _recovered, Optional<Long> _dead) {
		this(_type, _date, _country, _infected.orElse(null), _infected.orElse(null), _dead.orElse(null), _recovered.orElse(null));
	}

	@Override public KpiType 	getType()        { return type; }

	@Override public LocalDate 	getDate()        { return date; }
	@Override public Country 	getCountry()     { return country; }

	public Optional<Long> 		get(Population _population) {
		return switch(_population) {
		case Susceptible -> Optional.of(susceptible);
		case Infected    -> Optional.of(infected);
		case Dead        -> Optional.of(dead);
		case Immuned     -> Optional.empty();
		case Recovered   -> Optional.of(recovered);
		case Alive       -> Optional.of(susceptible + infected);
		case Total       -> Optional.of(susceptible + infected);
		};
	}

}
