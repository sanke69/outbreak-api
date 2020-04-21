package fr.javafx.utils;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;


public class FxUtils {
	/**
	 * Find the X coordinate in ancestor's coordinate system that corresponds to the X=0 axis in
	 * descendant's coordinate system.
	 *
	 * @param descendant a Node that is a descendant (direct or indirectly) of the ancestor
	 * @param ancestor   a Node that is an ancestor of descendant
	 */
	public static double getXShift( Node descendant, Node ancestor ) {
		double ret = 0.0;
		Node curr = descendant;
		while ( curr != ancestor ) {
			ret += curr.getLocalToParentTransform().getTx();
			curr = curr.getParent();
			if ( curr == null )
				throw new IllegalArgumentException( "'descendant' Node is not a descendant of 'ancestor" );
		}

		return ret;
	}

	/**
	 * Find the Y coordinate in ancestor's coordinate system that corresponds to the Y=0 axis in
	 * descendant's coordinate system.
	 *
	 * @param descendant a Node that is a descendant (direct or indirectly) of the ancestor
	 * @param ancestor   a Node that is an ancestor of descendant
	 */
	public static double getYShift( Node descendant, Node ancestor ) {
		double ret = 0.0;
		Node curr = descendant;
		while ( curr != ancestor ) {
			ret += curr.getLocalToParentTransform().getTy();
			curr = curr.getParent();
			if ( curr == null )
				throw new IllegalArgumentException( "'descendant' Node is not a descendant of 'ancestor" );
		}

		return ret;
	}

	/**
	 * Make a best attempt to replace the original component with the replacement, and keep the same
	 * position and layout constraints in the container.
	 * <p>
	 * Currently this method is probably not perfect. It uses three strategies:
	 * <ol>
	 *   <li>If the original has any properties, move all of them to the replacement</li>
	 *   <li>If the parent of the original is a {@link BorderPane}, preserve the position</li>
	 *   <li>Preserve the order of the children in the parent's list</li>
	 * </ol>
	 * <p>
	 * This method does not transfer any handlers (mouse handlers for example).
	 *
	 * @param original    non-null Node whose parent is a {@link Pane}.
	 * @param replacement non-null Replacement Node
	 */
	public static void replaceComponent( Node original, Node replacement ) {
		Pane parent = (Pane) original.getParent();
		//transfer any properties (usually constraints)
		replacement.getProperties().putAll( original.getProperties() );
		original.getProperties().clear();

		ObservableList<Node> children = parent.getChildren();
		int originalIndex = children.indexOf( original );
		if ( parent instanceof BorderPane ) {
			BorderPane borderPane = (BorderPane) parent;
			if ( borderPane.getTop() == original ) {
				children.remove( original );
				borderPane.setTop( replacement );

			} else if ( borderPane.getLeft() == original ) {
				children.remove( original );
				borderPane.setLeft( replacement );

			} else if ( borderPane.getCenter() == original ) {
				children.remove( original );
				borderPane.setCenter( replacement );

			} else if ( borderPane.getRight() == original ) {
				children.remove( original );
				borderPane.setRight( replacement );

			} else if ( borderPane.getBottom() == original ) {
				children.remove( original );
				borderPane.setBottom( replacement );
			}
		} else {
			//Hope that preserving the properties and position in the list is sufficient
			children.set( originalIndex, replacement );
		}
	}

}
