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

import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import fr.javafx.scene.PropertyEditors;
import fr.javafx.scene.properties.SelecterMulti;

import fr.geodesic.referential.api.countries.Country;
import fr.outbreak.api.Outbreak;
import fr.reporting.api.Report;
import fr.reporting.sdk.graphics.ReportViewerOptions;

public class OutbreakTablePaneOptions extends ReportViewerOptions<OutbreakTablePane> {
	SelecterMulti<Country> 	countrySelecter;

	public OutbreakTablePaneOptions() {
		super();

		countrySelecter = PropertyEditors.newMultiSelecter(Country.class, new StringConverter<Country>() {
			@Override public String  toString(Country c)       { return c.getName(); }
			@Override public Country fromString(String string) { return null; }
		});

		addEntry(countrySelecter.getNode());
	}

	@Override
	public void 					initialize(OutbreakTablePane _pane) {
		_pane.databaseProperty().addListener((_obs, _old, _new) -> countrySelecter.itemsProperty().setAll(_new.getIndicators(Report.Type.Variation, r -> r.getCountry(), Country.nameComparator)));

		selectedCountryProperty().addListener((ListChangeListener<Country>) lc -> {
			_pane.displayed.setAll(_pane.records.stream()
									.filter(dr -> lc.getList().contains(dr.getCountry()))
									.collect(Collectors.toCollection(() -> new TreeSet<Outbreak.Report>(Report.Daily.comparatorByDate()))));
		});
	}

	private ObservableList<Country> selectedCountryProperty() {
		return countrySelecter.selectedProperty();
	}

}
