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

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import fr.javafx.scene.PropertyListControl;

public abstract class OutbreakViewerOptions<OV extends OutbreakViewer> extends Control implements OutbreakViewer.Options<OV> {
	private static final int labelWidth = 120;
	private static final int rowHeight  = 27;

	private static record GridPaneColumnProperty(double width, Color color) {}
	private static final  GridPaneColumnProperty left   = new GridPaneColumnProperty( labelWidth, Color.GRAY  ); 
	private static final  GridPaneColumnProperty right  = new GridPaneColumnProperty( OutbreakViewer.Options.width - labelWidth, Color.GRAY.brighter() );
	private static final  GridPaneColumnProperty unique = new GridPaneColumnProperty( OutbreakViewer.Options.width, left.color.interpolate(right.color, 0.5) );

	private final PropertyListControl content;

	public OutbreakViewerOptions() {
		super();
		setPrefWidth( OutbreakViewer.Options.width );

		content = new PropertyListControl();
	}

	public abstract void initialize(OV _charts);

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

	protected Skin<OutbreakViewerOptions<OV>> createDefaultSkin() {
		return new Skin<OutbreakViewerOptions<OV>>() {

			@Override
			public OutbreakViewerOptions<OV> getSkinnable() {
				return OutbreakViewerOptions.this;
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
