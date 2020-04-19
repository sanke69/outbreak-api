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

import fr.outbreak.api.database.OutbreakDataBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public abstract class OutbreakViewerBase extends Control implements OutbreakViewer {
	private final StringProperty 					textProperty;
	private final ObjectProperty<Node>				graphicProperty;
	private final ObjectProperty<OutbreakDataBase>	databaseProperty;

	protected OutbreakViewerBase(String _text) {
		this(_text, null, null);
	}
	protected OutbreakViewerBase(String _text, ObservableValue<OutbreakDataBase> _database) {
		this(_text, null, _database);
	}
	protected OutbreakViewerBase(Node   _graphics) {
		this(null, _graphics, null);
	}
	protected OutbreakViewerBase(Node   _graphics, ObservableValue<OutbreakDataBase> _database) {
		this(null, _graphics, _database);
	}
	protected OutbreakViewerBase(String _text, Node _graphics) {
		this(_text, _graphics, null);
	}
	protected OutbreakViewerBase(String _text, Node _graphics, ObservableValue<OutbreakDataBase> _database) {
		super();

		textProperty     = new SimpleStringProperty(_text);
		graphicProperty  = new SimpleObjectProperty<Node>(_graphics);
		databaseProperty = new SimpleObjectProperty<OutbreakDataBase>();
		
		if(_database != null)
			databaseProperty . bind(_database);
	}

	protected abstract Skin<? extends OutbreakViewerBase>     createDefaultSkin();

	public final void									setText(String _text) {
		textProperty.set(_text);
	}
	public final String									getText() {
		return textProperty.get();
	}
	public final StringProperty 						textProperty() {
		return textProperty;
	}

	public final void									setGraphic(Node _graphics) {
		graphicProperty.set(_graphics);
	}
	public final Node									getGraphic() {
		return graphicProperty.get();
	}
	public final ObjectProperty<Node>					graphicProperty() {
		return graphicProperty;
	}

	public final void 									setDatabase(OutbreakDataBase _database) {
		databaseProperty.setValue(_database);
	}
	public final OutbreakDataBase 						getDatabase() {
		return databaseProperty.get();
	}
	public final ObjectProperty<OutbreakDataBase> 		databaseProperty() {
		return databaseProperty;
	}

}
