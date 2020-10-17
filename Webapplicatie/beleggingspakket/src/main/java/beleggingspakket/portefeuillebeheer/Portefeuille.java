package beleggingspakket.portefeuillebeheer;

//import com.vojtechruzicka.javafxweaverexample.util.Util;

import beleggingspakket.Constants;
import beleggingspakket.util.Util;
import javafx.event.ActionEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Portefeuille {


    private double rekeningTegoed = 10000.00;


    public HashMap<String, Integer> getPosities() {
        return posities;
    }
    private Orders orders = new Orders();
    private Transactions transactions = new Transactions();
    private HashMap<String, Integer> posities = new HashMap<>();

    public Portefeuille() {
    }

    public void addToPositie(String ticker, int aantal) {
        if (!posities.containsKey(ticker)) {
            posities.put(ticker, aantal);
        } else {
            Integer totAantal = posities.get(ticker) + aantal;
            posities.replace(ticker, totAantal);
        }
    }

    public PositieDTO geefPositieDTO(String aTicker, double aKoers) {
        int aantal = posities.get(aTicker).intValue();
        double waarde = aKoers * aantal;
        PositieDTO dto = new PositieDTO(
                aTicker,
                Integer.toString(aantal),
                Util.toCurrency(aKoers),
                Util.toCurrency(waarde));
        return dto;
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
            }
            
            try (	FileWriter writer = new FileWriter(myFile);
                     BufferedWriter bw = new BufferedWriter(writer)) {
                // exporteer het rekeningtegoed
                String currentLine = Util.toCurrency(rekeningTegoed);
                writer.write(currentLine +  "\n");

                // ... sla posities op
                orders.slaOp(writer);
                transactions.slaOp(writer);
            }



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void haalOp () {
        //... haal rekeningtegoed op
        //... haal posities op
        orders.haalOp();
        transactions.haalOp();
        System.out.println("Portefeuille van schijf halen");
    }

    public double getRekeningTegoed() {
        return rekeningTegoed;
    }

    public void setRekeningTegoed(double rekeningTegoed) {
        this.rekeningTegoed = rekeningTegoed;
    }


}
