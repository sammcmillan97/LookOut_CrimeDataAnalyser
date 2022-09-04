package seng202.group7.data;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.naming.directory.InvalidAttributeValueException;

/**
 * Used to create and store report objects which is the super class of Crime.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 * @author Shaylin Simadari
 */
public abstract class Report {
    private LocalDateTime date;
    private final SimpleStringProperty id = new SimpleStringProperty(null);
    private final SimpleStringProperty primaryDescription = new SimpleStringProperty(null);
    private final SimpleStringProperty secondaryDescription = new SimpleStringProperty(null);
    private final SimpleStringProperty locationDescription = new SimpleStringProperty(null);
    private final SimpleObjectProperty<Boolean> domestic = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<Integer> xCoord = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<Integer> yCoord = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<Double> latitude = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<Double> longitude = new SimpleObjectProperty<>(null);



    /**
     * Initializes a Report object.
     * @param id                    The unique identifier for the report object
     * @param date                  A required field which contains the year, month, day and time of the crime
     * @param primaryDescription    A required field which contains the primary description of the crime
     * @param secondaryDescription  A required field which contains the more descriptive secondary description of the crime
     * @param locationDescription   An optional field which contains the description of the location where the crime occurred
     * @param domestic              An optional field which contains if the crime was domestic
     * @param xCoord                An optional field which is has the x-coordinate of where the crime occurred
     * @param yCoord                An optional field which is has the y-coordinate of where the crime occurred
     * @param latitude              An optional field which is has the latitude of where the crime occurred
     * @param longitude             An optional field which is has the longitude of where the crime occurred
     */
    protected Report(String id, LocalDateTime date, String primaryDescription, String secondaryDescription, String locationDescription,
            Boolean domestic, Integer xCoord, Integer yCoord, Double latitude, Double longitude) {
        this.id.setValue(id);
        this.date = date;
        this.primaryDescription.setValue(primaryDescription);
        this.secondaryDescription.setValue(secondaryDescription);
        if (locationDescription != null && !locationDescription.isEmpty()) {
            this.locationDescription.setValue(locationDescription);
        }
        this.domestic.setValue(domestic);
        this.xCoord.setValue(xCoord);
        this.yCoord.setValue(yCoord);
        this.latitude.setValue(latitude);
        this.longitude.setValue(longitude);
    }

    /**
     * Gets the current id of a report.
     *
     * @return The reports's id
     */
    public String getId() {
        return this.id.get();
    }


    /**
     * Sets the report's id, handles an empty string as null.
     *
     * @param id                        A required string attribute which must be unique
     * @throws InvalidAttributeValueException   Value doesn't match type
     */
    public void setId(String id) throws InvalidAttributeValueException {
        if (Objects.equals(id, "") || (id == null)) {
            throw new InvalidAttributeValueException();
        } else if (!Objects.equals(getId(), id)){
            this.id.setValue(id);
        }
    }
    
    /**
     * Gets the current date value.
     *
     * @return The date the report was submitted
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * Sets the date the report was submitted.
     *
     * @param date      A required LocalDateTime attribute which must be unique
     * @throws InvalidAttributeValueException   If the attribute is null
     */
    public void setDate(LocalDateTime date) throws InvalidAttributeValueException {
        if (date == null) {
            throw new InvalidAttributeValueException("Date cannot be null");
        } if (!Objects.equals(getDate(), date)) {
            this.date = date;
        }
    }

    /**
     * Gets the primary description value.
     *
     * @return The primary description of the report
     */
    public String getPrimaryDescription() {
        return this.primaryDescription.get();
    }


    /**
     * Sets the primary description of the report.
     *
     * @param primaryDescription A required String attribute which must be unique
     * @throws InvalidAttributeValueException If the attribute is null
     */
    public void setPrimaryDescription(String primaryDescription) throws InvalidAttributeValueException {
        if (Objects.equals(primaryDescription, "") || (primaryDescription == null)) {
            throw new InvalidAttributeValueException("Primary description cannot be null");
        } else if (!Objects.equals(getPrimaryDescription(), primaryDescription)) {
            this.primaryDescription.setValue(primaryDescription);
        }
    }

    /**
     * Gets the secondary description value.
     *
     * @return The secondary description of the report
     */
    public String getSecondaryDescription() {
        return this.secondaryDescription.get();
    }
    

    /**
     * Sets the secondary description of the report.
     *
     * @param secondaryDescription      A required String attribute which must be unique
     * @throws InvalidAttributeValueException   If the attribute is null
     */
    public void setSecondaryDescription(String secondaryDescription) throws InvalidAttributeValueException {
        if (Objects.equals(secondaryDescription, "") || (secondaryDescription == null)) {
            throw new InvalidAttributeValueException("Secondary description cannot be null");
        } else if (!Objects.equals(getSecondaryDescription(), secondaryDescription)){
            this.secondaryDescription.setValue(secondaryDescription);
        }
    }

    /**
     * Gets the current location description.
     *
     * @return The description of the location for the report
     */
    public String getLocationDescription() {
        return this.locationDescription.get();
    }

    /**
     * Set the location description for the report
     * @param locationDescription       The location description value.
     */
    public void setLocationDescription(String locationDescription) {
        if (Objects.equals(locationDescription, "")) {
            this.locationDescription.setValue(null);
        } else if (!Objects.equals(getLocationDescription(), locationDescription)) {
            this.locationDescription.setValue(locationDescription);
        }
    }

    /**
     * Gets the current value for the domestic condition.
     *
     * @return If the report was domestic
     */
    public Boolean getDomestic() {
        return this.domestic.get();
    }

    /**
     * Sets if the report was domestic
     * @param domestic      The new domestic value.
     */
    public void setDomestic(Boolean domestic) {
        if (!Objects.equals(getDomestic(), domestic)) {
            this.domestic.setValue(domestic);
        }
    }

    /**
     * Gets the x cord value.
     *
     * @return The x-coordinate for the location the report took place
     */
    public Integer getXCoord() {
        return this.xCoord.get();
    }

    /**
     * Set the x-coordinate of where the report took place.
     *
     * @param xCoord    New x cord value.
     */
    public void setXCoord(Integer xCoord) {
        if (!Objects.equals(getXCoord(), xCoord)) {
            this.xCoord.setValue(xCoord);
        }
    }

    /**
     * Gets the current y cord value.
     *
     * @return The y-coordinate for the location the report took place
     */
    public Integer getYCoord() {
        return this.yCoord.get();
    }


    /**
     * Set the y-coordinate of where the report took place.
     *
     * @param yCoord    New y cord value.
     */
    public void setYCoord(Integer yCoord) {
        if (!Objects.equals(getYCoord(), yCoord)) {
            this.yCoord.setValue(yCoord);
        }
    }

    /**
     *  Gets the current latitude value.
     *
     * @return The geographical latitude of where the report took place
     */
    public Double getLatitude() {
        return this.latitude.get();
    }


    /**
     * Set the geographical latitude of where the report took place.
     *
     * @param latitude      New latitude value.
     */
    public void setLatitude(Double latitude) {
        if (!Objects.equals(getLatitude(), latitude)) {
            this.latitude.setValue(latitude);
        }
    }

    /**
     * Gets the current longitude value.
     *
     * @return The geographical longitude of where the report took place
     */
    public Double getLongitude() {
        return this.longitude.get();
    }


    /**
     * Set the geographical longitude of where the report took place.
     *
     * @param longitude     New longitude value.
     */
    public void setLongitude(Double longitude) {
        if (!Objects.equals(getLongitude(), longitude))
        this.longitude.setValue(longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(date, report.getDate())
            && Objects.equals(id.get(), report.getId())
            && Objects.equals(primaryDescription.get(), report.getPrimaryDescription())
            && Objects.equals(secondaryDescription.get(), report.getSecondaryDescription())
            && Objects.equals(locationDescription.get(), report.getLocationDescription())
            && Objects.equals(domestic.get(), report.getDomestic())
            && Objects.equals(xCoord.get(), report.getXCoord())
            && Objects.equals(yCoord.get(), report.getYCoord())
            && Objects.equals(latitude.get(), report.getLatitude())
            && Objects.equals(longitude.get(), report.getLongitude());
    }


    @Override
    public String toString() {
        return ", date=" + date +
                ", primaryDescription='" + getPrimaryDescription() + '\'' +
                ", secondaryDescription='" + getSecondaryDescription() + '\'' +
                ", locationDescription='" + getLocationDescription() + '\'' +
                ", domestic=" + getDomestic() +
                ", xCoord=" + getXCoord() +
                ", yCoord=" + getYCoord() +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude()
                ;
    }

}