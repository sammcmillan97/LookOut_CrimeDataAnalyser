package seng202.group7.controllers.menus;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seng202.group7.data.Crime;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.data.Report;
import seng202.group7.analyses.Comparer;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.view.MainScreen;

/**
 * The controller, used by / linked to, the compares FXML file.
 * Handles the comparisons of two crime objects.
 *
 * @author Jack McCorkindale
 * @author Shaylin Simadari
 * @author Sam McMillan
 */
public class CompareController implements Initializable {

    @FXML
    private TextField reportOneText;
    
    @FXML
    private TextField reportTwoText;

    @FXML
    private Label resultText;
    
    @FXML
    private VBox sideMenu;

    private static String report1;
    private static String report2;

    /**
     * A style class that can be added to a node to add error formatting.
     */
    private PseudoClass errorClass;

    /**
     * This method is run during the loading of the compare menu fxml.
     *
     * @param location      A URL object.
     * @param resources     A ResourceBundle object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        errorClass = PseudoClass.getPseudoClass("error");

        loadGUIFields();
    }


    /**
     * Gets the current side panel and replaces it with the general menu panel.
     */
    public void toMenu() {
        saveGUIFields();
        BorderPane pane = (BorderPane) sideMenu.getParent();
        try {
            VBox menuItems = FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/menus/generalMenu.fxml")));
            // Changes side menu to the filter menu.
            pane.setLeft(menuItems);
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the General Menu screens FXML file.", e.getClass().toString()));
        }

    }

    /**
     * Gets the two pieces of data and creates a string based on the distance and time comparison between the two values
     * If the two values are the same feedback will notify the user to select two distinct values
     */
    public void compareReports() {
        DataAccessor data = DataAccessor.getInstance();
        resultText.setText("");
        int list = ControllerData.getInstance().getCurrentList();
        String resultTextString = "";
        Crime reportOne = null;
        Crime reportTwo = null;
        try {
            reportOne = data.getCrime(reportOneText.getText(), list);
            reportTwo = data.getCrime(reportTwoText.getText(), list);
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
        }
        reportOneText.pseudoClassStateChanged(errorClass, reportOne == null);
        reportTwoText.pseudoClassStateChanged(errorClass, reportTwo == null);
        if (reportOne == null || reportTwo == null) { //Checks if ether of the reports are not if so doesnt compare
            return;
        } else {
            if (reportOne.getId().equals(reportTwo.getId())) { //Checks if the reports are the same value if so informs the user
                resultTextString += "The two crimes are the same value, please select two different values.";
            } else {
                resultTextString += "Distance:";
                resultTextString += compareDistance(reportOne, reportTwo); //Adds the distance between the reports
                resultTextString += "\n\nTime Difference:";
                resultTextString += compareTime(reportOne, reportTwo); //Adds the time difference between the reports
                }
        }
        resultText.setText(resultTextString);
    }

    /**
     * Compares the distance between the two reports and returns a string representing this value
     *
     * @param reportOne     The first report to be compared
     * @param reportTwo     The second report to be compared
     */
    private String compareDistance(Report reportOne, Report reportTwo) {
        double distance = Comparer.locationDifference(reportOne, reportTwo);
        if (distance == -1) {
            return String.format(" Crime %s has no location values.", reportOneText.getText());
        } else if(distance == -2) {
            return String.format(" Crime %s has no location values.", reportTwoText.getText());
        } else if(distance == -3) {
            return String.format(" Crime %s and Crime %s have no location values.", reportOneText.getText(), reportTwoText.getText());
        } else {
            return String.format(" %.2fkm.", distance);
        }
    }

    /**
     * Compares the time difference between the two reports and returns a string representing this value
     *
     * @param reportOne     The first report to be compared
     * @param reportTwo     The second report to be compared
     */
    private String compareTime(Report reportOne, Report reportTwo) {
        List<Long> time = Comparer.timeDifference(reportOne, reportTwo);
        String timeString = "";
        if (time.get(3) > 0) {
            timeString += String.format("%d year(s) ", time.get(3)); //Time in years
        }
        if (time.get(2) > 0) {
            timeString += String.format("%d day(s) ", time.get(2)); //Time in months
        }
        if (time.get(1) > 0) {
            timeString += String.format("%d hour(s) ", time.get(1)); //Time in hours
        }
        if (time.get(0) > 0) {
            timeString += String.format("%d minute(s) ", time.get(0)); //Time in minutes
        }
        if (timeString.equals("")) {
            return (" Occurred at the same time."); //If they occured at the same time
        } else {
            return String.format(" %sapart.", timeString);
        }
    }

    /**
     * Will check to see if the data is being viewed through the table or data entry. It will then run the respective
     * methods to get the case number of the crime and insert the value into the appropriate table based on the button clicked.
     *
     * @param event         The button event that triggered this method.
     */
    public void addSelected(ActionEvent event) {
        String selectedCrime;
        // Starts with getting the root panel.
        BorderPane pane = (BorderPane) (((Node) event.getSource()).getScene()).getRoot();
        try {
            // Then it gets the pagination node from the centre.
            Node centreNode = ((BorderPane) pane.getCenter()).getCenter();
            if (centreNode instanceof Pagination) {
                selectedCrime = getFromPages(centreNode);
            } else {
                selectedCrime = getFromEntry(centreNode);
            }
        } catch (Exception ignore) {
            selectedCrime = null; // No correct data loaded so no value can be selected.
        }

        if (selectedCrime != null) {
            Button addButton = (Button) event.getSource();
            // Checks to see which add button was clicked so that it can be added to the right text field.
            if (Objects.equals(addButton.getId(), "addR1")) {
                reportOneText.setText(selectedCrime);
            } else {
                reportTwoText.setText(selectedCrime);
            }
        }
    }

    /**
     * Gets given a node which corresponding to a Pagination node and from this it gets the table and the currently
     * selected value of the table which it then returns.
     *
     * @param centreNode    The Pagination node.
     * @return  The case number.
     */
    private String getFromPages(Node centreNode) {
        Pagination pagination = (Pagination) centreNode;
        // From currentStakePane being stored in the Pagination class the children (which is the dataView FXML file) are retrieved.
        BorderPane tablePane = ((BorderPane) ((StackPane) pagination.getChildrenUnmodifiable().get(0)).getChildren().get(0));
        // Uses the "?" as the casting is happening during the runtime of the application and so it can not check the type held within the classes.
        // Instead, now when retrieving items from the table we have to cast them to crime objects.
        TableView<?> tableView = (TableView<?>) tablePane.getCenter();
        Crime crime = (Crime) tableView.getSelectionModel().getSelectedItem();
        if (crime != null) {
            return crime.getId();
        } else {
            return null;
        }
    }

    /**
     * Gets given a node which corresponding to a GridPane which holds a set of VBoxes with TextFields inside them.
     * It uses the lookup method on first the GridPane to get the VBox needed and then the VBox to find the TextField.
     * It then returns the string within the textField.
     *
     * @param centreNode    The GridPane node.
     * @return  The case number.
     */
    private String getFromEntry(Node centreNode) {
        GridPane gridEntry = (GridPane) centreNode;
        String selectedCrime = ((TextField) gridEntry.lookup("#generalInformation").lookup("#cNoText")).getText();
        if (Objects.equals(selectedCrime, "")) {
            return null;
        } else {
            return selectedCrime;
        }
    }

    /**
     * Saves the current compared results so that they are stored when changing menus.
     */
    private void saveGUIFields(){
        if(!reportOneText.getText().equals("")) {
            report1 = reportOneText.getText();
        }
        if(!reportTwoText.getText().equals("")) {
            report2 = reportTwoText.getText();
        }
    }

    /**
     * Loads the current compared results so that they are stored when changing menus.
     */
    private void loadGUIFields(){
        if(report1 != null) {
            reportOneText.setText(report1);
        }
        if(report2 != null) {
            reportTwoText.setText(report2);
        }
    }
}
