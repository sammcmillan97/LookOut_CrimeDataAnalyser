package seng202.group7.data;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Used to create and store crime objects.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 */
public class Crime extends Report {
    private final SimpleStringProperty block = new SimpleStringProperty(null);
    private final SimpleStringProperty iucr = new SimpleStringProperty(null);
    private final SimpleStringProperty fbiCD = new SimpleStringProperty(null);
    private final SimpleObjectProperty<Boolean> arrest = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<Integer> beat = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<Integer> ward = new SimpleObjectProperty<>(null);


    /**
     * Initializes a crime object.
     * @param date                  A required field which contains the year, month, day and time of the crime
     * @param block                 An optional field which contains what block the crime occurred in
     * @param iucr                  An optional field which contains the Illinois Uniform Crime Reporting number
     * @param primaryDescription    A required field which contains the primary description of the crime
     * @param secondaryDescription  A required field which contains the more descriptive secondary description of the crime
     * @param locationDescription   An optional field which contains the description of the location where the crime occurred
     * @param arrest                An optional field which contains whether the crime resulted in an arrest
     * @param domestic              An optional field which contains if the crime was domestic
     * @param beat                  An optional field which contains the beat.
     * @param ward                  An optional field which contains the ward.
     * @param fbiCD                 An optional field which contains FBI crime code.
     * @param xCoord                An optional field which is has the x-coordinate of where the crime occurred
     * @param yCoord                An optional field which is has the y-coordinate of where the crime occurred
     * @param latitude              An optional field which is has the latitude of where the crime occurred
     * @param longitude             An optional field which is has the longitude of where the crime occurred
     */
    public Crime(String id, LocalDateTime date, String block, String iucr, String primaryDescription,
    String secondaryDescription, String locationDescription, Boolean arrest, Boolean domestic, Integer beat,
    Integer ward, String fbiCD, Integer xCoord, Integer yCoord, Double latitude, Double longitude) {
        super(id, date, primaryDescription, secondaryDescription, locationDescription, domestic, xCoord, yCoord, latitude, longitude);

        if (block != null && !block.isEmpty()) {
            this.block.setValue(block);
        }
        if (iucr != null && !iucr.isEmpty()) {
            this.iucr.setValue(iucr);
        }
        this.arrest.setValue(arrest);
        this.beat.setValue(beat);
        this.ward.setValue(ward);
        if (block != null && !fbiCD.isEmpty()) {
            this.fbiCD.setValue(fbiCD);
        }
    }

    /**
     * Gets the block of the report.
     *
     * @return The block the crime occurred in
     */
    public String getBlock() {
        return this.block.get();
    }

    /**
     * Sets the block the crime occurred in, handles an empty string as null.
     *
     * @param block     The new block value
     */
    public void setBlock(String block) {
        if (Objects.equals(block, "")) {
            this.block.setValue(null);
        } else if (!Objects.equals(getBlock(), block)){
            this.block.setValue(block);
        }
    }


    /**
     * Gets the current Iucr value.
     *
     * @return The crime's Illinois Uniform Crime Reporting number
     */
    public String getIucr() {
        return this.iucr.get();
    }


    /**
     * Sets the crime's Illinois Uniform Crime Reporting number, handles an empty string as null.
     *
     * @param iucr  The new iucr value.
     */
    public void setIucr(String iucr) {
        if (Objects.equals(iucr, "")) {
            this.iucr.setValue(null);
        } else if (!Objects.equals(getIucr(), iucr)){
            this.iucr.setValue(iucr);
        }
    }

    /**
     * Gets the current arrest value.
     *
     * @return Whether the crime resulted in an arrest
     */
    public Boolean getArrest() {
        return this.arrest.get();
    }

    /**
     * Sets whether the crime resulted in an arrest.
     *
     * @param arrest    The new arrest value.
     */
    public void setArrest(Boolean arrest) {
        if (!Objects.equals(getArrest(), arrest)) {
            this.arrest.setValue(arrest);
        }
    }

    /**
     * Gets the current beat value.
     *
     * @return What beat the crime was in
     */
    public Integer getBeat() {
        return this.beat.get();
    }

    /**
     * Sets what beat the crime was in.
     *
     * @param beat      The new beat value.
     */
    public void setBeat(Integer beat) {
        if (!Objects.equals(getBeat(), beat)) {
            this.beat.setValue(beat);
        }
    }

    /**
     * Gets the current ward value.
     *
     * @return What ward the crime was in
     */
    public Integer getWard() {
        return this.ward.get();
    }

    /**
     * Sets what ward the crime was in.
     *
     * @param ward      The new ward value.
     */
    public void setWard(Integer ward) {
        if (!Objects.equals(getWard(), ward)) {
            this.ward.setValue(ward);
        }
    }

    /**
     * Gets the current fbi code value.
     *
     * @return The crimes FBI CD number
     */
    public String getFbiCD() {
        return this.fbiCD.get();
    }

    /**
     * Sets the crime's FIB CD number, handles an empty string as null.
     *
     * @param fbiCD     The new fbi code value
     */
    public void setFbiCD(String fbiCD) {
        if (Objects.equals(fbiCD, "")) {
            this.fbiCD.setValue(null);
        } else if (!Objects.equals(getFbiCD(), fbiCD)){
            this.fbiCD.setValue(fbiCD);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        
        Crime crime = (Crime) o;
        return Objects.equals(arrest.get(), crime.getArrest())
            && Objects.equals(beat.get(), crime.getBeat())
            && Objects.equals(ward.get(), crime.getWard())
            && Objects.equals(block.get(), crime.getBlock())
            && Objects.equals(iucr.get(), crime.getIucr())
            && Objects.equals(fbiCD.get(), crime.getFbiCD());
    }

    @Override
    public String toString() {
        return "Crime{" +
                "caseNumber='" + getId() + '\'' +
                ", block='" + getBlock() + '\'' +
                ", iucr='" + getIucr() + '\'' +
                ", arrest=" + getArrest() +
                ", beat=" + getBeat() +
                ", ward=" + getWard() +
                ", fbi CD='" + getFbiCD() + '\'' +
                super.toString() + '\'' +
                '}';
    }

    /**
     * Makes a JSON Array as a String of selected values to be shown on the map.
     * @return  JSON Array as a String.
     */
    public String getJSONString() {
        return String.format("{title: '%s', position: {lat: %f, lng: %f}, " +
                        "description: {date: '%s', primDesc: '%s', secDesc: '%s', locDesc: '%s', arrest: '%s', domestic: '%s'}}, ",
                getId(), getLatitude(), getLongitude(), getDate().toLocalDate().toString(), getPrimaryDescription(),
                getSecondaryDescription(), getLocationDescription(), getArrest().toString(), getDomestic().toString());
    }
}