module Beleggingspakket {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;

    opens beleggingspakket to javafx.fxml;
    opens beleggingspakket.portefeuillebeheer to javafx.base;
    exports beleggingspakket;
    exports beleggingspakket.portefeuillebeheer;
}