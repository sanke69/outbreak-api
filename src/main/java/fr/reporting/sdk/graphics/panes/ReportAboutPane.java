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
package fr.reporting.sdk.graphics.panes;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;

import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.ReportViewerBase;

public class ReportAboutPane<R extends Report, DB extends Report.DataBase<R>> extends ReportViewerBase<R, DB> {
    public BorderPane    skin;
    public Labeled       title;
    public Labeled       content;
   
    final StringProperty textProperty;

    public ReportAboutPane() {
    	this("SnK-Reporting");
    }
    public ReportAboutPane(String _title) {
    	this(createDefaultTitle(_title), null);
    }
    public ReportAboutPane(String _title, Labeled _content) {
    	this(createDefaultTitle(_title), _content);
    }
    public ReportAboutPane(Labeled _title, Labeled _content) {
    	super(_title.getText());

    	title   = _title;
    	content = _content == null ? createDefaultContent() : _content;

    	textProperty = content.textProperty();

    	BorderPane.setMargin    (title,   new Insets(10, 69, 10, 69));
    	BorderPane.setAlignment (title,   Pos.TOP_CENTER);
    	BorderPane.setMargin    (content, new Insets(10, 69, 33, 69));
    	BorderPane.setAlignment (content, Pos.TOP_CENTER);

    	skin = new BorderPane(content, title, null, null, null);
    }

    public void				textProperty(String _text) {
    	textProperty.set(_text);
    }
    public String 			getText() {
    	return textProperty.get();
    }
    public StringProperty   textProperty() {
    	return textProperty;
    }

	protected Skin<? extends ReportAboutPane<R, DB>> 	createDefaultSkin() {
		return new Skin<ReportAboutPane<R, DB>>() {

			@Override
			public ReportAboutPane<R, DB> getSkinnable() {
				return ReportAboutPane.this;
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

    private static Labeled	createDefaultTitle(String _title) {
		String common = "-fx-alignment: center; -fx-text-alignment: center; -fx-content-display: center; -fx-font-weight: bold;";
		String color  = "-fx-text-fill: purple;";
		String font   = "-fx-font-size:60px; -fx-font-family: Biology;";

		Label 
		label = new Label(_title);
		label . setStyle(common + color + font);

		return label;
	}
	private static Label 	createDefaultContent() {
        Label 
        content = new Label();
        content . setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 21px; -fx-font-family: 'Monospaced'; -fx-background-color: transparent;");

		return content;
	}

}
