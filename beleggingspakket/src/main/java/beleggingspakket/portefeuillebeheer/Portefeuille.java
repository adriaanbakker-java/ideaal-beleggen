package beleggingspakket.portefeuillebeheer;

//import com.vojtechruzicka.javafxweaverexample.util.Util;

import beleggingspakket.Constants;
import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;

import java.io.*;
import java.util.*;

import static java.time.LocalDateTime.now;

public class Portefeuille {


    public String getPortefeuillenaam() {
        return portefeuillenaam;
    }

    public void setPortefeuillenaam(String portefeuillenaam) {
        this.portefeuillenaam = portefeuillenaam;
    }

    private String portefeuillenaam = "nog-toekennen";

    public void setEinddatum(IDate einddatum) {
        this.einddatum = einddatum;
    }

    private IDate einddatum = new IDate(1800,1,1);
    private double rekeningTegoed = 0.0;



    private double rekeningTegoedGestort = 0.0;


    public Set<Map.Entry<String, Positie>> getPosities() {
        return posities.getPosities();
    }
    private Orders orders = new Orders();
    private Transactions transactions = new Transactions();
    private Posities posities = new Posities();

    public IDate getEinddatum() {
        return einddatum;
    }
    public void addToPositie(String instrumentnaam, int i, double laatsteKoers,
                             boolean isOptie,
                             int contractgrootte) {
        posities.addToPositie(instrumentnaam, i, laatsteKoers, isOptie, contractgrootte);
    }
    public Portefeuille() {
        rekeningTegoed = 5000.00;
        rekeningTegoedGestort = rekeningTegoed;
    }
    public double getRekeningTegoedGestort() {
        return rekeningTegoedGestort;
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







    public double getRekeningTegoed() {
        return rekeningTegoed;
    }

    public void setRekeningTegoed(double rekeningTegoed) {
        this.rekeningTegoed = rekeningTegoed;
    }


    // nb de af/bij is bij (gewone) optie 100 keer de optieprijs
    // transactiekosten worden (nog) genegeerd.
    public void AddOptieTransactie(
            boolean isVerkoop,
            String ticker,
            boolean isCall,
            double uitoefenprijs,
            int aantal,
            int expMaand,
            int expJaar,
            double optiepremie,
            int contractgrootte) {
        String sCall = "C";
        if (!isCall)
            sCall = "P";
        String optieserie = sCall + " " + ticker + " " + Util.toCurrency(uitoefenprijs) + " " +
              expMaand + "-" + expJaar;

        double transactieBedrag = optiepremie * contractgrootte;

        Transaction t = new Transaction(
                einddatum,
                optieserie,
                isVerkoop,
                aantal,
                Util.toLocalDateTime(einddatum),
                optiepremie,
                true,
                contractgrootte
        );

        transactions.add(t);

        addToPositie(t);
        if (isVerkoop) {
            this.rekeningTegoed += transactieBedrag;
        } else {
            this.rekeningTegoed -= transactieBedrag;
        }
    }

    private void addToPositie(Transaction optieTransactie) {
        posities.addToPositie(optieTransactie);
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

                /* Now we know that the file exist etcetera we can now safely assign the name */
                this.portefeuillenaam = pfNaam;
                orders.deleteOrders();
                transactions.deleteTransactions();
                posities.deletePositions();

                while ((line = br.readLine()) != null) {
                    System.out.println("gelezen: " + line);
                    if (line.contains("ORDER")) {
                        orders.addOrderLineFromDisk(line);
                    }
                    if (line.contains("TRANSACTION")) {
                        transactions.addTransactionLineFromDisk(line);
                    }
                    if (line.contains("POSITIE")) {
                        posities.addPositionLineFromDisk(line);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Portefeuille haalop(): " + e.getMessage());
        }
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

                // exporteer de einddatum
                String laatsteDatum = this.einddatum.toString();
                writer.write(laatsteDatum +  "\n");


                // exporteer het rekeningtegoed
                String currentLine = Util.toCurrency(rekeningTegoed);
                writer.write(currentLine +  "\n");

                // ... sla posities op
                orders.slaOrdersOpNaarDisk(writer);
                transactions.slaTransactionOpNaarDisk(writer);
                posities.slaPositiesOpNaarDisk(writer);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}

