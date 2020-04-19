/**
 * OutBreak API
 * Copyright (C) 2007-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
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
package fr.outbreak.graphics.viewers.table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.outbreak.api.Outbreak;
import fr.outbreak.api.Outbreak.KpiType;
import fr.outbreak.api.Outbreak.LocalizedReport;
import fr.outbreak.api.Outbreak.Population;
import fr.outbreak.graphics.OutbreakViewerBase;
import fr.outbreak.graphics.OutbreakViewerTable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class OutbreakTablePane extends OutbreakViewerBase implements OutbreakViewerTable {
	private static record ColumnProperty(String name, 
										 String property, 
										 Callback<TableColumn<Outbreak.LocalizedReport, ?>, TableCell<Outbreak.LocalizedReport, ?>> cellFactory,
										 double width, Color color) {
		
		@SuppressWarnings("unchecked")
		public <T> ColumnProperty(String _name, 
									 String _property, 
									 Callback<TableColumn<Outbreak.LocalizedReport, T>, TableCell<Outbreak.LocalizedReport, T>> _cellFactory,
									 double _width, Color _color) {
			super();

			name        = _name;
			property    = _property;
			cellFactory = _cellFactory != null ? (o) -> _cellFactory.call((TableColumn<Outbreak.LocalizedReport, T>) o) : null;
			width       = _width;
			color       = _color;
		}

	}

	protected final static Callback<TableColumn<Outbreak.LocalizedReport, LocalDate>, TableCell<Outbreak.LocalizedReport, LocalDate>> 
	localDateCellFactory = rec -> {
		final Label label = new Label();
		final TableCell<Outbreak.LocalizedReport, LocalDate> cell = new TableCell<Outbreak.LocalizedReport, LocalDate>() {

			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null) {
					label.setDisable(true);
					label.setText("");
				} else {
					String newValue = item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

					label.setDisable(false);
					label.setText(newValue);

					commitEdit(item);
				}

			}

		};

		cell.setGraphic(label);
		return cell;
	};

	protected final static List<ColumnProperty> 
	columnList = new ArrayList<ColumnProperty>() {
		private static final long serialVersionUID = 1799444550933844180L;

		{
			add(new ColumnProperty("Date",       "date",            localDateCellFactory, 128, Color.BLUE ));
			add(new ColumnProperty("Pays",       "country",                         null, 128, Color.YELLOW ));
			add(new ColumnProperty("Population", "pop:Susceptible",                 null, 128, Color.BLUE ));
			add(new ColumnProperty("Nvx Cas",    "pop:Infected",                    null, 128, Color.ORANGE ));
			add(new ColumnProperty("Nvx Morts",  "pop:Dead",                        null, 128, Color.RED ));
			add(new ColumnProperty("Nvx Guéris", "pop:Recovered",                   null, 128, Color.RED ));
		}
	};


	final ObservableList<Outbreak.LocalizedReport> records   = FXCollections.observableArrayList();
	final ObservableList<Outbreak.LocalizedReport> displayed = FXCollections.observableArrayList();

	final StackPane 					 			containerPane;
	final ScrollPane 					 			scrollPane;
	final TableView<Outbreak.LocalizedReport>      table;

	public OutbreakTablePane() {
		this("Table View");
	}
	public OutbreakTablePane(String _title) {
		super(_title);

		table         = createTableView();
		scrollPane    = createScrollPane();
		containerPane = createContainerPane();

		databaseProperty().addListener((_obs, _old, _new) -> setData( _new.getReports(KpiType.Value) ));
	}

	public void 						setData(Collection<Outbreak.LocalizedReport> _entries) {
		if (_entries != null) {
			records.clear();
			records.addAll(_entries);

			displayed.setAll(records);
		}
	}

	protected final Skin<OutbreakTablePane>	createDefaultSkin() {
		return new Skin<OutbreakTablePane>() {
			@Override public OutbreakTablePane 	getSkinnable() 	{ return OutbreakTablePane.this; }
			@Override public Node 				getNode() 		{ return table; }
			@Override public void 				dispose() 		{  }
		};
	}

	@SuppressWarnings("unchecked")
	private TableView<Outbreak.LocalizedReport> 	createTableView() {
		TableView<Outbreak.LocalizedReport> table = new TableView<Outbreak.LocalizedReport>(displayed);

		double totalWidth = 0;
		for(int i = 0; i < columnList.size(); ++i) {
			ColumnProperty columnProperty = columnList.get(i);

			TableColumn<Outbreak.LocalizedReport, Object> 
			newColumn = new TableColumn<Outbreak.LocalizedReport, Object>(columnProperty.name());
			newColumn . setPrefWidth		(columnProperty.width());
			if(columnProperty.cellFactory() != null)
			newColumn . setCellFactory		((c) -> { return (TableCell<LocalizedReport, Object>) columnProperty.cellFactory().call(c); });
			
			if(columnProperty.property().contains("pop:")) {
				String population = columnProperty.property().substring(4);
				newColumn . setCellValueFactory	(olr -> {
					return new ObjectBinding<Object>() {
						{}
						@Override
						protected Object computeValue() {
							return olr.getValue().get(Population.valueOf(population)).orElse(-1L);
						}
					};
				});
			} else
				newColumn . setCellValueFactory	(new PropertyValueFactory<>(columnProperty.property()));

			table.getColumns().add(newColumn);
			
			totalWidth += columnProperty.width();
		}

		table.setPrefSize(totalWidth, 200);
		return table;
	}
	private StackPane 					createContainerPane() {
		StackPane 
		stackPane = new StackPane(scrollPane);
		stackPane . setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

		return stackPane;
	}
	private ScrollPane 					createScrollPane() {
		ScrollPane 
		scrollPane = new ScrollPane(table);
		scrollPane . setFitToHeight(true);
		scrollPane . setFitToWidth(true);
		scrollPane . setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
		scrollPane . setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		return scrollPane;
	}

}
