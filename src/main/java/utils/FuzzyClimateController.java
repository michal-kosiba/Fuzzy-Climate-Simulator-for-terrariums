package utils;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JDialogFis;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import org.antlr.runtime.RecognitionException;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

public class FuzzyClimateController {
    private FuzzyClimateSimulator fcs;
    String fcl;
    FIS fis;

    public FuzzyClimateController(FuzzyClimateSimulator fcs) throws RecognitionException {
        this.fcs = fcs;

        fcl = """
FUNCTION_BLOCK climate

VAR_INPUT
   temperature_difference : REAL;
   temperature : REAL;
END_VAR

VAR_OUTPUT
   heating : REAL;
   MAX_humidity : REAL;
END_VAR

FUZZIFY temperature_difference
   TERM below_zero := gbell 10 12 -10;
   TERM small := gauss 1.5 0.4 ;
   TERM medium_small := gauss 3 0.4 ;
   TERM medium := gauss 4.5 0.4 ;
   TERM medium_high := gauss 6 0.4 ;
   TERM high := gbell 10 12 17.5;
END_FUZZIFY

FUZZIFY temperature
   TERM very_cold := gauss 0 5;
   TERM cold := gauss 10 5;
   TERM mild := gauss 20 5;
   TERM warm := gauss 30 5;
   TERM hot := gauss 40 5;
END_FUZZIFY

DEFUZZIFY heating
   TERM none := gauss 0 0.1 ;
   TERM small := gauss 0.2 0.1 ;
   TERM medium_small := gauss 0.4 0.1 ;
   TERM medium := gauss 0.6 0.1 ;
   TERM medium_high := gauss 0.8 0.1 ;
   TERM high := gauss 1 0.1 ;
   METHOD : COG;
   DEFAULT := 0;
END_DEFUZZIFY

//DEFUZZIFY MAX_humidity
//   TERM MAX_very_cold := (0.5, 0) (5, 1) (9.5, 0);
//   TERM MAX_cold := (5, 0) (9.5, 1) (17, 0);
//   TERM MAX_mild := (9.5, 0) (17, 1) (30, 0);
//   TERM MAX_warm := (17, 0) (30, 1) (51, 0);
//   TERM MAX_hot := (30, 0) (51, 1) (71, 0);
//   METHOD : COG;
//   DEFAULT := 0;
//END_DEFUZZIFY

DEFUZZIFY MAX_humidity
   TERM MAX_very_cold := gauss 4.3 5;
   TERM MAX_cold := gauss 8.9 5;
   TERM MAX_mild := gauss 17.1 5;
   TERM MAX_warm := gauss 25 5;
   TERM MAX_hot := gauss 58.2 5;
   METHOD : COG;
   DEFAULT := 0;
END_DEFUZZIFY

RULEBLOCK No1
   ACCU : MAX;
   AND : MIN;
   ACT : MIN;

   RULE 1 : IF temperature_difference IS below_zero THEN heating IS none;
   RULE 2 : IF temperature_difference IS small THEN heating IS small;
   RULE 3 : IF temperature_difference IS medium_small THEN heating IS medium_small;
   RULE 4 : IF temperature_difference IS medium THEN heating IS medium;
   RULE 5 : IF temperature_difference IS medium_high THEN heating IS medium_high;
   RULE 5 : IF temperature_difference IS high THEN heating IS high;
   RULE 6 : IF temperature IS very_cold THEN MAX_humidity IS MAX_very_cold;
   RULE 7 : IF temperature IS cold THEN MAX_humidity IS MAX_cold;
   RULE 8 : IF temperature IS mild THEN MAX_humidity IS MAX_mild;
   RULE 9 : IF temperature IS warm THEN MAX_humidity IS MAX_warm;
   RULE 10 : IF temperature IS hot THEN MAX_humidity IS MAX_hot;
   
   
   
END_RULEBLOCK

END_FUNCTION_BLOCK""";

        fis = FIS.createFromString(fcl, true);
    }

    private double getTemperatureMeasurement(){
        /**
         * Tu ma pojawić się implementacja pobrania wartości temperatury z czujnika
         */
        System.out.print("Temperature measurement: ");
        Scanner s = new Scanner(System.in);
        return s.nextDouble();
    }
    private double getHumidityMeasurement(){
        /**
         * Tu ma pojawić się implementacja pobrania wartości wilgotności z czujnika
         */
        System.out.print("Humidity measurement: ");
        Scanner s = new Scanner(System.in);
        return s.nextDouble();
    }

    public void makePlot() throws InterruptedException {
        fis.setVariable("temperature", 0); // Set inputs
        fis.setVariable("temperature_difference", 0);
        fis.evaluate();

        JDialogFis jdf = null;
        if (!JFuzzyChart.UseMockClass) jdf = new JDialogFis(fis, 800, 600);

        System.out.println("Output value:" + fis.getVariable("temperature").getValue());
        System.out.println("Output value:" + fis.getVariable("temperature_difference").getValue());

        UtilityWriter uwu = new UtilityWriter("controller", new DecimalFormat("0.00000"));
        uwu.initialize("Temperature", "MAX humidity", "Temp. difference", "Heating");

        for (double temp = -0, tempDiff = -2.5; temp < 40; temp += 0.4, tempDiff += 0.125) {
            fis.getVariable("temperature_difference").setValue(tempDiff);
            fis.getVariable("temperature").setValue(temp);
            fis.evaluate();

            uwu.append(temp).append(fis.getVariable("MAX_humidity").getValue());
            uwu.append(tempDiff).append(fis.getVariable("heating").getValue());

            uwu.push();

            if (jdf != null) jdf.repaint();

            Thread.sleep(100);
        }
        uwu.save();

        System.out.println(fcl);
    }

    public double getHeating(LocalDateTime date){
        double month = date.getDayOfYear()/365.0 * 11;
        double time = Math.abs(12 - (date.getHour() + date.getMinute()/60.0));
        fcs.evaluate(time, month);

        double temperatureDifference = fcs.getDesiredTemperature() - getTemperatureMeasurement();

        fis.getVariable("temperature_difference").setValue(temperatureDifference);
        fis.evaluate();

        return fis.getVariable("heating").getValue();
    }

    public double getSprinkling(LocalDateTime date) {
        double month = date.getDayOfYear() / 365.0 * 11;
        double time = Math.abs(12 - (date.getHour() + date.getMinute() / 60.0));
        fcs.evaluate(time, month);

        double desiredHumidity = fcs.getDesiredHumidity();
        double currentHumidity = getHumidityMeasurement();
        double currentTemperature = getTemperatureMeasurement();

        if ((desiredHumidity - currentHumidity) > 0.02) {
            fis.getVariable("temperature").setValue(currentTemperature);
            fis.evaluate();

            double MaxHumidity = fis.getVariable("MAX_humidity").getValue();
            return (desiredHumidity - currentHumidity) * MaxHumidity;
        } else {
            return 0.0;
        }
    }
}
