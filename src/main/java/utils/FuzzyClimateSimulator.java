package utils;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JDialogFis;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import org.antlr.runtime.RecognitionException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class FuzzyClimateSimulator {
    private final ClimateData cd;
    String fcl;

    FIS fis;

    public FuzzyClimateSimulator(ClimateData cd) throws RecognitionException {
        this.cd = cd;

        fcl = "FUNCTION_BLOCK climate\n" + //
                "\n" + //
                "VAR_INPUT\n" + //
                "   time : REAL;\n" + //
                "   month : REAL;\n" + //
                "END_VAR\n" + //
                "\n" + //
                "VAR_OUTPUT\n" + //
                "   temperature : REAL;\n" + //
                "   humidity : REAL;\n" + //
                "END_VAR\n" + //
                "\n" + //
                "FUZZIFY time\n" + //
                "   TERM day := sigm -1.5 6;\n" + //
                "   TERM night := sigm 1.5 6;\n" + //
                "END_FUZZIFY\n" + //
                "\n" + //
                "FUZZIFY month\n";

        for (int i = 0; i < cd.size(); i++) {
            fcl += String.format("   TERM rec%d := gauss %d %f ;\n", i, cd.getRecord(i).month, 12.0/cd.size() * 0.4);
        }

                fcl += "END_FUZZIFY\n" + //
                "\n" + //
                "DEFUZZIFY temperature\n";

        for (int i = 0; i < cd.size(); i++) {
            fcl += String.format("   TERM rec%d_day := gauss %f %f;\n", i, cd.getRecord(i).dayTemperature, 12.0/cd.size() * 4);
            fcl += String.format("   TERM rec%d_night := gauss %f %f;\n", i, cd.getRecord(i).nightTemperature, 12.0/cd.size() * 4);
        }
                fcl += "   METHOD : COG;\n" + //
                "   DEFAULT := 0;\n" + //
                "END_DEFUZZIFY\n" + //
                "\n" + //
                "DEFUZZIFY humidity\n";

        for (int i = 0; i < cd.size(); i++) {
            fcl += String.format("   TERM rec%d_humidity := gauss %f %f;\n", i, cd.getRecord(i).humidity, 12.0/ cd.size() * 0.03);
        }

                fcl += "   METHOD : COG;\n" + //
                "   DEFAULT := 0;\n" + //
                "END_DEFUZZIFY\n" + //
                "\n" + //
                "RULEBLOCK No1\n" + //
                "   ACCU : MAX;\n" + //
                "   AND : MIN;\n" + //
                "   ACT : MIN;\n" + //
                "\n";

        for (int i = 1, j = 0; j < cd.size(); i+= 2, j++) {
            fcl += String.format("   RULE %1$d : IF time IS day AND month is rec%2$d THEN temperature IS rec%2$d_day, humidity IS rec%2$d_humidity;\n", i, j) + //
                    String.format("   RULE %1$d : IF time IS night AND month is rec%2$d THEN temperature IS rec%2$d_night, humidity IS rec%2$d_humidity;\n", i+1, j);
        }
                fcl += "END_RULEBLOCK\n" + //
                "\n" + //
                "END_FUNCTION_BLOCK\n";

        fis = FIS.createFromString(fcl, true);
    }

    public void evaluate(double time, double month){
        fis.setVariable("time", time); // Set inputs
        fis.setVariable("month", month);
        fis.evaluate();
    }

    public double getDesiredHumidity(){
        return fis.getVariable("humidity").getValue();
    }

    public double getDesiredTemperature(){
        return fis.getVariable("temperature").getValue();
    }
    public void makePlot() throws InterruptedException {
        fis.setVariable("time", 0); // Set inputs
        fis.setVariable("month", 6);
        fis.evaluate();

        JDialogFis jdf = null;
        if (!JFuzzyChart.UseMockClass) jdf = new JDialogFis(fis, 800, 600);

        System.out.println("Output value:" + fis.getVariable("temperature").getValue());
        System.out.println("Output value:" + fis.getVariable("humidity").getValue());

        UtilityWriter uwu = new UtilityWriter("test", new DecimalFormat("0.00000"));
        uwu.initialize("Month", "Day", "Dawn", "Night", "Humidity");

        for (double days = 0.0, time = 0.0; days < 365; days += 1) {

            fis.getVariable("month").setValue(days/365 * 11);

            fis.getVariable("time").setValue(0);
            fis.evaluate();
            uwu.append(Math.floor(days/365 * 12)).append(fis.getVariable("temperature").getValue());

            fis.getVariable("time").setValue(6);
            fis.evaluate();
            uwu.append(fis.getVariable("temperature").getValue());

            fis.getVariable("time").setValue(12);
            fis.evaluate();
            uwu.append(fis.getVariable("temperature").getValue());
            uwu.append(fis.getVariable("humidity").getValue());

            uwu.push();

            System.out.println(String.format("Season: %2.2f\tTime:%2.2f\t=> Temperature: %2.2f ", days, time, fis.getVariable("temperature").getValue()));
            if (jdf != null) jdf.repaint();

            Thread.sleep(100);
        }
        uwu.save();

        System.out.println(fcl);
    }

    public void fclSave(String name) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(name + ".fcl"));
        bw.write(fcl);
        bw.close();
    }
}
