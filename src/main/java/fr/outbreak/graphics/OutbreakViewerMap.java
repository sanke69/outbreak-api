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
package fr.outbreak.graphics;

public interface OutbreakViewerMap extends OutbreakViewer {

	public static record CountryInfo(long population, long susceptible, long infected, long recovered, long immuned, long dead) {}

//	public void setCountryInfos   (Map<Country, CountryInfo> _countryInfos);

//	public void setCountryStyle   (Country _country, String _style);
//	public void setCountryOnClick (Country _country, EventHandler<MouseEvent> _clickHandler);

}
