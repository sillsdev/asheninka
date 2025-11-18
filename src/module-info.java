module org.sil.syllableparser {
	// Exports
	exports org.sil.syllableparser;
	exports org.sil.syllableparser.backendprovider;
	exports org.sil.syllableparser.model;
	exports org.sil.syllableparser.model.cvapproach;
	exports org.sil.syllableparser.model.moraicapproach;
	exports org.sil.syllableparser.model.npapproach;
	exports org.sil.syllableparser.model.oncapproach;
	exports org.sil.syllableparser.model.otapproach;
	exports org.sil.syllableparser.model.sonorityhierarchyapproach;
	exports org.sil.syllableparser.service;
	exports org.sil.syllableparser.view;
	exports org.sil.antlr4;
	exports org.sil.antlr4.environmentparser;
	exports org.sil.antlr4.environmentparser.antlr4generated;
	exports org.sil.antlr4.templatefilterparser;
	exports org.sil.antlr4.templatefilterparser.antlr4generated;

	opens org.sil.syllableparser.view to javafx.fxml;

	// Java
	requires java.desktop;
	requires java.prefs;

	// JavaFX
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires javafx.graphics;
	requires javafx.swing;
	requires javafx.web;

	// JAXB
	requires jakarta.xml.bind;
	requires jakarta.activation;
	opens org.sil.syllableparser.model;

	// JNA
	requires transitive com.sun.jna;
	requires com.sun.jna.platform;
	
	//JSON
	requires org.json;

	// LibJavaDev
	requires transitive org.sil.utility;

	// JUnit
	requires junit;

	// Other modules/libraries
	requires antlr;
	requires transitive org.controlsfx.controls;
	requires javafx.base;
	requires javafx.media;
	requires java.base;
	requires transitive richtextfx.fat;
	requires org.sil.lingtree;
	requires transitive com.ibm.icu;
}
