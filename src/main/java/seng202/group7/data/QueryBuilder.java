package seng202.group7.data;

import seng202.group7.controllers.data.ControllerData;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * Builds queries based on parameters and returns them as Strings
 * @author Shaylin Simadari
 * @author Sam McMillan
 */
public final class QueryBuilder {

    private QueryBuilder(){}

    /**
     * builds an SQL where statement for data that meets the conditions of the supplied parameters.
     * null parameters are ignored.
     * conditions are connected by the AND connective
     * @return      A String representation of an SQL where statement
     */
    public static String where(FilterConditions fc){
        String query = "";

        if (fc.getDateFrom() != null && fc.getDateTo() != null){
            query += dateRangeCondition("date", fc.getDateFrom(), fc.getDateTo());
        }
        if (fc.getPrimaryDescription() != null){
            query += equalityCondition("primary_description", fc.getPrimaryDescription());
        }
        if (fc.getLocationDescription() != null){
            query += equalityCondition("location_description", fc.getLocationDescription());
        }
        if (fc.getWard() != null){
            query += equalityCondition("ward", fc.getWard());
        }
        if (fc.getBeat() != null){
            query += equalityCondition("beat", fc.getBeat());
        }
        if (fc.getArrest() != null){
            query += equalityCondition("arrest", fc.getArrest());
        }
        if (fc.getDomestic() != null){
            query += equalityCondition("domestic", fc.getDomestic());
        }

        if (query.equals("")) {
            return "";
        }
        return query.substring(0, query.length()-5);
    }

    /**
     * Is a builder that adds an and condition. For a boolean value.
     * @param field     The name of the column in the database
     * @param value     The value that the filed must be to meet the condition
     * @return          A String to append to a where query
     */
    private static String equalityCondition(String field, boolean value) {
        return field + "=" + (value ? 1:0) + " AND ";
    }

    /**
     *  Is a builder that adds an and condition. For an integer value.
     * @param field     The name of the column in the database
     * @param value     The value that the filed must be to meet the condition
     * @return          A String to append to a where query
     */
    private static String equalityCondition(String field, int value) {
        return field + "=" + value + " AND ";
    }

    /**
     *  Is a builder that adds an and condition. For a string value.
     * @param field     The name of the column in the database
     * @param value     The value that the filed must be to meet the condition
     * @return          A String to append to a where query
     */
    private static String equalityCondition(String field, String value) {
        return field + "='" + value + "' AND ";
    }

    /**
     *  Is a builder that adds an and condition. For a string value.
     * @param field     The name of the column in the database
     * @param dateFrom  The beginning date value for date range
     * @param dateTo    The ending date value for date range
     * @return          A String to append to a where query
     */
    private static String dateRangeCondition(String field, LocalDate dateFrom, LocalDate dateTo) {
        return field + " >= " + Timestamp.valueOf(dateFrom.atStartOfDay()).getTime() + " AND "
                + field + " < " + Timestamp.valueOf(dateTo.plusDays(1).atStartOfDay()).getTime() + " AND ";
    }

    /**
     * builds an SQL where statement for rows that contain the keyword.
     * @return      A String representation of an SQL where statement
     */
    public static String search(String keyword){
        // keyword = keyword.replace("/", "//");
        keyword = keyword.replace("'", "''");
        return "(primary_description LIKE '%" + keyword + "%' OR "
        + "secondary_description LIKE '%" + keyword + "%' OR "
        + "location_description LIKE '%" + keyword + "%' OR "
        + "id LIKE '%" + keyword + "%' OR "
        + "fbicd LIKE '%" + keyword + "%' OR "
        + "iucr LIKE '%" + keyword + "%')";
    }

    /**
     * Uses the strings from choices from graph menu controller and the where method to create the correct query
     * @return query, a string value query
     */
    public static String constructGraphQuery(List<String> choices) {
        String query = "SELECT * FROM crimedb WHERE list_id=" + ControllerData.getInstance().getCurrentList();
        String restrictions = where(new FilterConditions(null, null, choices.get(0), null,
                getIntegerFromString(choices.get(1)), getIntegerFromString(choices.get(2)), null, null));
        if (!restrictions.equals("")) {
            query += " AND " + restrictions;
        }
        query += " ORDER BY date ASC;";
        return query;
    }

    /**
     * Gets the integer value from the string and ensures if the string is empty it returnsS a null value.
     *
     * @param str       The choice selected.
     * @return value    The integer result.
     */
    public static Integer getIntegerFromString(String str) {
        if("".equals(str) || str == null){
            return null;
        }
        return Integer.parseInt(str);
    }
}
