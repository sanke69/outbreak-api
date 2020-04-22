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

import java.util.Collection;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

public class ListSelecter<T> extends Control {
	public static interface ListSelecterSkin<T>       extends Skin<ListSelecter<T>> {
		public void 								autoAjdustHeight(boolean _autoAdjust, int _maxLines);

		public ObservableList<T>   					getItems();
		public SelectionModel<T> 					getSelectionModel();
	}
	public static interface ListSelecterSkinSingle<T> extends ListSelecterSkin<T> {
	    public default ReadOnlyObjectProperty<T> 	selectedItemProperty()  { return getSelectionModel().selectedItemProperty(); }
		public SingleSelectionModel<T> 				getSelectionModel();
	}
	public static interface ListSelecterSkinMulti<T>  extends ListSelecterSkin<T> {
	    public default ObservableList<T> 			selectedItemsProperty() { return getSelectionModel().getSelectedItems(); }
		public MultipleSelectionModel<T> 			getSelectionModel();
	}

	private static final <T> StringConverter<T> func2strconv(Function<T, String> _f) {
		return new StringConverter<T>() {
			@Override public String  toString(T o)       { return _f.apply(o); }
			@Override public T fromString(String string) { return null; }
		};
	};

	public enum Visual { ComboBox, ListView }

	final protected Visual             visual;
	final protected boolean			   multiSelection;
	final protected StringConverter<T> stringConverter;

	public ListSelecter() {
		this((Class<T>) null, false, (StringConverter<T>) null, Visual.ListView);
	}
	public ListSelecter(Class<T> _class) {
		this((Class<T>) null, false, (StringConverter<T>) null, Visual.ListView);
	}
	public ListSelecter(Collection<T> _items) {
		this(_items, false, (StringConverter<T>) null, Visual.ListView);
	}

	public ListSelecter(boolean	_enableMulti) {
		this((Class<T>) null, _enableMulti, (StringConverter<T>) null, Visual.ListView);
	}
	public ListSelecter(Class<T> _items, boolean _enableMulti) {
		this((Class<T>) null, _enableMulti, (StringConverter<T>) null, Visual.ListView);
	}
	public ListSelecter(Collection<T> _items, boolean _enableMulti) {
		this(_items, _enableMulti, (StringConverter<T>) null, Visual.ListView);
	}

	public ListSelecter(Function<T, String> _stringConverter) {
		this((Class<T>) null, false, func2strconv(_stringConverter), Visual.ListView);
	}
	public ListSelecter(Class<T> _class, Function<T, String> _stringConverter) {
		this((Class<T>) null, false, func2strconv(_stringConverter), Visual.ListView);
	}
	public ListSelecter(Collection<T> _items, Function<T, String> _stringConverter) {
		this(_items, false, func2strconv(_stringConverter), Visual.ListView);
	}

	public ListSelecter(StringConverter<T> _stringConverter) {
		this((Class<T>) null, false, _stringConverter, Visual.ListView);
	}
	public ListSelecter(Class<T> _class, StringConverter<T> _stringConverter) {
		this((Class<T>) null, false, _stringConverter, Visual.ListView);
	}
	public ListSelecter(Collection<T> _items, StringConverter<T> _stringConverter) {
		this(_items, false, _stringConverter, Visual.ListView);
	}

	public ListSelecter(boolean	_enableMulti, Function<T, String> _stringConverter) {
		this((Class<T>) null, _enableMulti, func2strconv(_stringConverter), Visual.ListView);
	}
	public ListSelecter(Class<T> _items, boolean _enableMulti, Function<T, String> _stringConverter) {
		this((Class<T>) null, _enableMulti, func2strconv(_stringConverter), Visual.ListView);
	}
	public ListSelecter(Collection<T> _items, boolean _enableMulti, Function<T, String> _stringConverter) {
		this(_items, _enableMulti, func2strconv(_stringConverter), Visual.ListView);
	}

	public ListSelecter(boolean	_enableMulti, StringConverter<T> _stringConverter) {
		this((Class<T>) null, _enableMulti, _stringConverter, Visual.ListView);
	}
	public ListSelecter(Class<T> _items, boolean _enableMulti, StringConverter<T> _stringConverter) {
		this((Class<T>) null, _enableMulti, _stringConverter, Visual.ListView);
	}
	public ListSelecter(Collection<T> _items, boolean _enableMulti, StringConverter<T> _stringConverter) {
		this(_items, _enableMulti, _stringConverter, Visual.ListView);
	}

	public ListSelecter(boolean	_enableMulti, Function<T, String> _stringConverter, Visual _visual) {
		this((Class<T>) null, _enableMulti, func2strconv(_stringConverter), _visual);
	}
	public ListSelecter(Class<T> _class, boolean	_enableMulti, Function<T, String> _stringConverter, Visual _visual) {
		super();
		visual          = _visual;

		multiSelection  = _enableMulti;
		stringConverter = func2strconv(_stringConverter);

		setSkin( createSelecterSkin() );
	}
	public ListSelecter(Collection<T> _items, boolean _enableMulti, Function<T, String> _stringConverter, Visual _visual) {
		super();
		visual          = _visual;

		multiSelection  = _enableMulti;
		stringConverter = func2strconv(_stringConverter);

		setSkin( createSelecterSkin() );
		if(_items != null)
			getItems().setAll(_items);
	}

	public ListSelecter(boolean	_enableMulti, StringConverter<T> _stringConverter, Visual _visual) {
		this((Class<T>) null, _enableMulti, _stringConverter, _visual);
	}
	public ListSelecter(Class<T> _class, boolean	_enableMulti, StringConverter<T> _stringConverter, Visual _visual) {
		super();
		visual          = _visual;

		multiSelection  = _enableMulti;
		stringConverter = _stringConverter;

		setSkin( createSelecterSkin() );
	}
	public ListSelecter(Collection<T> _items, boolean _enableMulti, StringConverter<T> _stringConverter, Visual _visual) {
		super();
		visual          = _visual;

		multiSelection  = _enableMulti;
		stringConverter = _stringConverter;

		setSkin( createSelecterSkin() );
		if(_items != null)
			getItems().setAll(_items);
	}

	@SuppressWarnings({ "unchecked" })
	public ListSelecterSkin<T> 					getSelecterSkin() {
		Skin<?> skin = getSkin();

		if(! (skin instanceof ListSelecterSkin) )
			throw new IllegalStateException();

		return (ListSelecterSkin<T>) getSkin();
	}

	@Override
	protected Skin<ListSelecter<T>> 			createDefaultSkin() {
		return createSelecterSkin();
	}
	protected Skin<ListSelecter<T>> 			createSelecterSkin() {
		return switch(visual) {
		case ComboBox -> multiSelection ? new ListSelecterComboBoxMulti<T>(this, stringConverter) : new ListSelecterComboBoxSingle<T>(this, stringConverter);
		case ListView -> multiSelection ? new ListSelecterListViewMulti<T>(this, stringConverter) : new ListSelecterListViewSingle<T>(this, stringConverter);
		};
	}

	public void 								setItems(Collection<T> _layers) {
		getSelecterSkin().getItems().setAll(_layers);
	}
	public void 								setItems(Collection<T> _layers, T _default) {
		getSelecterSkin().getItems().setAll(_layers);
		if(_default != null)
			selectionModel().select(_default);
	}
	public void 								setItems(ObservableList<T> _layers) {
		getItems().setAll(_layers);
	}
	public void 								setItems(ObservableList<T> _layers, T _default) {
		getItems().setAll(_layers);
		if(_default != null)
			selectionModel().select(_default);
	}

	public ObservableList<T> 					getItems() {
		return getSelecterSkin().getItems();
	}
	public ObservableList<T>   					itemsProperty() {
		return getSelecterSkin().getItems();
	}

	public SelectionModel<T> 					selectionModel() {
		return getSelecterSkin().getSelectionModel();
	}
	public SingleSelectionModel<T>   			singleSelectionModel() {
		if(! (getSkin() instanceof ListSelecterSkinSingle) )
			throw new IllegalStateException();

		SingleSelectionModel<T> selectionModel = ((ListSelecterSkinSingle<T>) getSelecterSkin()).getSelectionModel();
		
		return selectionModel;
	}
	public MultipleSelectionModel<T> 			multiSelectionModel() {
		if(! (getSkin() instanceof ListSelecterSkinMulti) )
			throw new IllegalStateException();

		MultipleSelectionModel<T> selectionModel = ((ListSelecterSkinMulti<T>) getSelecterSkin()).getSelectionModel();
		
		return selectionModel;
	}

	public void									setMaxDisplayedItems(int _maxLines) {
		getSelecterSkin().autoAjdustHeight(true, _maxLines);
	}

}
