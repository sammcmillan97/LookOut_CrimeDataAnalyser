package seng202.group7.analyses;

import seng202.group7.data.Report;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;


/**
 * This class adds a set of static methods that allow reports to distance and time to be compared.
 *
 *  @author Sam McMillan
 */
public final class Comparer {

    private Comparer(){}

    /**
     * Compares two reports and returns the time difference.
     *
     * @param reportOne     Data to be compared with "reportTwo"
     * @param reportTwo     Data to be compared with "reportOne"
     * @return              A list of long values [Minutes, Hours, Days, Years]
     */
    public static List<Long> timeDifference(Report reportOne, Report reportTwo) {
        List<Long> timeDifferences = new ArrayList<>();
        Duration difference = Duration.between(reportTwo.getDate(), reportOne.getDate()); //Returns total time in seconds
        timeDifferences.add(Math.abs(TimeUnit.SECONDS.toMinutes(difference.getSeconds()) % 60));
        timeDifferences.add(Math.abs(TimeUnit.SECONDS.toHours(difference.getSeconds()) % 24));
        timeDifferences.add(Math.abs(TimeUnit.SECONDS.toDays(difference.getSeconds()) % 365));
        timeDifferences.add(Math.abs(TimeUnit.SECONDS.toDays(difference.getSeconds()) / 365));
        return timeDifferences;
    }

    /**
     *Checks for null or default values if they are then sends a value -1, -2 or -3 (-1: crime 1 missing location values,
     * -2: missing location values, -3: missing location values) if not then returns the displacement between the latitude and longitude values
     * in km
     *
     *
     * @param reportOne data to be compared with "reportTwo"
     * @param reportTwo data to be compared with "reportOne"
     * @return A double value corresponding the displacement between the two reports in km
     */

    public static double locationDifference(Report reportOne, Report reportTwo) {
        double lat1 = 0;
        double lat2 = 0;
        double lon1 = 0;
        double lon2 = 0;

        double checkNull = 0;

        try { //Checking for null values
            lat1 = reportOne.getLatitude();
            lon1 = reportOne.getLongitude();
        } catch (NullPointerException e) {
            //Report one has no location values
            checkNull = -1;
        } try {
            lat2 = reportTwo.getLatitude();
            lon2 = reportTwo.getLongitude();
        } catch (NullPointerException e) {
            //Report two has no location values
            if (checkNull == 0) {
                checkNull = -2;
            } else {
                //Both reports have no location values
                checkNull = -3;
            }
        }

        if (lat1 == 0.0 && lat2 ==0.0) { //Checking for default values
            checkNull = -3;
        } else if (lat1 == 0.0) {
            if (checkNull == -2) {
                checkNull = -3;
            } else {
                checkNull = -1;
            }
        } else if (lat2 == 0.0) {
            if (checkNull == -1) {
                checkNull = -3;
            } else {
                checkNull = -2;
            }
        }

        if (checkNull != 0) {
            return checkNull; //If default or null then return what value(s) is null or default
        }

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; //Converting to kilometers
        return (dist);
    }
}
