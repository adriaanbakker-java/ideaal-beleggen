package beleggingspakket.portefeuillebeheer;


import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;

import java.time.LocalDateTime;

public class Transaction {


    private IDate executionDate;
    private String ticker;
    private int txNumber;
    private boolean isSaleOrder;
    private int nrOfShares;
    private double sharePrice;

    static int transactionSeq = 1000;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
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

    public int getNrOfShares() {
        return nrOfShares;
    }

    public void setNrOfShares(int nrOfShares) {
        this.nrOfShares = nrOfShares;
    }


    public double getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(double sharePrice) {
        this.sharePrice = sharePrice;
    }

    public IDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(IDate executionDate) {
        this.executionDate = executionDate;
    }
    
    public Transaction(
                        IDate aDate,
                        String ticker,
                       boolean isSaleOrder,
                       int nrOfShares,
                       LocalDateTime orderDate,
                       Double sharePrice) {
        this.executionDate = aDate;
        this.ticker = ticker;
        this.txNumber = transactionSeq++;
        this.isSaleOrder = isSaleOrder;
        this.nrOfShares = nrOfShares;
        this.sharePrice = sharePrice;
    }


    public String toString() {
        return getTxNumber() + "," +
                getTicker() + "," +
                executionDate + "," +
                isSaleOrder + "," +
                nrOfShares + "," +
                Util.toCurrency(getSharePrice());
    }
}
