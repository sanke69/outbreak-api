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
package fr.run.outbreak;

import fr.javafx.scene.layouts.SlidedOverlayControl;

import fr.outbreak.api.OutbreakViewer;
import fr.run.outbreak.defaults.TestPane;
import fr.run.outbreak.defaults.TestPaneOptions;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class OutbreakPaneWithSliderTest {

	public static abstract class 	DemoApplicationBase extends Application {

		public abstract Parent 	getRoot();

		@Override
		public void 			start(Stage stage) throws Exception{
			stage.setScene(new Scene(getRoot(), 960, 480));
			stage.show();
		}

	}
	public static class 			DemoApplication     extends DemoApplicationBase {

		public Parent getRoot() {
//			TestPane        testPane        = new TestPane();
//			TestPaneOptions testPaneOptions = new TestPaneOptions();
			
			SlidedOverlayControl pane = new SlidedOverlayControl(new TestPane(), SlidedOverlayControl.TranslateDirection.RIGHT, OutbreakViewer.Options.width);
			pane.setStyle("-fx-background-color: " + "rgb(69, 169, 69)" + ";");

			pane.getSlidePane().getChildren().setAll(new TestPaneOptions());
//			pane.getSlidePane().setMouseTransparent(true);

			Button btn = new Button("OPTIONS");
			btn.setOnAction(e -> pane . handle(e));

			return new BorderPane(pane, null, null, btn, null);
		}

	}

	public static void main(String[] args) {
		Application.launch(DemoApplication.class, args);
	}

}
