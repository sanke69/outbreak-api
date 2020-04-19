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
package fr.javafx.scene.layouts;

import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class FitPaneWithSlideMenuOverlay extends Control implements EventHandler<ActionEvent> {
	public static enum TranslateDirection { LEFT, RIGHT, UP, DOWN };

	AnchorPane  			substitute;
	Node    				overlayed;
    AnchorPane				overlay;

    Rectangle 				rootBounds,  rootClip;
    Rectangle 				slideBounds, slideClip;

    TranslateDirection  	slideDir = TranslateDirection.DOWN;
	TranslateTransition 	slideIn;
	TranslateTransition 	slideOut;
    double 					slideBeg, slideEnd;
    ChangeListener<Number> 	slideUpdater = (_obs, _old, _new) -> {
    	rootClip   = new Rectangle(0, 0, getWidth(), getHeight());
		slideClip   = new Rectangle(0, 0, slideBounds.getWidth(), slideBounds.getHeight());

		substitute . setClip(rootClip);
		overlay    . setClip(slideClip);

    	slideBeg   = switch(slideDir) {
		case UP, LEFT -> 0;
		case RIGHT    -> substitute.getWidth()  - slideBounds.getWidth();
		case DOWN     -> substitute.getHeight() - slideBounds.getHeight();      	
    	};
    	slideEnd   = switch(slideDir) {
		case LEFT     -> - slideBounds.getWidth();
		case RIGHT    ->   substitute.getWidth();
		case UP       -> - slideBounds.getHeight();
		case DOWN     ->   substitute.getHeight();    	
    	};

		overlay.setPrefWidth  (slideBounds.getWidth());
		overlay.setPrefHeight (slideBounds.getHeight());
		overlay.setMaxWidth   (slideBounds.getWidth());
		overlay.setMaxHeight  (slideBounds.getHeight());

		switch(slideDir) {
		case LEFT, RIGHT : 	overlay . setLayoutX    (slideBeg);
							overlay . setLayoutY    (slideBounds.getY());

							overlay . setTranslateX (slideEnd);

				            slideIn  . setToX       (0);
				            slideOut . setToX       (slideEnd);
				            break;
		case UP, DOWN    :  overlay . setLayoutX    (slideBounds.getX());
							overlay . setLayoutY    (slideBeg);

							overlay . setTranslateY (slideEnd);

				            slideIn  . setToY       (0);
				            slideOut . setToY       (slideEnd);
				            break;
    	};
    };

    ObjectProperty<Color>	overlayColorProperty;

	public FitPaneWithSlideMenuOverlay(Node _overlayed) {
		this(_overlayed, TranslateDirection.LEFT, 0, -1, -1);
	}
    public FitPaneWithSlideMenuOverlay(Node _overlayed, TranslateDirection _dir) {
    	this(_overlayed, _dir, 0, -1, -1);
    }
    public FitPaneWithSlideMenuOverlay(Node _overlayed, TranslateDirection _dir, double _dim) {
    	super();
		setOverlayed(_overlayed);

		switch(slideDir = _dir) {
		case LEFT, RIGHT : 	initSlidePane(0, 0, _dim, -1); break;
		case UP,   DOWN  :  initSlidePane(0, 0, -1, _dim); break;
    	};
    	slideUpdater.changed(null, 0, 0);

    	if(overlayed instanceof Control) {
	    	((Control) overlayed).prefWidthProperty().bind(substitute.widthProperty());
	    	((Control) overlayed).prefHeightProperty().bind(substitute.heightProperty());
    	} else
	    	if(overlayed instanceof Region) {
		    	((Region) overlayed).prefWidthProperty().bind(substitute.widthProperty());
		    	((Region) overlayed).prefHeightProperty().bind(substitute.heightProperty());
	    	} else
	        	if(overlayed instanceof Pane) {
	    	    	((Pane) overlayed).prefWidthProperty().bind(substitute.widthProperty());
	    	    	((Pane) overlayed).prefHeightProperty().bind(substitute.heightProperty());
	        	} else {
	        		System.err.println("Big Issue");
	        	}
    }
    public FitPaneWithSlideMenuOverlay(Node _overlayed, TranslateDirection _dir, double _offset, double _width, double _height) {
    	super();
		setOverlayed(_overlayed);

		switch(slideDir = _dir) {
		case LEFT, RIGHT : 	initSlidePane(0, _offset, _width, _height); break;
		case UP,   DOWN  :  initSlidePane(_offset, 0, _width, _height); break;
    	};
    	slideUpdater.changed(null, 0, 0);

    	if(overlayed instanceof Control) {
	    	((Control) overlayed).prefWidthProperty().bind(substitute.widthProperty());
	    	((Control) overlayed).prefHeightProperty().bind(substitute.heightProperty());
    	} else
	    	if(overlayed instanceof Region) {
		    	((Region) overlayed).prefWidthProperty().bind(substitute.widthProperty());
		    	((Region) overlayed).prefHeightProperty().bind(substitute.heightProperty());
	    	} else
	        	if(overlayed instanceof Pane) {
	    	    	((Pane) overlayed).prefWidthProperty().bind(substitute.widthProperty());
	    	    	((Pane) overlayed).prefHeightProperty().bind(substitute.heightProperty());
	        	}
    }

	protected Skin<FitPaneWithSlideMenuOverlay> createDefaultSkin() {
		return new Skin<FitPaneWithSlideMenuOverlay>() {

			@Override
			public FitPaneWithSlideMenuOverlay getSkinnable() {
				return FitPaneWithSlideMenuOverlay.this;
			}

			@Override
			public Node getNode() {
				return substitute;
			}

			@Override
			public void dispose() {
				;
			}
			
		};
	}

	protected void		setOverlayed(Node _overlayed) {
		overlayed  = _overlayed;
    	overlay    = new AnchorPane();
 
		substitute = new AnchorPane();
		substitute.widthProperty()  . addListener((_obs, _old, _new) -> setWidth(_new.doubleValue()));
		substitute.heightProperty() . addListener((_obs, _old, _new) -> setHeight(_new.doubleValue()));

		substitute.getChildren().addAll(overlayed, overlay);
	}

	public boolean		isSlidePaneOpen() {
		return (overlay.getTranslateX() == 0 && overlay.getTranslateY() == 0);
	}

	public boolean		hasSlidePane() {
		return !overlay.getChildren().isEmpty();
	}
	public AnchorPane 	getSlidePane() {
		return overlay;
	}

    public void 		setSlideOffset(double _offset) {
    	slideBounds = switch(slideDir) {
		case LEFT, RIGHT -> new Rectangle(_offset, 0, slideBounds.getWidth(), slideBounds.getHeight());
		case UP,   DOWN  -> new Rectangle(0, _offset, slideBounds.getWidth(), slideBounds.getHeight());
    	};
    }
    public void 		setSlideSize(double _width, double _height) {
    	slideBounds = new Rectangle(slideBounds.getX(), slideBounds.getY(), _width, _height);
    }

    private void 		initSlidePane(double _x, double _y, double _width, double _height) {
    	slideBounds = new Rectangle(_x, _y, _width, _height);
    	if(_width < 0)
    		slideBounds.widthProperty().bind(substitute.widthProperty());
    	if(_height < 0)
    		slideBounds.heightProperty().bind(substitute.heightProperty());

		slideIn     = new TranslateTransition(new Duration(350), overlay);
		slideOut    = new TranslateTransition(new Duration(350), overlay);
		slideOut    . setOnFinished(e -> getSlidePane().setVisible(false));

        slideBounds . xProperty()      . addListener(slideUpdater);
        slideBounds . yProperty()      . addListener(slideUpdater);
        slideBounds . widthProperty()  . addListener(slideUpdater);
        slideBounds . heightProperty() . addListener(slideUpdater);

        substitute  . widthProperty()  . addListener(slideUpdater);
        substitute  . heightProperty() . addListener(slideUpdater);
    }

	@Override
	public void 		handle(ActionEvent event) {
		if (overlay.getTranslateX() != 0 || overlay.getTranslateY() != 0) {
			getSlidePane().setVisible(true);
			slideIn.play();
		} else {
			slideOut.play();
		}
	}

}
