package fr.javafx.scene.layouts;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import fr.javafx.utils.FxUtils;

public class TitledBorderSkin extends Region implements Skin<TitledBorder> {
	private final TitledBorder skinnable;

	static class ResizableRectangle extends Rectangle {

		ResizableRectangle() {
			super();
		}
		ResizableRectangle(double w, double h) {
			super(w, h);
		}

		@Override
		public boolean isResizable() {
			return true;
		}

		@Override
		public double minWidth(double height) {
			return 0.0;
		}

	}

	private final DoubleProperty			leftMarginProperty;
	private final DoubleProperty			rightMarginProperty;
	private final DoubleProperty			topMarginProperty;
	private final DoubleProperty			bottomMarginProperty;

	private final Label     				titleLabel;
	private final Rectangle     			contentClip;

	public TitledBorderSkin(TitledBorder _skinnable) {
		super();
		skinnable = _skinnable;

		leftMarginProperty   = new SimpleDoubleProperty( 3); leftMarginProperty   . addListener(this::updateStyle);
		rightMarginProperty  = new SimpleDoubleProperty( 3); rightMarginProperty  . addListener(this::updateStyle);
		topMarginProperty    = new SimpleDoubleProperty( 6); topMarginProperty    . addListener(this::updateStyle);
		bottomMarginProperty = new SimpleDoubleProperty( 3); bottomMarginProperty . addListener(this::updateStyle);
		contentClip          = new Rectangle();

		getChildren().addAll(titleLabel = createTitle(), skinnable.getContent());
		skinnable.getContent().setClip(contentClip);

		skinnable.contentProperty().addListener((_obs, _old, _new) -> {
			if(_old != null)
				getChildren().remove(_old);
			getChildren().add(_new);

			_new.setClip(contentClip);
		});

		registerListeners();
		updateStyle(null, 0, 0);
	}

	@Override
	public TitledBorder getSkinnable() {
		return skinnable;
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public void dispose() {
		;
	}

	// 7, 15, 22(RTT), 28, 29

	private Label createTitle() {
		Label titleLabel  = new Label();
		titleLabel.textProperty().bind(getSkinnable().titleProperty());
		titleLabel.setStyle("-fx-background-color: "+ FxUtils.rgb(getSkinnable().getColor()) +";");

		return titleLabel;
	}


	private void registerListeners() {
		DoubleBinding 		widthProperty      = new DoubleBinding() {
			{ bind( widthProperty(), leftMarginProperty, rightMarginProperty) ; }

			@Override protected double computeValue() {
				return widthProperty().get() - 2 * leftMarginProperty.get() - 2 * rightMarginProperty.get();
			}};
		DoubleBinding 		heightProperty      = new DoubleBinding() {
			{ bind( heightProperty(), topMarginProperty, bottomMarginProperty) ; }

			@Override protected double computeValue() {
				return heightProperty().get() - 2 * topMarginProperty.get() - 2 * bottomMarginProperty.get() - 2;
			}};

		contentClip . widthProperty()  . bind(widthProperty);
		contentClip . heightProperty() . bind(heightProperty);

		titleLabel.setLayoutX(27);
	
		getSkinnable().getContent() . layoutXProperty()     . bind(leftMarginProperty.multiply(2));
		getSkinnable().getContent() . layoutYProperty()     . bind(topMarginProperty.multiply(2).add(2));

	}
	private void updateStyle(ObservableValue<? extends Number> _obs, Number _old, Number _new) {
		double left   = leftMarginProperty.get();
		double right  = rightMarginProperty.get();
		double top    = topMarginProperty.get();
		double bottom = bottomMarginProperty.get();

		// Update the Border Rendering
		String cssThis = 
				"-fx-background-color: "+ FxUtils.rgb(getSkinnable().getColor()) +";\n" +
				"-fx-border-color: black;\n" +
				"-fx-border-insets: " + top + " " + right + " " + bottom + " " + left + ";\n" +
                "-fx-border-width: 3;\n";
		setStyle(cssThis);
	}

}