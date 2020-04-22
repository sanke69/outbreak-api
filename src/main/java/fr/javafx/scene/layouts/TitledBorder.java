package fr.javafx.scene.layouts;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

public class TitledBorder extends Control {
	private final StringProperty 		titleProperty;
	private final ObjectProperty<Node> 	contentProperty;
	private final ObjectProperty<Color> colorProperty;

	public TitledBorder() {
		super();
		titleProperty   = new SimpleStringProperty();
		contentProperty = new SimpleObjectProperty<Node>();
		colorProperty   = new SimpleObjectProperty<Color>();
	}
	public TitledBorder(String _title) {
		super();
		titleProperty   = new SimpleStringProperty(_title);
		contentProperty = new SimpleObjectProperty<Node>();
		colorProperty   = new SimpleObjectProperty<Color>();
	}
	public TitledBorder(String _title, Node _content) {
		super();
		titleProperty   = new SimpleStringProperty(_title);
		contentProperty = new SimpleObjectProperty<Node>(_content);
		colorProperty   = new SimpleObjectProperty<Color>();
	}

	@Override
	protected Skin<?> 				createDefaultSkin() {
		return new TitledBorderSkin(this);
	}

	public StringProperty			titleProperty() {
		return titleProperty;
	}
	public void 					setTitle(String title) {
		titleProperty.set(" " + title + " ");
	}
	public String 					getTitle() {
		return titleProperty.get();
	}

	public ObjectProperty<Node> 	contentProperty() {
		return contentProperty;
	}
	public void 					setContent(Node content) {
		contentProperty.set(content);
	}
	public Node 					getContent() {
		return contentProperty.get();
	}

	public ObjectProperty<Color> 	colorProperty() {
		return colorProperty;
	}
	public void 					setColor(Color _color) {
		colorProperty.set(_color);
	}
	public Color					getColor() {
		return colorProperty.get();
	}

}
