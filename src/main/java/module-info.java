module javafr.outbreak {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires javafx.base;

	// Required exports from my other APIs - COULD EASILY BE REMOVED !!!
	exports fr.geodesic.referential.api.countries;

	exports fr.java.time;

	exports fr.javafx.scene.control.list;

	exports fr.javafx.scene.control.slider;
	exports fr.javafx.scene.chart;
	exports fr.javafx.scene.chart.axis;
	exports fr.javafx.scene.chart.types.xy;
	exports fr.javafx.scene.layouts;
	exports fr.javafx.scene.properties;
	exports fr.javafx.scene;

	// The Reporting API
	exports fr.reporting.api;
	exports fr.reporting.sdk.utils;
	exports fr.reporting.sdk.processing;
	exports fr.reporting.sdk.graphics;

	// The Outbreak API
	exports fr.outbreak;
	exports fr.outbreak.api;
	exports fr.outbreak.sdk.data;
	exports fr.outbreak.graphics.timeseries;
	exports fr.outbreak.graphics.viewers;

	// For debug only... Eclipse issues with java modules and maven
	exports fr.run.graphics;
	exports fr.run.report;
	exports fr.run.outbreak;

}
