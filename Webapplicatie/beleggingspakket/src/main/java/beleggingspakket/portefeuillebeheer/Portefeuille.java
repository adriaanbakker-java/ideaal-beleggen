package beleggingspakket.portefeuillebeheer;

//import com.vojtechruzicka.javafxweaverexample.util.Util;

import beleggingspakket.Constants;
import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Portefeuille {




    private IDate einddatum = new IDate(1800,1,1);
    private double rekeningTegoed = 10000.00;


    public Set<Map.Entry<String, Integer>> getPosities() {
        return posities.getPosities();
    }
    private Orders orders = new Orders();
    private Transactions transactions = new Transactions();
    private Posities posities = new Posities();

    public IDate getEinddatum() {
        return einddatum;
    }
    public void addToPositie(String ticker, int i) {
        posities.addToPositie(ticker, i);
    }
    public Portefeuille() {
    }

    public ArrayList<Order> getOrders() {
        return orders.getOrders();
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void removeOrderById(int iVolgnr) {
        orders.getOrders().remove(iVolgnr);
    }

    public void deleteOrder(Order order) {
        orders.deleteOrder(order);
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions.getTransactions();
    }

    public void addTransaction(Transaction transaction) {
        transactions.getTransactions().add(transaction);
    }




    public void slaOp() {
        System.out.println("Portefeuille opslaan");
        String folder = Constants.getPFfolder();
        String filenamePF = Constants.getFilenamePF();

        try {
            String filename = Constants.getPFfolder()  + filenamePF;
            // check if file exists, otherwise create
            File myFile = new File(filename);
            if (!myFile.exists()) {
                myFile.createNewFile();
            } else {
                myFile.delete();
            }
            
            try (	FileWriter writer = new FileWriter(myFile);
                     BufferedWriter bw = new BufferedWriter(writer)) {
                // exporteer het rekeningtegoed
                String currentLine = Util.toCurrency(rekeningTegoed);
                writer.write(currentLine +  "\n");

                // ... sla posities op
                orders.slaOp(writer);
                transactions.slaOp(writer);
                posities.slaOp(writer);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void haalOp(String pfNaam) {
        System.out.println("Portefeuille " + pfNaam + " van schijf halen");
        String folder = Constants.getPFfolder();
        String filenamePF = pfNaam + ".csv";
        try {
            String filename = Constants.getPFfolder()  + filenamePF;
            // check if file exists, otherwise create
            File myFile = new File(filename);
            if (!myFile.exists()) {
                throw new Exception("bestand niet gevonden:" + filename);
            }

            String currentLine = "";

            File file = new File(filename);
            InputStream inputStream = new FileInputStream(file);
            StringBuilder resultStringBuilder = new StringBuilder();
            try (BufferedReader br
                         = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                if ((line = br.readLine()) != null) {
                    System.out.println("gelezen: " + line);

                    einddatum = Util.toIDate(line);
                } else
                    throw new Exception("onverwacht einde bestand bij lezen einddatum portefeuille");
                if ((line = br.readLine()) != null) {
                    System.out.println("gelezen: " + line);
                    rekeningTegoed = Util.toDouble(line);
                } else
                    throw new Exception("onverwacht einde bestand bij lezen rekeningtegoed portefeuille");
                orders.deleteOrders();
                transactions.deleteTransactions();

                while ((line = br.readLine()) != null) {
                    System.out.println("gelezen: " + line);
                    if (line.contains("ORDER")) {
                        orders.addOrderLineFromDisk(line);
                    }
                    if (line.contains("TRANSACTION")) {
                        transactions.addTransactionLineFromDisk(line);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Portefeuille haalop(): " + e.getMessage());
        }
    }

    public double getRekeningTegoed() {
        return rekeningTegoed;
    }

    public void setRekeningTegoed(double rekeningTegoed) {
        this.rekeningTegoed = rekeningTegoed;
    }


}
