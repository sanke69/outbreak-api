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
 */package fr.outbreak.graphics.viewers.chart;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;

import fr.javafx.scene.PropertyEditors;
import fr.javafx.scene.properties.Editor;
import fr.javafx.scene.properties.SelecterSingle;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.api.database.OutbreakDataBase;
import fr.outbreak.graphics.OutbreakViewerOptions;
import fr.outbreak.graphics.charts.OutbreakSeries;

public class OutbreakChartPaneOptionsComparison extends OutbreakViewerOptions<OutbreakChartPane> {
	private final SelecterSingle<Country> 	countrySelecterA, countrySelecterB;
	private final Editor<Integer>			daySelecterA,     daySelecterB;

	public OutbreakChartPaneOptionsComparison() {
		super();

		countrySelecterA = PropertyEditors.newSingleSelecter(Country.class, new StringConverter<Country>() {
			@Override public String  toString(Country c)       { return c.getName(); }
			@Override public Country fromString(String string) { return null; }
		});
		daySelecterA     = PropertyEditors.newDayEditor();

		countrySelecterB = PropertyEditors.newSingleSelecter(Country.class, new StringConverter<Country>() {
			@Override public String  toString(Country c)       { return c.getName(); }
			@Override public Country fromString(String string) { return null; }
		});
		daySelecterB     = PropertyEditors.newDayEditor();

		addEntry(countrySelecterA . getNode());
		addEntry(daySelecterA     . getNode());
		addEntry(countrySelecterB . getNode());
		addEntry(daySelecterB     . getNode());
	}

	@Override
	public void 							initialize(OutbreakChartPane _charts) {
		Consumer<OutbreakDataBase> updateDB = (db) -> {
			if(db != null) {
				SortedSet<Country> countries = _charts.getDatabase()
												 .getIndicators(KpiType.Variation, r -> r.getCountry(), Country.nameComparator);

				countrySelecterA.itemsProperty().setAll(countries);
				countrySelecterB.itemsProperty().setAll(countries);
			} else {
				countrySelecterA.itemsProperty().setAll(Collections.emptyList());
				countrySelecterB.itemsProperty().setAll(Collections.emptyList());
			}
		};

		Runnable update = () -> {
			KpiType          type       = KpiType.Value;
			Country          countryA   = selectedCountryAProperty() . getValue();
			Integer          shiftA     = selectedDayAProperty()     . getValue();
			Country          countryB   = selectedCountryBProperty() . getValue();
			Integer          shiftB     = selectedDayBProperty()     . getValue();
			List<Population> population = Arrays.asList(Population.Infected, Population.Dead);

			OutbreakSeries   cA_series  = new OutbreakSeries( countryA.getName(), _charts.getDatabase().getReports(type, r -> r.getCountry().equals(countryA)), true, shiftA.intValue() );
			OutbreakSeries   cB_series  = new OutbreakSeries( countryB.getName(), _charts.getDatabase().getReports(type, r -> r.getCountry().equals(countryB)), true, shiftB.intValue() );

			_charts.getData().clear();
			for(Population p : population) {
				_charts.getData().add( cA_series.getData(p) );
				_charts.getData().add( cB_series.getData(p) );
			}
		};

		updateDB.accept( _charts.getDatabase() );

		_charts.databaseProperty() . addListener((_obs, _old, _new) -> updateDB.accept(_new));
		selectedCountryAProperty() . addListener((_obs, _old, _new) -> update.run());
		selectedDayAProperty()     . addListener((_obs, _old, _new) -> update.run());
		selectedCountryBProperty() . addListener((_obs, _old, _new) -> update.run());
		selectedDayBProperty()     . addListener((_obs, _old, _new) -> update.run());
	}

	private final ObservableValue<Country> 	selectedCountryAProperty() {
		return countrySelecterA.selectedProperty();
	}
	private final ObservableValue<Integer>  selectedDayAProperty() {
		return daySelecterA.valueProperty();
	}
	private final ObservableValue<Country> 	selectedCountryBProperty() {
		return countrySelecterB.selectedProperty();
	}
	private final ObservableValue<Integer>  selectedDayBProperty() {
		return daySelecterB.valueProperty();
	}

}
