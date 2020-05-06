/**
 * JavaFR
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
package fr.javafx.scene.chart.plugins.behavior;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartUtils;
import fr.javafx.scene.chart.plugins.AbstractChartPlugin;

public class AbstractBehaviorPlugin<X, Y> extends AbstractChartPlugin<X, Y> {

    private final ObjectProperty<XY.Constraint> 		interactionMode;
	private final ObjectProperty<XY.ConstraintStrategy>	interactionStrategy;

    private final ObjectProperty<Cursor> 				dragCursor;
    private Cursor 										originalCursor;

    public AbstractBehaviorPlugin() {
        this(XY.Constraint.BOTH, Cursor.CLOSED_HAND);
    }
    public AbstractBehaviorPlugin(XY.Constraint _interactionMode) {
    	this(_interactionMode, Cursor.CLOSED_HAND);
    }
    protected AbstractBehaviorPlugin(XY.Constraint _interactionMode, Cursor _dragCursor) {
    	super();
    	interactionMode     = new SimpleObjectProperty<XY.Constraint>(_interactionMode);
    	interactionStrategy = new SimpleObjectProperty<XY.ConstraintStrategy>( XY.ConstraintStrategy.normal() );
    	dragCursor          = new SimpleObjectProperty<>(_dragCursor);
    }

    public final void 									setInteractionMode(XY.Constraint mode) {
        interactionMode.set(mode);
    }
    public final XY.Constraint 							getInteractionMode() {
        return interactionMode.get();
    }
    public final ObjectProperty<XY.Constraint> 			interactionModeProperty() {
        return interactionMode;
    }

    public final void 									setInteractionStrategy(XY.ConstraintStrategy mode) {
    	interactionStrategy.set(mode);
    }
    public final XY.ConstraintStrategy 					getInteractionStrategy() {
        return interactionStrategy.get();
    }
    public final ObjectProperty<XY.ConstraintStrategy> 	interactionStrategyProperty() {
        return interactionStrategy;
    }

    public final void 									setDragCursor(Cursor cursor) {
        dragCursor.set(cursor);
    }
    public final Cursor 								getDragCursor() {
        return dragCursor.get();
    }
    public final ObjectProperty<Cursor> 				dragCursorProperty() {
        return dragCursor;
    }

    protected final XY.Constraint						getInteractionModeInContext(MouseEvent _me) {
    	return getInteractionStrategy().getConstraint( XYChartUtils.getContext(getChartPane(), _me.getX(), _me.getY()) );
    }
    
    protected void 										installCursor() {
        originalCursor = getChartPane().getCursor();
        if(getDragCursor() != null)
            getChartPane().setCursor(getDragCursor());
    }
    protected void 										uninstallCursor() {
        getChartPane().setCursor(originalCursor);
    }

}
