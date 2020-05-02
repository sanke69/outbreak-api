package fr.javafx.scene.chart.plugins.indicators;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Label;

import fr.javafx.scene.chart.XY;
import fr.javafx.scene.chart.XYChartPane;
import fr.javafx.scene.chart.plugins.AbstractChartPlugin;

public abstract class AbstractValueIndicator<X, Y> extends AbstractChartPlugin<X, Y> {

    private final ObjectProperty<HPos> 	labelHorizontalAnchor = new SimpleObjectProperty<HPos>(this, "labelHorizontalAnchor", HPos.CENTER) {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };
    private final ObjectProperty<VPos> 	labelVerticalAnchor   = new SimpleObjectProperty<VPos>(this, "labelVerticalAnchor", VPos.CENTER) {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };

	protected final Label 				label = new Label();

    private final ChangeListener<? super Number> 
    									axisBoundsListener    = (_obs, _old, _new) -> layoutChildren();

    private final ListChangeListener<? super XY.ChartPlugin<?, ?>> 
    									pluginsListListener   = (Change<? extends XY.ChartPlugin<?, ?>> change) -> updateStyleClass();

    protected AbstractValueIndicator() {
        this(null);
    }
    protected AbstractValueIndicator(String _text) {
        setText(_text);
        label.setMouseTransparent(true);

        chartPaneProperty().addListener((_obs, _old, _new) -> {
            if (_old != null) {
                removeAxisListener(_old);
                removePluginsListListener(_old);
            }
            if (_new != null) {
                addAxisListener(_new);
                addPluginsListListener(_new);
            }
        });

        textProperty().addListener((_obs, _old, _new) -> layoutChildren());
    }

    public final void 					setLabelHorizontalAnchor(HPos _anchor) {
    	labelHorizontalAnchor.set(_anchor);
    }
	public final HPos 					getLabelHorizontalAnchor() {
        return labelHorizontalAnchor.get();
    }
    public final ObjectProperty<HPos> 	labelHorizontalAnchorProperty() {
        return labelHorizontalAnchor;
    }

    public final void 					setLabelVerticalAnchor(VPos _anchor) {
    	labelVerticalAnchor.set(_anchor);
    }
    public final VPos 					getLabelVerticalAnchor() {
        return labelVerticalAnchor.get();
    }
    public final ObjectProperty<VPos> 	labelVerticalAnchorProperty() {
        return labelVerticalAnchor;
    }

    public final void 					setText(String _text) {
        textProperty().set(_text);
    }
    public final String 				getText() {
        return textProperty().get();
    }
    public final StringProperty 		textProperty() {
        return label.textProperty();
    }

    protected abstract ValueAxis<?> 	getValueAxis(XYChartPane<X, Y> _chartPane);

    protected final void 				layoutLabel(Bounds _bounds, double _hPos, double _vPos) {
        if (label.getText() == null || label.getText().isEmpty()) {
            getChartChildren().remove(label);
            return;
        }

        double xPos = _bounds.getMinX() + _bounds.getWidth() * _hPos;
        double yPos = _bounds.getMinY() + _bounds.getHeight() * (1 - _vPos);

        double width  = label.prefWidth(-1);
        double height = label.prefHeight(width);

        if (getLabelHorizontalAnchor() == HPos.CENTER)
            xPos -= width / 2;
        else if (getLabelHorizontalAnchor() == HPos.RIGHT)
            xPos -= width;

        if (getLabelVerticalAnchor() == VPos.CENTER)
            yPos -= height / 2;
        else if (getLabelVerticalAnchor() == VPos.BASELINE)
            yPos -= label.getBaselineOffset();
        else if (getLabelVerticalAnchor() == VPos.BOTTOM)
            yPos -= height;
        
        label.resizeRelocate(xPos, yPos, width, height);
        addChildNodeIfNotPresent(label);
    }

    private void 						addAxisListener(XYChartPane<X, Y> _chartPane) {
        ValueAxis<?> valueAxis = getValueAxis(_chartPane);
        valueAxis.lowerBoundProperty().addListener(axisBoundsListener);
        valueAxis.upperBoundProperty().addListener(axisBoundsListener);
    }
    private void 						removeAxisListener(XYChartPane<X, Y> _chartPane) {
        ValueAxis<?> valueAxis = getValueAxis(_chartPane);
        valueAxis.lowerBoundProperty().removeListener(axisBoundsListener);
        valueAxis.upperBoundProperty().removeListener(axisBoundsListener);
    }

    private void 						addPluginsListListener(XYChartPane<X, Y> _chartPane) {
        _chartPane.getPlugins().addListener(pluginsListListener);
        updateStyleClass();
    }
    private void 						removePluginsListListener(XYChartPane<X, Y> _chartPane) {
        _chartPane.getPlugins().removeListener(pluginsListListener);
    }

    abstract void 						updateStyleClass();

    void 								setStyleClasses(Node _node, String _prefix, String _root) {
        _node.getStyleClass().setAll(_root, _prefix + _root, _prefix + _root + getIndicatorInstanceIndex());
    }

    void 								addChildNodeIfNotPresent(Node _node) {
        if (!getChartChildren().contains(_node)) {
            getChartChildren().add(_node);
        }
    }

    private int 						getIndicatorInstanceIndex() {
        if (getChartPane() == null)
            return 0;

        Class<?> thisClass = getClass();
        int      instanceIndex = -1;

        for (XY.ChartPlugin<X, Y> plugin : getChartPane().getPlugins()) {
            if (plugin.getClass().equals(thisClass))
                instanceIndex++;

            if (plugin == this)
                break;
        }

        return instanceIndex < 0 ? 0 : instanceIndex;
    }

}
