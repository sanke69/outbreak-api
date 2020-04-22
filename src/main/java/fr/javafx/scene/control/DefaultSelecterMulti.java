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
package fr.javafx.scene.control;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

import fr.javafx.scene.control.list.ListSelecter;
import fr.javafx.scene.properties.SelecterMulti;

public class DefaultSelecterMulti<T> extends ListSelecter<T> implements SelecterMulti<T> {

	public DefaultSelecterMulti() {
		super(true);
	}
	public DefaultSelecterMulti(Class<T> _class) {
		super(_class, true);
	}
	public DefaultSelecterMulti(Class<T> _class, Function<T, String> _sc) {
		super(_class, true, _sc);
	}
	public DefaultSelecterMulti(Class<T> _class, StringConverter<T> _sc) {
		super(_class, true, _sc);
	}
	public DefaultSelecterMulti(Class<T> _class, Function<T, String> _sc, Visual _visual) {
		super(_class, true, _sc, _visual);
	}
	public DefaultSelecterMulti(Class<T> _class, StringConverter<T> _sc, Visual _visual) {
		super(_class, true, _sc, _visual);
	}
	public DefaultSelecterMulti(T[] _array) {
		super(Arrays.asList(_array), true);
	}
	public DefaultSelecterMulti(T[] _array, Function<T, String> _sc) {
		super(Arrays.asList(_array), true, _sc);
	}
	public DefaultSelecterMulti(T[] _array, StringConverter<T> _sc) {
		super(Arrays.asList(_array), true, _sc);
	}
	public DefaultSelecterMulti(T[] _array, Function<T, String> _sc, Visual _visual) {
		super(Arrays.asList(_array), true, _sc, _visual);
	}
	public DefaultSelecterMulti(T[] _array, StringConverter<T> _sc, Visual _visual) {
		super(Arrays.asList(_array), true, _sc, _visual);
	}
	public DefaultSelecterMulti(Collection<T> _collection) {
		super(_collection, true);
	}
	public DefaultSelecterMulti(Collection<T> _collection, Function<T, String> _sc) {
		super(_collection, true, _sc);
	}
	public DefaultSelecterMulti(Collection<T> _collection, StringConverter<T> _sc) {
		super(_collection, true, _sc);
	}
	public DefaultSelecterMulti(Collection<T> _collection, Function<T, String> _sc, Visual _visual) {
		super(_collection, true, _sc, _visual);
	}
	public DefaultSelecterMulti(Collection<T> _collection, StringConverter<T> _sc, Visual _visual) {
		super(_collection, true, _sc, _visual);
	}

	@Override
	public Region 				getNode() {
		return this;
	}

	@Override
	public ObservableList<T> 	selectedProperty() {
		return multiSelectionModel().getSelectedItems();
	}

}
