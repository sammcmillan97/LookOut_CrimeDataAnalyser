package seng202.group7.controllers.menus;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import seng202.group7.controllers.views.MapController;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.data.CustomException;
import seng202.group7.view.MainScreen;


/**
 * The controller, used by / linked to, the Menu FXML file.
 * This controller links the main screen menu system together with its components.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 * @author Sami Elmadani
 * @author Sam McMillan
 */
public class MenuController implements Initializable {
    /**
     * The main boarder panel used to hold the windows components.
     */
    @FXML
    private BorderPane menuFrame;

    @FXML
    private Button menuButton;

    /**
     * This method is run during the loading of the data view fxml file.
     * It generates what values will be stored in the columns.
     *
     * @param location      A URL object.
     * @param resources     A ResourceBundle object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Loads the first side menu screen.
            VBox menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/generalMenu.fxml")));
            // Sets the menu to the main panel and hides it, so it starts closed.
            menuFrame.setLeft(menuItems);
            VBox pane = (VBox) menuFrame.getLeft();
            // This is used to determine the direction the open and close animation will play.
            pane.setTranslateX(-(pane.getPrefWidth()));
            menuFrame.getLeft().setVisible(false);
            menuFrame.getLeft().setManaged(false);
            toData();

        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the General Menu screens FXML file.", e.getClass().toString()));
        }
    }

    /**
     * Runs the opening and closing animation for the side menu screen.
     */
    public void menuAnimation() {
        VBox pane = (VBox) menuFrame.getLeft();
        // Determines by the off set position of the VBox if the open or close animation needs to be played.
        TranslateTransition toggleMenu = new TranslateTransition(new Duration(350), pane);
        if (pane.getTranslateX() != 0) {
            // Creates an animation for opening the menu.
            toggleMenu.setToX(0);
            menuFrame.getLeft().setVisible(true);
            menuFrame.getLeft().setManaged(true);
            toggleMenu.play();
        } else {
            // Creates an animation for closing the menu.
            toggleMenu.setOnFinished(action->{
                menuFrame.getLeft().setVisible(false);
                menuFrame.getLeft().setManaged(false);
            });
            toggleMenu.setToX(-(pane.getWidth()));
            toggleMenu.play();
        }
    }

    /**
     * Creates and switches the center view to the programmable google search scene.
     */
    public void toSearch(){
        menuFrame.setCenter(ControllerData.getInstance().getGoogleSearch());
        // Loads the first side menu screen.
        try {
            VBox menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/generalMenu.fxml")));
            // Sets the menu to the main panel and hides it, so it starts closed.
            menuFrame.setLeft(menuItems);
            menuButton.setDisable(false);
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the General Menu screens FXML file.", e.getClass().toString()));
        }
        menuFrame.setLeft(null);
        menuButton.setDisable(true);
    }


    /**
     * Loads and sets the center view to the paginator scene.
     * This is done this way so that the raw data button can call this method.
     */
    public void toData() {
        // Loads the paginator which generates the raw data tables.
        try {
            BorderPane dataView = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/views/pageView.fxml")));
            // Adds the paginator to the center of the screen.
            menuFrame.setCenter(dataView);
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the Pagination screens FXML file.",
                e.getClass().toString()));
        }

        // Loads the first side menu screen.
        if ((menuFrame.getLeft() == null) || !menuFrame.getLeft().getId().equals("sideMenu")) {
            try {
                VBox menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/generalMenu.fxml")));
                // Sets the menu to the main panel and hides it, so it starts closed.
                menuFrame.setLeft(menuItems);
                menuButton.setDisable(false);
            } catch (IOException | NullPointerException e) {
                MainScreen.createErrorWin(new CustomException("Error caused when loading the General Menu screens FXML file.",
                    e.getClass().toString()));
            }
        }
    }

    /**
     * Loads and sets the center view to the data view scene.
     */
    public void openHelpPage(){

        // Loads the help screen.
        try {
            BorderPane dataView = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/views/helpView.fxml")));
            // Adds the help screen to the center of the screen.
            menuFrame.setCenter(dataView);
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the Help screens FXML file.", e.getClass().toString()));
        }
        menuFrame.setLeft(null);
        menuButton.setDisable(true);
    }

    /**
     * Loads and sets the menu and centre screen to the graph view.
     */
    public void toGraph(){
        //Loads graph screen
        try {
            GridPane graphView = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/views/graphView.fxml")));
            //Adds the graph view to the center of the screen.
            menuFrame.setCenter(graphView);
        } catch (IOException | NullPointerException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Graph View screens FXML file.", e.getClass().toString()));
            return;
        }

        try {
            VBox graphMenu = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/graphMenu.fxml")));
            // Changes side menu to the filter menu.
            menuFrame.setLeft(graphMenu);
            menuButton.setDisable(false);
        } catch (IOException | NullPointerException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Graph Menu screens FXML file.", e.getClass().toString()));
        }

    }

    /**
     * Goes to and loads the mapping view of the application.
     */
    public void toMap() {
        try {
            // Changes side menu to the filter menu.
            StackPane mapView = ControllerData.getInstance().getGoogleMap();
            menuFrame.setCenter(mapView);
            //reLoad pins.
            WebView map = (WebView) mapView.getChildren().get(0);
            MapController.updatePins(map);
        } catch (NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the Map View screens FXML file.", 
                e.getClass().toString()));
        }

        if ((menuFrame.getLeft() == null) || !menuFrame.getLeft().getId().equals("sideMenu")) {
            // Loads the first side menu screen.
            try {
                VBox menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/generalMenu.fxml")));
                // Sets the menu to the main panel and hides it, so it starts closed.
                menuFrame.setLeft(menuItems);
                menuButton.setDisable(false);
            } catch (IOException | NullPointerException e) {
                MainScreen.createErrorWin(new CustomException("Error caused when loading the General Menu screens FXML file.", 
                    e.getClass().toString()));
            }
        }
    }

    /**
     * Loads and sets the screen to the main menu (start screen).
     */
    public void toMain() {
        try {
            Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/gui/mainScreen.fxml")));
            Scene scene = new Scene(view);
            ((Stage) menuFrame.getScene().getWindow()).setScene(scene);
            // windowStage.show();
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the Start screen's FXML file.", 
                e.getClass().toString()));
        }
    }


}