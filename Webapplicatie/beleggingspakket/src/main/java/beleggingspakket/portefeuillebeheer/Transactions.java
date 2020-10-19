package beleggingspakket.portefeuillebeheer;

import java.io.FileWriter;
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

    public void slaOp(FileWriter writer) throws Exception {
        System.out.println("transactions ->sla de transacties op");
        try {
            for (Transaction t:transactions) {
                String sTransaction = t.toString();
                writer.write("TRANSACTION," + sTransaction + "\n");
            }
        } catch (Exception e) {
            throw new Exception("Exception in slaOp() transactie:" + e.getLocalizedMessage());
        }
    }


    public void addTransactionLineFromDisk(String line) {
        System.out.println("ophalen transaction line:" + line);
    }

    // delete all transactions
    public void deleteTransactions() {
        transactions.clear();
    }
}
