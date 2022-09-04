package seng202.group7.controllers.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.controllers.data.InputType;
import seng202.group7.controllers.data.InputValidator;
import seng202.group7.controllers.menus.MenuController;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.view.MainScreen;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class creates the paginator that then itself generates the tables,
 * that are used to store selections of the database's information.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 */
public class PageController implements Initializable {
    /**
     * The paginator node.
     */
    @FXML
    private Pagination pages;

    @FXML
    private Node pageFrame;

    @FXML
    private TextField pageField;

    @FXML
    private Label dataTotal;


    /**
     * This method is run during the loading of the pages fxml file.
     * It generates the system for making tables when the pages are swapped.
     *
     * @param location      A URL object.
     * @param resources     A ResourceBundle object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputValidator.addValidation(pageField, InputType.INTEGER);

        pageFrame.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent != null) {
                int size;
                try {
                    size = DataAccessor.getInstance().getSize(ControllerData.getInstance().getCurrentList(),
                        ControllerData.getInstance().getWhereQuery());
                } catch (CustomException e) {
                    MainScreen.createErrorWin(e);
                    return;
                }
                dataTotal.setText("Data Total: "+size); // Sets current display total.
                int currentPage = ControllerData.getInstance().getCurrentPage();
                pages.setPageCount(1);
                pages.setPageCount((int) Math.ceil(size/1000.0)); // Sets the number of pages with 1000 crimes per page.
                pages.setCurrentPageIndex(currentPage);
            }
        });

        pages.setPageFactory(this::createPage); // When ever a page is swapped it calls this method.

    }

    /**
     * This method is called when creating a new table for the paginator to display to the user.
     *
     * @param pageIndex     The current page.
     * @return table        The table node that will be displayed.
     */
    private Node createPage(int pageIndex) {
        // Stores the current page number so when the table is initialized it can get the correct set of data.
        ControllerData.getInstance().setCurrentPage(pageIndex);
        try {
            return FXMLLoader.load(Objects.requireNonNull(MenuController.class.getResource("/gui/views/dataView.fxml")));
        } catch (IOException | NullPointerException e) {
            MainScreen.createErrorWin(new CustomException("Error caused when loading the Raw Data View screens FXML file.", e.getClass().toString()));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the current page to the start.
     */
    public void toFront() {
        pages.setCurrentPageIndex(0);
        pageField.setText("1");
    }

    /**
     * Sets the current page to the end.
     */
    public void toEnd() {
        pages.setCurrentPageIndex(pages.getPageCount());
        pageField.setText(""+pages.getPageCount());
    }


   /**
     * Gets the current text for the page the user wants to goto and then checks if it's valid after which it will either
     * change the page to the new page or change the text field back to a valid input and then if it can change pages.
     */
    public void gotoPage() {
        String input = pageField.getText();
        if (InputValidator.validate(pageField) && !input.isEmpty()) {
            int queryPage = Integer.parseInt(input);
            if (queryPage > 0 && queryPage <= pages.getPageCount()){
                pages.setCurrentPageIndex(Integer.parseInt(input) - 1); // Is valid and changes the page.
            }
        }
    }

}
