package beleggingspakket.portefeuillebeheer;

import java.util.ArrayList;

public class Transactions {
    // for now it is possible to mess around a little,
    // in future versions this field should be copied in the getter
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public void slaOp() {
    }

    public void haalOp() {
    }
}
