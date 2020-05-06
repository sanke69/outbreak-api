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
 */
package fr.outbreak.graphics.viewers;

import javafx.beans.value.ObservableValue;

import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.Report;
import fr.reporting.sdk.graphics.panes.ReportAboutPane;

public class OutbreakAboutPane extends ReportAboutPane<Outbreak.Report, Outbreak.DataBase> {

    public OutbreakAboutPane() {
    	this("About...");
    }
    public OutbreakAboutPane(String _title) {
    	super(_title);

    	databaseProperty().addListener(this::onPropertyChange);
    	onPropertyChange(null, null, null);
    }

    public <T> void onPropertyChange(ObservableValue<? extends T> _obs, T _old, T _new) {
    	Outbreak.DataBase db = databaseProperty().get();

    	StringBuilder sb = new StringBuilder();
    	sb.append( getAboutPrefix() );
    	
    	if(db == null) {
			sb.append("          Database is loading..." + "\n");
    	} else {
			sb.append("          Database is loaded." + "\n");
			sb.append("          " + db.getReports(Report.Type.Variation).size() + " records found." + "\n");
			sb.append("            from " + db.getPeriod().from() + " to " + db.getPeriod().to() + "\n");
			sb.append("            for " + db.getIndicators(Report.Type.Variation, r -> r.getCountry(), true).size() + " countries" + "\n");
    	}

    	textProperty().set( sb.toString() );
    }

	private String 						getAboutPrefix() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append("JavaFR: Covid-19" + "\n");
		sb.append("----------------" + "\n");
		sb.append("\n");
		sb.append("    version: 0.0.3" + "\n");
		sb.append("    license: MPL v.2" + "\n");
		sb.append("    author:  Steve PECHBERTI (a.k.a. sanke69)" + "\n");
		sb.append("    email:   steve.pechberti@gmail.com" + "\n");
		sb.append("\n");

		return sb.toString();
	}

}
