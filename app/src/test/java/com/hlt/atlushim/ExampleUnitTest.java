package com.hlt.atlushim;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private Time time;

    @Before
    public void setUp() throws Exception{
        time = new Time();
    }

    @Test
    public void time_isCorrect() {
        assertEquals(3.92 , time.dFromS("3:55"),0.00001);
        assertEquals(0 , time.dFromS("test"),0.00001);
        assertEquals("8:24",time.sFromD(8.4));
        assertEquals("0:00",time.sFromD(0.0));
        assertEquals("4:04",time.addTime("1:55","2:09"));
        assertEquals("3:09",time.addTime("1:35","1:35"));
        assertEquals("0:54",time.subTime("8:24","7:30"));
    }
}
