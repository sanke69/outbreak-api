module javafr.outbreak {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;

//	requires org.scenicview.scenicview;

	// Required exports from my other APIs - COULD EASILY BE REMOVED !!!
	exports fr.geodesic.referential.api.countries;

	exports fr.java.time;

	exports fr.javafx.scene.controls;
	exports fr.javafx.scene.layouts;
	exports fr.javafx.scene.properties;
	exports fr.javafx.scene;

	// The Outbreak API
	exports fr.outbreak;

	exports fr.outbreak.api;
	exports fr.outbreak.api.database;
	exports fr.outbreak.api.bean;
	exports fr.outbreak.api.records;

	exports fr.outbreak.api.forecast;

	exports fr.outbreak.graphics;
	exports fr.outbreak.graphics.charts;

	exports fr.outbreak.graphics.viewers.about;
	exports fr.outbreak.graphics.viewers.table;

	// For debug only... Eclipse issues with java modules and maven
	exports fr.run.outbreak;

}