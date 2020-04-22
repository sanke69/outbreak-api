package fr.run.outbreak.defaults;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import fr.javafx.scene.layouts.TitledBorder;

public class TitledBorderTest extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox pane = new VBox();
		pane.setStyle("-fx-background-color: red;");

		pane.getChildren().addAll(IntStream.range(0, 15).boxed().map(i -> new Button("" + i)).collect(Collectors.toList()));
		primaryStage.setScene(new Scene(new TitledBorder("TEST", pane), 320, 240));
		primaryStage.show();

//		pane.prefWidthProperty().bind(((Control) primaryStage.getScene().getRoot()).widthProperty()); 
//		pane.prefHeightProperty().bind(((Control) primaryStage.getScene().getRoot()).heightProperty()); 
		
//		ScenicView.show(primaryStage.getScene());
	}

}
