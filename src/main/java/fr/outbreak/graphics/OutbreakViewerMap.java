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

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;

public interface OutbreakViewerMap extends OutbreakViewer {

	public void setCountryColor   (Country _country, Color _fill);
	public void setCountryInfos   (Country _country, Outbreak.Report _infos);
	public void setCountryOnClick (Country _country, EventHandler<MouseEvent> _clickHandler);

}
