package beleggingspakket;

import beleggingspakket.portefeuillebeheer.*;
import beleggingspakket.Koersen.DayPriceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import beleggingspakket.Koersen.BufferedPrices;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.portefeuillebeheer.*;
import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;

public class PortefeuillebeheerController implements Initializable {
    public Main main;
    ObservableList aandelenlijst = FXCollections.observableArrayList();

    // for now, just for the ticker set to be able to order
    private GetPriceHistory getPriceHistory = new GetPriceHistory();
    private double rekeningTegoed = 10000.00;
    private Orders orders = new Orders();
    private Transactions transactions = new Transactions();
    private Portefeuille portefeuille = new Portefeuille();

    private TableView tableViewOrders = new TableView();
    private TableView tableViewTx = new TableView();
    private TableView tableViewPortefeuille = new TableView();
    private BufferedPrices bufferedPrices = new BufferedPrices();

    @FXML
    private Label lblMessage;

    @FXML
    private VBox vboxOrders;


    @FXML
    private TextField txtAantal;

    @FXML
    private TextField txtRekeningtegoed;


    @FXML
    private TextField txtLimietprijs;

    @FXML
    private TextField txtStopprijs;

    @FXML
    private TextField txtOrdernr;

    @FXML
    private RadioButton rbtKoop;

    @FXML
    private RadioButton rbtVerkoop;


    @FXML
    private RadioButton rbtBestens;

    @FXML
    private RadioButton rbtLimiet;

    @FXML
    private RadioButton rbtStopLimit;

    @FXML
    private RadioButton rbtStopLoss;


    @FXML
    private ChoiceBox<String> selecteerAandeel;

    TableColumn<String, OrderDTO> createColumn(String displayName, String name) {
        TableColumn<String, OrderDTO> column1 = new TableColumn<>(displayName);
        column1.setCellValueFactory(new PropertyValueFactory<>(name));
        return column1;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtRekeningtegoed.setText(Util.toCurrency(rekeningTegoed));

        final ToggleGroup groupOrderType = new ToggleGroup();
        rbtLimiet.setToggleGroup(groupOrderType);
        rbtBestens.setToggleGroup(groupOrderType);
        rbtStopLimit.setToggleGroup(groupOrderType);
        rbtStopLoss.setToggleGroup(groupOrderType);

        rbtLimiet.setSelected(true);
        txtAantal.setText("20");
        txtStopprijs.setVisible(false);

        final ToggleGroup groupKoopVerkoop = new ToggleGroup();
        rbtKoop.setToggleGroup(groupKoopVerkoop);
        rbtVerkoop.setToggleGroup(groupKoopVerkoop);
        rbtKoop.setSelected(true);

        aandelenlijst.removeAll(selecteerAandeel);

        ArrayList<String> tickerSet = new ArrayList<>();
        tickerSet.addAll(getPriceHistory.getTickers());
        Collections.sort(tickerSet);

        for (String ticker1 : tickerSet) {
            aandelenlijst.add(ticker1);
        }

        selecteerAandeel.getItems().addAll(aandelenlijst);

        tableViewOrders.getColumns().add(createColumn("Ordernr", "orderNr"));
        tableViewOrders.getColumns().add(createColumn("Aandeel", "aandeelNaam"));
        tableViewOrders.getColumns().add(createColumn("Aantal", "aantal"));
        tableViewOrders.getColumns().add(createColumn("Koop/Verkoop", "koopVerkoop"));
        tableViewOrders.getColumns().add(createColumn("Type", "orderType"));
        tableViewOrders.getColumns().add(createColumn("Limiet", "limietPrijs"));
        tableViewOrders.getColumns().add(createColumn("Stopprijs", "stopPrijs"));
        tableViewOrders.setPrefHeight(10000);


        tableViewPortefeuille.getColumns().add(createColumn("Aandeel", "aandeelNaam"));
        tableViewPortefeuille.getColumns().add(createColumn("Aantal", "aantal"));
        tableViewPortefeuille.getColumns().add(createColumn("Koers", "koers"));
        tableViewPortefeuille.getColumns().add(createColumn("Waarde", "waarde"));
        tableViewPortefeuille.setPrefHeight(10000);


        tableViewTx.getColumns().add(createColumn("Datum", "datum"));
        tableViewTx.getColumns().add(createColumn("TxNr", "txNr"));
        tableViewTx.getColumns().add(createColumn("Aandeel", "aandeelNaam"));
        tableViewTx.getColumns().add(createColumn("Aantal", "aantal"));
        tableViewTx.getColumns().add(createColumn("Koop/Verkoop", "koopVerkoop"));
        tableViewTx.getColumns().add(createColumn("Prijs", "aandeelPrijs"));
        tableViewTx.getColumns().add(createColumn("Bedrag", "bedrag"));

        tableViewTx.setPrefHeight(10000);
        // tableView.getItems().add(new OrderDTO("Jane", "Deer"));

        vboxOrders.getChildren().add(tableViewOrders);
    }


    public void toonOrders() {
        System.out.println("toon orders in scherm");
        vboxOrders.getChildren().clear();
        vboxOrders.getChildren().add(tableViewOrders);
    }

    public void toonPortefeuille() {
        System.out.println("toon portefeuille in scherm");
        vboxOrders.getChildren().clear();
        vboxOrders.getChildren().add(tableViewPortefeuille);
    }

    public void toonTransacties() {
        System.out.println("toon transacties in scherm");
        vboxOrders.getChildren().clear();
        vboxOrders.getChildren().add(tableViewTx);
    }

    public void klikOrderControl() {
        showMessage("");
        if (rbtBestens.isSelected()) {
            txtStopprijs.setVisible(false);
            txtLimietprijs.setVisible(false);
        }
        if (rbtLimiet.isSelected()) {
            txtStopprijs.setVisible(false);
            txtLimietprijs.setVisible(true);
        }
        if (rbtStopLimit.isSelected()) {
            txtStopprijs.setVisible(true);
            txtLimietprijs.setVisible(true);
        }
        if (rbtStopLoss.isSelected()) {
            txtStopprijs.setVisible(true);
            txtLimietprijs.setVisible(false);
        }

    }

    public void enterOrder() {
        String gekozenAandeel = null;
        Ordertype ordertype = null;
        double stopprijs = 0.0;
        double limietprijs = 0.0;
        int nrStocks = 0;

        /* Order(String aTicker,-
          Date aDate,
          Ordertype aOrderType,
          boolean aIsSaleOrder,
          double aStopprice,
          double aLimitprice,
          int aNrOfShares) throws Exception {
        */
        gekozenAandeel = selecteerAandeel.getValue();
        if (rbtStopLimit.isSelected())
            ordertype = Ordertype.STOPLIMIT;
        if (rbtBestens.isSelected())
            ordertype = Ordertype.MARKET;
        if (rbtLimiet.isSelected())
            ordertype = Ordertype.LIMIT;
        if (rbtStopLoss.isSelected()) {
            ordertype = Ordertype.STOPLOSS;
        }

        if (txtStopprijs.isVisible()) {
            try {
                String sStopprijs = txtStopprijs.getText();
                stopprijs = Double.parseDouble(sStopprijs);
            } catch (Exception e) {
                showMessage("Foute invoer bij stopprijs");
                return;
            }
        }

        if (txtLimietprijs.isVisible()) {
            try {
                String sLimietprijs = txtLimietprijs.getText();
                limietprijs = Double.parseDouble(sLimietprijs);
            } catch (Exception e) {
                showMessage("Foute invoer bij limietprijs");
                return;
            }
        }

        try {
            nrStocks = Integer.parseInt(txtAantal.getText());
        } catch (Exception e) {
            showMessage("Fout gevonden bij aantal aandelen");
            return;
        }
        try {
            Order order = new Order(
                    gekozenAandeel,
                    now(),
                    ordertype,
                    rbtVerkoop.isSelected(),
                    stopprijs,
                    limietprijs,
                    nrStocks
            );
            orders.add(order);
            addOrdersToScreen();
        } catch (Exception e) {
            showMessage("Fout bij aanmaken order, zie console");
            main.toonMessage(e.getMessage());
            return;
        }
        showMessage("order aangemaakt");
    }

    public void addOrdersToScreen() throws Exception {
        tableViewOrders.getItems().clear();

        txtRekeningtegoed.setText(Util.toCurrency(rekeningTegoed));
        for (Order order : orders.getOrders()) {
            String koopverkoop = "koop";
            if (order.isSaleOrder()) {
                koopverkoop = "verkoop";
            }
            String ordertype = "invalid";
            switch (order.getOrderType()) {
                case LIMIT:
                    ordertype = "limit";
                    break;
                case MARKET:
                    ordertype = "bestens";
                    break;
                case STOPLOSS:
                    ordertype = "stoploss";
                    break;
                case STOPLIMIT:
                    ordertype = "stoplimit";
                    break;
                default:
                    throw new Exception("addOrdersToScreen(): invalid order type");
            }
            OrderDTO orderDTO = new OrderDTO(
                    Integer.toString(order.getOrderNr()),
                    order.getTicker(),
                    Integer.toString(order.getNrOfShares()),
                    koopverkoop,
                    ordertype,
                    Util.toCurrency(order.getLimitprice()),
                    Util.toCurrency(order.getStopprice())
            );
            tableViewOrders.getItems().add(orderDTO);
        }
        //tableView.setPlaceholder(new Label("No rows to display"));
    }

    public void addTransactionsToScreen() throws Exception {
        tableViewTx.getItems().clear();
        for (Transaction tx : transactions.getTransactions()) {


            double bedrag = tx.getNrOfShares() * tx.getSharePrice();
            if (tx.isSaleOrder()) {
                bedrag = -bedrag;
            }

            IDate datum = tx.getExecutionDate();
            TransactionDTO transactionDTO = new TransactionDTO(
                    datum.toString(),
                    Integer.toString(tx.getTxNumber()),
                    tx.getTicker(),
                    Integer.toString(tx.getNrOfShares()),
                    Integer.toString(tx.getNrOfShares()),
                    Util.toCurrency(tx.getSharePrice()),
                    Util.toCurrency(bedrag)
            );

            tableViewTx.getItems().add(transactionDTO);
        }
        //tableView.setPlaceholder(new Label("No rows to display"));
    }

    public void addPositionsToScreen(int year, int month, int day) throws Exception {
        tableViewPortefeuille.getItems().clear();
        for (Map.Entry<String, Integer> entry : portefeuille.getPosities().entrySet()) {
            String aandeel = entry.getKey();
            int aantal = entry.getValue();
            String sAantal = Integer.toString(aantal);
            double dKoers = bufferedPrices.getClosePrice(aandeel,
                    year, month, day);
            double dWaarde = dKoers * aantal;
            PositieDTO positieDTO = new PositieDTO(
                    aandeel,
                    sAantal,
                    Util.toCurrency(dKoers),
                    Util.toCurrency(dWaarde));
            tableViewPortefeuille.getItems().add(positieDTO);
        }
    }

    public void verwijderOrder() {
        try {
            boolean deleted = false;
            int nr = Integer.parseInt(txtOrdernr.getText());
            int iIndex = 0;
            int iVolgnr = 0;
            for (Order order : orders.getOrders()) {
                if (order.getOrderNr() == nr) {
                    iVolgnr = iIndex;
                    deleted = true;
                }
                iIndex++;
            }
            if (!deleted) {
                showMessage("order niet gevonden");
            } else {
                orders.getOrders().remove(iVolgnr);
                addOrdersToScreen();
            }
        } catch (Exception e) {
            showMessage("fout bij verwijderen order");
            main.toonMessage(e.getMessage());
            return;
        }
        showMessage("order verwijderd");
    }

    private void showMessage(String localizedMessage) {
        lblMessage.setText(localizedMessage);
    }


    private ArrayList<Order> ordersToBeDeleted;
    private ArrayList<Transaction> transactionsToBeCreated;

    public void processMatchedOrders() {
        for (Order order : ordersToBeDeleted) {
            orders.deleteOrder(order);
        }
        for (Transaction transaction : transactionsToBeCreated) {
            transactions.getTransactions().add(transaction);
            double bedrag = transaction.getSharePrice() * transaction.getNrOfShares();
            int sign = 1;
            if (transaction.isSaleOrder()) {
                sign *= -1;
            }
            rekeningTegoed = rekeningTegoed -
                    sign * transaction.getSharePrice() * transaction.getNrOfShares();

            portefeuille.addToPositie(transaction.getTicker(), transaction.getNrOfShares() * sign);
        }
    }

    /*
     *   Because of concurrency constraints, the order that has been processed and the transaction
     *   that has been created must be stashed. In a later stage, they are to be processed.
     */
    public boolean verwerkOrders(DayPriceRecord dpr) {
        ordersToBeDeleted = new ArrayList<>();
        transactionsToBeCreated = new ArrayList();

        boolean orderProcessed = false;
        System.out.println("verwerk orders via main en grafiekenscherm");

        for (Order order : orders.getOrders()) {
            Transaction t = order.verwerkOrder(dpr);
            if (t != null) {
                ordersToBeDeleted.add(order);
                transactionsToBeCreated.add(t);
                orderProcessed = true;
            }
        }
        return orderProcessed;
    }


}
