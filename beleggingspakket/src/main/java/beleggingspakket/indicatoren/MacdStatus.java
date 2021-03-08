package beleggingspakket.indicatoren;


public class MacdStatus {
    private boolean isKopen;
    private double macdWaarde;

    public MacdStatus(boolean isKopen, double macdWaarde) {
        this.isKopen = isKopen;
        this.macdWaarde = macdWaarde;
    }

    public boolean getIsKopen() {
        return isKopen;
    }

    public double getMacdWaarde() {
        return macdWaarde;
    }
}
