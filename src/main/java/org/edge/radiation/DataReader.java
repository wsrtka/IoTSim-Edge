package org.edge.radiation;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataReader {

    private static final String path = "src/main/resources/ASP_radiation_2020-02.tab";

    private int directRadiationColumn;

    private int diffuseRadiationColumn;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

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

    /**
     * Returns the radiation data for the specified data. Might be null if there is no
     * radiation data for the specified date.
     * @param date Date and time of which data should be retrieved. Seconds and milliseconds
     *             should be set to 0.
     * @return {@link Pair} where key is set to direct radiation and value is set
     * to diffused radiation at the specified DateTime
     */
    public Pair<Integer, Integer> getData(LocalDateTime date) throws IOException {

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
                if(!data[0].equals("Date/Time") && date.equals(parseDate(data[0]))) {
                    return new Pair<>(Integer.valueOf(data[directRadiationColumn]), Integer.valueOf(data[diffuseRadiationColumn]));
                }
            }
        }
        return null;
    }

    private LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString.replace("T", "-"), dtf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
