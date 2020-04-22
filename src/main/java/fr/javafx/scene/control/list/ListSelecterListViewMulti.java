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

import javafx.scene.control.MultipleSelectionModel;
import javafx.util.StringConverter;

public class ListSelecterListViewMulti<T> extends ListSelecterListView<T> implements ListSelecter.ListSelecterSkinMulti<T> {

	public ListSelecterListViewMulti(ListSelecter<T> _skinnable, StringConverter<T> _stringConverter) {
		super(_skinnable, true, _stringConverter);
	}

	@Override
	public MultipleSelectionModel<T> 	getSelectionModel() {
		return getNode().getSelectionModel();
	}

	@Override
	public void 					dispose() {
		;
	}

}
