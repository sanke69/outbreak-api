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
package fr.run.outbreak.defaults;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;

import fr.outbreak.api.Outbreak;
import fr.reporting.sdk.graphics.ReportViewerBase;

public class TestPane extends ReportViewerBase<Outbreak.Report, Outbreak.DataBase> {

	public TestPane() {
		super("Test View");
	}
	public TestPane(String _title) {
		super(_title);
	}

	@Override
	protected Skin<TestPane> createDefaultSkin() {
		return new Skin<TestPane>() {
			Node skin = new BorderPane(new Label("OUTBREAK TEST PANE"));
			@Override public TestPane getSkinnable() 	{ return TestPane.this; }
			@Override public Node getNode() 					{ return skin; }
			@Override public void dispose() 					{  }
		};
	}

}
