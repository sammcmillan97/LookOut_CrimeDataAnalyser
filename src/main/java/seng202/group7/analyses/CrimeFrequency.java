package seng202.group7.analyses;

/**
 * A class that holds a String date and int frequency, a list of crime frequency is used for crime over graphing
 * @author Sam McMillan
 */

public class CrimeFrequency {

    private String date;
    private int frequency;

    public CrimeFrequency(String date, int frequency) {
        this.date = date;
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
