package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;

import java.util.ArrayList;

public abstract class Indicator {

    protected final boolean corrBehaviour;
    ArrayList<DayPriceRecord> myClosingPrices;
    ArrayList<IndicatorSignal> signalen = null;

    public Indicator(ArrayList<DayPriceRecord> aDayPriceArray, boolean aCorr) throws Exception {
        corrBehaviour = aCorr;
        initSpecifics();
        myClosingPrices = aDayPriceArray;
        calcIndicator();
        calcSignalLine();
        calcSignals();
    }

    public Indicator(ArrayList<DayPriceRecord> aDayPriceArray) throws Exception {
        this(aDayPriceArray, false);
    }

    public abstract ArrayList<Double> getIndicatorLine();

    public ArrayList<IndicatorSignal> getSignals() {
        return signalen;
    }

    // Because field initializations of the specific indicator only take place AFTER the object
    // is created via super() we need to initialize MACD fields from within the Indicator superclass
    protected abstract void initSpecifics();

    protected abstract void calcSignals();

    protected abstract void calcSignalLine() throws Exception;

    protected abstract void calcIndicator();


}
