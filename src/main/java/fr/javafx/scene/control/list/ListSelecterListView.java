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

import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.StringConverter;

abstract class ListSelecterListView<T> implements ListSelecter.ListSelecterSkin<T> {
	public static final double rowHeight = 27d;
	public static final double padHeight =  2d;

	private final ListSelecter<T>	skinnable;
	private final ListView<T>		control;

	public ListSelecterListView(ListSelecter<T> _skinnable, boolean _enableMulti, StringConverter<T> _stringConverter) {
		super();
		skinnable = _skinnable;
		control   = createNode(_enableMulti, _stringConverter);
	}

	@Override
	public final ListSelecter<T> 			getSkinnable() {
		return skinnable;
	}

	@Override
	public final ListView<T>				getNode() {
		return control;
	}

	@Override
	public final ObservableList<T> 			getItems() {
		return control.getItems();
	}

	public void 							autoAjdustHeight(boolean _autoAdjust, int _maxLines) {
		if(_autoAdjust) {
			ObjectBinding<Double> hb = new ObjectBinding<Double>() {
				@Override
				protected Double computeValue() {
					int nbLines = control.getItems().size();

					return (nbLines > _maxLines ? _maxLines : nbLines) * rowHeight + padHeight;
				}
			};
			control.prefHeightProperty().bind(hb);
		} else
			control.prefHeightProperty().unbind();
	}

	protected final ListView<T> 			createNode(boolean _enableMulti, StringConverter<T> _stringConverter) {
		ListView<T> control = new ListView<T>();
		if(_enableMulti)
			control.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
					setText(_stringConverter != null ? _stringConverter.toString(item) : item.toString());
			}

		}

		control.setCellFactory(lv -> new CustomListCell());


		return control;
	}

}
