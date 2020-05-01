package fr.run.graphics;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class ChangeSymbolSize extends Application {

@Override
public void start(Stage stage) {

    // Random chart
    // Defining the Axis
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    // Creating the chart
    LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);
    // Preparing the series
    XYChart.Series series = new XYChart.Series();
    series.setName("Grafico");

    for (double x = 0; x <= 10; x++) {
        double y = Math.random() * 100;
        XYChart.Data chartData;
        chartData = new XYChart.Data(x, y);
        chartData.setNode(new ShowCoordinatesNode(x, y));
        series.getData().add(chartData);
    }

    // Adding series to chart
    lineChart.getData().add(series);

    Scene scene = new Scene(lineChart, 800, 600);
    stage.setScene(scene);
    stage.show();
}

public static void main(String[] args) {
    launch(args);
}
}