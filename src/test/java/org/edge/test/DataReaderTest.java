package org.edge.test;

import javafx.util.Pair;
import org.edge.radiation.DataReader;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataReaderTest {

    @Test
    public void dataReaderCreationTest() {
        DataReader dr = null;
        try {
            dr = new DataReader();
        } catch (IOException ignored) {}
        assertNotNull(dr);
    }

    @Test
    public void readingRadiationDataTest() {
        Pair<Integer, Integer> data1 = null, data2 = null;
        Calendar date1 = new GregorianCalendar(2020, Calendar.FEBRUARY, 1, 0, 0);
        Calendar date2 = new GregorianCalendar(2020, Calendar.FEBRUARY, 29, 23, 59);
        try {
            DataReader dr = new DataReader();
            data1 = dr.getData(date1);
            data2 = dr.getData(date2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(data1);
        Integer dir1 = data1.getKey();
        Integer dif1 = data1.getValue();
        assertEquals(41, (int) dir1);
        assertEquals(258, (int) dif1);

        assertNotNull(data2);
        Integer dir2 = data2.getKey();
        Integer dif2 = data2.getValue();
        assertEquals(333, (int) dir2);
        assertEquals(308, (int) dif2);
    }
}
