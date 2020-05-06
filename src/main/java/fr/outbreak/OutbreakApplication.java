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
package fr.outbreak;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import fr.outbreak.api.Outbreak;
import fr.reporting.sdk.graphics.ReportStage;

public interface OutbreakApplication {

	public static final class Stage extends ReportStage<Outbreak.Report, Outbreak.DataBase> {
		Stage() { super(1280, 640); }
	}

    public static abstract class Graphics extends Application implements OutbreakApplication {
    	private final ObjectProperty<Outbreak.DataBase> databaseProperty;
    	private Stage 									stage;

    	public Graphics() {
    		super();

    		databaseProperty = new SimpleObjectProperty<Outbreak.DataBase>();
    	}

    	public final void 								setDatabase(Outbreak.DataBase _database) {
    		databaseProperty.set( _database );
    	}
    	@Override
    	public final Outbreak.DataBase 					getDatabase() {
    		return databaseProperty.get();
    	}
    	public final ObjectProperty<Outbreak.DataBase> 	databaseProperty() {
    		return databaseProperty;
    	}

    	public final Stage 								getPrimaryStage() {
    		if(stage != null)
    			return stage;

			stage = new Stage();
			stage . databaseProperty().bind(databaseProperty());
			stage . show();

			return stage;
    	}
 
    	public abstract void 							setViewers(Stage _stage);

		@Override
		public final void 								start(javafx.stage.Stage primaryStage) throws Exception {
			setViewers( getPrimaryStage() );
		}

    }

	public abstract Outbreak.DataBase getDatabase();

}
