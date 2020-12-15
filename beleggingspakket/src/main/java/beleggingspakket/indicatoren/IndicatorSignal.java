package beleggingspakket.indicatoren;

import beleggingspakket.util.IDate;

public class IndicatorSignal {
    private IDate date;

    public IDate getDate() {
        return date;
    }

    public Boolean getKoopsignaal() {
        return isKoopsignaal;
    }

    private Boolean isKoopsignaal;

    public IndicatorSignal(IDate date, Boolean isKoopsignaal) {
        this.date = date;
        this.isKoopsignaal = isKoopsignaal;
    }
}
