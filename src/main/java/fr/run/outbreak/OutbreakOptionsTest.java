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
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import fr.javafx.scene.PropertyEditors;
import fr.javafx.scene.PropertyListControl;
import fr.javafx.scene.properties.Editor;
import fr.javafx.scene.properties.SelecterMulti;
import fr.javafx.scene.properties.SelecterSingle;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.graphics.OutbreakViewer;
import fr.outbreak.graphics.OutbreakViewerOptions;

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
			SelecterSingle<Integer> singleInt = PropertyEditors.newSingleSelecter(IntStream.range(0, 369).boxed().collect(Collectors.toList()));
			SelecterSingle<Double>  singleDbl = PropertyEditors.newSingleSelecter(DoubleStream.iterate(1d, d -> d < 10d, d -> d * 1.01).boxed().collect(Collectors.toList()));
			SelecterSingle<Country> singleCtr = PropertyEditors.newSingleSelecter(Country.values());
			
			singleInt.setMaxDisplayedItems(2);
			
			SelecterMulti<Integer>  multiInt  = PropertyEditors.newMultiSelecter(IntStream.range(0, 369).boxed().collect(Collectors.toList()));
			SelecterMulti<Double>   multiDbl  = PropertyEditors.newMultiSelecter(DoubleStream.iterate(1d, d -> d < 10d, d -> d * 1.01).boxed().collect(Collectors.toList()));
			SelecterMulti<Country>  multiCtr  = PropertyEditors.newMultiSelecter(Country.values(), Country::getName);

			Editor<Integer>         editInt   = PropertyEditors.newIntegerEditor(-10, 10, 2, 0);
			Editor<Double>          editDbl   = PropertyEditors.newFloatingEditor(-1d, 1d, 0.05d, 0d);

			Editor<LocalDate>		editDate  = PropertyEditors.newLocalDateEditor(LocalDate.now().minusDays(60), LocalDate.now().plusDays(60));
			Editor<Integer>         editDay   = PropertyEditors.newDayEditor(-10, 10);

			OutbreakViewerOptions<?> options = new OutbreakViewerOptions<OutbreakViewer>() { public void initialize(OutbreakViewer _null) {} };

			PropertyListControl s1 = options.addSubPane("test");
			s1.addEntry(editInt.getNode());
			s1.addEntry(editDbl.getNode());

			options.addEntry(editDate.getNode());
			options.addEntry(editDay.getNode());

			options.addEntry(singleInt.getNode());
			options.addEntry(singleDbl.getNode());
			options.addEntry(singleCtr.getNode());

			options.addEntry(multiInt.getNode());
			options.addEntry(multiDbl.getNode());
			options.addEntry(multiCtr.getNode());

			options.addEntry(new Label("another"), new Button("Press me"));

			return new BorderPane(options);
		}

	}

	public static void main(String[] args) {
		Application.launch(DemoApplication.class, args);
	}

}
