package beleggingspakket;

import beleggingspakket.Koersen.BufferedPrices;
import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.portefeuillebeheer.*;
import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

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

    private String pfNaam = "";
    private Portefeuille portefeuille = new Portefeuille();

    private TableView tableViewOrders = new TableView();
    private TableView tableViewTx = new TableView();
    private TableView tableViewPortefeuille = new TableView();
    private BufferedPrices bufferedPrices = new BufferedPrices();
    // The price history from each stock in current portfolio
    // is kept in buffer
    // NB note that position 0 stocks could be removed from this buffer

    @FXML
    private Label lblMessage;

    @FXML
    private VBox vboxOrders;

    @FXML
    private TextField txtAantal;

    @FXML
    private TextField txtUitoefenprijs;


    @FXML
    private TextField txtAantalOpties;

    @FXML
    private TextField txtBedragOptiePremie;

    @FXML
    private TextField txtEinddatum;

    @FXML
    private TextField txtPortefeuillewaarde;

    @FXML
    private TextField txtWinstVerlies;

    @FXML
    private TextField txtTotaleWaarde;

    @FXML
    private TextField txtRekeningtegoed;

    @FXML
    private TextField txtPortefeuillenaam;

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
    private RadioButton rbtKoopOptie;

    @FXML
    private RadioButton rbtVerkoopOptie;

    @FXML
    private RadioButton rbtOptieContract100;

    @FXML
    private RadioButton rbtOptieContract10;



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

    @FXML
    private ChoiceBox<String> cmbCallPut;

    @FXML
    private ComboBox<String> cmbExpMaand;

    @FXML
    private ComboBox<String> cmbExpJaar;


    TableColumn<String, OrderDTO> createColumn(String displayName, String name) {
        TableColumn<String, OrderDTO> column1 = new TableColumn<>(displayName);
        column1.setCellValueFactory(new PropertyValueFactory<>(name));
        return column1;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtPortefeuillewaarde.setText("waarde nog invullen");
        txtWinstVerlies.setText("winst verlies nog invullen");
        txtTotaleWaarde.setText("totale waarde nog invullen");

        aandelenlijst.removeAll(selecteerAandeel);

        ArrayList<String> tickerSet = new ArrayList<>();
        tickerSet.addAll(getPriceHistory.getTickers());
        Collections.sort(tickerSet);

        for (int i = 1; i <= 12; i++) {
            cmbExpMaand.getItems().add(Integer.toString(i));
        }

        cmbCallPut.getItems().add("Call");
        cmbCallPut.getItems().add("Put");


        for (int i = 2014; i <= 2030; i++) {
            cmbExpJaar.getItems().add(Integer.toString(i));
        }

        for (String ticker1 : tickerSet) {
            aandelenlijst.add(ticker1);
        }

        selecteerAandeel.getItems().addAll(aandelenlijst);

        txtRekeningtegoed.setText(Util.toCurrency(
                portefeuille.getRekeningTegoed()));

        final ToggleGroup groupOrderType = new ToggleGroup();
        rbtLimiet.setToggleGroup(groupOrderType);
        rbtBestens.setToggleGroup(groupOrderType);
        rbtStopLimit.setToggleGroup(groupOrderType);
        rbtStopLoss.setToggleGroup(groupOrderType);

        final ToggleGroup groupOptieOrderType = new ToggleGroup();
        rbtKoopOptie.setToggleGroup(groupOptieOrderType);
        rbtVerkoopOptie.setToggleGroup(groupOptieOrderType);
        rbtKoopOptie.setSelected(true);

        final ToggleGroup groupContractgrootte = new ToggleGroup();
        rbtOptieContract10.setToggleGroup(groupContractgrootte);
        rbtOptieContract100.setToggleGroup(groupContractgrootte);
        rbtOptieContract100.setSelected(true);

        txtAantalOpties.setText("1");
        cmbCallPut.setValue("Call");
        cmbExpMaand.setValue("1");
        cmbExpJaar.setValue("2025");

        rbtLimiet.setSelected(true);
        txtAantal.setText("20");
        txtStopprijs.setVisible(false);

        final ToggleGroup groupKoopVerkoop = new ToggleGroup();
        rbtKoop.setToggleGroup(groupKoopVerkoop);
        rbtVerkoop.setToggleGroup(groupKoopVerkoop);
        rbtKoop.setSelected(true);

        aandelenlijst.removeAll(selecteerAandeel);

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
        tableViewPortefeuille.getColumns().add(createColumn("GAK", "gak"));
        tableViewPortefeuille.getColumns().add(createColumn("Gerealiseerd", "gerealiseerd"));
        tableViewPortefeuille.getColumns().add(createColumn("Ongerealiseerd", "ongerealiseerd"));

/* Kolommen voor winst/verlies:
   •	totaal aantal gekocht TG is 50 + 50 = 100 stuks.
•	Gemiddelde aankoopkoers GAK is gewogen gemiddelde = (50*50 + 60*50)/100 = 55 euro.
•	totaal aantal verkocht TV is 50.
•	Gemiddelde verkoopkoers GVK is gewogen gemiddelde van de verkoopkoersen is 70 euro.
•	Gerealiseerde winst/verlies GRW = is TV * (GVK – GAK ) = 50 * (70 – 55) euro = 50 * 15 = 750 euro.
•	Openstaande positie POS is gelijk aan POS = TG – TV = 50
•	Ongerealiseerde winst OW = POS * ( K – GAK ) waarbij K de huidige koers is
    Stel huidige koers K = 60 dan is ongerealiseerde winst/verlies gelijk aan
    POS * ( K – GAK) = 50 * ( 60  - 55 ) = 50 * 5 = 250 euro.

    Te tonen kolommen: POS, Gerealiseerd, Ongerealiseerd, GAK

*/

        tableViewPortefeuille.setPrefHeight(10000);


        tableViewTx.getColumns().add(createColumn("Datum", "datum"));
        tableViewTx.getColumns().add(createColumn("TxNr", "txNr"));
        tableViewTx.getColumns().add(createColumn("Instrumentnaam", "aandeelNaam"));
        tableViewTx.getColumns().add(createColumn("Aantal", "aantal"));
        tableViewTx.getColumns().add(createColumn("Koop/Verkoop", "koopVerkoop"));
        tableViewTx.getColumns().add(createColumn("Koers", "aandeelPrijs"));
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

        /* Order(String aTicker,
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
                    Util.toIDate(now()),
                    ordertype,
                    rbtVerkoop.isSelected(),
                    stopprijs,
                    limietprijs,
                    nrStocks
            );

            portefeuille.addOrder(order);
            addOrdersToScreen();
        } catch (Exception e) {

            showMessage("Fout bij aanmaken order" + e.getMessage());
            return;
        }
        showMessage("order aangemaakt");
    }

    public void enterOptieOrder() {
        String veldnaam = "";
        try {
            System.out.println("enter optie order");
            String ticker = selecteerAandeel.getValue();
            if (ticker == null) {
                throw new Exception("svp eerst aandeel kiezen");
            }
            veldnaam = "aantal";
            int aantal = Integer.parseInt(txtAantalOpties.getText());

            veldnaam = "expiratiemaand";
            int expMaand = Integer.parseInt(cmbExpMaand.getValue().toString());
            veldnaam = "expiratiejaar";
            int expJaar = Integer.parseInt(cmbExpJaar.getValue().toString());
            veldnaam = "callput";
            boolean isCall = cmbCallPut.getValue().equals("Call");
            veldnaam = "bijaf";
            String sOptiepremie = txtBedragOptiePremie.getText();
            double optiePremie = Util.toDouble(sOptiepremie);
            veldnaam = "uitoefenprijs";
            double uitoefenprijs = Util.toDouble(txtUitoefenprijs.getText());

            int nContract = 100;
            if (rbtOptieContract10.isSelected())
                nContract = 10;
            boolean isVerkoop = rbtVerkoopOptie.isSelected();
            verwerkOptieTransactie(
                    isVerkoop,
                    ticker,
                    isCall,
                    uitoefenprijs,
                    aantal,
                    expMaand,
                    expJaar,
                    optiePremie,
                    nContract
            );


        } catch (Exception e) {
            if (veldnaam.equals("aantal")) {
                logInTextArea("formaat van aantal onjuist");
            } else if (veldnaam.equals("expiratiemaand")) {
                logInTextArea("svp geldige expiratiemaand ingeven");
            } else if (veldnaam.equals("expiratiejaar")) {
                logInTextArea("svp geldig expiratiejaar ingeven");
            } else if (veldnaam.equals("bijaf")) {
                logInTextArea("fout in formaat bij/af bedrag");
            } else if (veldnaam.equals("uitoefenprijs")) {
                logInTextArea("fout in formaat uitoefenprijs");
            } else if (veldnaam.equals("callput")) {
                logInTextArea("svp call of put ingeven");
            } else
                logInTextArea(e.getLocalizedMessage());
        }
    }

    public void addOrdersToScreen() throws Exception {
        tableViewOrders.getItems().clear();

        txtRekeningtegoed.setText(Util.toCurrency(portefeuille.getRekeningTegoed()));
        for (Order order : portefeuille.getOrders()) {
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
        for (Transaction tx : portefeuille.getTransactions()) {
            double bedrag = 0.0;
            if (tx.getIsOptieTransactie()) {
                bedrag = tx.getNrOfItems() * tx.getPrice() * tx.getContractgrootte();
            } else {
                bedrag = tx.getNrOfItems() * tx.getPrice();
            }

            if (tx.isSaleOrder()) {
                bedrag = -bedrag;
            }

            IDate datum = tx.getExecutionDate();

            String sKoopVerkoop = "Koop";
            if (tx.isSaleOrder())
                sKoopVerkoop = "Verkoop";
            TransactionDTO transactionDTO = new TransactionDTO(
                    datum.toString(),
                    Integer.toString(tx.getTxNumber()),
                    tx.getInstrumentname(),
                    Integer.toString(tx.getNrOfItems()),
                    sKoopVerkoop,
                    Util.toCurrency(tx.getPrice()),
                    Util.toCurrency(bedrag)
            );

            tableViewTx.getItems().add(transactionDTO);
        }
        //tableView.setPlaceholder(new Label("No rows to display"));
    }

    // naam dekt niet meer helemaal de lading, toont ook totale portefeuillewaarde
    // en winst/verlies op de posities
    public void addPositionsToScreen(int year, int month, int day) throws Exception {
        tableViewPortefeuille.getItems().clear();
        double totaleWaarde = 0.0;
        for (Map.Entry<String, Positie> entry : portefeuille.getPosities()) {

            String instrumentnaam = entry.getKey();

            Positie pos = entry.getValue();

            // bereken winst/verlies op de positie
            Double dKoers = 0.0;
            if (pos.getIsAandeel()) {
                dKoers = bufferedPrices.getClosePrice(instrumentnaam,
                        year, month, day);
            } else {
                dKoers = pos.getHuidigeKoers();
            }
            pos.berekenWinstVerliesInstrument(portefeuille.getTransactions(), dKoers);
            PositieDTO positieDTO = new PositieDTO(pos);
            tableViewPortefeuille.getItems().add(positieDTO);
            totaleWaarde += pos.geefHuidigeWaarde();

        }

        txtPortefeuillewaarde.setText(Util.toCurrency(totaleWaarde));
        double rekeningTegoed =
                portefeuille.getRekeningTegoed();
        totaleWaarde += rekeningTegoed;
        double rekeningTegoedGestort = portefeuille.getRekeningTegoedGestort();
        double winstVerlies =
                totaleWaarde  - rekeningTegoedGestort;
        txtTotaleWaarde.setText(Util.toCurrency(totaleWaarde));
        txtWinstVerlies.setText(Util.toCurrency(winstVerlies));
        // Bereken winst/verlies op de posities
    }

    public void verwijderOrder() {
        try {
            boolean deleted = false;
            int nr = Integer.parseInt(txtOrdernr.getText());
            int iIndex = 0;
            int iVolgnr = 0;
            for (Order order : portefeuille.getOrders()) {
                if (order.getOrderNr() == nr) {
                    iVolgnr = iIndex;
                    deleted = true;
                }
                iIndex++;
            }
            if (!deleted) {
                showMessage("order niet gevonden");
            } else {
                portefeuille.removeOrderById(iVolgnr);
                addOrdersToScreen();
            }
        } catch (Exception e) {
            showMessage("fout bij verwijderen order");
            showMessage(e.getMessage());
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
            portefeuille.deleteOrder(order);
        }
        for (Transaction transaction : transactionsToBeCreated) {
            portefeuille.addTransaction(transaction);
            double bedrag = transaction.getPrice() * transaction.getNrOfItems();
            int sign = 1;
            if (transaction.isSaleOrder()) {
                sign *= -1;
            }
            portefeuille.setRekeningTegoed(
                    portefeuille.getRekeningTegoed() -
                            sign * transaction.getPrice() * transaction.getNrOfItems());

            portefeuille.addToPositie(
                    transaction.getInstrumentname(),
                    transaction.getNrOfItems() * sign,
                    transaction.getPrice(),
                    transaction.getIsOptieTransactie(),
                    transaction.getContractgrootte());
        }
    }


    // Anders dan een aandelenorder wordt een optieorder direct verwerkt
    // met als beursdag de beursdag de laatst zichtbare beursdag in
    // de grafiek
    public void verwerkOptieTransactie(
            boolean isVerkoop,
            String ticker,
            boolean isCall,
            double uitoefenprijs,
            int aantal,
            int expMaand,
            int expJaar,
            double optiepremie,
            int iContactgrootte) {
        String sCall = "Call";
        if (!isCall)
            sCall = "Put";
        String sKoopVerkoop = "koop";
        if (isVerkoop)
            sKoopVerkoop = "verkoop";
        System.out.println("Toevoegen optietransactie:" + sKoopVerkoop + " " +
                aantal + " " + ticker + " " + sCall + " " +
                Util.toCurrency(uitoefenprijs) +
                " " + expMaand + "-" + expJaar + "  af/bij:" + Util.toCurrency(optiepremie) + " contractgrootte = " +
                iContactgrootte);
        try {
            portefeuille.AddOptieTransactie(
                    isVerkoop,
                    ticker,
                    isCall,
                    uitoefenprijs,
                    aantal,
                    expMaand,
                    expJaar,
                    optiepremie,
                    iContactgrootte
            );

            addOrdersToScreen();
            addTransactionsToScreen();
            IDate einddatum = portefeuille.getEinddatum();
            addPositionsToScreen(einddatum.getYear(),
                    einddatum.getMonth(), einddatum.getDay());

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
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

        for (Order order : portefeuille.getOrders()) {
            Transaction t = order.verwerkOrder(dpr);
            if (t != null) {
                ordersToBeDeleted.add(order);
                transactionsToBeCreated.add(t);
                orderProcessed = true;
            }
        }
        return orderProcessed;
    }

    public void laadPortefeuille() throws Exception {
        System.out.println("Portefeuille van schijf halen");
        haalPortefeuilleVanSchijf(this.pfNaam);
    }


    public void toonGrafiekenscherm(ActionEvent actionEvent) throws Exception {
        System.out.println("Toon grafiekenscherm");
        String gekozenAandeel = selecteerAandeel.getValue();

        if (gekozenAandeel == null) {
            logInTextArea("Eerst aandeel kiezen svp");
        } else {
            logInTextArea("toon grafiekenscherm retro vanuit portefeuille - nog doen");
            int aantalKoersdagen = 30;

            logInTextArea("Vanuit grafiekenschermcontroller: Toon grafiekenscherm voor ticker:"
                    + gekozenAandeel + " aantalkoersdagen " + aantalKoersdagen
                    + " tot aan datum " + portefeuille.getEinddatum().toString());
            main.toonGrafiekenschermTot(gekozenAandeel, aantalKoersdagen, portefeuille.getEinddatum());
        }
    }

    private void logInTextArea(String logmessage) {
        showMessage(logmessage);
    }


    public void beursdagNaarRechts(IDate lastDate) {
        portefeuille.setEinddatum(lastDate);
        String einddatum = lastDate.toString();
        txtEinddatum.setText(einddatum);
    }

    public void haalPortefeuilleVanSchijf(String portefeuilleNaam) throws Exception {
        pfNaam = portefeuilleNaam;
        portefeuille = new Portefeuille();
        portefeuille.haalOp(pfNaam);
        txtPortefeuillenaam.setText(portefeuille.getPortefeuillenaam());

        String einddatum = portefeuille.getEinddatum().toString();
        txtEinddatum.setText(einddatum);
        String rekeningtegoed = Util.toCurrency(portefeuille.getRekeningTegoed());
        txtRekeningtegoed.setText(rekeningtegoed);
        addOrdersToScreen();
        addTransactionsToScreen();
        IDate iEinddatum = portefeuille.getEinddatum();
        addPositionsToScreen(iEinddatum.getYear(), iEinddatum.getMonth(), iEinddatum.getDay());
    }

    public void opslaanPortefeuille() {
        System.out.println("Portefeuille opslaan");
        portefeuille.slaOp();
        showMessage("portefeuille opgeslagen");
    }

}
