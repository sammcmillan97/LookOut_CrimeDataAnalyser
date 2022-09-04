package seng202.group7.controllers.views;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.data.Crime;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.data.Report;

import static seng202.group7.view.MainScreen.createErrorWin;

/**
 * The controller class that is initialized with the map view fxml file.
 * Also stores some static classes used to load pins and update the map itself.
 *
 * @author John Elliott
 */
public class MapController implements Initializable {

    @FXML
    private WebView webView;

    /**
     * This method is run during the loading of the map view fxml file.
     *
     * @param location      A URL object.
     * @param resources     A ResourceBundle object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMap();
    }

    /**
     * Initialize the map and store it in the webViews webEngine.
     */
    private void initMap() {
        try {
            WebEngine webEngine = webView.getEngine();
            webEngine.load(Objects.requireNonNull(MapController.class.getResource("/networking/mapView.html")).toExternalForm());
        } catch (Exception e) {
            createErrorWin(new CustomException("Error caused caused within the google map.", e.getClass().toString()));
        }
    }

    /**
     * Creates a JSON array that stores the information used when creating the pins on the map.
     *
     * @param reports   The current set of loaded reports that need pins.
     * @return The full JSON array.
     */
    private static String toJSONArray(List<Report> reports) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        reports.forEach(report -> stringBuilder.append(
                ((Crime) report).getJSONString()));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * Executes the script that calls the import data method in the mapView.html file.
     * This updates the pins on the map to match the current set of reports in the table.
     *
     * @param map   The map that needs its pins updated.
     */
    public static void updatePins(WebView map) {
        // Gets the current set of reports based on the pagination's current page.
        try {
            ControllerData data = ControllerData.getInstance();
            List<Report> reports = DataAccessor.getInstance().getPageSet(data.getCurrentList(),
                data.getCurrentPage(), data.getWhereQuery());
            String scriptToExecute = "importData(" + toJSONArray(reports) + ");";
            map.getEngine().executeScript(scriptToExecute);
        } catch (CustomException e) {
            createErrorWin(new CustomException("Error caused when loading pins into the map view.", e.getClass().toString()));
        }

    }
}
