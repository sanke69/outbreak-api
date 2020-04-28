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
package fr.javafx.scene.layouts;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;

import fr.reporting.api.ReportViewer;

public class TabberTag extends Control {
	private Node  skin;
	private Label label;

	public TabberTag(String _text) {
		super();

		label = new Label(_text);
//		label . textProperty().bind(_viewer.textProperty());
//		label . graphicProperty().bind(_viewer.graphicProperty());

		skin  = new StackPane(new Group(label));
	}
	public TabberTag(ReportViewer<?,?> _viewer) {
		super();

		label = new Label();
		label . textProperty().bind(_viewer.titleProperty());
		label . graphicProperty().bind(_viewer.graphicProperty());

		skin  = new StackPane(new Group(label));
	}

	protected Skin<TabberTag> 	createDefaultSkin() {
		return new Skin<TabberTag>() {
			@Override public TabberTag  getSkinnable()  { return TabberTag.this; }
			@Override public Node 		getNode() 		{ return skin; }
			@Override public void 		dispose() 		{  }
		};
	}

}
