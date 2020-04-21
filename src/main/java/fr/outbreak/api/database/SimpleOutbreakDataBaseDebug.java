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
import java.util.function.Predicate;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.KpiType;

public class SimpleOutbreakDataBaseDebug {

	public SimpleOutbreakDataBaseDebug(SimpleOutbreakDataBase db) {
		super();
		
		displaySummaryReports(db);
	}

	public void displaySummaryReports(SimpleOutbreakDataBase db) {
		StringBuilder sb = new StringBuilder();

		sb.append("reports:\n\t- count= " + db.dailyReports.size() + "\n");
		sb.append("period:\n\t- from= " + db.getPeriod().from() + ", to= " + db.getPeriod().to() + "\n");
		sb.append("country:\n\t- count= " + db.getIndicators(KpiType.Variation, r -> r.getCountry(), true).size() + "\n");
		
		Outbreak.KpiType 					type      = KpiType.Value;
		LocalDate        					date      = LocalDate.now().minusDays(1);
		Country          					country   = Country.FR;
		Predicate<Outbreak.LocalizedReport> predicate = r -> r.getCountry().equals(country) && r.getDate().equals(date);
		
		sb.append(">  " + db.getReports(type, predicate).parallelStream().findFirst().orElse(null));

//		for(Outbreak.LocalizedReport r : db.getReports(type, date)) {
//			System.out.println(">> " + r.getDate() + "\t" + r.getCountry().getName() + "\t" + r.get(Population.Infected).orElse(0L));
//		}
//		System.out.println(">> TOTAL= " + db.getReports(type, date).stream().mapToLong(s -> s.get(Population.Infected).orElse(0L)).sum() );
		
		
		
		
		

//		for(Outbreak.LocalizedReport r : db.getReports(type, date)) {
//			System.out.println(">> " + r.getDate() + "\t" + r.getCountry().getName() + "\t" + r.get(Population.Infected).orElse(0L));
//		}
		
		
		
		System.out.println(sb.toString());
	}
	
}
