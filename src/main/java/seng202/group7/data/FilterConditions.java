package seng202.group7.data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * a class used to encapsulate the fields that data can be filtered by.
 * @author Shaylin Simadari
 */
public class FilterConditions implements Serializable {
    private final LocalDate dateFrom;
    private final LocalDate dateTo;
    private final String primaryDescription;
    private final String locationDescription;
    private final Integer ward;
    private final Integer beat;
    private final Boolean arrest;
    private final Boolean domestic;

    /**
     * Initializes a new FilterConditions
     * @param dateFrom the date that the date range starts at
     * @param dateTo the date that the date range ends at
     * @param primaryDescription primary description of crime
     * @param locationDescription location description of crime
     * @param ward ward crime took place in
     * @param beat beat crime took place in
     * @param arrest whether an arrest was made for the crime
     * @param domestic whether the crime was a domestic offense
     */
    public FilterConditions(LocalDate dateFrom, LocalDate dateTo, String primaryDescription, String locationDescription,
                            Integer ward, Integer beat, Boolean arrest, Boolean domestic) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        if (primaryDescription != null) {
            this.primaryDescription = primaryDescription.replace("'", "''");
        } else {
            this.primaryDescription = null;
        }
        if (locationDescription != null) {
            this.locationDescription = locationDescription.replace("'", "''");
        } else {
            this.locationDescription = null;
        }
        this.ward = ward;
        this.beat = beat;
        this.arrest = arrest;
        this.domestic = domestic;
    }

    /**
     * gets the date that the date range starts at
     * @return the date that the date range starts at
     */
    public LocalDate getDateFrom() {
        return dateFrom;
    }

    /**
     * gets the date that the date range ends at
     * @return the date that the date range ends at
     */
    public LocalDate getDateTo() {
        return dateTo;
    }

    /**
     * gets primary description of crime
     * @return primary description of crime
     */
    public String getPrimaryDescription() {
        return primaryDescription;
    }

    /**
     * gets location description of crime
     * @return location description of crime
     */
    public String getLocationDescription() {
        return locationDescription;
    }

    /**
     * gets ward crime took place in
     * @return ward crime took place in
     */
    public Integer getWard() {
        return ward;
    }

    /**
     * gets beat crime took place in
     * @return beat crime took place in
     */
    public Integer getBeat() {
        return beat;
    }

    /**
     * gets whether an arrest was made for the crime
     * @return whether an arrest was made for the crime
     */
    public Boolean getArrest() {
        return arrest;
    }

    /**
     * gets whether the crime was a domestic offense
     * @return whether the crime was a domestic offense
     */
    public Boolean getDomestic() {
        return domestic;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        if (super.equals(obj)) {
            return true;
        }
        FilterConditions filter = (FilterConditions) obj;
        return this.arrest.equals(filter.getArrest()) &&
            this.domestic.equals(filter.getDomestic()) &&
            this.beat.equals(filter.getBeat()) &&
            this.ward.equals(filter.getWard()) &&
            this.dateTo.equals(filter.getDateTo()) &&
            this.dateFrom.equals(filter.getDateFrom()) &&
            this.primaryDescription.equals(filter.getPrimaryDescription()) &&
            this.locationDescription.equals(filter.getLocationDescription());
    }
}
