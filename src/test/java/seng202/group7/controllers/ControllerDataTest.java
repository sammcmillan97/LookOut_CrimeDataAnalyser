package seng202.group7.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.data.QueryBuilder;


public class ControllerDataTest {

    private static ControllerData controllerData;

    @BeforeAll
    public static void makeReports() {
        controllerData = ControllerData.getInstance();
    }

    @Test
    public void getWhereQuery_blank() {
        assertEquals("", controllerData.getWhereQuery());
    }

    @Test
    public void getWhereQuery_searchPopulated() {
        controllerData.setSearchQuery(QueryBuilder.search("5"));
        controllerData.setFilterQuery("");
        assertEquals("(primary_description LIKE '%5%' OR "
            + "secondary_description LIKE '%5%' OR "
            + "location_description LIKE '%5%' OR "
            + "id LIKE '%5%' OR "
            + "fbicd LIKE '%5%' OR "
            + "iucr LIKE '%5%')", controllerData.getWhereQuery());
    }

    @Test
    public void getWhereQuery_filterPopulated() {
        controllerData.setFilterQuery("ward=5");
        controllerData.setSearchQuery("");
        assertEquals("ward=5", controllerData.getWhereQuery());
    }

    @Test
    public void getWhereQuery_bothPopulated() {
        controllerData.setFilterQuery("arrest=true");
        controllerData.setSearchQuery(QueryBuilder.search("5"));
        assertEquals("arrest=true AND (primary_description LIKE '%5%' OR "
            + "secondary_description LIKE '%5%' OR "
            + "location_description LIKE '%5%' OR "
            + "id LIKE '%5%' OR "
            + "fbicd LIKE '%5%' OR "
            + "iucr LIKE '%5%')", controllerData.getWhereQuery());
    }    
}
