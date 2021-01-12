package org.edge.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    public enum Level {
        DEBUG("debug"), INFOR("infor"), ERROR("error"), WARNING("warning");

        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    private  LogUtil.Level level = LogUtil.Level.DEBUG;
    private  String path;
    private  boolean saveLogToFile;
    private  BufferedWriter bufferedWriter;
    private  boolean append=true;

    /**
     * must be called in initialization part if needing to write log into file
     */

    public Logger(LogUtil.Level level, String path, boolean saveLogToFile, boolean append) {
        this.append=append;
        this.path=path;
        this.saveLogToFile=saveLogToFile;
        this.level=level;
        if(saveLogToFile) {
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(path, append);
                bufferedWriter = new BufferedWriter(fileWriter);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void info(String msg) {
        if (level.ordinal() <= 1) {
            if (saveLogToFile) {
                appendTextToFile(msg);
            }

        }

    }

    public void simulationFinished() {
        if(saveLogToFile && bufferedWriter!=null) {
            try {
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    public void appendTextToFile(String text) {
        try {
            bufferedWriter.write(text);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
