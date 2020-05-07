/**
 * OutBreak API
 * Copyright (C) 2020-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
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
package fr.run.report;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

import fr.javafx.scene.chart.XYChartPane;
import fr.javafx.scene.chart.axis.NumericAxis;
import fr.javafx.scene.chart.plugins.behavior.ChartPanner;
import fr.javafx.scene.chart.plugins.behavior.ChartZoomer;
import fr.javafx.scene.chart.plugins.indicators.XValueIndicator;
import fr.javafx.scene.chart.plugins.indicators.YRangeIndicator;
import fr.javafx.scene.chart.plugins.overlays.CrosshairIndicator;
import fr.javafx.scene.chart.plugins.overlays.DataPointTooltip;

import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.ReportStage;
import fr.reporting.sdk.graphics.ReportViewerBase;

public class ReportChartTest extends ReportApplicationBase {

	class DemoChart extends ReportViewerBase<TestReport, Report.DataBase<TestReport>> {

		protected DemoChart() {
			super("DemoChart");
		}

		public XYChartPane<Number, Number> createSamplePane() {
			NumericAxis xAxis = new NumericAxis();
			xAxis.setAnimated(false);
			xAxis.setLabel("x");

			NumericAxis yAxis = new NumericAxis();
			yAxis.setAnimated(false);
			yAxis.setLabel("f(x)");

			LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
			lineChart.setTitle("Logarithmic Axis Example");
			lineChart.setAnimated(false);

			lineChart.getData().add(
					new Series<>("f(x) = A . sin(x)", 
								 IntStream. range(0, 360)
						        		  . mapToObj(i -> new Data<Number, Number>(i, Math.sin(i * Math.PI / 180d)))
						        		  . collect(Collectors.toCollection(() -> FXCollections.observableArrayList())))
									);

			XYChartPane<Number, Number> chartPane = new XYChartPane<>(lineChart);
			chartPane.getPlugins().addAll(
											new ChartPanner(), 
											new ChartZoomer(), 
											new DataPointTooltip(), 
											new CrosshairIndicator<>()
											);

			return chartPane;
		}

		void addOverlayChart(XYChartPane<Number, Number> chartPane) {
			NumericAxis xAxis = new NumericAxis();
			xAxis.setAnimated(false);
			xAxis.setForceZeroInRange(false);

			NumericAxis yAxis = new NumericAxis();
			yAxis.setAnimated(false);
			yAxis.setForceZeroInRange(false);
			yAxis.setAutoRangePadding(0.1);
			yAxis.setAutoRangeRounding(false);

			LineChart<Number, Number> chart2 = new LineChart<>(xAxis, yAxis);
			chart2.setAnimated(false);
			chart2.setCreateSymbols(false);
			chart2.setHorizontalZeroLineVisible(false);
//			chart2.getStylesheets().addAll(styles("chart.css", "chart2.css"));
			chart2.getYAxis().setSide(Side.RIGHT);
			chart2.getYAxis().setLabel("Data2");
			chart2.getData().add(
					new Series<>("f(x) = A . cos(x)", 
								 IntStream. range(0, 360)
						        		  . mapToObj(i -> new Data<Number, Number>(i, Math.cos(i * Math.PI / 180d)))
						        		  . collect(Collectors.toCollection(() -> FXCollections.observableArrayList())))
									);

			
			chartPane.getOverlayCharts().add(chart2);
		}
		
		void addOverlayBarChart(XYChartPane<Number, Number> chartPane) {
	        CategoryAxis xAxis = new CategoryAxis();
	        xAxis.setAnimated(false);

		    NumericAxis yRainfallAxis =new NumericAxis();
		    yRainfallAxis.setAnimated(false);
			yRainfallAxis.setForceZeroInRange(false);
			yRainfallAxis.setAutoRangePadding(0.1);
			yRainfallAxis.setAutoRangeRounding(false);
		    yRainfallAxis.setLowerBound(0);
		    yRainfallAxis.setUpperBound(100);
		    yRainfallAxis.setAutoRanging(false);

	        BarChart<String, Number> rainfallChart = new BarChart<String, Number>(xAxis, yRainfallAxis);
	        rainfallChart.setTitle("Rainfall (mm)");
	        rainfallChart.setAnimated(false);
	        rainfallChart.getYAxis().setLabel("RainFall (mm)");
	        rainfallChart.getYAxis().setTickLabelFill(Color.BLUE);
	        rainfallChart.getYAxis().lookup(".axis-label").setStyle("-fx-text-fill: blue;");
	        rainfallChart.getYAxis().setSide(Side.LEFT);

//			chartPane.getPlugins().add(rainfallChart);
		}
		
		void addOverlayPlugins(XYChartPane<Number, Number> chartPane) {
			XValueIndicator<Number> xValueIndicator1 = new XValueIndicator<>(90);
			XValueIndicator<Number> xValueIndicator2 = new XValueIndicator<>(180);
			YRangeIndicator<Number> yRangeIndicator  = new YRangeIndicator<>(.75, .95, (ValueAxis<Number>) chartPane.getYAxis());

			xValueIndicator1 . setText("min");
			xValueIndicator1 . setLabelPosition           (0.05);
			xValueIndicator1 . setLabelHorizontalAnchor   (HPos.CENTER);
			xValueIndicator1 . setLabelVerticalAnchor     (VPos.CENTER);

			xValueIndicator2 . setText("max");
			xValueIndicator2 . setLabelPosition           (0.05);
			xValueIndicator2 . setLabelHorizontalAnchor   (HPos.CENTER);
			xValueIndicator2 . setLabelVerticalAnchor     (VPos.CENTER);

			yRangeIndicator  . setText("optimal");
			yRangeIndicator  . setLabelHorizontalPosition (0.2);
			yRangeIndicator  . setLabelHorizontalAnchor   (HPos.CENTER);
			yRangeIndicator  . setLabelVerticalAnchor     (VPos.CENTER);
			yRangeIndicator  . setLabelHorizontalPosition (0.95);
			yRangeIndicator  . setLabelVerticalPosition   (0.5);

			chartPane.getPlugins().addAll(xValueIndicator1, xValueIndicator2, yRangeIndicator);
		}
		

		@Override
		protected Skin<DemoChart> createDefaultSkin() {
			return new Skin<DemoChart>() {
				XYChartPane<Number, Number> pane = null;

				@Override
				public DemoChart getSkinnable() {
					return DemoChart.this;
				}

				@Override
				public Node getNode() {
					if(pane != null)
						return pane;

					pane = DemoChart.this.createSamplePane();
					addOverlayPlugins( pane );
//					addOverlayChart( pane );
//					addOverlayBarChart( pane );
//					pane . backgroundProperty()
//						 . bind		(Bindings.when(pane.hoverProperty())
//				         . then		(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)))
//				         . otherwise(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))));

					return pane;
				}

				@Override
				public void dispose() {
					;
				}
				
			};
		}
		
	}
	
	
	public void setViewers(ReportStage<TestReport, Report.DataBase<TestReport>> _stage) {
		_stage.registerViewerPane(new DemoChart());
	}

	public static void main(String[] args) throws IOException {
		Application.launch(ReportChartTest.class, args);
	}

}
