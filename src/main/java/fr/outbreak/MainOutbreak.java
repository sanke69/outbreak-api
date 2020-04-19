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

import fr.outbreak.api.database.OutbreakDataBase;
import fr.outbreak.graphics.OutbreakStage;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;

public interface MainOutbreak {

    public static abstract class Graphics extends Application implements MainOutbreak {
    	private final ObjectProperty<OutbreakDataBase> 	databaseProperty;
    	private OutbreakStage 							stage;

    	public Graphics() {
    		super();

    		databaseProperty = new SimpleObjectProperty<OutbreakDataBase>();
    	}

    	public final void 								setDatabase(OutbreakDataBase _database) {
    		databaseProperty.set( _database );
    	}
    	@Override
    	public final OutbreakDataBase 					getDatabase() {
    		return databaseProperty.get();
    	}
    	public final ObjectProperty<OutbreakDataBase> 	databaseProperty() {
    		return databaseProperty;
    	}

    	public final OutbreakStage 						getPrimaryStage() {
    		if(stage != null)
    			return stage;

			stage = new OutbreakStage();
			stage . databaseProperty().bind(databaseProperty());
			stage . show();
			
//			ScenicView.show(stage.getScene());

			return stage;
    	}
 
    	public abstract void 							setViewers(OutbreakStage _stage);

		@Override
		public final void 								start(Stage primaryStage) throws Exception {
			setViewers( getPrimaryStage() );
		}

    }

	public abstract OutbreakDataBase 				getDatabase();

}
