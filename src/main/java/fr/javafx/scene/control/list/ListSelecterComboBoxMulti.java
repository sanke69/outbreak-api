/**
 * JavaFR
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
package fr.javafx.scene.control.list;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MultipleSelectionModel;
import javafx.util.StringConverter;

public class ListSelecterComboBoxMulti<T> implements ListSelecter.ListSelecterSkinMulti<T> {
	private final ListSelecter<T>		skinnable;

	public ListSelecterComboBoxMulti(ListSelecter<T> _skinnable, StringConverter<T> _stringConverter) {
		super();
		skinnable       = _skinnable;
	}

	@Override
	public ListSelecter<T> 				getSkinnable() {
		return skinnable;
	}

	@Override
	public Node 						getNode() {
		throw new RuntimeException();
	}

	@Override
	public ObservableList<T> 			getItems() {
		throw new RuntimeException();
	}

	@Override
	public MultipleSelectionModel<T> 	getSelectionModel() {
		throw new RuntimeException();
	}

	@Override
	public void 						dispose() {
		;
	}

	@Override
	public void autoAjdustHeight(boolean _autoAdjust, int _maxLines) {
		// TODO Auto-generated method stub
		
	}

}
