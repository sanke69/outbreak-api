/**
 * OutBreak API
 * Copyright (C) 2020-?XYZ  Steve PECHBERTI <steve.pechberti@gmail.com>
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
 */package fr.outbreak.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.javafx.scene.layouts.FitPaneWithSlideMenuOverlay;
import fr.outbreak.api.database.OutbreakDataBase;
import fr.outbreak.graphics.layouts.OutbreakTab;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OutbreakStage extends Stage {
	private static final boolean makeStageTransparent = false;

	final Scene      		 				scene;
    final BorderPane 		 				root;

    final TabPane    		 				tabs;
    final ToggleButton	 					options;

	final ObjectProperty<OutbreakDataBase>	databaseProperty;

	public OutbreakStage() {
		super();

		databaseProperty = new SimpleObjectProperty<OutbreakDataBase>();

        setScene(scene = new Scene(root = new BorderPane(tabs = createTabPane(), null, null, null, options = createOptionButton())));
        sizeToScene();

        if( makeStageTransparent )
			MakeTransparent.accept(this);
	}

	public TabPane									getTabs() {
		return tabs;
	}

	public final void 								setDatabase(OutbreakDataBase _database) {
		databaseProperty.setValue(_database);
	}
	public final OutbreakDataBase 					getDatabase() {
		return databaseProperty.get();
	}
	public final ObjectProperty<OutbreakDataBase> 	databaseProperty() {
		return databaseProperty;
	}

    private ToggleButton 							createOptionButton() {
		final double W = 14, H = 49;

		ToggleButton 
		button = new ToggleButton(">");
		button . setPadding    (new Insets(0));
		button . setMinWidth   (W);
		button . setPrefWidth  (W);
		button . setMaxWidth   (W);
		button . setMinHeight  (H);
		button . setPrefHeight (H);
		button . setMaxHeight  (H);

		BorderPane.setAlignment(button, Pos.CENTER_RIGHT);

		button.selectedProperty().addListener((_obs, _old, _new) -> button.setText(_new ? "<" : ">"));
		button.setOnAction(e -> {
			final FitPaneWithSlideMenuOverlay 
			overlay = (FitPaneWithSlideMenuOverlay) tabs.getSelectionModel().getSelectedItem().getContent();
			overlay . handle(e);
		});

    	return button;
    }
    private TabPane 								createTabPane() {
		final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    	TabPane 
    	tabs    = new TabPane();
        tabs    . setSide(Side.RIGHT);
        tabs    . setMinSize(screenBounds.getWidth() / 2, screenBounds.getHeight() / 2 + 27);
        tabs    . setMaxSize(screenBounds.getWidth(), screenBounds.getHeight());
        tabs    . setTabMinHeight(96);
        tabs    . setTabMaxHeight(96);

        return tabs;
    }

    public <OVP extends OutbreakViewerBase & OutbreakViewer> 
    void 											registerViewerPane		(OVP _viewer) {
        tabs    . getTabs().add(new OutbreakTab(_viewer, null).getTab());
    	_viewer . databaseProperty() . bind(databaseProperty());
    }
    public <OVP extends OutbreakViewerBase & OutbreakViewer, OO extends OutbreakViewerOptions<OVP>> 
    void 											registerViewerPane		(OVP _viewer, OO _options) {
    	OutbreakTab tab = new OutbreakTab(_viewer, _options);

    	_viewer  . databaseProperty() . bind( databaseProperty() );
    	_options . initialize(_viewer);
        tabs     . getTabs().add( tab.getTab() );
    }
    public Tab 										unregisterViewerPane	(OutbreakViewerBase _viewer) {
    	throw new RuntimeException();
    }

    // Transparent Hack
	public static class MakeTransparent {
		private final static Map<OutbreakStage, EventHandler<MouseEvent>> mouseListeners;
	
		static {
			mouseListeners = new HashMap<OutbreakStage, EventHandler<MouseEvent>>();
		}

		public static void accept(OutbreakStage stage) {
	    	Set<EventType<MouseEvent>> resizeEvents   = Set.of(MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_DRAGGED, MouseEvent.MOUSE_EXITED, MouseEvent.MOUSE_EXITED_TARGET);
			EventHandler<MouseEvent>   resizeListener = new ResizeListener(stage);
	
			makeTransparent(stage);
			addResizeListener(stage, resizeListener, resizeEvents);
	
			stage.setOnCloseRequest(evt -> removeResizeListener(stage, resizeListener, resizeEvents));
		}
	
		public static void makeTransparent(OutbreakStage _stage) {
			_stage.initStyle(StageStyle.TRANSPARENT);
	
			_stage.showingProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> _obs, Boolean _old, Boolean _new) {
	        		String makeTransparent = "-fx-background-color: transparent;";
	
		        	if(_new) {
		        		_stage.getScene() 			. setFill(Color.TRANSPARENT);
		        		_stage.getScene().getRoot() . setStyle(makeTransparent);
	
		        		_stage.getTabs()            . setStyle(makeTransparent);
		        		_stage.getTabs()            . lookup(".tab-header-background") . setStyle(makeTransparent);
		        		_stage.getTabs()            . lookup(".tab-header-area")       . setStyle(makeTransparent);
	
		        		_stage.showingProperty().removeListener(this);
					}
		        }
	        });
		}
		
	    public static void addResizeListener(OutbreakStage stage, EventHandler<MouseEvent> _eventListener, Set<EventType<MouseEvent>> _eventTypes) {
	    	for(EventType<MouseEvent> met : _eventTypes)
	    		stage.getScene().addEventHandler(met, _eventListener);
	
	        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
	        for(Node child : children)
	            addListenerDeeply(child, _eventListener, _eventTypes);
	
			mouseListeners.put(stage, _eventListener);
	    }
	    public static void removeResizeListener(OutbreakStage stage, EventHandler<MouseEvent> _eventListener, Set<EventType<MouseEvent>> _eventTypes) {
	    	for(EventType<MouseEvent> met : _eventTypes)
	    		stage.getScene().removeEventHandler(met, _eventListener);

	        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
	        for (Node child : children)
	            removeListenerDeeply(child, _eventListener, _eventTypes);

	        mouseListeners.remove(stage);
	    }
	
	    public static void addListenerDeeply(Node _node, EventHandler<MouseEvent> _eventListener, Set<EventType<MouseEvent>> _eventTypes) {
	    	for(EventType<MouseEvent> met : _eventTypes)
	    		_node.addEventHandler(met, _eventListener);

	        if(_node instanceof Parent parent) {
	            ObservableList<Node> children = parent.getChildrenUnmodifiable();

	            for(Node child : children)
	                addListenerDeeply(child, _eventListener, _eventTypes);
	        }
	    }
	    public static void removeListenerDeeply(Node _node, EventHandler<MouseEvent> _eventListener, Set<EventType<MouseEvent>> _eventTypes) {
	    	for(EventType<MouseEvent> met : _eventTypes)
	    		_node.removeEventHandler(met, _eventListener);

	        if(_node instanceof Parent parent) {
	            ObservableList<Node> children = parent.getChildrenUnmodifiable();

	            for(Node child : children)
	            	removeListenerDeeply(child, _eventListener, _eventTypes);
	        }
	    }

	    static class ResizeListener implements EventHandler<MouseEvent> {
	        private Stage  stage;
	        private Cursor cursorEvent = Cursor.DEFAULT;
	        private int    border = 4;
	        private double startX = 0;
	        private double startY = 0;

	        public ResizeListener(Stage _stage) {
	        	super();

	            stage = _stage;
	        }

	        @Override
	        public void handle(MouseEvent mouseEvent) {
	            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
	            Scene scene = stage.getScene();

	            double mouseEventX = mouseEvent.getSceneX(), 
	                   mouseEventY = mouseEvent.getSceneY(),
	                   sceneWidth  = scene.getWidth(),
	                   sceneHeight = scene.getHeight();

	            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType) == true) {
	                if (mouseEventX < border && mouseEventY < border) {
	                    cursorEvent = Cursor.NW_RESIZE;
	                } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
	                    cursorEvent = Cursor.SW_RESIZE;
	                } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
	                    cursorEvent = Cursor.NE_RESIZE;
	                } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
	                    cursorEvent = Cursor.SE_RESIZE;
	                } else if (mouseEventX < border) {
	                    cursorEvent = Cursor.W_RESIZE;
	                } else if (mouseEventX > sceneWidth - border) {
	                    cursorEvent = Cursor.E_RESIZE;
	                } else if (mouseEventY < border) {
	                    cursorEvent = Cursor.N_RESIZE;
	                } else if (mouseEventY > sceneHeight - border) {
	                    cursorEvent = Cursor.S_RESIZE;
	                } else {
	                    cursorEvent = Cursor.DEFAULT;
	                }
	                scene.setCursor(cursorEvent);
	            } else if(MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)){
	                scene.setCursor(Cursor.DEFAULT);
	            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType) == true) {
	                startX = stage.getWidth() - mouseEventX;
	                startY = stage.getHeight() - mouseEventY;
	            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) == true) {
	                if (Cursor.DEFAULT.equals(cursorEvent) == false) {
	                    if (Cursor.W_RESIZE.equals(cursorEvent) == false && Cursor.E_RESIZE.equals(cursorEvent) == false) {
	                        double minHeight = stage.getMinHeight() > (border*2) ? stage.getMinHeight() : (border*2);
	                        if (Cursor.NW_RESIZE.equals(cursorEvent) == true || Cursor.N_RESIZE.equals(cursorEvent) == true || Cursor.NE_RESIZE.equals(cursorEvent) == true) {
	                            if (stage.getHeight() > minHeight || mouseEventY < 0) {
	                                stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
	                                stage.setY(mouseEvent.getScreenY());
	                            }
	                        } else {
	                            if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
	                                stage.setHeight(mouseEventY + startY);
	                            }
	                        }
	                    }

	                    if (Cursor.N_RESIZE.equals(cursorEvent) == false && Cursor.S_RESIZE.equals(cursorEvent) == false) {
	                        double minWidth = stage.getMinWidth() > (border*2) ? stage.getMinWidth() : (border*2);
	                        if (Cursor.NW_RESIZE.equals(cursorEvent) == true || Cursor.W_RESIZE.equals(cursorEvent) == true || Cursor.SW_RESIZE.equals(cursorEvent) == true) {
	                            if (stage.getWidth() > minWidth || mouseEventX < 0) {
	                                stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
	                                stage.setX(mouseEvent.getScreenX());
	                            }
	                        } else {
	                            if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
	                                stage.setWidth(mouseEventX + startX);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }
	}

} 
