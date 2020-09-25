package nl.beleggingspakket.portefeuillebeheer;

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
}
