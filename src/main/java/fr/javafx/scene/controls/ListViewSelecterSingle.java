/**
 * OutBreak API
 * Copyright (C) 2007-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
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
package fr.javafx.scene.controls;

import java.util.Arrays;
import java.util.Collection;

import fr.javafx.scene.properties.SelecterSingle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

public class ListViewSelecterSingle<T> implements SelecterSingle<T> {
	private final ListSelecter<T> control;

	public ListViewSelecterSingle() {
		super();
		control = new ListSelecter<T>(false);
	}
	public ListViewSelecterSingle(Class<T> _class) {
		super();
		control = new ListSelecter<T>(_class, false);
	}
	public ListViewSelecterSingle(Class<T> _class, StringConverter<T> _sc) {
		super();
		control = new ListSelecter<T>(_class, false, _sc);
	}
	public ListViewSelecterSingle(T[] _array) {
		super();
		control = new ListSelecter<T>(Arrays.asList(_array), false);
	}
	public ListViewSelecterSingle(T[] _array, StringConverter<T> _sc) {
		super();
		control = new ListSelecter<T>(Arrays.asList(_array), false, _sc);
	}
	public ListViewSelecterSingle(Collection<T> _collection) {
		super();
		control = new ListSelecter<T>(_collection, false);
	}
	public ListViewSelecterSingle(Collection<T> _collection, StringConverter<T> _sc) {
		super();
		control = new ListSelecter<T>(_collection, false, _sc);
	}

	@Override
	public Region 				getNode() {
		return control;
	}

	@Override
	public ObservableList<T> 	itemsProperty() {
		return control.itemsProperty();
	}

	@Override
	public ObservableValue<T> 	selectedProperty() {
		return control.singleSelectionModel().selectedItemProperty();
	}

}
