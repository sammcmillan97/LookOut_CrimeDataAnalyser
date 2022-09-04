package seng202.group7.data;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * This class works to link the SQLite database with our java application.
 * It gets key data from the database during the runtime of the application.
 *
 * @author Jack McCorkindale
 * @author John Elliott
 * @author Shaylin Simadari
 * @author Sam McMillan
 */
public final class DataAccessor {
    /**
     * This creates a singleton instants of the class.
     */
    private final static DataAccessor INSTANCE = new DataAccessor();

    /**
     * The connection made to the database. This is closed when the JavaFX stage is closed.
     */
    private Connection connection;

    /**
     * The constructor which is made private so that it can not be initialized from other classes.
     */
    private DataAccessor() {
        try {
            createDatabase("MainDatabase.db");
            connection = DriverManager.getConnection("jdbc:sqlite:MainDatabase.db");
            runStatement("PRAGMA foreign_keys = ON;");
        } catch (SQLException | CustomException e) {
            System.err.println("FatalError: Could not connect to the database.");
        }
    }

    /**
     * Used to get the singleton instants of the class when assessing. This is done over a "static" class due to it
     * implementing an interface.
     *
     * @return INSTANCE     The only instants of this class.
     */
    public static DataAccessor getInstance() {
        return INSTANCE;
    }

    /**
     * Changes the connection of the database that is currently loaded.
     * @param path The new path of the database.
     * @throws CustomException  Error connecting to the new database.
     */
    public void changeConnection(String path) throws CustomException{
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:"+path);
            runStatement("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            throw new CustomException("Error connecting to database.", e.getMessage());
        }
    }

    /**
     * Creates a new database if one doesn't exist at the specified location.
     * @param location The path to the selected file.
     * @throws CustomException Error creating new database.
     */
    private void createDatabase(String location) throws CustomException {
        try (Connection newdb = DriverManager.getConnection("jdbc:sqlite:" + location);Statement stmt = newdb.createStatement()) {
        
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("CREATE TABLE IF NOT EXISTS lists (" + 
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(32) UNIQUE NOT NULL" +
                    ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS reports (" + 
                    "id VARCHAR(8) NOT NULL, " + 
                    "list_id INTEGER NOT NULL, " +
                    "date TIMESTAMP NOT NULL, " +
                    "primary_description VARCHAR(50) NOT NULL, " +
                    "secondary_description VARCHAR(50) NOT NULL, " + 
                    "domestic BOOLEAN NOT NULL, " +
                    "x_coord INT, " +
                    "y_coord INT, " +
                    "latitude FLOAT, " +
                    "longitude FLOAT, " +
                    "location_description VARCHAR(50) NOT NULL, " +
                    "PRIMARY KEY(list_id, id), " +
                    "FOREIGN KEY(list_id) REFERENCES lists(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                    ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS crimes (" + 
                    "report_id VARCHAR(8) NOT NULL, " +
                    "list_id INTEGER NOT NULL, " +
                    "block VARCHAR(50) NOT NULL, " +
                    "iucr VARCHAR(4) NOT NULL, " +
                    "fbicd VARCHAR(3) NOT NULL, " + 
                    "arrest BOOLEAN NOT NULL, " +
                    "beat INT NOT NULL, " +
                    "ward INT NOT NULL, " +
                    "FOREIGN KEY(list_id, report_id) REFERENCES reports(list_id, id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "PRIMARY KEY(list_id, report_id)" +
                    ")");
            stmt.execute("CREATE VIEW IF NOT EXISTS crimedb AS " +
                    "SELECT *" + 
                    "FROM reports JOIN crimes c ON id=c.report_id AND reports.list_id=c.list_id");
        } catch (SQLException e) {
            throw new CustomException("Main database could not be generated.", e.getMessage());
        }
    }

    /**
     * Getter for the connected to the database.
     * @return       The connection to the database.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Gets the number of entries in the database.
     *
     * @return     The number of entries.
     * @throws CustomException Error getting size of the database.
     */
    public int getSize(int listId, String conditions) throws CustomException {
        // See how many entries are in the view crimedb.
        String query = "SELECT COUNT(*) FROM crimedb WHERE "+ conditions;
        if (!conditions.isEmpty()) {
            query += " AND";
        }
        query += " list_id=" + listId +";";
        int size;
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            size = rs.getInt(1);
        } catch (SQLException e) {
            throw new CustomException("Error getting size of the database.", e.getMessage());
        }
        return size;
    }

    /**
     * Using the current page of the paginator this method returns the relevant section
     * of reports to be used from the database in the TableView.
     *
     * @return reports      The list of reports to display.
     * @throws CustomException Error getting page set from the database.
     */
    public List<Report> getPageSet(int listId, int page, String condition) throws CustomException {
        // This only get 1000 reports per page of the paginator.
        int start = page * 1000;
        int end = 1000;

        String query = "SELECT * FROM crimedb WHERE " + condition;
        if (!condition.isEmpty()) {
            query += " AND ";
        }
        query +=" list_id=" + listId + " ORDER BY id LIMIT "+end+" OFFSET "+start+";";
        return selectReports(query);
    }

    /**
     * Gets all the reports from the database.
     *
     * @return  All reports from the database.
     * @throws CustomException Error getting result set from the database.
     */
    public List<Report> getAll(int listId) throws CustomException {
        String query = "SELECT * FROM crimedb WHERE list_id=" + listId;
        return selectReports(query);
    }

    /**
     * Generic method for passing any query to the dataBase
     * @param query The string query used to query the database
     * @return List of reports
     * @throws CustomException Error getting result set from the database.
     */
    public List<Report> getData(String query) throws CustomException {
        return selectReports(query);
    }

    /**
     * Method which returns all unique strings from a column of a database
     * @param column The column that will be searched
     * @param conditions Constrictions to the search
     * @return A list of unique Strings
     * @throws CustomException Error getting result set from the database.
     */
    public List<String> getColumnString(String column, String conditions) throws CustomException {
        List<String> crimeTypeList = new ArrayList<>();
        String query = "SELECT DISTINCT " +column+ " from crimedb " + conditions;
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            // Converts all results into crimes.
            while (rs.next()) {
                String columnString = rs.getString(column);
                crimeTypeList.add(columnString);
            }
        } catch (SQLException e) {
            throw new CustomException("Could not find column " + column + " in active database.", e.getMessage());
        }
        return crimeTypeList;
     }

    /**
     * Method which returns all unique Integers from a column of a database
     * @param column The column that will be searched
     * @param conditions Constrictions to the search
     * @return A list of unique Integers
     * @throws CustomException  Error getting result set from the database.
     */
    public ArrayList<Integer> getColumnInteger(String column, String conditions) throws CustomException {
        ArrayList<Integer> crimeTypeList = new ArrayList<>();
        String query = "SELECT DISTINCT " +column+ " from crimedb " + conditions;
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            // Converts all results into crimes.
            while (rs.next()) {
                Integer columnInteger = rs.getInt(column);
                crimeTypeList.add(columnInteger);
            }
        } catch (SQLException e) {
            throw new CustomException("Could not find column " + column + " in active database.", e.getMessage());
        }
        return crimeTypeList;
    }

    /**
     * Uses a query for the crimedb view to get a selection of reports.
     *
     * @param query     The query for the crimedb view.
     * @return          A list of reports.
     * @throws CustomException  Error getting a selection of reports.
     */
    private List<Report> selectReports(String query) throws CustomException {
        List<Report> reports = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query)){
            // Converts all results into crimes.
            while (rs.next()) {
                Report crime = generateReport(rs);
                reports.add(crime);
            }
        } catch (SQLException e) {
            throw new CustomException("Error getting set of reports from the database.", e.getMessage());
        }
        return reports;
    }

    /**
     * Turns an entry in a result set into a Report object.
     * @param rs The result of the result set to be turned into a Report object.
     * @return The created Report object.
     * @throws SQLException Thrown if a value is invalid.
     */
    private Report generateReport(ResultSet rs) throws SQLException{
        Timestamp tt = rs.getTimestamp("date");
        return new Crime(
                rs.getString("id"),
                tt.toLocalDateTime(),
                rs.getString("block"),
                rs.getString("iucr"),
                rs.getString("primary_description"),
                rs.getString("secondary_description"),
                rs.getString("location_description"),
                rs.getBoolean("arrest"),
                rs.getBoolean("domestic"),
                rs.getInt("beat"),
                rs.getInt("ward"),
                rs.getString("fbicd"),
                rs.getInt("x_coord"),
                rs.getInt("y_coord"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude")
        );
    }

    /**
     * Gets a single crime entry from the database.
     *
     * @param entry     The case number of a crime object.
     * @return          A single report.
     * @throws CustomException Error getting a crime object from the database.
     */
    public Crime getCrime(String entry, int listId) throws CustomException {
        Crime crime = null;
        try (PreparedStatement psReport = connection.prepareStatement("SELECT " + 
            "id, list_id, date, primary_description, secondary_description, domestic, " +
            "x_coord, y_coord, latitude, longitude, location_description, block, iucr, fbicd, arrest, beat, ward" +
            " FROM crimedb WHERE id=? AND list_id=?;")) {
            
            psReport.setString(1, entry);
            psReport.setInt(2, listId);
            
            ResultSet rs = psReport.executeQuery();
            while (rs.next()) {
                crime = (Crime) generateReport(rs);
            }
            
            // Closes the result set.
            rs.close();
        } catch (SQLException e) {
            throw new CustomException("Error getting single crime from the database.", e.getMessage());
        }
        return crime;
    }

    /**
     * Deletes an entry from the database.
     *
     * @param entryId       The case number of the crime object.
     */
    public void deleteReport(String entryId, int listId) throws CustomException{
        String reportQuery = "DELETE FROM reports WHERE id = '" + entryId + "' AND list_id=" + listId;
        try {
            runStatement("BEGIN;");
            runStatement(reportQuery);
            runStatement("COMMIT;");
        } catch (SQLException e) {
            if (e.getMessage().contains("(cannot start a transaction within a transaction)")) {
                throw new CustomException("Database is busy. Please wait until the current action is finished.", e.getMessage());
            }
            throw new CustomException("Could not delete entry.", e.getMessage());
        }
    }

    /**
     * Runs during the importing of a outside database into the main database.
     *
     * @param selectedFile      The Database file.
     */
    private void importInDB(File selectedFile, int listId, String behavior) throws CustomException {
        try {
            runStatement("BEGIN;");
            runStatement("ATTACH '" + selectedFile + "' AS " + "newReportDB;");
            runStatement("INSERT OR " + behavior + " INTO reports " +
                "SELECT " +
                "id, " + 
                "'" + listId + "' ," +
                "date, " +
                "primary_description, " +
                "secondary_description, " + 
                "domestic, " +
                "x_coord, " +
                "y_coord, " +
                "latitude, " +
                "longitude, " +
                "location_description " + 
                "FROM newReportDB.reports");

            runStatement("INSERT OR " + behavior + " INTO crimes SELECT " +
                "report_id, " + 
                "'" + listId + "', " +
                "block, " +
                "iucr, " +
                "fbicd, " + 
                "arrest, " +
                "beat, " +
                "ward " + 
                "FROM newReportDB.crimes");

            throw new CustomException("Import complete.", "Import complete");

        } catch (SQLException e) {
            if (e.getMessage().contains("(cannot start a transaction within a transaction)")) {
                throw new CustomException("Database is busy. Please wait until the current action is finished.", e.getMessage());
            }
            if (e.getMessage().contains("(UNIQUE constraint failed: reports.list_id, reports.id)")) {
                throw new CustomException("Duplicate data detected.", e.getMessage());
            }
            throw new CustomException("Invalid data in database", e.getMessage());
        } finally {
            try {
                runStatement("COMMIT;");
                runStatement("DETACH DATABASE " + "'newReportDB';");
            } catch (SQLException e) {
                throw new CustomException("Error while writing to the database", e.getMessage());
            }
        }
    }


    /**
     * Runs a query with no result set and ensures that the statements are then closed.
     * @param query     The query being used.
     * @throws SQLException Error running a sqlite statement.
     */
    private void runStatement(String query) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
        }
    }

    /**
     * Is given a CSV file and imports it into the database.
     *
     * @param pathname      CSV file.
     * @throws CustomException  Error reading the CSV into the database.
     */
    private void readToDB(File pathname, int listId, String behavior, boolean skipBadValue) throws CustomException {
        String[] schemaDefault = {"CASE#", "DATE OF OCCURRENCE", "BLOCK", "IUCR", "PRIMARY DESCRIPTION", "SECONDARY DESCRIPTION",
                "LOCATION DESCRIPTION", "ARREST", "DOMESTIC", "BEAT", "WARD", "FBI CD", "X COORDINATE", "Y COORDINATE",
                "LATITUDE", "LONGITUDE", "LOCATION"};
        try (FileReader csvFile = new FileReader(pathname); CSVReader reader = new CSVReader(csvFile)) {
            String[] schema = reader.readNext(); // Checks the data will be the type required

            for (int i = 0; i < schema.length; i++) {
                if (!schemaDefault[i].equals(schema[i])) {
                    throw new CustomException("Invalid Schema in CSV file. " + schema[i] + " should be " + schemaDefault[i] + ".", "Invalid Schema");
                }
            }
                // After getting all rows goes to write them into the database.
            List<String[]> rows = reader.readAll();
            
            write(rows, listId, behavior, skipBadValue);

        } catch (IOException e) {
            throw new CustomException("Error reading to database.", e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("(cannot start a transaction within a transaction)")) {
                throw new CustomException("Database is busy. Please wait until the current action is finished.", e.getMessage());
            }
            throw new CustomException("Error writing to database.", e.getMessage());
        }
    }

    /**
     * Adds the ability to edit or add data into the database from a crime object.
     *
     * @param crime     A crime object.
     * @throws CustomException  Error updating a crime within the database.
     */
    public void editCrime(Crime crime, int listId) throws CustomException {
        try (PreparedStatement psReport = connection.prepareStatement("INSERT OR REPLACE INTO reports(id, list_id, date, primary_description, secondary_description, domestic, x_coord, y_coord, latitude, longitude, location_description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement psCrime = connection.prepareStatement("INSERT OR REPLACE INTO crimes(report_id, list_id, block, iucr, fbicd, arrest, beat, ward) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);")){
            runStatement("BEGIN;");

            PSTypes.setPSString(psCrime, 1, crime.getId()); // Case Number
            PSTypes.setPSInteger(psCrime, 2, listId); // List
            PSTypes.setPSString(psCrime, 3, crime.getBlock()); // Block
            PSTypes.setPSString(psCrime, 4, crime.getIucr()); // Iucr
            PSTypes.setPSString(psCrime, 5, crime.getFbiCD()); // FbiCD
            PSTypes.setPSBoolean(psCrime, 6, crime.getArrest()); // Arrest
            PSTypes.setPSInteger(psCrime, 7, crime.getBeat()); // Beat
            PSTypes.setPSInteger(psCrime, 8, crime.getWard()); // Ward

            Timestamp date = Timestamp.valueOf(crime.getDate());
            PSTypes.setPSString(psReport, 1, crime.getId()); // Case Number
            PSTypes.setPSInteger(psReport, 2, listId); // List
            psReport.setTimestamp(3, date); // Date
            PSTypes.setPSString(psReport, 4, crime.getPrimaryDescription()); // Primary Description
            PSTypes.setPSString(psReport, 5, crime.getSecondaryDescription()); // Secondary Description
            PSTypes.setPSBoolean(psReport, 6, crime.getDomestic()); // Domestic
            PSTypes.setPSInteger(psReport, 7, crime.getXCoord()); // X Coordinate
            PSTypes.setPSInteger(psReport, 8, crime.getYCoord()); // Y Coordinate
            PSTypes.setPSDouble(psReport, 9, crime.getLatitude()); // Latitude
            PSTypes.setPSDouble(psReport, 10, crime.getLongitude()); // Longitude
            PSTypes.setPSString(psReport, 11, crime.getLocationDescription()); // Location Description


            psReport.execute();
            psCrime.execute();
            runStatement("COMMIT;");

        } catch (SQLException e) {
            if (e.getMessage().contains("(cannot start a transaction within a transaction)")) {
                throw new CustomException("Database is busy. Please wait until the current action is finished.", e.getMessage());
            }
            throw new CustomException("Error updating crime in the database.", e.getMessage());
        }
    }


    /**
     * Adds a list of reports in the form of a string[] into the database.
     *
     * @param rows                  All rows from the CSV file.
     * @throws SQLException         An error during the insertion.
     */
    private void write(List<String[]> rows, int listId, String behavior, boolean skipBadValue) throws SQLException, CustomException {
        // Turn off autocommit to increase speed.
        List<Integer> errorRows = new ArrayList<>();
        runStatement("BEGIN;");
        for (int i = 0; i < rows.size(); i++) {
            try (PreparedStatement psCrime = connection.prepareStatement("INSERT OR " + behavior + " INTO crimes(report_id, list_id, " + 
                    "block, iucr, fbicd, arrest, beat, ward) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
                PreparedStatement psReport = connection.prepareStatement("INSERT OR " + behavior + " INTO reports(id, list_id, date, " + 
                    "primary_description, secondary_description, domestic, x_coord, y_coord, latitude, longitude, location_description) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
                try {
                    addReport(rows.get(i), listId, psReport, psCrime);
                } catch (SQLException | DateTimeParseException e) {
                    // Row i+2 as indexing starts at 0 and row 1 is removed as the schema
                    errorRows.add(i+2);
                    if (!skipBadValue) {
                        throw new CustomException("Invalid data was detected. No changes have been made.", e.getMessage());
                    } 
                    continue;
                }
                try {
                    psReport.execute();
                    psCrime.execute();
                } catch (SQLException e) {
                    if (e.getMessage().contains("(UNIQUE constraint failed: reports.list_id, reports.id)")) {
                        throw new CustomException("Duplicate data was detected. No changes have been made.", e.getMessage());
                    } else {
                        throw e;
                    }
                }
                // Commits the changes and re-enables the auto commit.
            } catch (SQLException e) {
                if (e.getMessage().contains("(cannot start a transaction within a transaction)")) {
                    throw new CustomException("Database is busy. Please wait until the current action is finished.", e.getMessage());
                }
                runStatement("ROLLBACK;");
                throw new CustomException("Error writing to the database.", e.getMessage());
            }
        }
        runStatement("COMMIT");
        throw new CustomException("Import complete. " + (rows.size() - errorRows.size()) + "/" + rows.size() +
            " entries were imported", "Import complete");
    }

    /**
     * Adds a crime to the report and crime statements required to add the crime to the database.
     */
    private void addReport(String[] row, int listId, PreparedStatement psReport, PreparedStatement psCrime) throws SQLException {
        for (int index = 0; index <= 11; index++) {
            if ("".equals(row[index])) {
                throw new SQLException("A NOT NULL constraint has failed.");
            }
        }
        psReport.setString(1, row[0]); // Case Number
        PSTypes.setPSInteger(psReport, 2, listId); // List
        if (Objects.equals(row[1], "")) {
            psReport.setNull(3, Types.TIME);
        } else {
            psReport.setTimestamp(3, Timestamp.valueOf(parseDate(row[1])));
        } // Date
        PSTypes.setPSString(psReport, 4, row[4]); // Primary Description
        PSTypes.setPSString(psReport, 5, row[5]); // Secondary Description
        PSTypes.setPSBoolean(psReport, 6, row[8]); // Domestic
        PSTypes.setPSInteger(psReport, 7, row[12]); // X Coordinate
        PSTypes.setPSInteger(psReport, 8, row[13]); // Y Coordinate
        PSTypes.setPSDouble(psReport, 9, row[14]); // Latitude
        PSTypes.setPSDouble(psReport, 10, row[15]); // Longitude
        PSTypes.setPSString(psReport, 11, row[6]); // Location Description
        
        PSTypes.setPSString(psCrime, 1, row[0]); // Case Number
        PSTypes.setPSInteger(psCrime, 2, listId); // List
        PSTypes.setPSString(psCrime, 3, row[2]); // Block
        PSTypes.setPSString(psCrime, 4, row[3]); // Iucr
        PSTypes.setPSString(psCrime, 5, row[11]); // FbiCD
        PSTypes.setPSBoolean(psCrime, 6, row[7]); // Arrest
        PSTypes.setPSInteger(psCrime, 7, row[9]); // Beat
        PSTypes.setPSInteger(psCrime, 8, row[10]); // Ward
    }

    /**
     * Tries the 2 valid date formats used by the application.
     * @param date The string format of the date to be converted to LocalDateTime.
     * @return The LocalDateTime from a successful parsing.
     */
    private LocalDateTime parseDate(String date) throws DateTimeParseException {
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.US);
            return LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException ignore) {}
        try {
            formatter = DateTimeFormatter.ofPattern("M/dd/yyyy HH:mm", Locale.US);
            return LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException ignore) {}
        formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * Creates a new list in the database with the name passed in.
     * @param name The name of the new database.
     * @throws CustomException Error creating a list in the database.
     */
    public void createList(String name) throws CustomException {
        try (PreparedStatement psList = connection.prepareStatement("INSERT or ABORT INTO lists(name) VALUES (?);")) {
            runStatement("BEGIN;");
            PSTypes.setPSString(psList, 1, name);
            psList.execute();
            runStatement("COMMIT;");
        } catch (SQLException e) {
            if (e.getMessage().contains("(cannot start a transaction within a transaction)")) {
                throw new CustomException("Database is busy. Please wait until the current action is finished.", e.getMessage());
            }
            throw new CustomException("Unable to create a new list.", e.getMessage());
        }
    }

    /**
     * 
     * @return An ObservableList containing all the lists in the database.
     * @throws CustomException Error getting data of a given list.
     */
    public ObservableList<String> getLists() throws CustomException {
        List<String> lists = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM lists")) {
            while (rs.next()) {
                lists.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new CustomException("Error getting the list of reports.", e.getMessage());
        }
        return FXCollections.observableList(lists);
    }

    /**
     * Renames a list contained in the database.
     * @param oldName The current name of the list.
     * @param newName The name that the list will be changed to.
     * @throws CustomException Error renaming list.
     */
    public void renameList(String oldName, String newName) throws CustomException {
        try (PreparedStatement psList = connection.prepareStatement("UPDATE lists SET name=? WHERE name=?;")) {
            psList.setString(1, newName);
            psList.setString(2, oldName);
            psList.execute();
        } catch (SQLException e) {
            throw new CustomException("List name already being used.", e.getMessage());
        }  
    }

    /**
     * Returns the numeric ID of a list given its name.
     * @param selectedList The name of the required list
     * @return The numeric ID of the selected list.
     * @throws CustomException Error getting list ID.
     */
    public Integer getListId(String selectedList) throws CustomException {
        Integer listId = null;
        try (PreparedStatement psList = connection.prepareStatement("SELECT id FROM lists WHERE name=?;")) {
            psList.setString(1, selectedList);
            ResultSet lists = psList.executeQuery();
            while(lists.next()) {
                listId = lists.getInt("id");
            }
            // Closes the result set.
            lists.close();
        } catch (SQLException e) {
            throw new CustomException("Error getting the list ID.", e.getMessage());
        }
        return listId;
    }

    /**
     * Deletes the selected list from the database.
     * @param selectedList The name of the list to be deleted.
     * @throws CustomException Error deleting list of data.
     */
    public void deleteList(String selectedList) throws CustomException {
        try (PreparedStatement psList = connection.prepareStatement("DELETE FROM lists WHERE name=?;")) {
            runStatement("BEGIN");
            psList.setString(1, selectedList);
            psList.execute();
            runStatement("COMMIT");
        } catch (SQLException e) {
            if (e.getMessage().contains("(cannot start a transaction within a transaction)")) {
                throw new CustomException("Database is busy. Please wait until the current action is finished.", e.getMessage());
            }
            throw new CustomException("Error deleting list from the database.", e.getMessage());
        }
    }

    /**
     * Determines what type of file needs to be exported to and navigates to the relevant method.
     * @param conditions The filter conditions to be complied with.
     * @param listId The list the data is being exported from.
     * @param saveLocation The location tof the new file.
     * @throws CustomException Error exporting the results to a file.
     */
    public void export(String conditions, int listId, String saveLocation) throws CustomException {
        if (saveLocation.endsWith(".db")) {
            try {
                exportDB(conditions, listId, saveLocation);
            } catch (SQLException e) {
                throw new CustomException("Database failed to sync correctly.", e.getMessage());
            }
        } else if (saveLocation.endsWith(".csv")) {
            exportCSV(conditions, listId, saveLocation);
        }
    }

    /**
     * Writes the data contained in the database relevant to the current filter conditions into a csv file.
     * @param conditions The filter conditions to be complied with.
     * @param listId The list the data is being exported from.
     * @param saveLocation The location of the new file.
     * @throws CustomException Error exporting CSV file.
     */
    private void exportCSV(String conditions, int listId, String saveLocation) throws CustomException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(saveLocation), ',');
            Statement stmt = connection.createStatement()){
        
            writer.writeNext(("CASE#,DATE OF OCCURRENCE,BLOCK,IUCR,PRIMARY DESCRIPTION,SECONDARY DESCRIPTION," +
            "LOCATION DESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBI CD,X COORDINATE,Y COORDINATE,LATITUDE,LONGITUDE,LOCATION").split(","));

            String query = "SELECT " +
                "id, " + 
                "date, " +
                "block, " +
                "iucr, " +
                "primary_description, " +
                "secondary_description, " + 
                "location_description, " + 
                "arrest, " +
                "domestic, " +
                "beat, " +
                "ward, " + 
                "fbicd, " +
                "x_coord, " +
                "y_coord, " +
                "latitude, " +
                "longitude " +
                "FROM crimedb WHERE ";
            query = addConditions(query, conditions, listId);

            ResultSet rs = stmt.executeQuery(query);
            writer.writeAll(rs, false);
            rs.close();
        } catch (SQLException | IOException e) {
            throw new CustomException("Error exporting CSV from the database.", e.getMessage());
        }   
    }

    /**
     * Writes the data contained in the database relevant to the current filter conditions into a database file.
     * @param conditions The filter conditions to be complied with.
     * @param listId The list the data is being exported from.
     * @param saveLocation The location of the new file.
     * @throws SQLException     Error when running the statements.
     * @throws CustomException  Error when exporting the database.
     */
    private void exportDB(String conditions, int listId, String saveLocation) throws CustomException, SQLException {
        createExportDatabase(saveLocation);
        runStatement("ATTACH DATABASE '" + saveLocation + "' AS other;");
        try {
            String query = "INSERT INTO other.reports SELECT " +
            "id, " + 
            "date, " +
            "primary_description, " +
            "secondary_description, " + 
            "domestic, " +
            "x_coord, " +
            "y_coord, " +
            "latitude, " +
            "longitude, " +
            "location_description " + 
            "FROM crimedb WHERE ";
            query = addConditions(query, conditions, listId);
            runStatement(query);

            query = "INSERT INTO other.crimes SELECT " +
            "report_id, " + 
            "block, " +
            "iucr, " +
            "fbicd, " + 
            "arrest, " +
            "beat, " +
            "ward " + 
            "FROM crimedb WHERE ";
            query = addConditions(query, conditions, listId);
            runStatement(query);
        } catch (SQLException e) {
            throw new CustomException("Could not export entries into database", e.getMessage());
        } finally {
            runStatement("DETACH DATABASE other;");
        }
    }

    /**
     * Adds the required list to the conditions.
     * @param query SELECT statement to be refined.
     * @param conditions Filter conditions applied to query.
     * @param listId List to query data from.
     * @return  The query to be run.
     */
    private String addConditions(String query, String conditions, int listId) {
        query += conditions;
        if (!conditions.isEmpty()) {
            query += " AND";
        }
        query += " list_id=" + listId +";";
        return query;
    }

    /**
     * Creates a database for the data to be exported into at the given location.
     * @param location The location to create the database.
     * @throws CustomException  Throws an error when creating the exported data.
     */
    private void createExportDatabase(String location) throws CustomException {
        try (Connection newdb = DriverManager.getConnection("jdbc:sqlite:" + location); Statement stmt = newdb.createStatement()) {
            
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("DROP TABLE IF EXISTS reports;");
            stmt.execute("CREATE TABLE reports (" + 
                    "id VARCHAR(8) NOT NULL, " + 
                    "date TIMESTAMP NOT NULL, " +
                    "primary_description VARCHAR(50) NOT NULL, " +
                    "secondary_description VARCHAR(50) NOT NULL, " + 
                    "domestic BOOLEAN, " +
                    "x_coord INT, " +
                    "y_coord INT, " +
                    "latitude FLOAT, " +
                    "longitude FLOAT, " +
                    "location_description VARCHAR(50), " +
                    "PRIMARY KEY(id) " +
                    ")");
            stmt.execute("DROP TABLE IF EXISTS crimes;");
            stmt.execute("CREATE TABLE crimes (" + 
                    "report_id VARCHAR(8) NOT NULL, " +
                    "block VARCHAR(50), " +
                    "iucr VARCHAR(4), " +
                    "fbicd VARCHAR(3), " + 
                    "arrest BOOLEAN, " +
                    "beat INT, " +
                    "ward INT, " +
                    "FOREIGN KEY(report_id) REFERENCES reports(id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "PRIMARY KEY(report_id)" +
                    ")");
        } catch (SQLException e) {
            throw new CustomException("Error while creating new database.", e.getMessage());
        }
    }

    /**
     * Checks what type of file is being imported and sends it to the appropriate method
     */
    public void importFile(File fileLocation, int currentList, String behavior, boolean skipBadValue) throws CustomException {
        // If the file chooser is exited before a file is selected it will be a NULL value and should not continue.
        if (fileLocation != null) {
            String fileName = fileLocation.getName();
            if (fileName.endsWith(".csv")) {
                // Reads a CSV into the database.
                readToDB(fileLocation, currentList, behavior, skipBadValue);
            } else {
                // Reads a outside database into the main database.
                importInDB(fileLocation, currentList, behavior);
            }
        }

    }

}
