package fr.javafx.scene.layouts;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

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

	private ObjectProperty<Insets> 	borderMargins;
	DoubleProperty				    leftMarginProperty   = new SimpleDoubleProperty();
	DoubleProperty				rightMarginProperty  = new SimpleDoubleProperty();
	DoubleProperty				topMarginProperty    = new SimpleDoubleProperty();
	DoubleProperty				bottomMarginProperty = new SimpleDoubleProperty();
/*
	DoubleBinding 					leftMarginProperty   = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getLeft(); } };
	DoubleBinding 					rightMarginProperty  = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getRight(); } };
	DoubleBinding 					topMarginProperty    = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getTop(); } };
	DoubleBinding 					bottomMarginProperty = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getBottom(); } };
*/
	private Label     				titleLabel;
	private BorderPane				contentPane;
	private Rectangle 				contentClip;

	public TitledBorderSkin(TitledBorder _skinnable) {
		super();
		skinnable = _skinnable;

		contentClip   = new ResizableRectangle();
		borderMargins = new SimpleObjectProperty<Insets>( new Insets(7, 3, 3, 3) );
		borderMargins . addListener(this::bordersUpdate);
		
		getChildren().addAll(getTitle(), getContentPane());
		getContentPane().setStyle("-fx-background-color: red;");
		updateBorders();
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

	private Label getTitle() {
		if(titleLabel != null)
			return titleLabel;

		String cssTitle = 
				"-fx-background-color: white;\n" +
				"-fx-translate-y: 0;\n";

		titleLabel  = new Label();
		titleLabel.setStyle(cssTitle);
		titleLabel.textProperty().bind(getSkinnable().titleProperty());
		titleLabel.setLayoutX(27);

//		StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
		StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
		StackPane.setMargin(titleLabel, new Insets(0, 0, 0, 15));
		
		return titleLabel;
	}
	private BorderPane getContentPane() {
		if(contentPane != null)
			return contentPane;

		contentPane = new BorderPane();

		
		
		
		
//		ObjectBinding<Node> clipProperty         = new ObjectBinding<Node>() { @Override protected Node   computeValue() { return contentClip; } };
		DoubleBinding 		leftMarginProperty   = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getLeft(); } };
		DoubleBinding 		rightMarginProperty  = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getRight(); } };
		DoubleBinding 		topMarginProperty    = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getTop(); } };
		DoubleBinding 		bottomMarginProperty = new DoubleBinding()       { @Override protected double computeValue() { return borderMargins.get().getBottom(); } };
		DoubleBinding 		widthProperty        = widthProperty()  . subtract( leftMarginProperty . add( rightMarginProperty  ) );
		DoubleBinding 		heightProperty       = heightProperty() . subtract( topMarginProperty  . add( bottomMarginProperty ) );

		getContentPane() . layoutXProperty()      . bind(leftMarginProperty);
		getContentPane() . layoutYProperty()      . bind(topMarginProperty);

		getContentPane() . minWidthProperty()    . bind(widthProperty);
		getContentPane() . prefWidthProperty()   . bind(widthProperty);
		getContentPane() . maxWidthProperty()    . bind(widthProperty);

		getContentPane() . minHeightProperty()   . bind(heightProperty);
		getContentPane() . prefHeightProperty()  . bind(heightProperty);
		getContentPane() . maxHeightProperty()   . bind(heightProperty);
		
		
		
		
		
		
		
	/*	
		
		contentClip      . xProperty()       . set(30);
		contentClip      . yProperty()       . set(50);
		contentClip      . widthProperty()       . bind(widthProperty);
		contentClip      . heightProperty()      . bind(heightProperty);
		
		getContentPane() . centerProperty()      . bind(getSkinnable().contentProperty());
		getContentPane() . clipProperty()        . bind(clipProperty);
		
//		getContentPane() . setLayoutX( borderMargins.get().getLeft() );
//		getContentPane() . setLayoutY( borderMargins.get().getTop() );
/*
		getContentPane() . layoutXProperty()     . bind(leftMarginProperty);
		getContentPane() . layoutYProperty()     . bind(topMarginProperty);
/*
		getContentPane() . minWidthProperty()    . bind(widthProperty);
		getContentPane() . prefWidthProperty()   . bind(widthProperty);
		getContentPane() . maxWidthProperty()    . bind(widthProperty);

		getContentPane() . minHeightProperty()   . bind(heightProperty);
		getContentPane() . prefHeightProperty()  . bind(heightProperty);
		getContentPane() . maxHeightProperty()   . bind(heightProperty);
*/
		return contentPane;
	}
	
	private void updateBorders() {
		Insets margins = borderMargins.get();
		String cssThis = 
				"-fx-border-color: black;\n" +
				"-fx-border-insets: " + margins.getTop() + " " + margins.getRight() + " " + margins.getBottom() + " " + margins.getLeft() + ";\n" +
                "-fx-border-width: 3;\n";
		setStyle(cssThis);

	}

	// 7, 15, 22(RTT), 28, 29

//	private void setupBindings() {
//		getContentPane().clipProperty().bind(new ObjectBinding<Node>() { @Override protected Node computeValue() { return contentClip; }});
//	}
	private void bordersUpdate(ObservableValue<? extends Insets> _obs, Insets _old, Insets _new) {
		if(_new == null)
			return ;

		Insets border = _new;

		// Update the Border Rendering
		String cssThis = 
				"-fx-border-color: black;\n" +
				"-fx-border-insets: " + border.getTop() + " " + border.getRight() + " " + border.getBottom() + " " + border.getLeft() + ";\n" +
                "-fx-border-width: 3;\n";
		setStyle(cssThis);

		getContentPane() . setLayoutX( borderMargins.get().getLeft() );
//		getContentPane() . setLayoutY( borderMargins.get().getTop() );
		/*
		getContentPane().setLayoutX( border.getLeft() );
		getContentPane().setLayoutY( border.getTop() );

		contentClip.setWidth  ( getWidth()  - border.getLeft() + border.getRight());
		contentClip.setHeight ( getHeight() - border.getTop()  + border.getBottom());
		*/
	}

	
	
	
}