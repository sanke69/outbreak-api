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

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;

import fr.outbreak.api.Outbreak;
import fr.outbreak.api.records.OutbreakPeriod;

public interface OutbreakDataBase {

	public OutbreakPeriod 							getPeriod();

	public <T> Collection<T> 						getIndicators		(Outbreak.KpiType _type, Function<Outbreak.LocalizedReport, T> _mapper, boolean _distinct);
	public <T> SortedSet<T> 						getIndicators		(Outbreak.KpiType _type, Function<Outbreak.LocalizedReport, T> _mapper, Comparator<T> _comparator);

	public Collection <Outbreak.LocalizedReport> 	getGlobalReports	(Outbreak.KpiType _type);
	public Collection <Outbreak.LocalizedReport> 	getGlobalReports	(Outbreak.KpiType _type, Predicate<Outbreak.LocalizedReport> _filter);
	public SortedSet  <Outbreak.LocalizedReport> 	getGlobalReports 	(Outbreak.KpiType _type, Comparator<Outbreak.LocalizedReport> _comparator);
	public SortedSet  <Outbreak.LocalizedReport> 	getGlobalReports 	(Outbreak.KpiType _type, Predicate<Outbreak.LocalizedReport> _filter, Comparator<Outbreak.LocalizedReport> _comparator);

	public Collection <Outbreak.LocalizedReport> 	getReports 			(Outbreak.KpiType _type);
	public Collection <Outbreak.LocalizedReport> 	getReports 			(Outbreak.KpiType _type, Predicate<Outbreak.LocalizedReport> _filter);
	public SortedSet  <Outbreak.LocalizedReport> 	getReports 			(Outbreak.KpiType _type, Comparator<Outbreak.LocalizedReport> _comparator);
	public SortedSet  <Outbreak.LocalizedReport> 	getReports 			(Outbreak.KpiType _type, Predicate<Outbreak.LocalizedReport> _filter, Comparator<Outbreak.LocalizedReport> _comparator);

}
