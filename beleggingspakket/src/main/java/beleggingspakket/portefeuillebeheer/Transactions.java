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


    // TRANSACTION, <nr>, <instrumentnaam>, <instrumentsoort>, <>, <is verkoop>, <orderdate>,
    //              <aantal>, <koers>
    // bijv voor aandeel ASML
    //    TRANSACTION, 1003, ASML ,AANDEEL, 15-10-2020,true,10,329.12
    // bijv voor optie ASML Call feb 2022 met uitoefenprijs 330
    //    TRANSACTION, 1004, ASML C 02-2022 330.00, OPTIE, 15-10-2020, true, 10, 329.12
    public void addTransactionLineFromDisk(String line) throws Exception {
        try {
            System.out.println("ophalen transaction line:" + line);
            String[] transactionelements = line.split(",");
            String sTicker = transactionelements[2];
            boolean isOptietransactie = (transactionelements[3].equals("OPTIE"));
            IDate iDate = Util.toIDate(transactionelements[4].trim());
            boolean isSalesOrder = false;
            if (transactionelements[5].equals("true"))
                isSalesOrder = true;
            int nrOfShares = Integer.parseInt(transactionelements[6]);
            double koers = Util.toDouble(transactionelements[7]);

            Transaction transaction = new Transaction(
                    iDate,
                    sTicker,
                    isSalesOrder,
                    nrOfShares,
                    Util.toLocalDateTime(iDate),
                    koers,
                    isOptietransactie);
            transactions.add(transaction);
        } catch (Exception e) {
            throw new Exception("addTransactionLineFromDisk:" + line + ":" + e.getLocalizedMessage());
        }
    }

    // delete all transactions
    public void deleteTransactions() {
        transactions.clear();
    }
}
