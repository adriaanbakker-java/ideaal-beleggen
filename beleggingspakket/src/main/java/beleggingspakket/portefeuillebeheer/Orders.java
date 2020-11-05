package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Orders {
    // for now it is possible to mess around a little,
    // in future versions this field should be copied in the getter
    public ArrayList<Order> getOrders() {
        return orders;
    }

    private ArrayList<Order> orders = new ArrayList<>();

    public void add(Order order) {
        orders.add(order);
    }

    public void deleteOrder(Order order) {
        int foundIndex = -1;
        int i = 0;
        for (Order o: getOrders()) {
            if (o.getOrderNr() == order.getOrderNr()) {
                foundIndex = i;
            }
            i++;
        }
        if (foundIndex >= 0) {
            getOrders().remove(foundIndex);
        }
    }

    public void slaOrdersOpNaarDisk(FileWriter writer) throws Exception {
        System.out.println("orders ->sla de orders op");
        try {
            for (Order order: orders) {
                String sOrder = "ORDER," + order.toString();
                writer.write(sOrder + "\n");
            }
        } catch (Exception e) {
            throw new Exception("Exception in slaOp() order:" + e.getLocalizedMessage());
        }
    }



    public void addOrderLineFromDisk(String line) throws Exception {
        Order order = new Order(line);
        orders.add(order);
    }

    // delete all orders
    public void deleteOrders() {
        orders.clear();
    }
}
