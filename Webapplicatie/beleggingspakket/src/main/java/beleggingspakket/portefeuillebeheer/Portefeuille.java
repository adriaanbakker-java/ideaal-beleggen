package beleggingspakket.portefeuillebeheer;

//import com.vojtechruzicka.javafxweaverexample.util.Util;

import beleggingspakket.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class Portefeuille {
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
}
