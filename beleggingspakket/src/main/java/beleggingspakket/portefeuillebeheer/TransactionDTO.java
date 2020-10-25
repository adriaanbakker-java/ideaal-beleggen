package beleggingspakket.portefeuillebeheer;


public class TransactionDTO {
    private String datum = null;
    private String txNr = null;
    private String aandeelNaam = null;
    private String aantal = null;
    private String koopVerkoop = null;
    private String bedrag = null;
    private String aandeelPrijs = null;

    public String getBedrag() {
        return bedrag;
    }

    public void setBedrag(String bedrag) {
        this.bedrag = bedrag;
    }


    public String getTxNr() {
        return txNr;
    }

    public void setTxNr(String txNr) {
        this.txNr = txNr;
    }

    public String getAandeelNaam() {
        return aandeelNaam;
    }

    public void setAandeelNaam(String aandeelNaam) {
        this.aandeelNaam = aandeelNaam;
    }

    public String getAantal() {
        return aantal;
    }

    public void setAantal(String aantal) {
        this.aantal = aantal;
    }

    public String getKoopVerkoop() {
        return koopVerkoop;
    }

    public void setKoopVerkoop(String koopVerkoop) {
        this.koopVerkoop = koopVerkoop;
    }

    public String getAandeelPrijs() {
        return aandeelPrijs;
    }

    public void setAandeelPrijs(String aandeelPrijs) {
        this.aandeelPrijs = aandeelPrijs;
    }


    public TransactionDTO(
            String aDatum,
            String txNr,
            String aandeelNaam,
            String aantal,
            String koopVerkoop,
            String aandeelPrijs,
            String bedrag) {
        this.datum = aDatum;
        this.txNr = txNr;
        this.aandeelNaam = aandeelNaam;
        this.aantal = aantal;
        this.koopVerkoop = koopVerkoop;
        this.aandeelPrijs = aandeelPrijs;
        this.bedrag = bedrag;
    }

    public TransactionDTO() {
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}