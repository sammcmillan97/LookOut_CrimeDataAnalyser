package seng202.group7.controllers.menus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import seng202.group7.controllers.views.MapController;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.data.QueryBuilder;
import seng202.group7.data.FilterConditions;
import seng202.group7.data.Serializer;
import seng202.group7.view.MainScreen;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.controllers.data.InputType;
import seng202.group7.controllers.data.InputValidator;

/**
 * Controller class. Linked to filter menu FXML.
 * Handles initialization of filter type boxes.
 * Links GUI to filter class.
 *
 * @author Sami Elmadani
 * @author Shaylin Simadari
 * @author John Elliott
 */
public class FilterMenuController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private ComboBox<String> primaryBox;

    @FXML
    private ComboBox<String> locationBox;

    @FXML
    private TextField wardField;

    @FXML
    private TextField beatField;

    @FXML
    private ComboBox<String> arrestBox;

    @FXML
    private ComboBox<String> domesticBox;

    private List<Node> allValues;

    private static FilterConditions filterConditions;


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
            List<String> crimeTypes = DataAccessor.getInstance().getColumnString("primary_description", "");
            for (String type: crimeTypes) {
                primaryBox.getItems().add(type);
            }
            primaryBox.getItems().sort(null);
            primaryBox.getItems().add(0, null);
            List<String> locationTypes = DataAccessor.getInstance().getColumnString("location_description", "");
            for (String type: locationTypes) {
                locationBox.getItems().add(type);
            }
            locationBox.getItems().sort(null);
            locationBox.getItems().add(0, null);

        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
            return;
        }
        arrestBox.getItems().addAll(null, "Y", "N");

        domesticBox.getItems().addAll(null, "Y", "N");

        prepareValidation();

        loadGUIFields();
    }

    /**
     * Sets the types of validation required on each input node
     */
    private void prepareValidation() {
        TextField dateText = datePicker.getEditor();
        TextField dateText2 = datePicker2.getEditor();

        allValues = Arrays.asList(datePicker, dateText, primaryBox, locationBox, wardField, beatField,
        arrestBox, domesticBox);
        
        dateText.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate date = LocalDate.parse(dateText.getText(), dateTimeFormat);
                datePicker.setValue(date);
            } catch (DateTimeParseException ignored) {}
        });

        dateText2.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate date = LocalDate.parse(dateText2.getText(), dateTimeFormat);
                datePicker2.setValue(date);
            } catch (DateTimeParseException ignored) {}
        });

        InputValidator.addValidation(wardField, InputType.INTEGER);
        InputValidator.addValidation(beatField, InputType.INTEGER);
        InputValidator.addValidation(dateText, InputType.DATE);
        InputValidator.addValidation(dateText2, InputType.DATE);
    }

    /**
     * Loads a filter from a file
     * @param event   The event action that was triggered.
     */
    public void loadFilter(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Select filter");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("filters", "*.ser"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            try {
                filterConditions = Serializer.deserialize(file);
                loadGUIFields();
            } catch (CustomException e) {
                MainScreen.createWarnWin(e);
            }
        }
    }

    /**
     * Clears the current filter fields and clears filter from table
     * @param event   The event action that was triggered.
     */
    public void clearFilter(ActionEvent event) {
        datePicker.setValue(null);
        datePicker2.setValue(null);
        primaryBox.setValue(null);
        locationBox.setValue(null);
        wardField.setText("");
        beatField.setText("");
        arrestBox.setValue(null);
        domesticBox.setValue(null);
        applyFilter(event);
    }

    /**
     * Saves the current filter to a file
     * @param event   The event action that was triggered.
     */
    public void saveFilter(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Save filter");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("filters", "*.ser"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                saveGUIFields();
                Serializer.serialize(file, filterConditions);
            } catch (CustomException e) {
                MainScreen.createWarnWin(e);
            }
        }
    }

    /**
     * Applies the current filter values to the table
     * @param event   The event action that was triggered.
     */
    public void applyFilter(ActionEvent event){
        for (Node node : allValues) {
            if (!InputValidator.validate(node)) {
                return;
            }
        }
        saveGUIFields();
        String query = QueryBuilder.where(filterConditions);

        // By setting this where query when the paginator is generated the data accessor will apply it to the search.
        ControllerData.getInstance().setFilterQuery(query);
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

    /**
     * Clears the date stored in the date picker.
     */
    public void clearDate(){
        datePicker.setValue(null);
    }

    /**
     * Clears the date stored in the date picker 2.
     */
    public void clearDate2(){
        datePicker2.setValue(null);
    }

    /**
     * Returns the user to the general menu
     * Gets the current side panel and replaces it with the general menu panel.
     * @param event             The event action that was triggered.
     */
    public void toMenu(ActionEvent event) {
        saveGUIFields();
        // As the side panels root is the main border panel we use .getRoot().
        try {
            BorderPane pane = (BorderPane) (((Node) event.getSource()).getScene()).getRoot();
            Node menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/generalMenu.fxml")));
            // Changes side menu to the filter menu.
            pane.setLeft(menuItems);
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the General Menu screens FXML file.", e.getClass().toString()));
        }

    }

    /**
     * Loads the fields in from a file.
     */
    private void loadGUIFields(){
        if(filterConditions == null){
            return;
        }
        datePicker.setValue(filterConditions.getDateFrom());
        datePicker2.setValue(filterConditions.getDateTo());
        primaryBox.setValue(filterConditions.getPrimaryDescription());
        locationBox.setValue(filterConditions.getLocationDescription());
        wardField.setText(filterConditions.getWard() == null ? "" : filterConditions.getWard().toString());
        beatField.setText(filterConditions.getBeat() == null ? "" : filterConditions.getBeat().toString());
        arrestBox.setValue(filterConditions.getArrest() == null ? null : filterConditions.getArrest() ? "Y" : "N");
        domesticBox.setValue(filterConditions.getDomestic() == null ? null : filterConditions.getDomestic() ? "Y" : "N");
    }

    /**
     * Saves the fields to a file.
     */
    private void saveGUIFields(){
        filterConditions = new FilterConditions(
                datePicker.getValue(),
                datePicker2.getValue(),
                primaryBox.getValue(),
                locationBox.getValue(),
                getIntegerFromString(wardField.getText()),
                getIntegerFromString(beatField.getText()),
                getBooleanFromString(arrestBox.getValue()),
                getBooleanFromString(domesticBox.getValue())
        );
    }

    /**
     * Gets the integer value from the string and ensures if the string is empty it returns a null value.
     */
    private Integer getIntegerFromString(String str) {
        return str.equals("") ? null : Integer.parseInt(str);
    }

    /**
     * Determines if a value in the ComboBox corresponds to a true or false value.
     */
    private Boolean getBooleanFromString(String str) {
        return str == null ? null : str.equals("Y");
    }
}
