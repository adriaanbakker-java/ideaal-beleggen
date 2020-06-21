package beleggingspakket.portefeuillebeheer;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public class Order {
    private String ticker = "";

    static int orderSeq = 1000;

    public int getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(int orderNr) {
        this.orderNr = orderNr;
    }

    private int orderNr;

    public void setSaleOrder(boolean saleOrder) {
        isSaleOrder = saleOrder;
    }

    private boolean isSaleOrder;
    private int nrOfShares;
    private LocalDateTime orderDate;
    private Ordertype orderType;
    Double limitprice = 0.0;
    Double stopprice = 0.0;


    public String getTicker() {
        return ticker;
    }

    public boolean isSaleOrder() {
        return isSaleOrder;
    }

    public int getNrOfShares() {
        return nrOfShares;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Ordertype getOrderType() {
        return orderType;
    }

    public Double getLimitprice() {
        return limitprice;
    }

    public Double getStopprice() {
        return stopprice;
    }



    /* Ordertype:
        MARKET - no limit price, no stop price, buys or sells at opening price
        LIMIT  - only limit price, triggered by share price
                  sell order: sells when price is higher
                  buy order: buys when price is lower

        STOPLIMIT - triggered by current share price
                  Order can be either sell or buy, needs to be specified.

                  Limit price and stopprice are mandatory.

                  sell order:  when market price is higher than stopprice,
                  a sell order with the given limit price is sent to the stockmarket
                  limit price should be lower than stopprice

                  buy order:  when price is higher than stopprice,
                  a limit order is created and sent to the stockmarket
                  limit price should be higher than stopprice

        STOPLOSS - sell order (market order) triggered by current share price
                   Triggers a sale at the current market price when current price is lower
                   than stopprice. Stopprice is mandatory.

    */


    public Order(String aTicker,
                 LocalDateTime aDate,
                 Ordertype aOrderType,
                 boolean aIsSaleOrder,
                 double aStopprice,
                 double aLimitprice,
                 int aNrOfShares) throws Exception {
        ticker = aTicker;
        orderDate = aDate;
        orderType = aOrderType;
        nrOfShares = aNrOfShares;
        stopprice = aStopprice;
        limitprice = aLimitprice;
        isSaleOrder = aIsSaleOrder;
        orderNr = orderSeq++;


        if (orderType == Ordertype.STOPLIMIT) {
            if (isSaleOrder) {
                if (limitprice >= stopprice) {
                    throw new Exception("limietprijs moet lager zijn dan stopprijs bij stoplimit verkooporder");
                }
            } else {
                if (limitprice <= stopprice) {
                    throw new Exception("limietprijs moet hoger zijn dan stopprijs bij stoplimit kooporder");
                }
            }
        }
    }

    // Check if this order can be processed against the condition of the
    // DayPriceRecord (dpr) of the trading day
    // If so, return the transaction that corresponds to the processed order

    /*
    *  At this moment the following orders are implemented:
    *
    * MARKET sell order and MARKET buy order
    *    sell number of stocks in the order at opening price
    *
    * STOPLOSS order (sell is presumed even if order says differently)
    *    if opening price lower than stopprice then sell the nr of shares at opening price
    *    otherwise, if low price lower than stopprice, sell the nr of shares at stopprice
    *
    * LIMIT order (can be sell or buy)
    *    buy order: if opening price lower than limit, buy the nr of shares at opening price
    *               otherwise, if low lower than limit, buy the nr of shares at limit price
    *
    *    sell order: if opening price higher than limit, sell at opening price
    *                otherwise, if high higer than limit, sell at limit price
    *
    * STOPLIMIT order (can be sell or buy)
    *    stoplimit buy order: officially, a LIMIT buy order will be sent to market
    *    as soon as the market price is higher than the stop-price.
    *    The limit price must be higher than the stop-price.
    *
    *    The next day trigger provides us with a candle values for the next day. This
    *    allows us to decide what would have happened most likely at the stock
    *    trading floor with our stoplimit order:
    *
    *    1. is opening price higher than stopprice but lower than limit, buy at opening price
    *    2. is opening price lower than stopprice but high is higher, buy at stopprice
    *    3. is opening price higher than limit, but low is lower than limit, buy at limit price.
    *
    *    stoplimit sell order: quite analogous to the above. Limit must be lower than stop-price
    *
    *    1. is opening price lower than stopprice but higher than limit, sell at opening price
    *    2. is opening price higher than stopprice but low is lower, sell at stopprice
    *    3. is opening price lower than limit, but high is higher than limit, sell at limit price.
    */
    public Transaction verwerkOrder(DayPriceRecord dpr) {
        Transaction t = null;
        int year = dpr.getYear();
        int month = dpr.getMonth();
        int day = dpr.getDay();
        IDate iDate = new IDate(year, month, day);

        if (this.getOrderType() == Ordertype.MARKET) {
            return sell_or_buy_at_price(iDate, dpr.getOpen(), isSaleOrder());
        } else if (this.getOrderType() == Ordertype.STOPLOSS) { // is always a sell, not a buy
            return verwerk_ordertype_stoploss(iDate, dpr);
        } else if (this.getOrderType() == Ordertype.LIMIT) {
            return verwerk_ordertype_limit(iDate, dpr);
        } else if (this.getOrderType() == Ordertype.STOPLIMIT) {
            return verwerk_ordertype_stoplimit(iDate, dpr);
        }
        return null;
    }

    /*    Buy order:
     *    1. is opening price higher than stopprice but lower than limit, buy at opening price
     *    2. is opening price lower than stopprice but high is higher, buy at stopprice
     *    3. is opening price higher than limit, but low is lower than limit, buy at limit price.
     *
     *    Sell order
     *    1. is opening price lower than stopprice but higher than limit, sell at opening price
     *    2. is opening price higher than stopprice but low is lower, sell at stopprice
     *    3. is opening price lower than limit, but high is higher than limit, sell at limit price.
     *
    */
    private Transaction verwerk_ordertype_stoplimit(IDate iDate, DayPriceRecord dpr) {
        if (!this.isSaleOrder()) {
            if  ((dpr.getOpen() > stopprice) && (dpr.getOpen() < limitprice)) {
                return sell_or_buy_at_price(iDate, dpr.getOpen(), false);
            }
            if ((dpr.getOpen() < stopprice) && (dpr.getHigh() > stopprice)) {
                return sell_or_buy_at_price(iDate, stopprice, false);
            }
            if ((dpr.getOpen() > limitprice) && (dpr.getLow() < limitprice)) {
                return sell_or_buy_at_price(iDate, limitprice, false);
            }
        } else {
            if  ((dpr.getOpen() < stopprice) && (dpr.getOpen() > limitprice)) {
                return sell_or_buy_at_price(iDate, dpr.getOpen(), true);
            }
            if ((dpr.getOpen() > stopprice) && (dpr.getLow() < stopprice)) {
                return sell_or_buy_at_price(iDate, stopprice, true);
            }
            if ((dpr.getOpen() < limitprice) && (dpr.getHigh() > limitprice)) {
                return sell_or_buy_at_price(iDate, limitprice, true);
            }
        }
        return null;
    }

    /*
    *   STOPLOSS (is always a sell order)
    */
    private Transaction verwerk_ordertype_stoploss(IDate iDate, DayPriceRecord dpr) {
        if (dpr.getOpen() < stopprice) {
            return sell_or_buy_at_price(iDate, dpr.getOpen(), true);
        }
        else if (dpr.getLow() < stopprice) {
            return sell_or_buy_at_price(iDate, stopprice, true);
        }
        return null;
    }


    /*
     *  LIMIT order
     */
    private Transaction verwerk_ordertype_limit(IDate iDate, DayPriceRecord dpr) {
        if (this.isSaleOrder) {
            if (dpr.getOpen() > limitprice) {
                return sell_or_buy_at_price(iDate, dpr.getOpen(), true);
            } else if (dpr.getHigh() > limitprice) {
                return sell_or_buy_at_price(iDate, limitprice, true);
            }
        } else { // buy order, limit
            if (dpr.getOpen() < limitprice) {
                return sell_or_buy_at_price(iDate, dpr.getOpen(), false);
            } else if (dpr.getLow() < this.getLimitprice()) {
                return sell_or_buy_at_price(iDate, limitprice, false);
            }
        }
        return null;
    }


    /*
    * sell or buy at the given price
    */
    private Transaction sell_or_buy_at_price(
            IDate iDate, double price, boolean isSale) {
        Transaction t = new Transaction(
                iDate,
                getTicker(),
                isSale,
                getNrOfShares(),
                now(),
                price
        );
        return t;
    }

}
