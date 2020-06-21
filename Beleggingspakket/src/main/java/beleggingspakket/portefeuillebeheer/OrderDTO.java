package beleggingspakket.portefeuillebeheer;


public class OrderDTO {



    private String orderNr = null;
    private String aandeelNaam = null;

    public OrderDTO(String orderNr, String aandeelNaam, String aantal, String koopVerkoop, String orderType, String limietPrijs, String stopPrijs) {
        this.orderNr = orderNr;
        this.aandeelNaam = aandeelNaam;
        this.aantal = aantal;
        this.koopVerkoop = koopVerkoop;
        this.orderType = orderType;
        this.limietPrijs = limietPrijs;
        this.stopPrijs = stopPrijs;
    }

    private String aantal = null;
    private String koopVerkoop = null;
    private String orderType = null;
    private String limietPrijs = null;
    private String stopPrijs = null;


    public String getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(String orderNr) {
        this.orderNr = orderNr;
    }

    public String getStopPrijs() {
        return stopPrijs;
    }

    public void setStopPrijs(String stopPrijs) {
        this.stopPrijs = stopPrijs;
    }




    public String getLimietPrijs() {
        return limietPrijs;
    }

    public void setLimietPrijs(String limietPrijs) {
        this.limietPrijs = limietPrijs;
    }




    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getKoopVerkoop() {
        return koopVerkoop;
    }

    public void setKoopVerkoop(String koopVerkoop) {
        this.koopVerkoop = koopVerkoop;
    }



    public OrderDTO() {
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

}