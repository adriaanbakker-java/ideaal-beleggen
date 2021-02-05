package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;

public class TopOfDal {
    private final boolean isTop;
    private final int index;
    private final DayPriceRecord dpr;
    private final int orde;

    public DayPriceRecord getDpr() {
        return dpr;
    }

    public boolean isTop() {
        return isTop;
    }

    public int getIndex() {
        return index;
    }


    TopOfDal(boolean aIsTop, DayPriceRecord aDpr, int aIndex, int aOrde) {
        isTop = aIsTop;
        dpr = aDpr;
        index = aIndex;
        orde = aOrde;
    }

    public String toString() {
        String result = (isTop? "Top:" : "Dal:")  + dpr.toString();
        result += "  orde =" + orde;
        return result;
    }

    public int getOrde() {
        return orde;
    }
}
