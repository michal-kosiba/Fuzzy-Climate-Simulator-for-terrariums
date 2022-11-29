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
                "   season : REAL;\n" + //
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
            fcl += String.format("   TERM rec%d := gbell 2 4 %d;\n", i, cd.getRecord(i).month);
        }

                fcl += "END_FUZZIFY\n" + //
                "\n" + //
                "DEFUZZIFY temperature\n";

        for (int i = 0; i < cd.size(); i++) {
            fcl += String.format("   TERM rec%d_day := gbell 2 4 %f;\n", i, cd.getRecord(i).dayTemperature);
            fcl += String.format("   TERM rec%d_night := gbell 2 4 %f;\n", i, cd.getRecord(i).nightTemperature);
        }
                fcl += "   METHOD : COG;\n" + //
                "   DEFAULT := 0;\n" + //
                "END_DEFUZZIFY\n" + //
                "\n" + //
                "DEFUZZIFY humidity\n";

        for (int i = 0; i < cd.size(); i++) {
            fcl += String.format("   TERM rec%d_humidity := gauss %f 0.1;\n", i, cd.getRecord(i).humidity);
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

        // Create a plot
        JDialogFis jdf = null;
        if (!JFuzzyChart.UseMockClass) jdf = new JDialogFis(fis, 800, 600);

        // Show output variable
        System.out.println("Output value:" + fis.getVariable("temperature").getValue());
        System.out.println("Output value:" + fis.getVariable("humidity").getValue());

        UtilityWriter uwu = new UtilityWriter("test", new DecimalFormat("0.00"));
        uwu.initialize("Month", "Day", "Night", "Humidity");

        // Set different values for 'food' and 'service'. Evaluate the system and show variables
        for (double season = 0.0, time = 0.0; season < 12; season += 1) {
            // Evaluate system using these parameters
            fis.getVariable("month").setValue(Math.abs(season));
            fis.getVariable("time").setValue(0);
            fis.evaluate();
            uwu.append(season).append(fis.getVariable("temperature").getValue());

            fis.getVariable("time").setValue(12);
            fis.evaluate();
            uwu.append(fis.getVariable("temperature").getValue());
            uwu.append(fis.getVariable("humidity").getValue());

            uwu.push();

            // Print result & update plot
            System.out.println(String.format("Season: %2.2f\tTime:%2.2f\t=> Temperature: %2.2f ", season, time, fis.getVariable("temperature").getValue()));
            if (jdf != null) jdf.repaint();

            // Small delay
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
