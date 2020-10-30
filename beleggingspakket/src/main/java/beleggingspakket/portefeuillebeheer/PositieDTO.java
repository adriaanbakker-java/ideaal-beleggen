package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

public class PositieDTO {

    private String aandeelNaam;
    private String aantal;
    private String koers;
    private String waarde;
    private String gak;
    private String gerealiseerd;
    private String ongerealiseerd;

/*
    aandeel,
    sAantal,
            Util.toCurrency(dKoers),
            Util.toCurrency(dWaarde));
*/

    public String getGerealiseerd() {
        return gerealiseerd;
    }

    public String getOngerealiseerd() {
        return ongerealiseerd;
    }

    public PositieDTO(Positie pos) {
        aandeelNaam = pos.getInstrumentnaam();
        aantal = Integer.toString(pos.getPOS());
        koers = Util.toCurrency(pos.getHuidigeKoers());
        waarde = Util.toCurrency(pos.getHuidigeKoers() * pos.getPOS());
        gak = Util.toCurrency(pos.getGAK());
        gerealiseerd = Util.toCurrency(pos.getGWV());
        ongerealiseerd = Util.toCurrency(pos.getOWV());
    }

    public String getAandeelNaam() {
        return aandeelNaam;
    }

    public String getAantal() {
        return aantal;
    }

    public String getKoers() {
        return koers;
    }

    public String getWaarde() {
        return waarde;
    }

    public String getGak() {
        return gak;
    }

    public PositieDTO(String aandeelNaam, String aantal, String koers, String waarde) {
        this.aandeelNaam = aandeelNaam;
        this.aantal = aantal;
        this.koers = koers;
        this.waarde = waarde;
    }



}
