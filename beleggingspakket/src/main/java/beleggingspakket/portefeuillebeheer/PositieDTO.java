package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

public class PositieDTO {




    private String volgnr;
    private String aandeelNaam;
    private String aantal;
    private String koers;
    private String waarde;
    private String gak;
    private String gerealiseerd;
    private String ongerealiseerd;

    public String getVolgnr() {
        return volgnr;
    }

    public String getGerealiseerd() {
        return gerealiseerd;
    }

    public String getOngerealiseerd() {
        return ongerealiseerd;
    }

    public PositieDTO(Positie pos) {
        volgnr = Integer.toString(pos.getSeqNr());
        aandeelNaam = pos.getInstrumentnaam();
        aantal = Integer.toString(pos.getPOS());
        koers = Util.toCurrency(pos.getHuidigeKoers());
        double dWaarde = pos.geefHuidigeWaarde();
        waarde = Util.toCurrency(dWaarde);
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

    public PositieDTO(String aandeelNaam,
                      String aantal,
                      String koers,
                      String waarde,
                      String volgnr) {
        this.volgnr = volgnr;
        this.aandeelNaam = aandeelNaam;
        this.aantal = aantal;
        this.koers = koers;
        this.waarde = waarde;
    }



}
