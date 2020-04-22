package fr.javafx.scene;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import fr.javafx.scene.layouts.TitledBorder;

import fr.outbreak.graphics.OutbreakViewer;

public class PropertyListControl extends Control {
	private static final int labelWidth = 120;
	private static final int rowHeight  = 27;
	
	private static record GridPaneColumnProperty(double width, Color color) {}
	private static final  GridPaneColumnProperty left   = new GridPaneColumnProperty( labelWidth, Color.GRAY  ); 
	private static final  GridPaneColumnProperty right  = new GridPaneColumnProperty( OutbreakViewer.Options.width - labelWidth, Color.GRAY.brighter() );
	private static final  GridPaneColumnProperty unique = new GridPaneColumnProperty( OutbreakViewer.Options.width, left.color.interpolate(right.color, 0.5) );

	public static record Entry(Labeled label, Region control) {
		Entry(Region _control)                { this((Labeled) null, _control); }
		Entry(String _label, Region _control) { this(new Label(_label), _control); }
		Entry(PropertyListControl _submenu)   { this((Labeled) null, _submenu); }

		boolean 			hasLabel()  { return label != null; }
		boolean 			isSubMenu() { return control instanceof PropertyListControl; }

		PropertyListControl toSubMenu() { return (PropertyListControl) control; }

	}

	class PropertyListPane extends GridPane {
		GridPane content;

		PropertyListPane() {
			super();
		}

	}

	TitledBorder     							border;
	PropertyListPane 							content;

	ObservableList<PropertyListControl.Entry>	entries;

	public PropertyListControl() {
		this(null);
	}
	public PropertyListControl(String _title) {
		super();

		if(_title != null)
			border = new TitledBorder(_title, content = new PropertyListPane());
		else {
			border  = null;
			content = new PropertyListPane();
		}

		entries = FXCollections.observableArrayList(new ArrayList<PropertyListControl.Entry>());
		entries.addListener((ListChangeListener<Entry>) lc -> {
			while(lc.next()) {
				int na = lc.getAddedSize();
				int nr = lc.getRemovedSize();
				int i0 = lc.getFrom(), i1 = lc.getTo();

				if(lc.wasPermutated()) {
					for(int i = i0; i < i1; ++i) {
						int it = lc.getPermutation(i);

					}
				} else if(lc.wasReplaced()) {
					for(int i = i0; i < i1; ++i) {

					}
				} else if(lc.wasUpdated()) {
					for(int i = i0; i < i1; ++i) {
						;
					}
				} else if(lc.wasAdded()) {
					for(Entry e : lc.getAddedSubList()) {
						if(e.isSubMenu())
							addEntry(content, e.toSubMenu());
						else if(e.hasLabel())
							addEntry(content, e.label(), e.control());
						else
							addEntry(content, e.control());
					}
				} else if(lc.wasRemoved()) {
					for(Entry e : lc.getRemoved()) {
						if(e.isSubMenu())
							removeEntry(content, e.toSubMenu());
						else if(e.hasLabel())
							removeEntry(content, e.label(), e.control());
						else
							removeEntry(content, e.control());
					}
				} else {
					System.err.println("Failed to find ");
				}
			}
		});
	}

	public PropertyListControl 			getSubMenu(String _title) {
		return entries	.stream()
						.filter(Entry::isSubMenu)
						.findAny()
						.map(Entry::toSubMenu)
						.orElse(null);
	}
	public PropertyListControl 			addSubPane(String _title) {
		PropertyListControl subMenu;
		entries.add(new Entry(subMenu = new PropertyListControl(_title)));

		return subMenu;
	}

	public void 						addEntry(Region _control) {
		entries.add(new Entry( _control));
	}
	public void 						addEntry(String _name, Region _control) {
		entries.add(new Entry(_name, _control));
	}
	public void 						addEntry(Labeled _label, Region _control) {
		entries.add(new Entry(_label, _control));
	}

	public void 						removeEntry(String _name) {
		entries .stream()
				.filter(e -> e.hasLabel() && e.label().getText().compareTo(_name) == 0)
				.findFirst()
				.ifPresent(e -> entries.remove(e));
	}
	public void 						removeEntry(Region _control) {
		entries .stream()
		.filter(e -> e.control().equals(_control))
		.findFirst()
		.ifPresent(e -> entries.remove(e));
	}

	protected Skin<PropertyListControl> createDefaultSkin() {
		return new Skin<PropertyListControl>() {

			@Override
			public PropertyListControl getSkinnable() {
				return PropertyListControl.this;
			}

			@Override
			public Node getNode() {
				return border != null ? border : content;
			}

			@Override
			public void dispose() {
				;
			}
			
		};
	}

	private static	void 				addEntry(GridPane _pane, Region _control) {
		final int nextRow = _pane.getRowCount();

		_control        . setBackground(new Background(new BackgroundFill(unique.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		_control        . setMinWidth   (unique.width());
		_control        . setPrefWidth  (unique.width());
		_control        . setMaxWidth   (unique.width());
		_control        . setMinHeight  (rowHeight);
		_control        . setMaxHeight  (5 * rowHeight);

		_pane.add(_control, 0, nextRow, 2, 1);
	}
	private static 	void 				addEntry(GridPane _pane, Region _label, Region _control) {
		final int nextRow = _pane.getRowCount();

		_label          . setBackground (new Background(new BackgroundFill(left.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		_label          . setMinWidth   (left.width());
		_label          . setPrefWidth  (left.width());
		_label          . setMaxWidth   (left.width());
		_label          . setMinHeight  (rowHeight);
		_label          . setPrefHeight (rowHeight);
		_label          . setMaxHeight  (rowHeight);

		_control        . setBackground(new Background(new BackgroundFill(right.color(), CornerRadii.EMPTY, Insets.EMPTY)));
		_control        . setMinWidth   (right.width());
		_control        . setPrefWidth  (right.width());
		_control        . setMaxWidth   (right.width());
		_control        . setMinHeight  (rowHeight);
		_control        . setMaxHeight  (rowHeight);

		_pane.add(_label,   0, nextRow, 1, 1);
		_pane.add(_control, 1, nextRow, 1, 1);
	}

	private static	void 				removeEntry(GridPane _pane, Region _control) {
		_pane.getChildren().remove(_control);
	}
	private static 	void 				removeEntry(GridPane _pane, Region _label, Region _control) {
		_pane.getChildren().remove(_label);
		_pane.getChildren().remove(_control);
	}

}
