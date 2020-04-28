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

import java.util.Optional;

public interface Outbreak {

	// Specific to Outbreak
	public enum      Population { Susceptible, Infected, Recovered, Immuned, Dead, Alive, Total; }

	// Specialization
	public interface Report   extends fr.reporting.api.Report.Daily, fr.reporting.api.Report.Located { public Optional<Long> get(Population _population); }
	public interface DataBase extends fr.reporting.api.Report.DailyDataBase<Report> { }

}
