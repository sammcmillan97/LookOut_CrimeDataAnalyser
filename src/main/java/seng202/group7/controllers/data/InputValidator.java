package seng202.group7.controllers.data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import javafx.css.PseudoClass;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.view.MainScreen;

/**
 * Used to prepare and validate input nodes.
 * @author Jack McCorkindale
 */
public class InputValidator {

    private InputValidator(){}

    /**
     * Sets the error PseudoClass which gives a Node a red border.
     */
    private static final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    /**
     * Passes the value through the required validation methods and sets the Node's error style.
     *
     * @param inputNode The input to be validated.
     * @return If the input is valid.
     */
    public static boolean validate(Node inputNode) {
        if (inputNode.isDisabled()) {
            return true;
        }
        boolean valid = true;
        String input = getInput(inputNode);
        if (inputNode.getPseudoClassStates().contains(InputType.REQUIRED.getValidationType())) {
            valid = validateRequired(input);
        }
        if (valid && !"".equals(input)) {
            try {
                valid = validateInput(input, inputNode.getPseudoClassStates());
            } catch (CustomException e) {
                MainScreen.createWarnWin(e);
            }
        }
        inputNode.pseudoClassStateChanged(errorClass, !valid);
        return valid;
    }

    /**
     * Gets the user input from the given node.
     * @param inputNode The node that the user can use.
     * @return The input value contained in the input node.
     */
    private static String getInput(Node inputNode) {
        String input = null;
        if (inputNode instanceof TextField) {
            input = ((TextField) inputNode).getText();
        } else if (inputNode instanceof TextArea) {
            input = ((TextArea) inputNode).getText();
        } else if (inputNode instanceof DatePicker) {
            input = ((DatePicker) inputNode).getEditor().getText();
        }
        return input;
    }

    /**
     * Checks if the input has a value and adds the error class if it is invalid.
     *
     * @return If the field has an entry
     */
    private static boolean validateRequired(String input) {
        boolean valid;
        valid = !(input.isEmpty());
        return valid;
    }

    /**
     * Validates the value in each box according to the validation PseudoClasses previously set.
     *
     * @param input             The input to be validated
     * @param classes           The pseudo classes.
     * @return                  If the input is valid
     */
    private static boolean validateInput(String input, ObservableSet<PseudoClass> classes)  throws CustomException{
        boolean valid = true;
        // Checks input is an Integer
        if (classes.contains(InputType.INTEGER.getValidationType())) {
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException e) {
                valid = false;
            }
        }

        // Checks input is a Double
        if (classes.contains(InputType.DOUBLE.getValidationType())) {
            try {
                Double.parseDouble(input);
            } catch (NumberFormatException e) {
                valid = false;
            }
        }

        // Checks input is a valid time string
        if (classes.contains(InputType.TIME.getValidationType())) {
            try {
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("H:mm");
                LocalTime.parse(input, dateTimeFormat);
            } catch (DateTimeParseException e) {
                valid = false;
            }
        }

        // Checks input is a valid date string
        if (classes.contains(InputType.DATE.getValidationType())){
            try {
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate.parse(input, dateTimeFormat);
            } catch (DateTimeParseException e) {
                valid = false;
            }
        }

        // Checks input is unique name in the list database
        if (classes.contains(InputType.LISTNAME.getValidationType()) && DataAccessor.getInstance().getListId(input) != null) {
            valid = false;
        }

        // Checks input is a unique Id in the report database
        if (classes.contains(InputType.ID.getValidationType()) && 
            DataAccessor.getInstance().getCrime(input, ControllerData.getInstance().getCurrentList()) != null) {
            valid = false;
        }
        return valid;
    }
    
    /**
     * Adds the validation PseudoClass to the input node and makes it validated on relevant input
     * @param inputNode The node that needs to be validated
     * @param requiredValidation The type of validation that needs to be applied to the input node
     */
    public static void addValidation(Node inputNode, InputType requiredValidation) {
        inputNode.pseudoClassStateChanged(requiredValidation.getValidationType(), true);
        if (inputNode instanceof TextField) {
            inputNode.setOnKeyTyped(event -> validate(inputNode));
        } else if (inputNode instanceof TextArea) {
            inputNode.setOnKeyTyped(event -> validate(inputNode));
        } else if (inputNode instanceof DatePicker) {
            ((DatePicker) inputNode).valueProperty().addListener((observable, oldDate, newDate)-> InputValidator.validate(inputNode));
        }
    }
}

