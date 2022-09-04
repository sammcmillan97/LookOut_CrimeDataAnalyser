package seng202.group7.data;

/**
 * A custom exception that allows for the type of exception to be stored as a string
 * in the info field.
 *
 * @author John Elliott
 */
public class CustomException extends Exception {
    String info;

    /**
     * Constructor of the exception.
     * @param message       A message related to the exception.
     * @param cause         The specific class of exception.
     */
    public CustomException(String message, String cause) {
        super(message);
        info = cause;
    }

    /**
     * Gets the specific class of exception as a string.
     * @return  The exceptions class.
     */
    public String getInfo() {
        return info;
    }

}
