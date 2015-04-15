package sil.org.syllableparser.view;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.FileChooser;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.SyllableParserException;
import sil.org.syllableparser.model.ApproachView;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author Based on a tutorial by Marco Jakob:
 *         http://code.makery.ch/library/javafx-8-tutorial/part5/
 */
public class RootLayoutController implements Initializable {

	private MainApp mainApp;
	private Locale currentLocale;
	@FXML private ListView<ApproachView> approachViews;
	private ObservableList<ApproachView> cvApproachViews = FXCollections.observableArrayList();
	private ObservableList<ApproachView> oncApproachViews = FXCollections.observableArrayList();
		
	@FXML private Button buttonCVApproach;
	@FXML private Button buttonSonorityHierarchyApproach;
	@FXML private Button buttonONCApproach;
	@FXML private Button buttonMoraicApproach;
	@FXML private Button buttonNuclearProjectionApproach;
	@FXML private Button buttonOTApproach;
	private Button currentButtonSelected;

	private static String kSyllableParserDataExtension = ".sylpdata";
	private String syllableParserFilterDescription;
	private String syllableParserFilterExtensions;

	private ResourceBundle bundle;
	private String sAboutHeader;
	private String sAboutContent;
	private String sFileFilterDescription;
	private String sChangeInterfaceLanguage;
	private String sChooseInterfaceLanguage;
	private String sChooseLanguage;
	private String sNotImplementedYetHeader;
	private String sNotImplementedYetContent;

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 * @param locale
	 *            TODO
	 */
	public void setMainApp(MainApp mainApp, Locale locale) {
		this.mainApp = mainApp;
		this.currentLocale = locale;
		syllableParserFilterDescription = sFileFilterDescription + " (*."
				+ kSyllableParserDataExtension + ")";
		syllableParserFilterExtensions = "*" + kSyllableParserDataExtension;

		cvApproachViews.add(new ApproachView("Segment Inventory", "handleSegmentInventory"));
		cvApproachViews.add(new ApproachView("Natural Classes", "handleNaturalClasses"));
	
		oncApproachViews.add(new ApproachView("Segment Inventory", "handleSegmentInventory"));
		oncApproachViews.add(new ApproachView("Onset Segments", "handleOnsetSegments"));
		
	}

	/**
	 * Creates an empty language project.
	 */
	@FXML
	private void handleNew() {
		// mainApp.getPersonData().clear();
		// mainApp.setPersonFilePath(null);
	}

	/**
	 * Opens a FileChooser to let the user select an address book to load.
	 */
	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				syllableParserFilterDescription, syllableParserFilterExtensions);
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// mainApp.loadPersonDataFromFile(file);
		}
	}

	/**
	 * Saves the file to the person file that is currently open. If there is no
	 * open file, the "save as" dialog is shown.
	 */
	@FXML
	private void handleSave() {
		File personFile = null; // mainApp.getPersonFilePath();
		if (personFile != null) {
			// mainApp.savePersonDataToFile(personFile);
		} else {
			handleSaveAs();
		}
	}

	/**
	 * Opens a FileChooser to let the user select a file to save to.
	 */
	@FXML
	private void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				syllableParserFilterDescription, syllableParserFilterExtensions);
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(kSyllableParserDataExtension)) {
				file = new File(file.getPath() + kSyllableParserDataExtension);
			}
			// mainApp.savePersonDataToFile(file);
		}
	}

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(MainApp.kApplicationTitle);
		alert.setHeaderText(sAboutHeader);
		alert.setContentText(sAboutContent);

		alert.showAndWait();
	}

	private void showNotImplemetnedYet() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(MainApp.kApplicationTitle);
		alert.setHeaderText(sNotImplementedYetHeader);
		alert.setContentText(sNotImplementedYetContent);

		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}
	
	@FXML
	private void handleApproachClick() {
		
	}

	/**
	 * CV Approach
	 */
	@FXML
	private void handleCVApproach() {
		toggleButtonSelectedStatus(buttonCVApproach);
		approachViews.setItems(cvApproachViews);
	}

	private void toggleButtonSelectedStatus(Button myButton) {
		if (currentButtonSelected != null) {
			currentButtonSelected.setStyle("-fx-background-color: #BFD0FF;");
		}
		currentButtonSelected = myButton;
		myButton.setStyle("-fx-background-color: #F5DEB3;");
	}

	/**
	 * Sonority Hierarchy Approach
	 */
	@FXML
	private void handleSonorityHierarchyApproach() {
		toggleButtonSelectedStatus(buttonSonorityHierarchyApproach);
		showNotImplemetnedYet();
	}

	/**
	 * ONC Approach
	 */
	@FXML
	private void handleONCApproach() {
		toggleButtonSelectedStatus(buttonONCApproach);
		approachViews.setItems(oncApproachViews);
	}

	/**
	 * Moraic Approach
	 */
	@FXML
	private void handleMoraicApproach() {
		toggleButtonSelectedStatus(buttonMoraicApproach);
		showNotImplemetnedYet();
	}

	/**
	 * Nuclear Projection Approach
	 */
	@FXML
	private void handleNuclearProjectionApproach() {
		toggleButtonSelectedStatus(buttonNuclearProjectionApproach);
		showNotImplemetnedYet();
	}

	/**
	 * OT Approach
	 */
	@FXML
	private void handleOTApproach() {
		toggleButtonSelectedStatus(buttonOTApproach);
		showNotImplemetnedYet();
	}

	/**
	 * Change interface language.
	 */
	@FXML
	private void handleChangeInterfaceLanguage() {

		Map<String, ResourceBundle> validLocales = new TreeMap<String, ResourceBundle>();
		getListOfValidLocales(validLocales);

		ChoiceDialog<String> dialog = new ChoiceDialog<>(
				currentLocale.getDisplayLanguage(currentLocale), validLocales.keySet());
		dialog.setTitle(sChangeInterfaceLanguage);
		dialog.setHeaderText(sChooseInterfaceLanguage);
		dialog.setContentText(sChooseLanguage);

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(locale -> mainApp.setLocale(validLocales.get(locale).getLocale()));

	}

	private void getListOfValidLocales(Map<String, ResourceBundle> choices) {
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			ResourceBundle rb = ResourceBundle.getBundle(
					"sil.org.syllableparser.resources.SyllableParser", locale);
			if (rb != null) {
				String localeName = rb.getLocale()
						.getDisplayName(currentLocale);
				choices.putIfAbsent(localeName, rb);
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		sAboutHeader = bundle.getString("about.header");
		sAboutContent = bundle.getString("about.content");
		sFileFilterDescription = bundle.getString("file.filterdescription");
		sChangeInterfaceLanguage = bundle
				.getString("menu.changeinterfacelanguage");
		sChooseInterfaceLanguage = bundle
				.getString("dialog.chooseinterfacelanguage");
		sChooseLanguage = bundle.getString("dialog.chooselanguage");
		sNotImplementedYetHeader = bundle.getString("misc.niyheader");
		sNotImplementedYetContent = bundle.getString("misc.niycontent");

		toggleButtonSelectedStatus(buttonCVApproach);
		approachViews.setItems(cvApproachViews);
		
		approachViews.setCellFactory((list) -> {
		    return new ListCell<ApproachView>() {
		        @Override
		        protected void updateItem(ApproachView item, boolean empty) {
		            super.updateItem(item, empty);

		            if (item == null || empty) {
		                setText(null);
		            } else {
		                setText(item.getViewName());
		            }
		        }
		    };
		});

		// Handle ListView selection changes.
		MultipleSelectionModel<ApproachView> m = approachViews.getSelectionModel();
		ReadOnlyObjectProperty<ApproachView> p = m.selectedItemProperty();
		p.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				System.out.println("ListView Selection Changed (selected: " + 
						newValue.getViewHandler() + ")");
				try {
					Class<?> c = Class.forName(this.getClass().getName());
					Method method = c.getDeclaredMethod(newValue.getViewHandler(), (Class<?>[])null);
					method.invoke(this, (Object[])null);
				} catch (NoSuchMethodException nsme) {
					SyllableParserException spe = new SyllableParserException(newValue.getViewHandler() +
							" method not found in RootLayoutController\n" +
							nsme.getMessage());
					System.out.println(spe.getMessage());
					} 

				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
	}
}
