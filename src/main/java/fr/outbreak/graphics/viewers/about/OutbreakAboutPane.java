/**
 * OutBreak API
 * Copyright (C) 2020-?XYZ  Steve PECHBERTI <steve.pechberti@gmail.com>
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
package fr.outbreak.graphics.viewers.about;

import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.graphics.OutbreakViewerBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;

public class OutbreakAboutPane extends OutbreakViewerBase {

    public BorderPane skin;
    public Node       title;
    public Node	      content;

    public OutbreakAboutPane() {
    	this(null);
    }
    public OutbreakAboutPane(Node _title) {
    	this(_title, null);
    }
    public OutbreakAboutPane(Node _title, Node _content) {
    	super("About...");

    	title   = _title   == null ? createDefaultTitle()   : _title;
    	content = _content == null ? createDefaultContent() : _content;

    	BorderPane.setMargin    (title,   new Insets(10, 69, 10, 69));
    	BorderPane.setAlignment (title,   Pos.TOP_CENTER);
    	BorderPane.setMargin    (content, new Insets(10, 69, 33, 69));
    	BorderPane.setAlignment (content, Pos.TOP_CENTER);

    	skin    = new BorderPane(content, title, null, null, null);
    }

    private Node 			createDefaultTitle() {
		String common = "-fx-alignment: center; -fx-text-alignment: center; -fx-content-display: center; -fx-font-weight: bold;";
		String color  = "-fx-text-fill: purple;";
		String font   = "-fx-font-size:60px; -fx-font-family: Biology;";

		Label 
		label = new Label("Covid-19");
		label . setStyle(common + color + font);

		return label;
	}
	private Node 				createDefaultContent() {
        Label about = new Label();
        about.setText(getAboutPrefix() + "             Database is loading..." + "\n");
        about.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 21px; -fx-font-family: 'Monospaced'; -fx-background-color: transparent;");

		databaseProperty().addListener((_obs, _old, _new) -> {
			StringBuilder sb = new StringBuilder();

			sb.append(getAboutPrefix());

			if(_new != null) {
				sb.append("          Database is loaded..." + "\n");
				sb.append("          " + _new.getReports(KpiType.Variation).size() + " records found." + "\n");
				sb.append("            from " + _new.getPeriod().from() + " to " + _new.getPeriod().to() + "\n");
				sb.append("            for " + _new.getIndicators(KpiType.Variation, r -> r.getCountry(), true).size() + " countries" + "\n");
				
			} else
				sb.append("             Database has been cleared..." + "\n");

			about.setText(sb.toString());
		});

		return about;
	}
	
	private String getAboutPrefix() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append("JavaFR: Covid-19" + "\n");
		sb.append("----------------" + "\n");
		sb.append("\n");
		sb.append("    version: 0.0.3" + "\n");
		sb.append("    license: MPL v.2" + "\n");
		sb.append("    author:  Steve PECHBERTI (a.k.a. sanke69)" + "\n");
		sb.append("    email:   steve.pechberti@gmail.com" + "\n");
		sb.append("\n");

		return sb.toString();
	}

	protected Skin<OutbreakAboutPane>  createDefaultSkin() {
		return new Skin<OutbreakAboutPane>() {

			@Override
			public OutbreakAboutPane getSkinnable() {
				return OutbreakAboutPane.this;
			}

			@Override
			public Node getNode() {
				return skin;
			}

			@Override
			public void dispose() {
				;
			}
			
		};
	}

}
