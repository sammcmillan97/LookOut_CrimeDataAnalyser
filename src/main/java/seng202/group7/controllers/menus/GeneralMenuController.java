package seng202.group7.controllers.menus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.controllers.views.EntryController;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.view.MainScreen;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * The controller, used by / linked to, the General Menu FXML file.
 * It links it buttons to the main menu screen and its controller.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 * @author Sam McMillan
 */
public class GeneralMenuController {

    @FXML
    private Node sideMenu;

    /**
     * Gets the current side panel and replace it with the filter menu panel.
     */
    public void toFilter() {
        BorderPane pane = (BorderPane) sideMenu.getParent();
        try {
            Node menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/filterMenu.fxml")));
            // Changes side menu to the filter menu.
            pane.setLeft(menuItems);
        } catch (IOException | NullPointerException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Filter Menu screens FXML file.", e.getClass().toString()));
        }

    }

    /**
     * Changes the menu bar to show the comparison menu.
     */
    public void toCompare() {
        BorderPane pane = (BorderPane) sideMenu.getParent();
        try {
            VBox menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/compareMenu.fxml")));
            // Changes side menu to the filter menu.
            pane.setLeft(menuItems);
        } catch (IOException | NullPointerException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Compare Menu screens FXML file.", e.getClass().toString()));
        }

    }

    /**
     * Gets a new file to be stored in the main database.
     * If no file is selected the table is reloaded with the current file.
     *
     * @param event             The event action that was triggered.
     */
    public void newImport(ActionEvent event) {

        File file = ControllerData.getInstance().getFile(event);
        if (file == null) {
            return;
        }
        String behavior;
        boolean skipBadValue;
        if (file.getName().endsWith(".csv")) {
            behavior = duplicateSelection();
            skipBadValue = badValueSelection();
        } else {
            try {
                if (DataAccessor.getInstance().getSize(ControllerData.getInstance().getCurrentList(), "") != 0){
                    behavior = duplicateSelection();
                } else {
                    behavior = "REPLACE";
                }
            } catch (CustomException e) {
                MainScreen.createWarnWin(e);
                return;
            }
            skipBadValue = false;
        }
        Scene window = ((Node) event.getTarget()).getScene();
        BorderPane rootPane = (BorderPane) sideMenu.getParent();
        Node center = rootPane.getCenter();
        new Thread(() -> {
            Platform.runLater(() ->window.setCursor(Cursor.WAIT));

            try {
                DataAccessor.getInstance().importFile(file, ControllerData.getInstance().getCurrentList(), behavior, skipBadValue);
            } catch (CustomException e) {
                if (e.getMessage().contains("busy")) {
                    return;
                } else if (e.getMessage().contains("complete")) {
                    Platform.runLater(()->MainScreen.createSuccessWin(e));
                } else {
                    Platform.runLater(()->MainScreen.createWarnWin(e));
                }
            }

            // Loads the paginator screen.
            try {
                BorderPane dataView = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/views/pageView.fxml")));
                // Adds the data view to the center of the screen if it is the selected screen.
                if (((BorderPane) sideMenu.getParent()).getCenter() == center) {
                    Platform.runLater(()->rootPane.setCenter(dataView));
                }
            } catch (IOException | NullPointerException e) {
                Platform.runLater(()->MainScreen.createWarnWin(new CustomException("Error caused when loading the Pagination screens FXML file.", e.getClass().toString())));
            }
            Platform.runLater(() ->window.setCursor(Cursor.DEFAULT));            
        }).start();
    }

    /**
     * Gets user input on how to handle invalid data in the imported data.
     * @return If invalid data is skipped or reverts changes.
     */
    private static boolean badValueSelection() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Invalid data response");
        alert.setHeaderText("How do you want to deal with invalid data?");

        ButtonType skip = new ButtonType("Skip invalid data");
		ButtonType cancel = new ButtonType("Don't import data");

        alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(skip, cancel);

        Optional<ButtonType> option = alert.showAndWait();

        return option.get() != null && option.get() == skip;
            
    }

    /**
     * Gets user input on how to handle duplicate ID in the imported data.
     * @return the SQL query string for handling duplicate ids
     */
    private static String duplicateSelection() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Duplicate data detected");
		alert.setHeaderText("Select a handling method for duplicate IDs.");

        ButtonType overwrite = new ButtonType("Overwrite");
		ButtonType skip = new ButtonType("Skip");
		ButtonType cancel = new ButtonType("Undo");

        alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(overwrite, skip, cancel);

        Optional<ButtonType> option = alert.showAndWait();
        String behavior = "ABORT";
        if (option.get() == overwrite) {
            behavior = "REPLACE";
        } else if (option.get() == skip) {
            behavior = "IGNORE";
        }
        return behavior;
    }

    /**
     * Gets the conditions the user has active and the location to save a new file before sending it to the DataAccessor to export
     */
    public void exportWithFilter() {
        String conditions = ControllerData.getInstance().getWhereQuery();
        File saveLocation = getLocation();
        if (saveLocation == null) {
            return;
        }
        Scene window = sideMenu.getScene();
        new Thread(() -> {
            Platform.runLater(()->window.setCursor(Cursor.WAIT));

            try {
                DataAccessor.getInstance().export(conditions, ControllerData.getInstance().getCurrentList(), saveLocation.toString());
            } catch (CustomException e) {
                Platform.runLater(()->MainScreen.createWarnWin(e));
            }
            Platform.runLater(()->window.setCursor(Cursor.DEFAULT));
            
        }).start();
    }

    /**
     * Gets the location to save a new file before sending it to the DataAccessor to export
     */
    public void exportWithoutFilter() {
        File saveLocation = getLocation();
        if (saveLocation == null) {
            return;
        }
        Scene window = sideMenu.getScene();
        new Thread(() -> {
            Platform.runLater(()->window.setCursor(Cursor.WAIT));

            try {
                DataAccessor.getInstance().export("", ControllerData.getInstance().getCurrentList(), saveLocation.toString());
            } catch (CustomException e) {
                Platform.runLater(()->MainScreen.createWarnWin(e));
            }
            Platform.runLater(()->window.setCursor(Cursor.DEFAULT));
            
        }).start();
    }

    /**
     * Allows the user to select a location to save the database they are exporting.
     * @return The file to be created.
     */
    private File getLocation() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File("./"));
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("Database (*.db)", "*.db");
        FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().addAll(dbFilter, csvFilter);

        fileChooser.setTitle("Select save location");
        Stage stage = (Stage) sideMenu.getScene().getWindow();

        // Launches the file chooser.
        File selectedFile = fileChooser.showSaveDialog(stage);
        // If the file chooser is exited before a file is selected it will be a NULL value and should not continue.
        if (selectedFile != null) {
            if ((!selectedFile.getName().endsWith(".db")) && (!selectedFile.getName().endsWith(".csv"))) {
                if (fileChooser.getSelectedExtensionFilter().getExtensions().get(0).equals("*.db")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".db");
                } else {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
                }
            }
            return selectedFile;
        }
        return null;
    }

    /**
     * Moves the program to an empty entry view to create a new Report object.
     */
    public void toNewEntry() {
        BorderPane rootPane = (BorderPane) sideMenu.getParent();

        try {
            ControllerData.getInstance().setCurrentRow(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/entryView.fxml"));

            Node newFrame = loader.load();

            ((EntryController) loader.getController()).setLastFrame(rootPane.getCenter());

            rootPane.setCenter(newFrame);
        } catch (IOException | IllegalStateException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Entry View screens FXML file.", e.getClass().toString()));
        }
    }

    /**
     * Loads the search menu into the side menu view.
     */
    public void toSearch() {
        BorderPane rootPane = (BorderPane) sideMenu.getParent();
        try {
            VBox searchMenu = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/searchMenu.fxml")));
            rootPane.setLeft(searchMenu);
        } catch (IOException | NullPointerException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Search Menu screens FXML file.", e.getClass().toString()));
        }

    }
}
