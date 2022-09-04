package seng202.group7.controllers.views;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.data.Crime;
import seng202.group7.data.CustomException;
import seng202.group7.data.Report;
import seng202.group7.data.DataAccessor;
import seng202.group7.view.MainScreen;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The controller, used by / linked to, the Data View FXML file.
 * Handles the generation of the table on initialization.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 */
public class DataViewController implements Initializable {
    /**
     * This is the GridPane that holds the table and is the root node.
     */
    @FXML
    private BorderPane frame;

    /**
     * This is the Table.
     */
    @FXML
    private TableView<Crime> tableView;


    /**
     * This method is run during the loading of the data view fxml file.
     * It generates what values will be stored in the columns.
     *
     * @param location      A URL object.
     * @param resources     A ResourceBundle object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> possibleColumns = Arrays.asList("Case Number,Id", "Date,date",
                "Primary Description,PrimaryDescription","Secondary Description,SecondaryDescription", "Domestic,Domestic",
                "X Coordinate,XCoord", "Y Coordinate,YCoord", "Latitude,Latitude","Longitude,Longitude",
                "Location Description,LocationDescription", "Block,Block", "Iucr,Iucr", "FBI CD,FbiCD", "Arrest,arrest",
                "Beat,Beat", "Ward,Ward");

        ContextMenu contextMenu = new ContextMenu();

        for (String columnName : possibleColumns) {
            TableColumn<Crime, ?> newColumn = createColumn(columnName, contextMenu);
            tableView.getColumns().add(newColumn);
        }

        tableView.setContextMenu(contextMenu);

        // On a double click and the row isn't empty it will trigger the swap view method.
        tableView.setRowFactory( tv -> {
            TableRow<Crime> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Crime rowData = row.getItem();
                    swapViews(rowData);
                }
            });
            return row;
        });

        // Refreshes the table data when the table view is returned to.
        frame.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent != null) {
                newParent.parentProperty().addListener((obs1, oldParent1, pagination) ->
                        pagination.getParent().parentProperty().addListener((obs2, oldParent2, newParent2) -> {
                    if (newParent2 != null) {
                        setTableContent();
                    }
                }));
            }
        });
        setTableContent();
    }

    /**
     * Creates a new column with the given name and adds it to the tables context menu.
     * @param columnName The name of the column
     * @param contextMenu The tables context menu
     * @return The new column
     */
    private TableColumn<Crime, ?> createColumn(String columnName, ContextMenu contextMenu) {

        String[] columnData = columnName.split(",");
        TableColumn<Crime, String> newColumn = new TableColumn<>(columnData[0]);
        newColumn.setCellValueFactory(new PropertyValueFactory<>(columnData[1]));

        if (columnData[0].equals("Date")) {
            newColumn.setCellValueFactory(setup -> {
                SimpleStringProperty property = new SimpleStringProperty();
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                property.setValue(dateFormat.format(setup.getValue().getDate()));
                return property;
            });
        }
        MenuItem columnMenu = new MenuItem(columnData[0]);
        // Make it so when clicked will hide/show the column
        columnMenu.setOnAction(this::visibilityToggleInit);

        contextMenu.getItems().add(columnMenu);

        if (ControllerData.getInstance().getVisibleColumns() == null) {
            ControllerData.getInstance().setVisibleColumns(
                    Arrays.asList("Case Number", "Date", "Primary Description","Arrest", "Ward")
            );
        }
        List<String> visibleColumns = ControllerData.getInstance().getVisibleColumns();
        // Only show default columns
        if (!visibleColumns.contains(columnData[0])) {
            newColumn.setVisible(false);
        }
        return newColumn;
    }

    /**
     * Makes the menuItem toggle the visibility of the relevant column.
     * @param event The event that triggers this method.
     */
    private void visibilityToggleInit(ActionEvent event) {
        for (TableColumn<Crime, ?> col : tableView.getColumns()) {
            if (col.getText().equals(((MenuItem) event.getSource()).getText())) {
                col.setVisible(!col.visibleProperty().get());
                List<String> visibleColumns = ControllerData.getInstance().getVisibleColumns();
                List<String> visibleUpdate = new ArrayList<>();
                for (String column : visibleColumns) {
                    if (!column.equals(col.getText())){
                        visibleUpdate.add(column);
                    }
                }
                if (!visibleColumns.contains(col.getText())) {
                    visibleUpdate.add(col.getText());
                }
                ControllerData.getInstance().setVisibleColumns(visibleUpdate);
                return;
            }
        }
    }


    /**
     * This method stores that current state of the table and the selected row in the ControllerData and then loads,
     * the detailed data view screen and swaps it for the current raw data view screen.
     *
     * @param rowData       The Crime object from the selected row.
     */
    private void swapViews(Crime rowData){
        // This section must come first as the rowData is need when initializing the crimeEdit FXML.
        ControllerData controllerData = ControllerData.getInstance();
        controllerData.setCurrentRow(rowData);

        BorderPane rootPane = (BorderPane) frame.getParent().getParent().getParent().getParent();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/entryView.fxml"));
            Node newFrame = loader.load();

            ((EntryController) loader.getController()).setLastFrame(rootPane.getCenter());

            rootPane.setCenter(newFrame);
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the Entry View screens FXML file.", e.getClass().toString()));
        }
    }


    /**
     * Creates an observable list which is used to store the data that will be displayed in the table.
     */
    private void setTableContent() {
        // Gets the current set of reports based on the pagination's current page.
        try {
            ControllerData controllerData = ControllerData.getInstance();
            List<Report> reports = DataAccessor.getInstance().getPageSet(controllerData.getCurrentList(),
                controllerData.getCurrentPage(), controllerData.getWhereQuery());
            List<Crime> crimes = new ArrayList<>();
            reports.forEach(report -> crimes.add((Crime) report));
            ObservableList<Crime> data = FXCollections.observableList(crimes);
            tableView.setItems(data);
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
        }

    }
}
