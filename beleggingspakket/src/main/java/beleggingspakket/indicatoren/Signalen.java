package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.util.IDate;

import java.util.ArrayList;

public class Signalen {
    private ArrayList<DayPriceRecord> prices;
    private String ticker;

    public Signalen(String aTicker) throws Exception {
        ticker = aTicker;
        GetPriceHistory myGPH = new GetPriceHistory();
        prices = myGPH.getHistoricPricesFromFile(ticker);
    }

    private String listLaatsteSignaal(ArrayList<IndicatorSignal> signalen, IDate aDate) {
        IndicatorSignal lastSignal = geefLaatsteSignaal(signalen,  aDate);
        String sResult = "";
        if (lastSignal != null) {
            sResult =     "koop    ";
            if (!lastSignal.getKoopsignaal())
                sResult = "verkoop ";
            sResult += lastSignal.getDate().toString();
        }
        return sResult;
    }

    public String checkSignal(String aIndicatorNaam, IDate aDate) throws Exception {

        String sResult = "";
        Indicator indicator = null;
        if (aIndicatorNaam.equals("MACD")) {
            indicator = new MACD(prices);
        } else  if (aIndicatorNaam.equals("RSI")) {
            indicator = new RSI(prices);
        } else  if (aIndicatorNaam.equals("MOM")) {
            indicator = new Momentum(prices);
        } else  if (aIndicatorNaam.equals("OBV")) {
            indicator = new OnBalanceVolume(prices);
        } else {
            throw new Exception("Signalen.checkSignal: onbekende indicatornaam: " + aIndicatorNaam);
        }
        String sSignaal = listLaatsteSignaal(indicator.getSignals(), aDate);

//        String sResult = "";
//        if (!sSignaal.equals(""))
//            sResult += aIndicatorNaam + ":" + sSignaal;
//        return sResult;
        if (!sSignaal.equals(""))
            sResult += " " + aIndicatorNaam + ":" + sSignaal;
        return sResult;
    }

    private IndicatorSignal geefLaatsteSignaal(ArrayList<IndicatorSignal> signalen, IDate aDate) {
        IndicatorSignal lastSignal = null;

        for (IndicatorSignal sig: signalen) {
            if (sig.getDate().isSmallerEqual(aDate)) {
                lastSignal = sig;
            }
        }
        return lastSignal;
    }
}
