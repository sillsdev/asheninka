module org.sil.syllableparser {
	// Exports
	exports org.sil.syllableparser;
	exports org.sil.syllableparser.backendprovider;
	exports org.sil.syllableparser.model;
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

	// LibJavaDev
	requires transitive org.sil.utility;

	// JAXB
	requires jakarta.xml.bind;
	requires jakarta.activation;

	// JNA
	requires transitive com.sun.jna;
	requires com.sun.jna.platform;
	
	//JSON
	requires org.json;

	// JUnit
	requires junit;

	// Other modules/libraries
	requires antlr;
	requires transitive org.controlsfx.controls;
	requires javafx.base;
	requires javafx.media;
	requires java.base;
	requires org.sil.lingtree;
}
