/**
 * JavaFR
 * Copyright (C) 2007-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
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
package fr.java.time;

public enum TimeUnit {
	MILLENIUM,
	CENTURY,
	DECADE,
	YEAR, 
	MONTH, 
	WEEK, 
	DAY, 
	HOUR, 
	MINUTE, 
	SECOND,
	MILLISECOND,
	NANOSECOND;
	
	public TimeUnit upper() {
		int i = ordinal();
		i = i - 1 > 0 ? i - 1 : i;
		return values()[i];
	}
	public TimeUnit lower() {
		int i = ordinal();
		i = i + 1 < values().length ? i + 1 : i;
		return values()[i];
	}
	
}
