package seng202.group7;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import seng202.group7.data.Crime;

/**
 * Testing that a crime object is correctly created.
 */
public class CrimeTest
{

    /**
     * Tests the creation of a crime object
     */
    @Test
    public void init_crimeTest() {
        Crime crime = new Crime(null, null, null, null, null, null, null, true, false, 0, 0, null, 0, 0, null, null);
        assertTrue(crime.getArrest());
    }

}