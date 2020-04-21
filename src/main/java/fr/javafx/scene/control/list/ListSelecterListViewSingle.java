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

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.StringConverter;

public class ListSelecterListViewSingle<T> implements ListSelecter.ListSelecterSkinSingle<T> {
	private final ListSelecter<T>		skinnable;
	private final StringConverter<T> 	stringConverter;

	ListView<T>							control;
	SingleSelectionModel<T>				selectionModel = new SingleSelectionModel<T>() {

		@Override
		protected T getModelItem(int index) {
			return control.getItems().get(index);
		}

		@Override
		protected int getItemCount() {
			return control.getItems().size();
		}
		
	};

	public ListSelecterListViewSingle(ListSelecter<T> _skinnable, StringConverter<T> _stringConverter) {
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
		return selectionModel;
	}

	@Override
	public void 					dispose() {
		;
	}

	private ListView<T> 			createNode() {
		if(control != null)
			return control;

		control = new ListView<T>();
		control . selectionModelProperty().get().getSelectedItems().addListener((ListChangeListener<? super T>) _c -> {
			while(_c.next()) {
				if(_c.wasPermutated() || _c.wasUpdated() || _c.wasReplaced()) {
					selectionModel.select(_c.getAddedSubList().get(0));
				} else if(_c.wasAdded()) {
					selectionModel.select(_c.getAddedSubList().get(0));					
				} else if(_c.wasRemoved()) {
					selectionModel.clearSelection();
				} else {
					throw new RuntimeException("WTF");
				}
			}
		});

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

		control.prefHeightProperty().bind(Bindings.size(control.getItems()).multiply(24).add(2));

		return control;
	}

}
