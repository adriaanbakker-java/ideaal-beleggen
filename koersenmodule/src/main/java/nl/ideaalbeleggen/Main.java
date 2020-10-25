package nl.ideaalbeleggen;


import java.util.Set;
import java.util.logging.Level;

public class Main {
    static String sUrl = "https://www.iex.nl/Aandeel-Koers/613007/ADYEN-NV/historische-koersen.aspx?maand=1";
    static String expectedTitle = "ADYEN NV » Historische koersen (Aandeel) | IEX.nl";

    public static void main(String[] args) {
        System.out.println("Koersenmodule: java -jar Koersenmodule<..>.jar [endYear endMonth]");
        System.out.println("ververst koersen in " + Constants.getPricefolder());
        System.out.println("tot aan huidige datum of (indien gegeven) endYear endMonth");
        System.out.println("Door de week zal de laatste beursdag ontbreken wegens nog niet aanwezig op iex");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");


        MainController.logInTextArea("koersen verversen");
        int endYear = -1;
        int endMonth = -1;
        if (args.length == 2) {
            System.out.println("arguments given: endYear=" + args[0] + " endMonth=" + args[1]);
            endYear = Integer.parseInt(args[0]);
            endMonth = Integer.parseInt(args[1]);
        } else {
            System.out.println("Geen argumenten gegeven (eindJaar en eindMaand)" );
        }

        System.out.println("Webcomponent aanmaken, duurt ongeveer 20 seconden...");
        System.out.println("svp de volgende waarschuwingen negeren!");
        GetPriceHistory gph = new GetPriceHistory();


        try {
            Set<String> tickerSet = gph.getTickers();

            for (String ticker1 : tickerSet) {
                if (ticker1.equals("hier ticket")) {
                    System.out.println("Skip" + ticker1+"  for now");

                } else {
                    MainController.LocalLogging localLogging = new MainController.LocalLogging();
                    gph.updatePriceHistory(
                            ticker1,
                            Constants.startYear, 1,
                            endYear, endMonth,
                            localLogging);
                }

            }
        } catch (Exception e) {
            MainController.logInTextArea(e.getLocalizedMessage());
            MainController.logInTextArea(e.getLocalizedMessage());
        }


        MainController.logInTextArea( "koersen ververst");
        gph.closeDriver();
    }

}
