package seng202.group7;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import seng202.group7.data.Crime;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.data.PSTypes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests to ensure that data inserted using the PSType classes methods
 * are correctly inserted into the database.
 *
 * @author John Elliott
 */
public class PSTypeTest {

    public static Connection connection;


    /**
     * Creates an empty database before running the tests.
     */
    @BeforeAll
    public static void connectDatabase() {
        try {
            DataAccessor.getInstance().changeConnection("src/test/files/TestDatabase.db");
        } catch (CustomException e) {
            System.err.println("Connect to database: " + e.getMessage());
        }
        connection = DataAccessor.getInstance().getConnection();
        try (Statement stmt = DataAccessor.getInstance().getConnection().createStatement();) {
            stmt.execute("DELETE FROM reports");
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    /**
     * Tests to see if data inserted using the PSType classes methods correctly inserts the data for Strings.
     */
    @Test
    public void setString() {
        try {
            // Uses the int psType method
            Crime crimeOne = new Crime("TestNumber", LocalDateTime.now(), "3", "3", "test", "test", "test", false, false, 5, 5, "5", 5, null, null, null);
            DataAccessor.getInstance().editCrime(crimeOne, 1);
            PreparedStatement ps = connection.prepareStatement("UPDATE reports SET primary_description=? WHERE id='TestNumber'");

            PSTypes.setPSString(ps, 1, "TestDescription");
            ps.execute();
            ps.close(); // Closes the ps statement, so it can use the connection.

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reports WHERE primary_description='TestDescription'");
            assertEquals(rs.getString(1), "TestNumber");
            stmt.close();
            rs.close();
        } catch (SQLException | CustomException e) {
            e.printStackTrace();
            fail(e);
        }
    }


    /**
     * Tests to see if data inserted using the PSType classes methods correctly inserts the data for Doubles.
     */
    @Test
    public void setZeroDouble() {

        try {
            Crime crimeOne = new Crime("TestNumber", LocalDateTime.now(), "3", "3", "test", "test", "test", false, false, 5, 5, "5", 5, null, null, null);
            DataAccessor.getInstance().editCrime(crimeOne, 1);
            // Uses the int psType method
            PreparedStatement ps = connection.prepareStatement("UPDATE reports SET latitude=? WHERE id='TestNumber'");

            PSTypes.setPSDouble(ps, 1, 0.0); // Case Number
            ps.execute();
            ps.close(); // Closes the ps statement, so it can use the connection.

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reports WHERE latitude=0.0");
            assertEquals(rs.getString(1), "TestNumber");
            stmt.close();
            rs.close();
        } catch (SQLException | CustomException e) {
            e.printStackTrace();
            fail(e);
        }
    }


    /**
     * Tests to see if data inserted using the PSType classes methods correctly inserts the data for Ints.
     */
    @Test
    public void setNegativeInt() {
        try {
            
            Crime crimeOne = new Crime("TestNumber", LocalDateTime.now(), "3", "3", "test", "test", "test", false, false, 5, 5, "5", 5, null, null, null);
            DataAccessor.getInstance().editCrime(crimeOne, 1);
            // Uses the int psType method
            PreparedStatement ps = connection.prepareStatement("UPDATE crimes SET beat=? WHERE report_id='TestNumber'");
            PSTypes.setPSInteger(ps, 1, -10); // Case Number
            ps.execute();
            ps.close(); // Closes the ps statement, so it can use the connection.
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM crimes WHERE beat=-10");
            assertEquals(rs.getString(1), "TestNumber");
            stmt.close();
            rs.close();
        } catch (SQLException | CustomException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    /**
     * Tests to see if data inserted using the PSType classes methods correctly inserts the data for Booleans.
     */
    @Test
    public void setBoolean() {

        try {
            Crime crimeOne = new Crime("TestNumber", LocalDateTime.now(), "3", "3", "test", "test", "test", false, false, 5, 5, "5", 5, null, null, null);
            DataAccessor.getInstance().editCrime(crimeOne, 1);
            // Uses the int psType method
            PreparedStatement ps = connection.prepareStatement("UPDATE crimes SET arrest=? WHERE report_id='TestNumber'");
            PSTypes.setPSBoolean(ps, 1, true); // Case Number
            ps.execute();
            ps.close(); // Closes the ps statement, so it can use the connection.

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM crimes WHERE arrest=1");
            assertEquals(rs.getString(1), "TestNumber");
            stmt.close();
            rs.close();
        } catch (SQLException | CustomException e) {
            e.printStackTrace();
            fail(e);
        }
    }

}
