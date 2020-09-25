package nl.beleggingspakket.portefeuillebeheer;

public class PositieDTO {
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

    private String aandeelNaam;
    private String aantal;
    private String koers;
    private String waarde;

    public PositieDTO(String aandeelNaam, String aantal, String koers, String waarde) {
        this.aandeelNaam = aandeelNaam;
        this.aantal = aantal;
        this.koers = koers;
        this.waarde = waarde;
    }



}
