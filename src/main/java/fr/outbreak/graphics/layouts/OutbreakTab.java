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
package fr.outbreak.graphics.layouts;

import fr.javafx.scene.layouts.FitPaneWithSlideMenuOverlay;
import fr.outbreak.graphics.OutbreakViewerBase;
import fr.outbreak.graphics.OutbreakViewer;
import javafx.scene.control.Tab;

public class OutbreakTab extends FitPaneWithSlideMenuOverlay {

	private final OutbreakViewer			viewer;
	private final OutbreakViewer.Options<?>	viewerOptions;

	public <OVP extends OutbreakViewerBase & OutbreakViewer> 
	OutbreakTab(OVP _visual) {
		this(_visual, null);
	}
	public <OVP extends OutbreakViewerBase & OutbreakViewer, OO extends OutbreakOptions<OVP> & OutbreakViewer.Options<OVP>> 
	OutbreakTab(OVP _visual, OO _option) {
		super(_visual, FitPaneWithSlideMenuOverlay.TranslateDirection.RIGHT, OutbreakViewer.Options.width);
		setStyle("-fx-background-color: " + "rgb(69, 169, 69)" + ";");

		if(_option != null) {
			getSlidePane().getChildren().setAll(_option);
			getSlidePane().setMouseTransparent(true);
		} else
			getSlidePane().setStyle("-fx-background-color: " + "rgb(69, 169, 169)" + ";");

		viewer        = _visual;
		viewerOptions = _option;
	}

	public Tab							getTab() {
    	Tab
        tab = new Tab(null, this);
        tab . setGraphic  ( new OutbreakTag( viewer ) );
        tab . setClosable (false);

        return tab;
	}

	public OutbreakViewer 				getViewer() {
		return viewer;
	}
	public OutbreakViewer.Options<?>	getOptions() {
		return viewerOptions;
	}

}
