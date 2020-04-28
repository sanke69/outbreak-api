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

public interface OutbreakForecast extends Outbreak {

	// Forecast
	/**
	 * Default based on SIS outbreak model, i.e. Birth = Death
	 *
	 */
	public interface 	PopulationRates { 
		public double			getBirthRate();
		public default double 	getDeathRate() { return getBirthRate(); }
	}

	/**
	 * Default based on SIS outbreak model, i.e. only Susceptible & Infected.
	 *
	 */
	public interface 	OutbreakRates {
		public double 			getInfectionRate();
		public double 			getRecoveringRate();
		public default double 	getImmunityRate()	{ return 0d; };
		public default double 	getDeathRate()		{ return 0d; };
	}

	@FunctionalInterface
	public interface 	DeterministSolver {

		public Report[] 	compute(long S0, long I0, int _nbEpochs);

	}

	public PopulationRates   	getPopulationRates();
	public OutbreakRates     	getOutbreakRates();
	public DeterministSolver 	getSolver();

}
