package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;

public class IndicatorSignal {
    private IDate date;
    private DayPriceRecord dpr;
    private boolean isKoopsignaal;
    private int indexKoersreeks;

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


    public IndicatorSignal(IDate date, Boolean isKoopsignaal, DayPriceRecord dpr, int indexKoersreeks) {
        this.date = date;
        this.isKoopsignaal = isKoopsignaal;
        this.dpr = dpr;
        this.indexKoersreeks = indexKoersreeks;
    }

    public String toString() {
        String result = (isKoopsignaal ? "Koopsignaal:": "Verkoopsignaal");
        result += dpr.toString();

        return result;
    }
}
