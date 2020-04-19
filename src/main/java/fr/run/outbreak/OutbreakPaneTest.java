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

import fr.run.outbreak.defaults.TestPane;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class OutbreakPaneTest {

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
			return new TestPane();
		}

	}

	public static void main(String[] args) {
		Application.launch(DemoApplication.class, args);
	}

}
