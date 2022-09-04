package seng202.group7.controllers.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import seng202.group7.controllers.views.MapController;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.data.CustomException;
import seng202.group7.view.MainScreen;
import seng202.group7.data.QueryBuilder;
import java.io.IOException;
import java.util.Objects;

/**
 * This menu allows a user to pick a field to search and then attempts to match
 * text entered by the user to that in the database.
 *
 * @author John Elliott
 * @author Shaylin Simadari
 */
public class SearchMenuController {
    @FXML
    private TextField inputField;


    /**
     * Gets the current side panel and replaces it with the general menu panel.
     *
     * @param event             The event action that was triggered.
     */
    public void toMenu(ActionEvent event){
        // As the side panels root is the main border panel we use .getRoot().
        BorderPane pane = (BorderPane) (((Node) event.getSource()).getScene()).getRoot();
        // This removes the current search effect being applied to the table when the paginator is initialized.
        ControllerData.getInstance().setSearchQuery("");
        try {
            VBox menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/generalMenu.fxml")));
            // Changes side menu to the filter menu.
            pane.setLeft(menuItems);
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the General Menu screens FXML file.", 
                e.getClass().toString()));
        }

        if (pane.getCenter().getId().equals("mapViewPane")) {
            try {
                // Changes side menu to the filter menu.
                StackPane mapView = ControllerData.getInstance().getGoogleMap();
                pane.setCenter(mapView);
                // Reload pins.
                WebView map = (WebView) mapView.getChildren().get(0);
                MapController.updatePins(map);
            } catch (NullPointerException e) {
                MainScreen.createErrorWin(new CustomException("Error caused when loading the Map View screens FXML file.", e.getClass().toString()));
            }
        } else {
            try {
                BorderPane tableView = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/views/pageView.fxml")));
                // Changes side menu to the filter menu.
                pane.setCenter(tableView);
            } catch (IOException | NullPointerException e) {
                MainScreen.createErrorWin(new CustomException("Error caused when loading the Pagination screens FXML file.", e.getClass().toString()));
            }
        }

    }

    /**
     * This method is used to determine the condition that should be applied to when the paginator is initialized
     * and therefore allow a user to search based on the text entered.
     *
     * @param event             The event action that was triggered.
     */
    public void search(KeyEvent event) {
        // Determines the condition that will be used.
        String query = QueryBuilder.search(inputField.getText());
        // By setting this where query when the paginator is generated the data accessor will apply it to the search.
        ControllerData.getInstance().setSearchQuery(query);
        // As the side panels root is the main border panel we use .getRoot().
        BorderPane pane = (BorderPane) (((Node) event.getSource()).getScene()).getRoot();
        if (pane.getCenter().getId().equals("mapViewPane")) {
            try {
                // Changes side menu to the filter menu.
                StackPane mapView = ControllerData.getInstance().getGoogleMap();
                pane.setCenter(mapView);
                //reLoad pins.
                WebView map = (WebView) mapView.getChildren().get(0);
                MapController.updatePins(map);
            } catch (NullPointerException e) {
                MainScreen.createErrorWin(new CustomException("Error caused when loading the Map View screens FXML file.", e.getClass().toString()));
            }
        } else {
            try {
                BorderPane tableView = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/views/pageView.fxml")));
                // Changes side menu to the filter menu.
                pane.setCenter(tableView);
            } catch (IOException | NullPointerException e) {
                MainScreen.createErrorWin(new CustomException("Error caused when loading the Pagination screens FXML file.", e.getClass().toString()));
            }
        }
    }
}
