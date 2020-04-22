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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;

import fr.outbreak.api.database.OutbreakDataBase;

public interface OutbreakViewer {

	public interface Options<OV extends OutbreakViewer> {
		public static final int width = 320;
	
		public abstract void initialize(OV _charts);
	
		public 			void addEntry(Region _control);
		public 			void addEntry(String    _name, Region _control);
		public 			void addEntry(Labeled  _label, Region _control);
	
	}

	public StringProperty 						textProperty();
	public ObjectProperty<Node>					graphicProperty();

	public ObjectProperty<OutbreakDataBase> 	databaseProperty();

}
