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
package fr.run.outbreak.defaults;

import java.util.Arrays;
import java.util.List;

import fr.javafx.scene.PropertyEditors;
import fr.javafx.scene.properties.Editor;
import fr.javafx.scene.properties.SelecterMulti;
import fr.javafx.scene.properties.SelecterSingle;
import fr.outbreak.api.Outbreak;
import fr.outbreak.graphics.layouts.OutbreakOptions;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class TestPaneOptions extends OutbreakOptions<TestPane> {
	private final Editor <Integer> 						dayEditor;
	private final SelecterSingle <Outbreak.Population> 	singleSelecter;
	private final SelecterMulti  <Outbreak.Population>  multiSelecter;

	public TestPaneOptions() {
		super();
		dayEditor      = PropertyEditors.newDayEditor(-69, 69);
		singleSelecter = PropertyEditors.newPopulationSelecterSingle();
		multiSelecter  = PropertyEditors.newPopulationSelecterMulti();

		addEntry(dayEditor.getNode());
		addEntry(singleSelecter.getNode());
		addEntry(multiSelecter.getNode());
	}

	public void                                       	initialize(TestPane _pane) {
		singleSelectionProperty() . addListener((_obs, _old, _new) -> System.out.println("selected:   " + _new));
		multiSelectionProperty()  . addListener((ListChangeListener<Outbreak.Population>) _c -> {
			while(_c.next()) {
				for(Outbreak.Population population : _c.getRemoved())
					System.out.println("unselected: " + population);
				for(Outbreak.Population population : _c.getAddedSubList())
					System.out.println("selected:   " + population);
			}
		});
	}

	public  final List<Node>                           	getNodes() {
		return Arrays.asList(dayEditor.getNode(), singleSelecter.getNode(), multiSelecter.getNode());
	}

	private final ObservableValue<Outbreak.Population>  singleSelectionProperty() {
		return singleSelecter.selectedProperty();
	}
	private final ObservableList<Outbreak.Population> 	multiSelectionProperty() {
		return multiSelecter.selectedProperty();
	}

}
