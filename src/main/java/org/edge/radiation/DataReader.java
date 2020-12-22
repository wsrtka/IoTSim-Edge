package org.edge.radiation;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DataReader {

    private static final String path = "src/main/resources/ASP_radiation_2020-02.tab";

    private static int directRadiationColumn;

    private static int diffuseRadiationColumn;

    public DataReader() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(path));
        boolean afterComment = false;
        String line;
        while((line = br.readLine()) != null) {
            if(!afterComment) {
                if(line.equals("*/")) {
                    afterComment = true;
                }
            } else {
                String[] data = line.split("\t");
                for(int i = 0; i < data.length; i++) {
                    if(data[i].equals("DIR [W/m**2]")) {
                        directRadiationColumn = i;
                    } else if(data[i].equals("DIF [W/m**2]")) {
                        diffuseRadiationColumn = i;
                    }
                }
                break;
            }
        }
    }

    public static Pair<Integer, Integer> getData(Calendar date) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(path));
        boolean afterComment = false;
        String line;
        while((line = br.readLine()) != null) {
            if(!afterComment) {
                if(line.equals("*/")) {
                    afterComment = true;
                }
            } else {
                String[] data = line.split("\t");
                if(!data[0].equals("Date/Time") && parseDate(data[0]).equals(date)) {
                    //System.out.println(parseDate(data[0]).getTime());
                    return new Pair<>(Integer.valueOf(data[directRadiationColumn]), Integer.valueOf(data[diffuseRadiationColumn]));
                }
            }
        }
        return null;
    }

    private static Calendar parseDate(String dateTime) {

        String[] parts = dateTime.split("T");
        String[] date = parts[0].split("-");
        String[] time = parts[1].split(":");
        return new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]),
                Integer.parseInt(time[0]), Integer.parseInt(time[1]));

    }
}
