package seng202.group7;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import seng202.group7.analyses.CrimeFrequency;
import seng202.group7.analyses.GraphUtil;
import seng202.group7.analyses.Tuple;
import seng202.group7.data.Crime;
import seng202.group7.data.Report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests ranking reports.
 *
 * @author Sam McMillan
 */
public class GraphUtilTest {

    private static ArrayList<Report> data;
    private static ArrayList<Report> data2;
    private static ArrayList<Report> data3;
    private static ArrayList<Report> data4;
    /**
     * Creates a list of reports to be used for the ranked tests.
     */
    @BeforeAll
    public static void setup() {
        data = new ArrayList<>();
        data2 = new ArrayList<>();
        data3 = new ArrayList<>();
        data4 = new ArrayList<>();
        LocalDateTime date1 = LocalDateTime.of(2021, 2, 15, 20, 20);
        LocalDateTime date2 = LocalDateTime.of(2020, 2, 20, 19, 33);
        Crime reportOne = new Crime("1", date2, "073XX S SOUTH SHORE DR", "NA", "THEFT", "NA", null, false, false, 1, 1, "NA", 1183633, 1851786, 41.748486365, -87.602675062);
        Crime reportTwo = new Crime("2", date2, "109XX S WALLACE ST", "NA", "SEX OFFENSE", "NA", null, false, false, 1, 20, "NA", 1148227, 1899678, 41.880660786, -87.731186405);
        Crime reportThree = new Crime("3", date2, "029XX S DR MARTIN LUTHER KING JR DR", "NA", "THEFT", "NA", null, false, false, 1, 10, "NA", 1148227, 1899678, 41.880660786, -87.731186405);
        Crime reportFour = new Crime("4", date1, "064XX S DR MARTIN LUTHER KING JR DR", "NA", "MURDER", "NA", null, false, false, 2, 1, "NA", 1148227, 1899678, 41.880660786, -87.731186405);
        Crime reportFive = new Crime("5", date1, "055XX S DR MARTIN LUTHER KING JR DR", "NA", "THEFT", "NA", null, false, false, 2, 1, "NA", 1148227, 1899678, 41.880660786, -87.731186405);
        data.add(reportOne);
        data.add(reportTwo);
        data.add(reportThree);
        data.add(reportFour);
        data.add(reportFive);
        data2.add(reportOne);
        data3.add(reportOne);
        data3.add(reportOne);
    }

    /**
     * Tests to see the correct crime type(theft) is returned from frequency ranker
     */
    @Test
    public void primaryFrequencyRank() {
        List<Tuple<String, Integer>> list = GraphUtil.primaryFrequencyRank(data);
        assertEquals("THEFT", list.get(list.size() - 1).x);
    }


    /**
     * Tests to see the correct ward (1) is returned as it has the highest amount of crimes in the data set
     */
    @Test
    public void wardFrequencyRank() {
        List<Tuple<String, Integer>> list = GraphUtil.wardFrequencyRank(data);
        assertEquals("1", list.get(list.size() - 1).x);
    }

    /**
     * Tests to see the correct street is returned ("S DR MARTIN LUTHER KING JR DR") is returned as it has the
     * highest crime rate in the data set
     */
    @Test
    public void streetRankTest() {
        List<Tuple<String, Integer>> list = GraphUtil.streetRank(data);
        assertEquals("S DR MARTIN LUTHER KING JR DR", list.get(list.size() - 1).x);
    }

    /**
     * Tests to see the correct beat value is returned ("1") is returned as it highest the highest crime rate
     */
    @Test
    public void beatFrequencyRankTest() {
        List<Tuple<String, Integer>> list = GraphUtil.beatFrequencyRank(data);
        assertEquals("1", list.get(list.size() - 1).x);
    }

    /**
     * Tests to the correct number of crime frequency values are added to the list between the first and last date value in the list
     * of reports
     */
    @Test
    public void getDateListTest_General() {
        List<CrimeFrequency> dateList = GraphUtil.getDateList(data);
        assertEquals(13, dateList.size());
        assertEquals("2 2020", dateList.get(0).getDate());
        assertEquals("2 2021", dateList.get(dateList.size() -1).getDate());
    }

    /**
     * Tests to see the correct number of crime frequency values are added to a list from a single value report list
     */
    @Test
    public void getDateListTest_OneValue() {
        List<CrimeFrequency> dateList = GraphUtil.getDateList(data2);
        assertEquals(1, dateList.size());
    }

    /**
     * Tests to see the correct number of crime frequency values are added to a list from a two of the same value report list
     */
    @Test
    public void getDateListTest_TwoSameValue() {
        List<CrimeFrequency> dateList = GraphUtil.getDateList(data3);
        assertEquals(1, dateList.size());
    }

    /**
     * Tests to see the correct number of crime frequency values are added to a list from a zero value report list
     */
    @Test
    public void getDateListTest_NoValue() {
        List<CrimeFrequency> dateList = GraphUtil.getDateList(data4);
        assertEquals(0, dateList.size());
    }

    /**
     * Tests to see the correct frequency values are added to a list of crime frequencies from the crime over time method
     */
    @Test
    public void crimeOverTime_General() {
        List<CrimeFrequency> crimeOverTime = GraphUtil.crimeOverTime(data);
        assertEquals(13, crimeOverTime.size());
        assertEquals(3, crimeOverTime.get(0).getFrequency());
        assertEquals(2, crimeOverTime.get(crimeOverTime.size() -1).getFrequency());
        assertEquals(0, crimeOverTime.get(1).getFrequency());
    }

    /**
     * Tests to see the correct frequency values are added to a list of crime frequencies from the crime over time method
     *when a list of zero values is used
     */
    @Test
    public void CrimeOVerTime_NoValue() {
        List<CrimeFrequency> crimeOverTime = GraphUtil.crimeOverTime(data4);
        assertEquals(0, crimeOverTime.size());
    }

    /**
     * Checks to see the correct title is returned from the get title method.
     */
    @Test
    public void getTitleTest() {
        List<String> choices = new ArrayList<>();
        choices.add("BIG      sad mOnStER");
        choices.add("1");
        choices.add("5");
        assertEquals("Big Sad Monster Over Time in Ward 1 and Beat 5", GraphUtil.getTitle(choices));
    }
}
