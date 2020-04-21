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
 */package fr.outbreak.graphics.viewers.table;

import java.util.stream.Collectors;

import fr.geodesic.referential.api.countries.Country;
import fr.javafx.scene.PropertyEditors;
import fr.javafx.scene.properties.SelecterMulti;
import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.graphics.OutbreakViewerOptions;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class OutbreakTablePaneOptions extends OutbreakViewerOptions<OutbreakTablePane> {
	SelecterMulti<Country> 	countrySelecter;

	public OutbreakTablePaneOptions() {
		super();

		countrySelecter = PropertyEditors.newMultiSelecter(Country.class);

		addEntry(countrySelecter.getNode());
	}

	@Override
	public void 					initialize(OutbreakTablePane _pane) {
		_pane.databaseProperty().addListener((_obs, _old, _new) -> countrySelecter.itemsProperty().setAll(_new.getIndicators(KpiType.Variation, r -> r.getCountry(), true)));

		selectedCountryProperty().addListener((ListChangeListener<Country>) lc -> {
			_pane.displayed.setAll(_pane.records.stream()
									.filter(dr -> lc.getList().contains(dr.getCountry()))
									.sorted()
									.collect(Collectors.toList()));
		});
	}

	private ObservableList<Country> selectedCountryProperty() {
		return countrySelecter.selectedProperty();
	}

}
