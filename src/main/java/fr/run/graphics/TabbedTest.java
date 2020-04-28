package fr.run.graphics;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import fr.javafx.scene.layouts.Tabber;

public class TabbedTest extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Tabber main     = new Tabber();
		Tabber first    = newTabber("first",  Color.DARKRED);
		Tabber second   = newTabber("second", Color.DARKGREEN);
		Tabber thirst   = newTabber("thirst", Color.DARKBLUE);

		Tabber subOne   = newTabber("first",  Color.DARKRED);
		Tabber subTwo   = newTabber("second", Color.DARKGREEN);
		Tabber subThree = newTabber("thirst", Color.DARKBLUE);
		Tabber subFour  = newTabber("four",   Color.DARKORCHID);
		Tabber subFive  = newTabber("five",   Color.DARKGREY);

		main . addTab( first  . addTab(  subOne . addTab(subTwo)) . addTab(subThree) )
			 . addTab( second . addTab( subFour ) )
			 . addTab( thirst . addTab( subFive ) );

		primaryStage.setScene(new Scene(main, 800, 600));
		primaryStage.show();
	}

	public static Tabber newTabber(String _name, Color _color) {
		Tabber 
		tabber = new Tabber(_name);
		tabber . backgroundProperty().bind(Bindings.when(tabber.hoverProperty())
               . then(new Background(new BackgroundFill(_color, CornerRadii.EMPTY, Insets.EMPTY)))
               . otherwise(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))));

		return tabber;
	}

	public static Tabber newTabber2(String _tabber, String _first, Color _color) {
		BorderPane 
		content = new BorderPane();
		content . backgroundProperty().bind(Bindings.when(content.hoverProperty())
                . then(new Background(new BackgroundFill(_color, CornerRadii.EMPTY, Insets.EMPTY)))
                . otherwise(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))));

		Tabber tabber = new Tabber(_tabber, _first, content);
		return tabber;
	}

}
