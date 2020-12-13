package beleggingspakket.indicatoren;

import beleggingspakket.util.IDate;

public class MacdSignaal {
    private IDate date;

    public IDate getDate() {
        return date;
    }

    public Boolean getKoopsignaal() {
        return isKoopsignaal;
    }

    private Boolean isKoopsignaal;

    public MacdSignaal(IDate date, Boolean isKoopsignaal) {
        this.date = date;
        this.isKoopsignaal = isKoopsignaal;
    }
}
