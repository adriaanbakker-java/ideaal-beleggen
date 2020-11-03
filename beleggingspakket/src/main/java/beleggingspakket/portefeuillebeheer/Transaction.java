package beleggingspakket.portefeuillebeheer;


import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;

import java.time.LocalDateTime;

public class Transaction {

    private IDate executionDate;
    private IDate orderdate;
    private String Instrumentname;  // ticker in geval van aandeel, anders optieserie
                                    // in de vorm van bijv "PHI C 40.0 0920" voor de september
                                    // call voor Philips met uitoefenprijs 40.0 expiratie in sept 2020
    private int txNumber;
    private boolean isSaleOrder;
    private int nrOfItems;
    private double price;



    private boolean isOptietransactie;
    static int transactionSeq = 1000;

    public Transaction(
            IDate aDate,   // execution date
            String instrumentname,
            boolean isSaleOrder,
            int nrOfItems,
            LocalDateTime orderDate,   // order date
            double sharePrice,
            boolean isOptietransactie) {
        this.executionDate = aDate;
        this.Instrumentname = instrumentname;
        this.txNumber = transactionSeq++;
        this.isSaleOrder = isSaleOrder;
        this.nrOfItems = nrOfItems;
        this.price = sharePrice;
        this.isOptietransactie = isOptietransactie;
    }

    public boolean getIsOptieTransactie() {
        return isOptietransactie;
    }

    public String getInstrumentname() {
        return Instrumentname;
    }

    public void setInstrumentname(String instrumentname) {
        this.Instrumentname = instrumentname;
    }

    public static int getTransactionSeq() {
        return transactionSeq;
    }

    public static void setTransactionSeq(int transactionSeq) {
        Transaction.transactionSeq = transactionSeq;
    }

    public int getTxNumber() {
        return txNumber;
    }

    public void setTxNumber(int txNumber) {
        this.txNumber = txNumber;
    }

    public boolean isSaleOrder() {
        return isSaleOrder;
    }

    public void setSaleOrder(boolean saleOrder) {
        isSaleOrder = saleOrder;
    }

    public int getNrOfItems() {
        return nrOfItems;
    }

    public void setNrOfItems(int nrOfItems) {
        this.nrOfItems = nrOfItems;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public IDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(IDate executionDate) {
        this.executionDate = executionDate;
    }
    



    public String toString() {
        String soort = "AANDEEL";
        if (this.isOptietransactie)
            soort = "OPTIE";
        return getTxNumber() + "," +
                getInstrumentname() + "," +
                soort + "," +
                executionDate + "," +
                isSaleOrder + "," +
                nrOfItems + "," +
                Util.toCurrency(getPrice());
    }
}
