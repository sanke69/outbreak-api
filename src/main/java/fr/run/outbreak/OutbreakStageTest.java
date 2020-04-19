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
package fr.run.outbreak;

import java.io.IOException;

import fr.outbreak.MainOutbreak;
import fr.outbreak.graphics.OutbreakStage;
import fr.run.outbreak.defaults.TestPane;
import fr.run.outbreak.defaults.TestPaneOptions;
import javafx.application.Application;

public class OutbreakStageTest extends MainOutbreak.Graphics {

	public static void main(String[] args) throws IOException {
		Application.launch(OutbreakStageTest.class, args);
	}

	public OutbreakStageTest() {
		super();
	}

	@Override
	public void setViewers(OutbreakStage _stage) {
		_stage.registerViewerPane(new TestPane("No options..."));
		_stage.registerViewerPane(new TestPane("With options..."), new TestPaneOptions());
	}

}
