package org.fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JDialogFis;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import utils.ClimateData;
import utils.ClimateRecord;
import utils.FuzzyClimateSimulator;
import utils.UtilityWriter;

import java.text.DecimalFormat;

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


//        String fcl = "FUNCTION_BLOCK climate\n" +
//                "\n" +
//                "VAR_INPUT\n" +
//                "   time : REAL;\n" +
//                "   season : REAL;\n" +
//                "END_VAR\n" + //
//                "\n" + //
//                "VAR_OUTPUT\n" + //
//                "   temperature : REAL;\n" + //
//                "END_VAR\n" + //
//                "\n" + //
//                "FUZZIFY time\n" + //
//                "   TERM day := sigm -1.5 6;\n" + //
//                "   TERM night := sigm 1.5 6;\n" + //
//                "END_FUZZIFY\n" + //
//                "\n" + //
//                "FUZZIFY season\n" + //
//                "   TERM summer := (0, 1) (2, 1) (4,0) ;\n" + //
//                "   TERM winter := (2,0) (4,1) (6,1);\n" + //
//                "END_FUZZIFY\n" + //
//                "\n" + //
//                "DEFUZZIFY temperature\n" + //
//                "   TERM SumDay := gbell 2 4 34;\n" + //
//                "   TERM SumNig := gbell 2 4 22;\n" + //
//                "   TERM WinDay := gbell 2 4 30;\n" + //
//                "   TERM WinNig := gbell 2 4 23;\n" + //
//                "   METHOD : COG;\n" + //
//                "   DEFAULT := 0;\n" + //
//                "END_DEFUZZIFY\n" + //
//                "\n" + //
//                "DEFUZZIFY humidity\n" + //
//                "   TERM SumHum := gbell 2 4 82;\n" + //
//                "   TERM WinHum := gbell 2 4 88;\n" + //
//                "   METHOD : COG;\n" + //
//                "   DEFAULT := 0;\n" + //
//                "END_DEFUZZIFY\n" + //
//                "\n" + //
//                "RULEBLOCK No1\n" + //
//                "   ACCU : MAX;\n" + //
//                "   AND : MIN;\n" + //
//                "   ACT : MIN;\n" + //
//                "\n" + //
//                "   RULE 1 : IF time IS day AND season is summer THEN temperature IS SumDay, humidity IS SumHum;\n" + //
//                "   RULE 2 : IF time IS night AND season is summer THEN temperature IS SumNig, humidity IS SumHum;\n" + //
//                "   RULE 3 : IF time IS day AND season is winter THEN temperature IS WinDay, humidity IS WinHum;\n" + //
//                "   RULE 4 : IF time IS night AND season is winter THEN temperature IS WinNig, humidity IS WinHum;\n" + //
//                "END_RULEBLOCK\n" + //
//                "\n" + //
//                "END_FUNCTION_BLOCK\n";
//
//        FIS fis = FIS.createFromString(fcl, true);
//        fis.setVariable("time", 0); // Set inputs
//        fis.setVariable("season", 6);
//        fis.evaluate();
//
//        // Create a plot
//        JDialogFis jdf = null;
//        if (!JFuzzyChart.UseMockClass) jdf = new JDialogFis(fis, 800, 600);
//
//        // Show output variable
//        System.out.println("Output value:" + fis.getVariable("temperature").getValue());
//        System.out.println("Output value:" + fis.getVariable("humidity").getValue());
//
//        UtilityWriter uwu = new UtilityWriter("test", new DecimalFormat("0.00"));
//        uwu.initialize("Month", "Day", "Night");
//
//        // Set different values for 'food' and 'service'. Evaluate the system and show variables
//        for (double season = 1.0, time = 0.0; season <= 12; season += 1) {
//            // Evaluate system using these parameters
//            fis.getVariable("season").setValue(Math.abs(season - 6));
//            fis.getVariable("time").setValue(0);
//            fis.evaluate();
//            uwu.append(season).append(fis.getVariable("temperature").getValue());
//
//            fis.getVariable("time").setValue(12);
//            fis.evaluate();
//            uwu.append(fis.getVariable("temperature").getValue());
//
//            uwu.push();
//
//            // Print result & update plot
//            System.out.println(String.format("Season: %2.2f\tTime:%2.2f\t=> Temperature: %2.2f ", season, time, fis.getVariable("temperature").getValue()));
//            if (jdf != null) jdf.repaint();
//
//            // Small delay
//            Thread.sleep(300);
//        }
//        uwu.save();

        ClimateData cd = new ClimateData().
                addRecord(new ClimateRecord(JANUARY, 13.5, 5, 0.76)).
                addRecord(new ClimateRecord(FEBRUARY, 14.5, 5.5, 0.76)).
                addRecord(new ClimateRecord(MARCH, 17, 7, 0.76)).
                addRecord(new ClimateRecord(APRIL, 19, 9, 0.76)).
                addRecord(new ClimateRecord(MAY, 22.5, 12, 0.76)).
                addRecord(new ClimateRecord(JUNE, 27, 16, 0.76)).
                addRecord(new ClimateRecord(JULY, 29.5, 18, 0.6)).
                addRecord(new ClimateRecord(AUGUST, 29.5, 18, 0.61)).
                addRecord(new ClimateRecord(SEPTEMBER, 27, 16, 0.66)).
                addRecord(new ClimateRecord(OCTOBER, 22, 12.5, 0.76)).
                addRecord(new ClimateRecord(NOVEMBER, 17, 8.5, 0.76)).
                addRecord(new ClimateRecord(DECEMBER, 11, 6, 0.76));


        FuzzyClimateSimulator fcs = new FuzzyClimateSimulator(cd);
        fcs.makePlot();
        fcs.fclSave("output/sample_fcl");

    }
}