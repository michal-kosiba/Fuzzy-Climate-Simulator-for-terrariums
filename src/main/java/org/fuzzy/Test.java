package org.fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JDialogFis;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import utils.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class Test {

    public static final int JANUARY = 0;
    public static final int FEBRUARY = 1;
    public static final int MARCH = 2;
    public static final int APRIL = 3;
    public static final int MAY = 4;
    public static final int JUNE = 5;
    public static final int JULY = 6;
    public static final int AUGUST = 7;
    public static final int SEPTEMBER = 8;
    public static final int OCTOBER = 9;
    public static final int NOVEMBER = 10;
    public static final int DECEMBER = 11;

    public static void main(String[] args) throws Exception {

        ClimateData brazil_amazonas = new ClimateData().
                addRecord(new ClimateRecord(JANUARY, 32, 23.5, 0.85)).
                addRecord(new ClimateRecord(FEBRUARY, 32, 23.5, 0.87)).
                addRecord(new ClimateRecord(MARCH, 32, 23.5, 0.88)).
                addRecord(new ClimateRecord(APRIL, 32, 23.5, 0.87)).
                addRecord(new ClimateRecord(MAY, 32, 23.5, 0.87)).
                addRecord(new ClimateRecord(JUNE, 32, 23, 0.82)).
                addRecord(new ClimateRecord(JULY, 32.5, 22.5, 0.80)).
                addRecord(new ClimateRecord(AUGUST, 33, 22.5, 0.77)).
                addRecord(new ClimateRecord(SEPTEMBER, 33.5, 23, 0.77)).
                addRecord(new ClimateRecord(OCTOBER, 33, 23.5, 0.79)).
                addRecord(new ClimateRecord(NOVEMBER, 32.5, 23.5, 0.80)).
                addRecord(new ClimateRecord(DECEMBER, 32, 23.5, 0.85));

        FuzzyClimateSimulator fcs = new FuzzyClimateSimulator(brazil_amazonas);
        //fcs.makePlot();
        FuzzyClimateController fcc = new FuzzyClimateController(fcs);
        fcc.makePlot();
        //System.out.println( fcc.getHeating(LocalDateTime.now()));
        //System.out.println(fcc.getSprinkling(LocalDateTime.now()));
    }
}