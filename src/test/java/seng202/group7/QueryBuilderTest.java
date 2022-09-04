package seng202.group7;

import org.junit.jupiter.api.Test;

import seng202.group7.data.DataAccessor;
import seng202.group7.data.FilterConditions;
import seng202.group7.data.QueryBuilder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to ensure that where query is correctly generated when called.
 *
 * @author John Elliott
 * @author Sam McMillan
 */
public class QueryBuilderTest {

    public static DataAccessor accessor = DataAccessor.getInstance();

    /**
     * Tests the creation of a where query using the query builder.
     */
    @Test
    public void whereQueryTest() {
        LocalDate testDate = LocalDate.now();
        FilterConditions conditions = new FilterConditions(testDate, testDate, "test", null, 30, null, true, null);
        String query = QueryBuilder.where(conditions);
        assertEquals("date >= "+ Timestamp.valueOf(testDate.atStartOfDay()).getTime() +
                " AND date < "+ Timestamp.valueOf(testDate.plusDays(1).atStartOfDay()).getTime() +
                " AND primary_description='test' AND ward=30 AND arrest=1", query);
    }

    /**
     * Tests that the WHERE query returns an empty string if the inputs are all null.
     */
    @Test
    public void whereQueryEmptyTest() {
        FilterConditions conditions = new FilterConditions(null, null, null, null, null, null, null, null);
        String query = QueryBuilder.where(conditions);
        assertEquals("", query);
    }

    /**
     * Tests to see the correct query is constructed given the parameters crime type: THEFT, ward: null and beat: null
     */
    @Test
    public void constructGraphQueryTest() {
        List<String> choices = new ArrayList<>();
        choices.add("THEFT");
        choices.add(null);
        choices.add(null);
        String query = QueryBuilder.constructGraphQuery(choices);
        assertEquals("SELECT * FROM crimedb WHERE list_id=1 AND primary_description='THEFT' ORDER BY date ASC;", query);
    }

    /**
     * Tests to see the correct query is constructed given the parameters crime type: FRAUD, ward: 1 and beat: null
     */
    @Test
    public void constructGraphQueryTest_TypeWard() {
        List<String> choices = new ArrayList<>();
        choices.add("FRAUD");
        choices.add("1");
        choices.add(null);
        String query = QueryBuilder.constructGraphQuery(choices);
        assertEquals("SELECT * FROM crimedb WHERE list_id=1 AND primary_description='FRAUD' AND ward=1 ORDER BY date ASC;", query);
    }

    /**
     * Tests to see the correct query is constructed given the parameters crime type: HELLO, ward: 1 and beat: 2
     */
    @Test
    public void constructGraphQueryTest_TypeWardBeat() {
        List<String> choices = new ArrayList<>();
        choices.add("HELLO");
        choices.add("1");
        choices.add("2");
        String query = QueryBuilder.constructGraphQuery(choices);
        assertEquals("SELECT * FROM crimedb WHERE list_id=1 AND primary_description='HELLO' AND ward=1 AND beat=2 ORDER BY date ASC;", query);
    }
}
