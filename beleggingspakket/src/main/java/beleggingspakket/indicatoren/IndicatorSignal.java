package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;

public class IndicatorSignal {
    private IDate date;
    private DayPriceRecord dpr;
    private boolean isKoopsignaal;
    private int indexKoersreeks;
    private double indicatorWaarde;


    public double getIndicatorWaarde() {
        return indicatorWaarde;
    }

    public void setIndicatorWaarde(double indicatorWaarde) {
        this.indicatorWaarde = indicatorWaarde;
    }


    public int getIndexKoersreeks() {
        return indexKoersreeks;
    }

    public DayPriceRecord getDpr() {
        return dpr;
    }

    public IDate getDate() {
        return date;
    }

    public Boolean getKoopsignaal() {
        return isKoopsignaal;
    }


    public IndicatorSignal(
            IDate date,
            Boolean isKoopsignaal,
            DayPriceRecord dpr,
            int indexKoersreeks) {
        this.date = date;
        this.isKoopsignaal = isKoopsignaal;
        this.dpr = dpr;
        this.indexKoersreeks = indexKoersreeks;
        this.indicatorWaarde = 0;
    }


    public String toString() {
        String result = (isKoopsignaal ? "Koopsignaal:": "Verkoopsignaal");
        result += dpr.toString();

        return result;
    }
}
