package seng202.group7.controllers.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import seng202.group7.data.Report;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.controllers.data.InputType;
import seng202.group7.controllers.data.InputValidator;
import seng202.group7.data.Crime;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.view.MainScreen;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Arrays;
import java.util.ResourceBundle;


/**
 * The controller, used by / linked to, the crime edit FXML file.
 * Makes a detailed view of the data retrieved from the current row selected in the ControllerData object.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 */
public class EntryController implements Initializable {
    /**
     * The current selected rows crime object.
     */
    Report data;

    @FXML
    private TextField cNoText;
    @FXML
    private TextField iucrText;
    @FXML
    private TextField fbiText;
    @FXML
    private TextField blockText;
    @FXML
    private TextField beatText;
    @FXML
    private TextField wardText;
    @FXML
    private TextField xCoordText;
    @FXML
    private TextField yCoordText;
    @FXML
    private TextField latText;
    @FXML
    private TextField longText;
    @FXML
    private TextField priText;
    @FXML
    private TextField timeText;
    @FXML
    private TextArea secText;
    @FXML
    private TextArea locAreaText;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox arrestCheck;
    @FXML
    private CheckBox domesticCheck;
    @FXML
    private Node editButton;
    @FXML
    private Node deleteButton;
    @FXML
    private Node saveButton;
    @FXML
    private Node cancelButton;
    @FXML
    private Node frame;

    private TextField dateText;

    private List<Node> allInputs;
    private List<Node> editableInputs;

    private Node lastFrame;


    /**
     * This method is run during the loading of the crime edit fxml file.
     * It initializes the value validation classes before running methods to get
     * and store values in the text fields of the screen.
     *
     * @param location      A URL object.
     * @param resources     A ResourceBundle object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerData master = ControllerData.getInstance();
        dateText = datePicker.getEditor();

        prepareValidation();
    
        allInputs = Arrays.asList(cNoText, iucrText, fbiText, blockText, beatText, wardText, xCoordText, yCoordText, latText, longText,
        priText, secText, locAreaText, dateText, datePicker, arrestCheck, domesticCheck, timeText);
        editableInputs = Arrays.asList(iucrText, fbiText, blockText, beatText, wardText, xCoordText, yCoordText, latText, longText,
        priText, secText, locAreaText, dateText, datePicker, arrestCheck, domesticCheck, timeText);

        data = master.getCurrentRow();
        if (data != null) {
            setData();
        } else {
            editEntry();
        }
    }

    /**
     * Sets the types of validation required on each input node
     */
    private void prepareValidation() {
        dateText.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate date = LocalDate.parse(dateText.getText(), dateTimeFormat);
                datePicker.setValue(date);
            } catch (DateTimeParseException ignored) {}
        });

        InputValidator.addValidation(cNoText, InputType.REQUIRED);
        InputValidator.addValidation(cNoText, InputType.ID);
        
        InputValidator.addValidation(dateText, InputType.REQUIRED);
        InputValidator.addValidation(dateText, InputType.DATE);

        InputValidator.addValidation(timeText, InputType.REQUIRED);
        InputValidator.addValidation(timeText, InputType.TIME);

        InputValidator.addValidation(priText, InputType.REQUIRED);

        InputValidator.addValidation(secText, InputType.REQUIRED);

        InputValidator.addValidation(locAreaText, InputType.REQUIRED);

        InputValidator.addValidation(beatText, InputType.REQUIRED);
        InputValidator.addValidation(beatText, InputType.INTEGER);

        InputValidator.addValidation(wardText, InputType.REQUIRED);
        InputValidator.addValidation(wardText, InputType.INTEGER);

        InputValidator.addValidation(iucrText, InputType.REQUIRED);
        
        InputValidator.addValidation(fbiText, InputType.REQUIRED);
        
        InputValidator.addValidation(blockText, InputType.REQUIRED);
        
        InputValidator.addValidation(xCoordText, InputType.INTEGER);
        InputValidator.addValidation(yCoordText, InputType.INTEGER);

        InputValidator.addValidation(latText, InputType.DOUBLE);
        InputValidator.addValidation(longText, InputType.DOUBLE);

    }

    /**
     *  This method gets all the related data from the crime object and set it as the default text in its relevant field.
     */
    private void setData() {
        if (data instanceof Crime) {
            Crime temp = (Crime) data;
            // CheckBoxes:
            arrestCheck.setSelected(temp.getArrest());
            // General Information:
            cNoText.setText(temp.getId());
            iucrText.setText(temp.getIucr());
            fbiText.setText(temp.getFbiCD());
            // Location Information:
            blockText.setText(temp.getBlock());
            beatText.setText(String.valueOf(temp.getBeat()));
            wardText.setText(String.valueOf(temp.getWard()));
        }
        
        domesticCheck.setSelected(data.getDomestic());
        LocalDateTime date = data.getDate();
        if (date == null) {
            datePicker.setValue(null);
            timeText.setText(null);
        } else {
            datePicker.setValue(date.toLocalDate());
            String time = date.getHour() + ":" + String.format("%02d", date.getMinute());
            timeText.setText(time);
        }
        if (data.getXCoord() != null) {
            xCoordText.setText(String.valueOf(data.getXCoord()));
        }
        if (data.getYCoord() != null) {
            yCoordText.setText(String.valueOf(data.getYCoord()));
        }
        if (data.getLatitude() != null) {
            latText.setText(String.valueOf(data.getLatitude()));
        }
        if (data.getLongitude() != null) {
            longText.setText(String.valueOf(data.getLongitude()));
        }

        // Case Description:
        priText.setText(data.getPrimaryDescription());
        secText.setText(data.getSecondaryDescription());
        locAreaText.setText(data.getLocationDescription());

        for (Node node : allInputs) {
            node.setDisable(true);
        }

    }

    /**
     * Returns to the stored table state by retrieving it from the ControllerData object and setting it on the root pane.
     */
    public void returnView() {
        BorderPane pane = (BorderPane) frame.getParent();

        pane.setCenter(lastFrame);

    }

    /**
     * Allows the user to edit the values in value boxes by enabling them all. Also changes to save/cancel buttons.
     */
    public void editEntry() {
        editButton.setVisible(false);
        editButton.setManaged(false);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);

        saveButton.setVisible(true);
        saveButton.setManaged(true);
        cancelButton.setVisible(true);
        cancelButton.setManaged(true);

        for (Node node : editableInputs) {
            node.setDisable(false);
        }
    }

    /**
     * Checks if there is valid data, if there is, modifies the visible buttons and calls the method
     * to fill all fields with the data. Otherwise, returns to previous view.
     */
    public void finishEdit() {
        if (data == null) {
            returnView();
            return;
        }
        editButton.setVisible(true);
        editButton.setManaged(true);
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);

        saveButton.setVisible(false);
        saveButton.setManaged(false);
        cancelButton.setVisible(false);
        cancelButton.setManaged(false);
        setData();
    }

    /**
     * Checks if all the data is valid, then modifies the existing data object to reflect the changes the user made.
     */
    public void saveEdit() {
        boolean valid = true;
        for (Node node : allInputs) {
            if (!InputValidator.validate(node)) {
                valid = false;
            }
        }
        if (!valid) {
            return;
        } 

        // CheckBoxes:
        Boolean arrest = arrestCheck.isSelected();
        Boolean domestic = domesticCheck.isSelected();
        // General Information:
        
        String caseNumber = cNoText.getText();
        LocalDateTime date = getDateTime();
        String iucr = iucrText.getText();
        String fbiCD = fbiText.getText();

        // Location Information:
        String block = blockText.getText();

        Integer beat = getInt(beatText);
        Integer ward = getInt(wardText);
        Integer xCoord = getInt(xCoordText);
        Integer yCoord = getInt(yCoordText);
 
        Double latitude = getDouble(latText);
        Double longitude = getDouble(longText);

        // Case Description:
        String primaryDescription = priText.getText();
        String secondaryDescription = secText.getText();
        String locationDescription = locAreaText.getText();

        DataAccessor dataAccessor = DataAccessor.getInstance();

        data = new Crime(caseNumber, date, block, iucr, primaryDescription, secondaryDescription, locationDescription, arrest, domestic, beat, ward, fbiCD, xCoord, yCoord, latitude, longitude);
        
        try {
            dataAccessor.editCrime((Crime) data, ControllerData.getInstance().getCurrentList());
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
        }

        finishEdit();
    }

    /**
     * Converts the LocalDate and LocalTime values from datePicker and timeText into a LocalDateTime object.
     *
     * @return LocalDateTime combination of datePicker and timeText
     */
    private LocalDateTime getDateTime() {
        if (dateText.getText() != null && timeText.getText() != null) {
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
            LocalTime time = LocalTime.parse(timeText.getText(), timeFormat);
            return LocalDateTime.of(datePicker.getValue(), time);
        }
        return null;
    }

    /**
     * Turns a TextField node into an integer value.
     *
     * @param node      A TextField to convert to an Integer
     * @return          The integer value.
     */
    private Integer getInt(TextField node) {
        if (!node.getText().isEmpty()) {
            return Integer.parseInt(node.getText());
        }
        return null;
    }

    /**
     * Turns a TextField node into a Double value.
     *
     * @param node      A TextField to convert to a Double
     * @return          The double value.
     */
    private Double getDouble(TextField node) {
        if (!node.getText().isEmpty()) {
            return Double.parseDouble(node.getText());
        }
        return null;
    }

    /**
     * Deletes the entry that is currently being viewed.
     */
    public void deleteEntry() {
        DataAccessor dataAccessor = DataAccessor.getInstance();
        try {
            dataAccessor.deleteReport(cNoText.getText(), ControllerData.getInstance().getCurrentList());
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
        }
        returnView();
    }

    /**
     * Sets the last frame the application was on.
     * @param lastFrame The last frame to return to when the bck button is pressed.
     */
    public void setLastFrame(Node lastFrame) {
        this.lastFrame = lastFrame;
    }

}
