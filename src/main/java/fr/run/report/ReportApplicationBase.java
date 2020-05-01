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

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;

import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.ReportStage;

public abstract class ReportApplicationBase extends Application {

	public interface    TestReport     extends Report {}
	public static class ReportStageDef extends ReportStage<TestReport, Report.DataBase<TestReport>> {}
	
	private final ObjectProperty<Report.DataBase<TestReport>> 		databaseProperty;
	private ReportStage<TestReport, Report.DataBase<TestReport>> 	stage;

	public ReportApplicationBase() {
		super();
		databaseProperty = new SimpleObjectProperty<Report.DataBase<TestReport>>();
	}

	public final void setDatabase(Report.DataBase<TestReport> _database) {
		databaseProperty.set( _database );
	}
	public final Report.DataBase<TestReport> getDatabase() {
		return databaseProperty.get();
	}
	public final ObjectProperty<Report.DataBase<TestReport>> databaseProperty() {
		return databaseProperty;
	}

	public final ReportStage<TestReport, Report.DataBase<TestReport>> getPrimaryStage() {
		if(stage != null)
			return stage;

		stage = new ReportStage<TestReport, Report.DataBase<TestReport>>(1280, 640);
		stage . databaseProperty().bind(databaseProperty());
		stage . show();

		return stage;
	}

	public abstract void setViewers(ReportStage<TestReport, Report.DataBase<TestReport>> _stage);

	@Override
	public final void start(Stage primaryStage) throws Exception {
		setViewers( getPrimaryStage() );
	}

	public static void main(String[] args) throws IOException {
		Application.launch(ReportApplicationBase.class, args);
	}

}
