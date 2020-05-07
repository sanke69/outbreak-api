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
package fr.javafx.scene.chart.plugins.indicators;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Line;

public abstract class AbstractValueIndicatorSingle<X, Y> extends AbstractValueIndicator<X, Y> {
    static final String STYLE_CLASS_LABEL = "value-indicator-label";
    static final String STYLE_CLASS_LINE  = "value-indicator-line";

    static final double MIDDLE_POSITION   = 0.5;

    private final DoubleProperty 	labelPosition = new SimpleDoubleProperty(this, "labelPosition", 0.5) {
        @Override
        protected void invalidated() {
            if (get() < 0 || get() > 1) {
                throw new IllegalArgumentException("labelPosition must be in rage [0,1]");
            }
            layoutChildren();
        }
    };

    private final DoubleProperty 	value = new SimpleDoubleProperty(this, "value") {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };

    protected final Line 			line = new Line();

    protected AbstractValueIndicatorSingle(double _value, String _text) {
        super(_text);
        setValue(_value);
        
        line.setMouseTransparent(true);

        getChartChildren().addAll(line, label);
    }

    public final void 				setLabelPosition(double _value) {
    	labelPosition.set(_value);
    }
    public final double 			getLabelPosition() {
        return labelPosition.get();
    }
    public final DoubleProperty 	labelPositionProperty() {
        return labelPosition;
    }

    public final void 				setValue(double _Value) {
        value.set(_Value);
    }
    public final double 			getValue() {
        return value.get();
    }
    public final DoubleProperty 	valueProperty() {
        return value;
    }

    protected void 					layoutLine(double _startX, double _startY, double _endX, double _endY) {
        line.setStartX(_startX);
        line.setStartY(_startY);
        line.setEndX(_endX);
        line.setEndY(_endY);

        addChildNodeIfNotPresent(line);
    }
   
}
