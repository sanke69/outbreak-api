package fr.javafx.scene.chart.plugins.indicators;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

public abstract class AbstractValueIndicatorRange<X, Y> extends AbstractValueIndicator<X, Y> {
    static final String STYLE_CLASS_LABEL = "range-indicator-label";
    static final String STYLE_CLASS_RECT  = "range-indicator-rect";

    private final DoubleProperty lowerBound              = new SimpleDoubleProperty(this, "lowerBound") {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };
    private final DoubleProperty upperBound              = new SimpleDoubleProperty(this, "upperBound") {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };

    private final DoubleProperty labelHorizontalPosition = new SimpleDoubleProperty(this, "labelHorizontalPosition",
            0.5) {
        @Override
        protected void invalidated() {
            if (get() < 0 || get() > 1) {
                throw new IllegalArgumentException("labelHorizontalPosition must be in rage [0,1]");
            }
            layoutChildren();
        }
    };
    private final DoubleProperty labelVerticalPosition   = new SimpleDoubleProperty(this, "labelVerticalPosition", 0.5) {
        @Override
        protected void invalidated() {
            if (get() < 0 || get() > 1) {
                throw new IllegalArgumentException("labelVerticalPosition must be in rage [0,1]");
            }
            layoutChildren();
        }
    };

    protected final Rectangle    rectangle = new Rectangle(0, 0, 0, 0);

    protected AbstractValueIndicatorRange(double _lower, double _upper, String _text) {
        super(_text);
        setLowerBound(_lower);
        setUpperBound(_upper);

        rectangle.setMouseTransparent(true);
        getChartChildren().addAll(rectangle, label);
    }

    public final void 				setLowerBound(double _value) {
        lowerBoundProperty().set(_value);
    }
    public final double 			getLowerBound() {
        return lowerBoundProperty().get();
    }
    public final DoubleProperty 	lowerBoundProperty() {
        return lowerBound;
    }

    public final void 				setUpperBound(double _value) {
        upperBoundProperty().set(_value);
    }
    public final double 			getUpperBound() {
        return upperBoundProperty().get();
    }
    public final DoubleProperty 	upperBoundProperty() {
        return upperBound;
    }

    public final void 				setLabelHorizontalPosition(double _value) {
        labelHorizontalPositionProperty().set(_value);
    }
    public final double 			getLabelHorizontalPosition() {
        return labelHorizontalPositionProperty().get();
    }
    public final DoubleProperty 	labelHorizontalPositionProperty() {
        return labelHorizontalPosition;
    }

    public final void 				setLabelVerticalPosition(double _value) {
        labelVerticalPositionProperty().set(_value);
    }
    public final double 			getLabelVerticalPosition() {
        return labelVerticalPositionProperty().get();
    }
    public final DoubleProperty 	labelVerticalPositionProperty() {
        return labelVerticalPosition;
    }

    protected void 					layout(Bounds _bounds) {
        if(_bounds.intersects(getChartPane().getPlotAreaBounds())) {
            rectangle.setX(_bounds.getMinX());
            rectangle.setY(_bounds.getMinY());
            rectangle.setWidth(_bounds.getWidth());
            rectangle.setHeight(_bounds.getHeight());

            addChildNodeIfNotPresent(rectangle);

            layoutLabel(_bounds, getLabelHorizontalPosition(), getLabelVerticalPosition());
        } else
            getChartChildren().clear();
    }

}
