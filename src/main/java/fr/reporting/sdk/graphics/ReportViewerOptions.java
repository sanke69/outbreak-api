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

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

import fr.javafx.scene.PropertyListControl;

import fr.reporting.api.ReportViewer;

public abstract class ReportViewerOptions<RV extends ReportViewer<?,?>> extends Control implements ReportViewer.Options<RV> {
	private final PropertyListControl content;

	public ReportViewerOptions() {
		super();
		setPrefWidth( ReportViewer.Options.width );

		content = new PropertyListControl();
	}

	public abstract void initialize(RV _charts);

	public 			void addEntry(Region _control) {
		content.addEntry(_control);
	}
	public 			void addEntry(String    _name, Region _control) {
		content.addEntry(_name,  _control);
	}
	public 			void addEntry(Labeled   _label, Region _control) {
		content.addEntry(_label, _control);
	}

	public PropertyListControl addSubPane(String _name) {
		return content.addSubPane(_name);
	}

	protected Skin<? extends ReportViewerOptions<RV>> createDefaultSkin() {
		return new Skin<ReportViewerOptions<RV>>() {

			@Override
			public ReportViewerOptions<RV> getSkinnable() {
				return ReportViewerOptions.this;
			}

			@Override
			public Node getNode() {
				return content;
			}

			@Override
			public void dispose() {
				
			}
			
		};
	}

}
