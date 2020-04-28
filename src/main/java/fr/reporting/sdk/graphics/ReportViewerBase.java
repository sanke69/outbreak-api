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
package fr.reporting.sdk.graphics;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import fr.reporting.api.Report;
import fr.reporting.api.ReportViewer;

// <DB extends Report.DataBase<R>>
public abstract class ReportViewerBase<R extends Report, DB extends Report.DataBase<R>> extends Control implements ReportViewer<R, DB> {
	private final StringProperty 		titleProperty;
	private final ObjectProperty<Node>	graphicProperty;
	private final ObjectProperty<DB>	databaseProperty;

	protected ReportViewerBase(String _title) {
		this(_title, null, null);
	}
	protected ReportViewerBase(String _title, ObservableValue<? extends DB> _database) {
		this(_title, null, _database);
	}
	protected ReportViewerBase(Node   _graphics) {
		this(null, _graphics, null);
	}
	protected ReportViewerBase(Node   _graphics, ObservableValue<? extends DB> _database) {
		this(null, _graphics, _database);
	}
	protected ReportViewerBase(String _title, Node _graphics) {
		this(_title, _graphics, null);
	}
	protected ReportViewerBase(String _title, Node _graphics, ObservableValue<? extends DB> _database) {
		super();

		titleProperty    = new SimpleStringProperty(_title);
		graphicProperty  = new SimpleObjectProperty<Node>(_graphics);
		databaseProperty = new SimpleObjectProperty<DB>();

		if(_database != null)
			databaseProperty . bind(_database);
	}

	@Override
	protected abstract Skin<? extends ReportViewerBase<R, DB>> 
										createDefaultSkin();

	public final void					setTitle(String _text) {
		titleProperty.set(_text);
	}
	public final String					getTitle() {
		return titleProperty.get();
	}
	@Override
	public final StringProperty 		titleProperty() {
		return titleProperty;
	}

	public final void					setGraphic(Node _graphics) {
		graphicProperty.set(_graphics);
	}
	public final Node					getGraphic() {
		return graphicProperty.get();
	}
	@Override
	public final ObjectProperty<Node>	graphicProperty() {
		return graphicProperty;
	}

	public void 						setDatabase(DB _database) {
		databaseProperty.setValue(_database);
	}
	public DB							getDatabase() {
		return databaseProperty.get();
	}
	@Override
	public ObjectProperty<DB>		
										databaseProperty() {
		return databaseProperty;
	}

}
