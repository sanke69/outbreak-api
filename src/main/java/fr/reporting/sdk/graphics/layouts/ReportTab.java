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
package fr.reporting.sdk.graphics.layouts;

import javafx.scene.control.Tab;

import fr.javafx.scene.layouts.SlidedOverlayControl;
import fr.javafx.scene.layouts.TabberTag;

import fr.reporting.api.Report;
import fr.reporting.api.ReportViewer;
import fr.reporting.sdk.graphics.ReportViewerBase;
import fr.reporting.sdk.graphics.ReportViewerOptions;

public class ReportTab extends SlidedOverlayControl {

	private final ReportViewer<?,?>			viewer;
	private final ReportViewer.Options<?>	viewerOptions;

	public <R extends Report, DB extends Report.DataBase<R>, OVP extends ReportViewerBase<R,DB> & ReportViewer<R,DB>> 
	ReportTab(OVP _visual) {
		this(_visual, null);
	}
	public <R extends Report, DB extends Report.DataBase<R>, OVP extends ReportViewerBase<R,DB> & ReportViewer<R,DB>, OO extends ReportViewerOptions<OVP> & ReportViewer.Options<OVP>> 
	ReportTab(OVP _visual, OO _option) {
		super(_visual, SlidedOverlayControl.TranslateDirection.RIGHT, ReportViewer.Options.width);

		if(_option != null)
			getSlidePane().getChildren().setAll(_option);
		else
			getSlidePane().setStyle("-fx-background-color: darkgray;");

		viewer        = _visual;
		viewerOptions = _option;
	}

	public Tab							getTab() {
    	Tab
        tab = new Tab(null, this);
        tab . setGraphic  ( new TabberTag( viewer ) );
        tab . setClosable (false);

        return tab;
	}

	public ReportViewer<?,?> 			getViewer() {
		return viewer;
	}
	public ReportViewer.Options<?>		getOptions() {
		return viewerOptions;
	}

}
