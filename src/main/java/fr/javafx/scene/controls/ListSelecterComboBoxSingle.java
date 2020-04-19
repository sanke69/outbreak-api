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
package fr.javafx.scene.controls;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.StringConverter;

public class ListSelecterComboBoxSingle<T> implements ListSelecter.ListSelecterSkinSingle<T> {
	private final ListSelecter<T>		skinnable;
	private final StringConverter<T> 	stringConverter;

	ComboBox<T>							control;

	public ListSelecterComboBoxSingle(ListSelecter<T> _skinnable, StringConverter<T> _stringConverter) {
		super();
		skinnable       = _skinnable;
		stringConverter = _stringConverter;

		control         = createNode();
	}

	@Override
	public ListSelecter<T> 			getSkinnable() {
		return skinnable;
	}

	@Override
	public Node 					getNode() {
		return control;
	}

	@Override
	public ObservableList<T> 		getItems() {
		return control.getItems();
	}

	@Override
	public SingleSelectionModel<T> 	getSelectionModel() {
		return control.getSelectionModel();
	}

	@Override
	public void 					dispose() {
		;
	}

	private ComboBox<T> 			createNode() {
		if(control != null)
			return control;

		control = new ComboBox<T>();

		class CustomListCell extends ListCell<T> {

			public CustomListCell() {
				super();
			}

			@Override
			public void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);

				if(empty || item == null)
					setText(null);
				else
					setText(stringConverter != null ? stringConverter.toString(item) : item.toString());
			}

		}

		control.setCellFactory(lv -> new CustomListCell());

		return control;
	}

}
