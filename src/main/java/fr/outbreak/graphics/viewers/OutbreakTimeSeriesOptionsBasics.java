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
 */package fr.outbreak.graphics.viewers;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.StringConverter;

import fr.javafx.scene.PropertyEditors;
import fr.javafx.scene.PropertyListControl;
import fr.javafx.scene.properties.SelecterMulti;
import fr.javafx.scene.properties.SelecterSingle;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.graphics.timeseries.OutbreakSeries;
import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.ReportViewerOptions;

public class OutbreakTimeSeriesOptionsBasics extends ReportViewerOptions<OutbreakTimeSeries> {
	private final SelecterSingle<Country> 			  countrySelecter;
	private final SelecterMulti<Outbreak.Population>  curveSelecter;

	public OutbreakTimeSeriesOptionsBasics() {
		super();
		curveSelecter = PropertyEditors.newMultiSelecter(EnumSet.of(Population.Infected, Population.Dead));
		curveSelecter . setMaxDisplayedItems(2);

		countrySelecter = PropertyEditors.newSingleSelecter(Country.class, new StringConverter<Country>() {
			@Override public String  toString(Country c)       { return c.getName(); }
			@Override public Country fromString(String string) { return null; }
		});

		PropertyListControl curvePane   = addSubPane("Curves");
		PropertyListControl countryPane = addSubPane("Countries");

		curvePane   . addEntry(curveSelecter   . getNode());
		countryPane . addEntry(countrySelecter . getNode());
	}

	public void                                       	initialize(OutbreakTimeSeries _charts) {
		_charts.databaseProperty().addListener((_obs, _old, _new) -> {
			if(_new != null)
				countrySelecter.itemsProperty().setAll(_new.getIndicators(Report.Type.Variation, r -> r.getCountry(), Country.nameComparator));
		});

		Runnable update = () -> {
			Report.Type      type       = Report.Type.Value;
			Country          country    = selectedCountryProperty().getValue();
			List<Population> population = selectedCurveProperty();

			if(country == null || population == null || population.isEmpty())
				return ;

			OutbreakSeries 
			c_series = new OutbreakSeries( country.getName(), _charts.getDatabase().getReports(type, r -> r.getCountry().equals(country)) );

			_charts.getData().clear();
			for(Population p : population)
				_charts.getData().add( c_series.getData(p) );
		};

		selectedCountryProperty() . addListener((_obs, _old, _new) -> update.run());
		selectedCurveProperty()   . addListener((ListChangeListener<Outbreak.Population>) _c -> update.run());
	}

	public  final List<Node>                           	getNodes() {
		return Arrays.asList(curveSelecter.getNode(), countrySelecter.getNode());
	}

	private final ObservableList<Outbreak.Population> 	selectedCurveProperty() {
		return curveSelecter.selectedProperty();
	}
	private final ObservableValue<Country>     			selectedCountryProperty() {
		return countrySelecter.selectedProperty();
	}

}
