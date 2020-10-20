package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


    public void addTransactionLineFromDisk(String line) throws Exception {
        System.out.println("ophalen transaction line:" + line);
        String[] transactionelements = line.split(",");
        String sTicker = transactionelements[2];
        LocalDateTime dDate = Util.toLocalDateTime(transactionelements[3]);
        IDate iDate = new IDate(2020, 10, 20);   // transactionelements[4] is
        int nrOfShares = Integer.parseInt(transactionelements[5]);
        double sharePrice = Util.toDouble(transactionelements[6]);
        boolean isSalesOrder = false;
        if (transactionelements[4].equals("true"))
            isSalesOrder = true;

        Transaction transaction = new Transaction(iDate, sTicker, isSalesOrder, nrOfShares, dDate, sharePrice);
        transactions.add(transaction);
    }

    // delete all transactions
    public void deleteTransactions() {
        transactions.clear();
    }
}
