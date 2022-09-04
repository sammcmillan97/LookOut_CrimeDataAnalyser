package seng202.group7;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import seng202.group7.analyses.Comparer;
import seng202.group7.data.Crime;
import seng202.group7.data.Report;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing that the comparer class correctly is able to compare two reports and return valid result.
 *
 * @author Sam McMillan
 * @author John Elliott
 */
public class ComparerTest {

    public static Report reportOne;
    public static Report reportTwo;
    public static Report reportThree;

    public static Report reportFive;

    public static Duration dateDifference;
    public static Report reportFour;

    /**
     * Sets up and creates all the reports that will be used for testing.
     */
    @BeforeAll
    public static void makeReports() {
        LocalDate testDate = LocalDate.now();
        LocalDateTime dateStart = testDate.atStartOfDay();
        LocalDateTime dateEnd = testDate.atTime(LocalTime.MIDNIGHT);
        dateDifference = Duration.between(dateStart, dateEnd);

        reportOne = new Crime("TestNumber1", dateStart, null, null, null, null, null, true, false, null, null, null, null, null, -43.540919, 172.585284);
        reportTwo = new Crime("TestNumber2", dateEnd, null, null, null, null, null, true, false, null, null, null, null, null, -43.508434, 172.630126);
        reportThree = new Crime("TestNumber3", dateEnd, null, null, null, null, null, true, false, null, null, null, null, null, -43.508434, 172.630126);
        reportFour = new Crime("TestNumber4", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        reportFive = new Crime("TestNumber4", null, null, null, null, null, null, null, null, null, null, null, null, null, 0.0, 0.0);
    }


    /**
     * Tests timeDifference with a specific case to check if returns the correct value for minutes.
     */
    @Test
    public void timeDifferenceTest_specificCase() {
        List<Long> differences = Comparer.timeDifference(reportOne, reportTwo);
        Long diffMins = Math.abs(TimeUnit.SECONDS.toMinutes(dateDifference.getSeconds()) % 60);
        assertEquals(differences.get(0), diffMins);
    }

    /**
     * Test timeDifference function with the same time value to ensure it returns 0 for hours.
     */
    @Test
    public void timeDifferenceTest_boundaryCase() {
        List<Long> list = Comparer.timeDifference(reportTwo, reportThree);
        assertEquals(0, list.get(1));
    }

    /**
     * Tests locationDifference with a specific case to check if it returns the correct value
     */
    @Test
    public void locationDifference_specificCase() {
        double difference = Comparer.locationDifference(reportOne, reportTwo);
        assertEquals(5.110405795660774, difference);
    }

    /**
     * Tests the locationDifference with the same value to ensure it returns 0
     */
    @Test
    public void locationDifference_boundaryCase() {
        Double difference = Comparer.locationDifference(reportTwo, reportThree);
        assertEquals(0, difference);
    }

    /**
     * Tests the location difference between two null values
     */
    @Test
    public void locationDifferenceTest_BothVoid() {
        Double difference = Comparer.locationDifference(reportFour, reportFour);
        assertEquals(-3, difference);
    }

    /**
     * Tests the location difference between a void and valid value
     */
    @Test
    public void locationDifferenceTest_FirstVoid() {
        Double difference = Comparer.locationDifference(reportFour, reportOne);
        assertEquals(-1, difference);
    }

    /**
     * Tests the location between a valid and void value
     */
    @Test
    public void locationDifferenceTest_SecondVoid() {
        Double difference = Comparer.locationDifference(reportOne, reportFour);
        assertEquals(-2, difference);
    }

    /**
     * Tests the location values between between a void and default value
     */
    @Test
    public void locationDifferenceTest_FirstVoidSecondDefault() {
        Double difference = Comparer.locationDifference(reportFour, reportFive);
        assertEquals(-3, difference);
    }

}
