package utils;

import net.sourceforge.jFuzzyLogic.FIS;

public class FuzzyClimateController {
    FuzzyClimateSimulator fcs;
    public double currentTemperature;
    public double currentHumidity;

    String fcl;
    FIS fis;

    FuzzyClimateController(FuzzyClimateSimulator fcs){
        this.fcs = fcs;

        fcl = "FUNCTION_BLOCK climate\n" + //
                "\n" + //
                "VAR_INPUT\n" + //
                "   temp_difference : REAL;\n" + //
                "   hum_difference : REAL;\n" + //
                "END_VAR\n" + //
                "\n" + //
                "VAR_OUTPUT\n" + //
                "   temperature : REAL;\n" + //
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
    }


}
