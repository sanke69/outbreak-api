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
package fr.run.report;

import java.io.IOException;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.ReportStage;
import fr.reporting.sdk.graphics.ReportViewerBase;

public class ReportStageTest extends ReportApplicationBase {

	class DummyChart extends ReportViewerBase<TestReport, Report.DataBase<TestReport>> {

		protected DummyChart() {
			super("DemoChart");
		}

		@Override
		protected Skin<DummyChart> createDefaultSkin() {
			return new Skin<DummyChart>() {
				BorderPane pane = null;

				@Override
				public DummyChart getSkinnable() {
					return DummyChart.this;
				}

				@Override
				public Node getNode() {
					if(pane != null)
						return pane;

					pane = new BorderPane();
					pane . backgroundProperty()
						 . bind		(Bindings.when(pane.hoverProperty())
				         . then		(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)))
				         . otherwise(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))));

					return pane;
				}

				@Override
				public void dispose() {
					;
				}
				
			};
		}
		
	}
	
	public void setViewers(ReportStage<TestReport, Report.DataBase<TestReport>> _stage) {
		_stage.registerViewerPane(new DummyChart());
	}

	public static void main(String[] args) throws IOException {
		Application.launch(ReportStageTest.class, args);
	}

}
