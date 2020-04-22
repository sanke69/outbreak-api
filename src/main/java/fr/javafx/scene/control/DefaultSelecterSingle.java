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

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

import fr.javafx.scene.control.list.ListSelecter;
import fr.javafx.scene.properties.SelecterSingle;

public class DefaultSelecterSingle<T> extends ListSelecter<T> implements SelecterSingle<T> {

	public DefaultSelecterSingle() {
		super(false);
	}
	public DefaultSelecterSingle(Class<T> _class) {
		super(_class, false);
	}
	public DefaultSelecterSingle(Class<T> _class, Function<T, String> _sc) {
		super(_class, false, _sc);
	}
	public DefaultSelecterSingle(Class<T> _class, StringConverter<T> _sc) {
		super(_class, false, _sc);
	}
	public DefaultSelecterSingle(Class<T> _class, Function<T, String> _sc, Visual _visual) {
		super(_class, false, _sc, _visual);
	}
	public DefaultSelecterSingle(Class<T> _class, StringConverter<T> _sc, Visual _visual) {
		super(_class, false, _sc, _visual);
	}
	public DefaultSelecterSingle(T[] _array) {
		super(Arrays.asList(_array), false);
	}
	public DefaultSelecterSingle(T[] _array, Function<T, String> _sc) {
		super(Arrays.asList(_array), false, _sc);
	}
	public DefaultSelecterSingle(T[] _array, StringConverter<T> _sc) {
		super(Arrays.asList(_array), false, _sc);
	}
	public DefaultSelecterSingle(T[] _array, Function<T, String> _sc, Visual _visual) {
		super(Arrays.asList(_array), false, _sc, _visual);
	}
	public DefaultSelecterSingle(T[] _array, StringConverter<T> _sc, Visual _visual) {
		super(Arrays.asList(_array), false, _sc, _visual);
	}
	public DefaultSelecterSingle(Collection<T> _collection) {
		super(_collection, false);
	}
	public DefaultSelecterSingle(Collection<T> _collection, Function<T, String> _sc) {
		super(_collection, false, _sc);
	}
	public DefaultSelecterSingle(Collection<T> _collection, StringConverter<T> _sc) {
		super(_collection, false, _sc);
	}
	public DefaultSelecterSingle(Collection<T> _collection, Function<T, String> _sc, Visual _visual) {
		super(_collection, false, _sc, _visual);
	}
	public DefaultSelecterSingle(Collection<T> _collection, StringConverter<T> _sc, Visual _visual) {
		super(_collection, false, _sc, _visual);
	}

	@Override
	public Region 				getNode() {
		return this;
	}

	@Override
	public ObservableValue<T> 	selectedProperty() {
		return singleSelectionModel().selectedItemProperty();
	}

}
