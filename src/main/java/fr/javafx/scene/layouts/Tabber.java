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
package fr.javafx.scene.layouts;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class Tabber extends Control {
	private StringProperty 		name;
	private TabPane        		skin;

	private Map<String, Tabber> subTabbers;

	public Tabber() {
		this(null);
	}
	public Tabber(String _name) {
		super();

		subTabbers = new HashMap<String, Tabber>();

		name = new SimpleStringProperty(_name);

		skin = new TabPane();
        skin . setSide(Side.RIGHT);
//      skin . setMinSize(screenBounds.getWidth() / 2, screenBounds.getHeight() / 2 + 27);
//      skin . setMaxSize(screenBounds.getWidth(), screenBounds.getHeight());
        skin . setTabMinHeight(96);
        skin . setTabMaxHeight(96);
	}
	public Tabber(String _name, String _firstText, Node _firstNode) {
		this(_name);
		addTab(_firstText, _firstNode);
	}

	protected Skin<Tabber> 	createDefaultSkin() {
		return new Skin<Tabber>() {
			@Override public Tabber  getSkinnable() { return Tabber.this; }
			@Override public TabPane getNode() 		{ return skin; }
			@Override public void 	 dispose() 		{  }
		};
	}

	public void 			setName(String _name) {
        name.set(_name);
	}
	public String 			getName() {
        return name.get();
	}
	public StringProperty 	nameProperty() {
        return name;
	}

	public Tabber 			addTab(Tab _tab) {
		skin.getTabs().add(_tab);
		return this;
	}
	public Tabber 			addTab(Tabber _tabber) {
    	Tab
        tab = new Tab(null, _tabber);
        tab . setGraphic  ( new TabberTag( _tabber.getName() ) );
        tab . setClosable (false);

        subTabbers.put(_tabber.getName(), _tabber);

		skin.getTabs().add(tab);
		return this;
	}
	public Tabber 			addTab(String _name, Node _control) {
    	Tab
        tab = new Tab(null, new SlidedOverlayControl(_control));

        TabberTag 
        tag = new TabberTag( _name );
        tab . setGraphic  ( tag );
        tab . setClosable ( false );

		skin.getTabs().add( tab );
		return this;
	}

	public Tabber			getTabber(String _name) {
		return subTabbers.get(_name);
	}

	public Node				getSelectedTab() {
		return skin.getSelectionModel().getSelectedItem().getContent();
	}

}
