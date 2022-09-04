package seng202.group7.controllers.menus;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;

import seng202.group7.controllers.views.BarGraphViewController;
import seng202.group7.controllers.views.LineGraphViewController;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.data.QueryBuilder;
import seng202.group7.view.MainScreen;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Graph Menu Controller is the class which controls which graph is displayed by the program.
 *
 * @author Jack McCorkindale
 * @author Sam McMillan
 */
public class GraphMenuController implements Initializable {

    @FXML
    private ComboBox<String> graphType;

    @FXML
    private Node frame;

    @FXML
    private ComboBox<String> crimeType;

    @FXML
    private ComboBox<String> wardField;

    @FXML
    private ComboBox<String> beatField;

    @FXML
    private Parent root;


    /**
     * This method is run during the loading of the graph menu fxml file. Uses a query to scan the database and gives
     * the user the available choices for crime type and ward in the combo boxes
     * @param location      A URL object.
     * @param resources     A ResourceBundle object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> crimeType = new ArrayList<>();
        List<Integer> wards = new ArrayList<>();

        try {
            crimeType = DataAccessor.getInstance().getColumnString("primary_description", "");
            wards = DataAccessor.getInstance().getColumnInteger("ward", "");
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
        }

        Collections.sort(wards);

        this.crimeType.getItems().add(null);
        for (String type: crimeType) {
            this.crimeType.getItems().add(type); //adding crime types
        }

        this.wardField.getItems().add(null);
        for (Integer ward: wards) {
            this.wardField.getItems().add(String.valueOf(ward)); //adding ward values
        }
        graphType.getItems().add("Most Frequent Crime Types"); //adding bar graph options
        graphType.getItems().add("Most Dangerous Wards");
        graphType.getItems().add("Most Dangerous Beats");
        graphType.getItems().add("Most Dangerous Streets");
        graphType.getItems().add("Less Frequent Crime Types");
        graphType.getItems().add("Safest Beats");
        graphType.getItems().add("Safest Wards");
    }

    /**
     * This method monitors every time a ward combo box is changed and then updates the beat ComboBox so only 
     * values are encapsulated by the corresponding ward is available to be selected by the user.
     */
    public void changeBeat() {
        List<Integer> beats;
        try {
            beats = DataAccessor.getInstance().getColumnInteger("beat", "WHERE WARD=" + wardField.getValue());
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
            return;
        }
        beatField.setValue(null);
        if (wardField.getValue() == null) {
            beatField.setDisable(true);
            return;
        } else {
            beatField.setDisable(false);
        }
        beatField.getItems().clear();
        this.beatField.getItems().add(null);
            Collections.sort(beats);
        for (Integer beat: beats) {
            this.beatField.getItems().add(String.valueOf(beat));
        }
    }

    /**
     * Method triggered when the user changes the value in the crime type combo box, Checks what selection is made
     * and reloads the bar graph view.
     */
    public void selectBarGraph() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/barGraphView.fxml"));
            root = loader.load();
            BarGraphViewController graphView = loader.getController();
            graphView.prepareBarGraph(graphType.getValue());

            ((BorderPane) frame.getParent()).setCenter(root);
        } catch (IOException | IllegalStateException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Graph View screens FXML file.", e.getClass().toString()));
        }
    }

    /**
     * Method triggered when the user clicks on the display graph button, Checks what selections have been made by the user
     * in the crime type, ward and beat combo box's and displays the appropriate crime over time graph\
     */
    public void selectLineGraph() {
        try {
            ArrayList<String> choices = getChoices();
            String query = QueryBuilder.constructGraphQuery(choices);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/lineGraphView.fxml"));
            root = loader.load();
            LineGraphViewController graphView = loader.getController();
            graphView.prepareLineGraph(query, choices);
            ((BorderPane) frame.getParent()).setCenter(root);
        } catch (IllegalStateException | IOException e) {
            MainScreen.createWarnWin(new CustomException("Error caused when loading the Graph View screens FXML file.", e.getClass().toString()));
        }
    }

    /**
     * Gets the values from crime type, ward, and beat combo box's and creates a string list of the values
     * @return choices an ArrayList of strings to send to line graph view.
     */
    public ArrayList<String> getChoices() {
        ArrayList<String> choices = new ArrayList<>();
        choices.add(crimeType.getValue());
        choices.add(wardField.getValue());
        choices.add(beatField.getValue());
        return choices;
    }

}



