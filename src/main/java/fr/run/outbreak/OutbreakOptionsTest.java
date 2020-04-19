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

import java.time.LocalDate;

import fr.geodesic.referential.api.countries.Country;
import fr.javafx.scene.PropertyEditors;
import fr.outbreak.graphics.OutbreakViewerOptions;
import fr.outbreak.graphics.OutbreakViewer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class OutbreakOptionsTest {

	public static abstract class 	DemoApplicationBase extends Application {

		public abstract Parent 	getRoot();

		@Override
		public void 			start(Stage stage) throws Exception{
			stage.setScene(new Scene(getRoot()));
			stage.show();
		}

	}
	public static class 			DemoApplication     extends DemoApplicationBase {

		public Parent getRoot() {
			OutbreakViewerOptions<?> options = new OutbreakViewerOptions<OutbreakViewer>() { public void initialize(OutbreakViewer _null) {} };

			options.addEntry(PropertyEditors.newIntegerEditor(-10, 10, 2, 0).getNode());
			options.addEntry(PropertyEditors.newFloatingEditor(-1d, 1d, 0.05d, 0d).getNode());
			options.addEntry(PropertyEditors.newDayEditor().getNode());
			options.addEntry(PropertyEditors.newLocalDateEditor(LocalDate.now().minusDays(60), LocalDate.now().plusDays(60)).getNode());
			options.addEntry("property-0", PropertyEditors.newSingleSelecter(Country.class).getNode());
			options.addEntry("property-1", PropertyEditors.newDayEditor().getNode());
			options.addEntry(new TextArea("property-2"), PropertyEditors.newDayEditor().getNode());

			return new BorderPane(options);
		}

	}

	public static void main(String[] args) {
		Application.launch(DemoApplication.class, args);
	}

}
