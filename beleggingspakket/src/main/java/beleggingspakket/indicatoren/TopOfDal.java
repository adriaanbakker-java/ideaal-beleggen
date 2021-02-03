package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;

public class TopOfDal {
    private final boolean isTop;
    private final int index;
    private final DayPriceRecord dpr;

    public DayPriceRecord getDpr() {
        return dpr;
    }

    public boolean isTop() {
        return isTop;
    }

    public int getIndex() {
        return index;
    }


    TopOfDal(boolean aIsTop, DayPriceRecord aDpr, int aIndex) {
        isTop = aIsTop;
        dpr = aDpr;
        index = aIndex;
    }

    public String toString() {
        return (isTop? "Top:" : "Dal:")  + dpr.toString();
    }
}
