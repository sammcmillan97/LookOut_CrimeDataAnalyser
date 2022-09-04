package seng202.group7.controllers.data;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import seng202.group7.data.Report;
import java.io.File;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * This class acts as a connection between the controllers passing data through here so that only the controller's
 * initialization it can get the relevant data it needs. It accomplishes this by being a singleton class so only one
 * instance of it can exist.
 *
 * @author John Elliott
 * @author Jack McCorkindale
 */
public final class ControllerData {
    /**
     * This creates a singleton instants of the class.
     */
    private final static ControllerData INSTANCE = new ControllerData();

    /**
     * This holds the current row selected when going to the edit menu screen.
     */
    private Report currentRow;

    /**
     * Stores the current page of the paginator.
     */
    private int currentPage = 0;

    /**
     * Stores the list that is currently in use.
     */
    private int currentList = 1;

    /**
     * This is a condition that is used by the data accessor when searching the database.
     */
    private String searchQuery = "";

    /**
     * This is a condition that is used by the data accessor when filtering the database.
     */
    private String filterQuery = "";

    /**
     * What columns are currently visible in the data table
     */
    private List<String> visibleColumns;
    /**
     * Holds the Google map to prevent reloading each time.
     */
    private StackPane map;

    /**
     * Holds the Google search to prevent reloading each time.
     */
    private WebView search;


    /**
     * The constructor which is made private so that it can not be initialized from other classes.
     */
    private ControllerData() {}


    /**
     * Used to get the singleton instants of the class when assessing. This is done over a "static" class due to it
     * implementing an interface.
     *
     * @return INSTANCE     The only instants of this class.
     */
    public static ControllerData getInstance() {
        return INSTANCE;
    }

    /**
     * Setter for storing a crime object assigned to a selected row.
     *
     * @param rowData       The selected crime object.
     */
    public void setCurrentRow(Report rowData) {
        currentRow = rowData;
    }

    /**
     * Getter for retrieving a crime object assigned to a selected row.
     *
     * @return currentRow       The stored selected crime object.
     */
    public Report getCurrentRow() {
        return currentRow;
    }

    /**
     *
     * @return The list that is currently loaded in the database.
     */
    public int getCurrentList() {
        return currentList;
    }

    /**
     * Changes what list is recorded as being loaded in the database.
     */
    public void setCurrentList(int listId) {
        currentList = listId;
    }


    /**
     * Makes a screen to get a file from a user using the FilerChooser class.
     * Then it sends this files data to the data access layer and returns the data that need to be stored.
     *
     * @param event     The event action that was triggered.
     */
    public File getFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Select file");
        fileChooser.getExtensionFilters().add(new ExtensionFilter(".csv, .db files", "*.csv", "*.db"));

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        // Launches the file chooser.
        return fileChooser.showOpenDialog(stage);
    }


    /**
     * Gets the current page of the paginator.
     *
     * @return Page     The current page.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Sets the current page of the paginator.
     *
     * @param currentPage       The current page.
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * The getter for the where query.
     *
     * @return whereQuery       The condition being applied.
     */
    public String getWhereQuery() {
        if (searchQuery.isEmpty() || filterQuery.isEmpty()) {
            return "" + filterQuery + searchQuery;
        }
        return "" + filterQuery + " AND " + searchQuery;
    }

    /**
     * The setter for the search query.
     *
     * @param searchQuery    The condition being applied.
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    /**
     * The setter for the filter query.
     *
     * @param filterQuery    The condition being applied.
     */
    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    /**
     * Setter for the Google map view.
     * @param mapView   The map view node.
     */
    public void setGoogleMap(StackPane mapView) {
        map = mapView;
    }

    /**
     * Getter for the Google map view.
     * @return  Returns the map view node.
     */
    public StackPane getGoogleMap() {
        return map;
    }

    /**
     * Setter for the Google search.
     * @param externalSearch    The search view node.
     */
    public void setGoogleSearch(WebView externalSearch) {
        search = externalSearch;
    }

    /**
     * Getter for the Google search.
     * @return  Returns the search view node.
     */
    public WebView getGoogleSearch() {
        return search;
    }


    public List<String> getVisibleColumns() {
        return visibleColumns;
    }

    public void setVisibleColumns(List<String> columns) {
        visibleColumns = columns;
    }
}
