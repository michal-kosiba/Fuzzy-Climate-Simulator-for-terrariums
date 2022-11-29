package utils;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class UtilityWriter {
    CSVWriter writer;
    DecimalFormat format = new DecimalFormat("0.00000");
    String fileName;
    String[] lineToWrite;
    public UtilityWriter(String fileName, DecimalFormat format){
        this.format = format;
        this.fileName = fileName;
        this.lineToWrite = null;
    }

    public void initialize(String... columnNames){
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("output\\");
            sb.append(fileName);
            sb.append('-');
            //sb.append(LocalDateTime.now().toString().replace('.', '-').replace(':', '-'));
            sb.append("latest");
            sb.append(".csv");
            writer = new CSVWriter(new FileWriter(sb.toString()));

            writer.writeNext(columnNames);
            writer.flush();
        } catch (IOException exception){
            System.out.println(exception.toString());
            System.exit(-1);
        }
    }

    public UtilityWriter append(double number){
        lineToWrite = ArrayUtils.add(lineToWrite, format.format(number).replace('.', ','));

        return this;
    }

    public void push(){
        writer.writeNext(lineToWrite);
        lineToWrite = null;
    }

    public void save() {
        try {
            writer.flush();
        }catch (IOException exception) {
            System.out.println(exception.toString());
        };
    }
}
