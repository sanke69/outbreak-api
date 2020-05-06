package fr.javafx.scene.chart;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import fr.javafx.scene.chart.plugins.behavior.ChartPanner;
import fr.javafx.scene.chart.plugins.behavior.ChartSelecter;
import fr.javafx.scene.chart.plugins.behavior.ChartZoomer;
import fr.javafx.scene.chart.plugins.overlays.CrosshairIndicator;
import fr.javafx.scene.chart.plugins.overlays.DataPointTooltip;
import fr.javafx.utils.MouseEvents;

public class XYChartTests extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());

        Series<Number, Number> series1 = new Series<>();
        series1.setName("Data set 1");

        Series<Number, Number> series2 = new Series<>();
        series2.setName("Data set 2");

        chart.getData().add(series1);
        chart.getData().add(series2);

        Random rng = new Random();
        for (int i = 0 ; i <= 20 ; i++) {
            series1.getData().add(new Data<>(i, rng.nextInt(100)));
            series2.getData().add(new Data<>(i, rng.nextInt(100)));
        }

        XYChartPane<Number, Number> 
        pane = new XYChartPane<Number, Number>(chart);
        pane . getPlugins().addAll  (
									new ChartPanner(), 
									new ChartZoomer(), 
									new ChartSelecter(XY.Constraint.BOTH,
											me -> MouseEvents.isOnlyPrimaryButtonDown(me) && MouseEvents.isOnlyCtrlModifierDown(me),
											(xychart, selection) -> ChartZoomer.performZoom(xychart, selection, XY.Constraint.BOTH)),
									new DataPointTooltip(), 
									new CrosshairIndicator<>()
								  );
        pane.getXAxis().addEventHandler(MouseEvent.ANY, me -> { if (me.getClickCount() == 2) pane.getXAxis().setAutoRanging(true); });
        pane.getYAxis().addEventHandler(MouseEvent.ANY, me -> { if (me.getClickCount() == 2) pane.getYAxis().setAutoRanging(true); });


        pane.setStyle(series1, new XY.Series.Style(Color.RED, 1d, XY.Symbols.cross.path, Color.RED, Color.RED, Color.RED));
        pane.setStyle(series2, new XY.Series.Style(Color.GREEN, 1d, null, Color.GREEN, Color.GREEN, Color.GREEN));
        
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

}