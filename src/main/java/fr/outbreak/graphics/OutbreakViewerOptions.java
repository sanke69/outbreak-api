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

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public abstract class OutbreakViewerOptions<OV extends OutbreakViewer> extends GridPane implements OutbreakViewer.Options<OV> {
	private static final int labelWidth = 120;
	private static final int rowHeight  = 27;

	private static record GridPaneColumnProperty(double width, Color color) {}
	private static final  GridPaneColumnProperty left   = new GridPaneColumnProperty( labelWidth, Color.GRAY  ); 
	private static final  GridPaneColumnProperty right  = new GridPaneColumnProperty( OutbreakViewer.Options.width - labelWidth, Color.GRAY.brighter() );
	private static final  GridPaneColumnProperty unique = new GridPaneColumnProperty( OutbreakViewer.Options.width, left.color.interpolate(right.color, 0.5) );

	public OutbreakViewerOptions() {
		super();
		setPrefWidth( OutbreakViewer.Options.width );
	}

	public abstract void initialize(OV _charts);

	public 			void addEntry(Region _control) {
		final int nextRow = getRowCount();

		_control        . setBackground(new Background(new BackgroundFill(unique.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		_control        . setMinWidth   (unique.width());
		_control        . setPrefWidth  (unique.width());
		_control        . setMaxWidth   (unique.width());
		_control        . setMinHeight  (rowHeight);
		_control        . setMaxHeight  (5 * rowHeight);

		add(_control, 0, nextRow, 2, 1);
	}
	public 			void addEntry(String    _name, Region _control) {
		final int nextRow = getRowCount();

		Label label     = new Label(_name);
		label           . setBackground (new Background(new BackgroundFill(left.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		label           . setMinWidth   (left.width());
		label           . setPrefWidth  (left.width());
		label           . setMaxWidth   (left.width());
		label           . setMinHeight  (rowHeight);
		label           . setPrefHeight (rowHeight);
		label           . setMaxHeight  (rowHeight);

		_control        . setBackground(new Background(new BackgroundFill(right.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		_control        . setMinWidth   (right.width());
		_control        . setPrefWidth  (right.width());
		_control        . setMaxWidth   (right.width());
		_control        . setMinHeight  (rowHeight);
		_control        . setMaxHeight  (5 * rowHeight);

		add(new Label(_name), 0, nextRow, 1, 1);
		add(        _control, 1, nextRow, 1, 1);
	}
	public 			void addEntry(Region   _label, Region _control) {
		final int nextRow = getRowCount();

		_label          . setBackground (new Background(new BackgroundFill(left.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		_label          . setMinWidth   (left.width());
		_label          . setPrefWidth  (left.width());
		_label          . setMaxWidth   (left.width());
		_label          . setMinHeight  (rowHeight);
		_label          . setPrefHeight (rowHeight);
		_label          . setMaxHeight  (rowHeight);

		_control        . setBackground(new Background(new BackgroundFill(right.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		_control        . setMinWidth  (right.width());
		_control        . setPrefWidth (right.width());
		_control        . setMaxWidth  (right.width());
		_control        . setMinHeight  (rowHeight);
		_control        . setMaxHeight  (rowHeight);

		add(_label,   0, nextRow, 1, 1);
		add(_control, 1, nextRow, 1, 1);
	}

}
