package seng202.group7.controllers.data;

import javafx.css.PseudoClass;

/**
 * The types of input validators that can be applied to a Node.
 * @author Jack McCorkindale
 */
public enum InputType {
    DOUBLE (PseudoClass.getPseudoClass("double")),
    INTEGER (PseudoClass.getPseudoClass("integer")), 
    REQUIRED (PseudoClass.getPseudoClass("required")), 
    ID (PseudoClass.getPseudoClass("id")), 
    DATE (PseudoClass.getPseudoClass("dateEditor")), 
    TIME (PseudoClass.getPseudoClass("time")),
    LISTNAME (PseudoClass.getPseudoClass("list_name"));

    private final PseudoClass validationType;

    /**
     * Sets the type of validation that will be done when applied to a Node.
     * @param validationType The PseudoClass that determines what validation is required.
     */
    InputType(PseudoClass validationType) {
        this.validationType = validationType;
    }

    /**
     * Returns the validation type.
     *
     * @return type of validation that will be done when applied to a Node.
     */
    public PseudoClass getValidationType() {
        return this.validationType;
    }
}
