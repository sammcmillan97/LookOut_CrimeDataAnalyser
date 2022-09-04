package seng202.group7.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * This class adds a set of static methods that are used when checking if a CSV
 * is null which is "" inserting the corrected null value into the database.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 * @author Shaylin Simadari
 */
public final class PSTypes {
    private PSTypes(){}

    /**
     * Checks if an "integer" is "" or null and sets the PreparedStatement to null-Types.INTEGER instead.
     *
     * @param ps                    The PreparedStatement being used.
     * @param parameterIndex        The index in the PreparedStatement.
     * @param x                     The string being checked.
     */
    public static void setPSInteger(PreparedStatement ps, int parameterIndex, String x) throws SQLException {
        if (x.equals("")) {
            ps.setNull(parameterIndex, Types.INTEGER);
        } else {
            ps.setInt(parameterIndex, Integer.parseInt(x));
        }

    }

    /**
     * Checks if an "integer" null and sets the PreparedStatement to null-Types.INTEGER instead.
     *
     * @param ps                    The PreparedStatement being used.
     * @param parameterIndex        The index in the PreparedStatement.
     * @param x                     The Integer being checked.
     */
    public static void setPSInteger(PreparedStatement ps, int parameterIndex, Integer x) throws SQLException {
        if (x == null) {
            ps.setNull(parameterIndex, Types.INTEGER);
        } else {
            ps.setInt(parameterIndex, x);
        }
    }

    /**
     * Checks if a "double" is "" or null and sets the PreparedStatement to null-Types.DOUBLE instead.
     *
     * @param ps                    The PreparedStatement being used.
     * @param parameterIndex        The index in the PreparedStatement.
     * @param x                     The string being checked.
     */
    public static void setPSDouble(PreparedStatement ps, int parameterIndex, String x) throws SQLException {
        if (x.equals("")) {
            ps.setNull(parameterIndex, Types.DOUBLE);
        } else {
            ps.setDouble(parameterIndex, Double.parseDouble(x));
        }

    }

    /**
     * Checks if a double is null and sets the PreparedStatement to null-Types.DOUBLE instead.
     *
     * @param ps                    The PreparedStatement being used.
     * @param parameterIndex        The index in the PreparedStatement.
     * @param x                     The Double being checked.
     */
    public static void setPSDouble(PreparedStatement ps, int parameterIndex, Double x) throws SQLException {
        if (x == null) {
            ps.setNull(parameterIndex, Types.DOUBLE);
        } else {
            ps.setDouble(parameterIndex, x);
        }
    }

    /**
     * Checks if a "string" is "" or null and sets the PreparedStatement to null-Types.VARCHAR instead.
     *
     * @param ps                    The PreparedStatement being used.
     * @param parameterIndex        The index in the PreparedStatement.
     * @param x                     The string being checked.
     */
    public static void setPSString(PreparedStatement ps, int parameterIndex, String x) throws SQLException {
        if (x == null || x.equals("")) {
            ps.setNull(parameterIndex, Types.VARCHAR);
        } else {
            ps.setString(parameterIndex, x);
        }
    }

    /**
     * Checks if a "boolean" is "" or null and sets the PreparedStatement to null-Types.BOOLEAN instead.
     *
     * @param ps                    The PreparedStatement being used.
     * @param parameterIndex        The index in the PreparedStatement.
     * @param x                     The string being checked.
     */
    public static void setPSBoolean(PreparedStatement ps, int parameterIndex, String x) throws SQLException {
        if (x.equals("")) {
            ps.setNull(parameterIndex, Types.BOOLEAN);
        } else {
            ps.setBoolean(parameterIndex, x.equalsIgnoreCase("Y"));
        }
    }

    /**
     * Checks if a "boolean" is null and sets the PreparedStatement to null-Types.BOOLEAN instead.
     *
     * @param ps                    The PreparedStatement being used.
     * @param parameterIndex        The index in the PreparedStatement.
     * @param x                     The Boolean being checked.
     * @throws SQLException         Error setting Prepared Statement.
     */
    public static void setPSBoolean(PreparedStatement ps, int parameterIndex, Boolean x) throws SQLException {
        if (x == null) {
            ps.setNull(parameterIndex, Types.BOOLEAN);
        } else {
            ps.setBoolean(parameterIndex, x);
        }
    }
}
