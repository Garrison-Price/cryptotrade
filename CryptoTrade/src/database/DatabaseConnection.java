package database;

import api.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Garrison Price
 */

public class DatabaseConnection {
    private Connection db;
    public DatabaseConnection(String dbURL) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.toString());
        }
        try {
            db = DriverManager.getConnection(dbURL, "postgres", "******");
        } catch (SQLException ex) {
            System.err.println(ex.toString());
        }
    }
    
    public void addNewCurrency(String currencyCode, String currencyName) {
        try {
            Statement st = db.createStatement();
            st.execute("INSERT INTO \"Currencies\" (\"Currency_Code\", \"Currency_Name\") VALUES (\'"+currencyCode+"\',\'"+currencyName+"\')");
            st.close(); 
        }
        catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    private void addNewCurrencyToBatch(String currencyCode, String currencyName, Statement st) {
        try {
            st.addBatch("INSERT INTO \"Currencies\" (\"Currency_Code\", \"Currency_Name\") VALUES (\'"+currencyCode+"\',\'"+currencyName+"\')");
        }
        catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void insertAllCurrencies() {
        Cryptsy c = new Cryptsy();

        try {
            Cryptsy.PublicMarket[] markets = c.getPublicMarketData();
            Statement st = db.createStatement();
            for(Cryptsy.PublicMarket m : markets) {
                addNewCurrencyToBatch(m.primarycode, m.primaryname, st);
                addNewCurrencyToBatch(m.secondarycode, m.secondaryname, st);
            }
            st.executeBatch();
            st.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void addNewMarket(String marketLabel, String primaryCode, String secondaryCode) {
        try {
            Statement st = db.createStatement();
            st.execute("INSERT INTO \"Markets\" (\"Market_Label\", \"Primary_Currency\", \"Secondary_Currency\") VALUES (\'"+marketLabel+"\',(SELECT \"CID\" FROM \"Currencies\" WHERE (\"Currencies\".\"Currency_Code\" = '"+primaryCode+"')), (SELECT \"CID\" FROM \"Currencies\" WHERE (\"Currencies\".\"Currency_Code\" = '"+secondaryCode+"')))");
            st.close(); 
        }
        catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    private void addNewMarketToBatch(String marketLabel, String primaryCode, String secondaryCode, Statement st) {
        try {
            st.addBatch("INSERT INTO \"Markets\" (\"Market_Label\", \"Primary_Currency\", \"Secondary_Currency\") VALUES (\'"+marketLabel+"\',(SELECT \"CID\" FROM \"Currencies\" WHERE (\"Currencies\".\"Currency_Code\" = '"+primaryCode+"')), (SELECT \"CID\" FROM \"Currencies\" WHERE (\"Currencies\".\"Currency_Code\" = '"+secondaryCode+"')))");
        }
        catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void insertAllMarkets() {
        Cryptsy c = new Cryptsy();

        try {
            Cryptsy.PublicMarket[] markets = c.getPublicMarketData();
            Statement st = db.createStatement();
            for(Cryptsy.PublicMarket m : markets) {
                addNewMarketToBatch(m.label, m.primarycode, m.secondarycode, st);
            }
            st.executeBatch();
            st.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void addMarketUpdate(String marketLabel, double lastTradePrice, Cryptsy.MarketSellOrder[] sellOrders, Cryptsy.MarketBuyOrder[] buyOrders) {
        String jsonSellOrders = "{\"sellOrders\":[";
        String jsonBuyOrders = "{\"buyOrders\":[";
        if(sellOrders != null) {
            for(Cryptsy.MarketSellOrder sellOrder: sellOrders) {
                jsonSellOrders += "["+sellOrder.getPrice()+","+sellOrder.getQuantity()+","+sellOrder.getTotal()+"], ";
            }
            jsonSellOrders = jsonSellOrders.substring(0, jsonSellOrders.length()-2);
        }
        jsonSellOrders += "]}";
        
        if(buyOrders != null) {
            for(Cryptsy.MarketBuyOrder buyOrder: buyOrders) {
                jsonBuyOrders += "["+buyOrder.getPrice()+","+buyOrder.getQuantity()+","+buyOrder.getTotal()+"], ";
            }
            jsonBuyOrders = jsonBuyOrders.substring(0, jsonBuyOrders.length()-2);
        }
        jsonBuyOrders += "]}";
        
        try {
            Statement st = db.createStatement();
            st.execute("INSERT INTO \"Market_Updates\" (\"MID\", \"Last_Trade_Price\", \"Sell_Orders\", \"Buy_Orders\") VALUES ((SELECT \"MID\" FROM \"Markets\" WHERE (\"Markets\".\"Market_Label\" = '"+marketLabel+"')), \'"+lastTradePrice+"\',\'"+jsonSellOrders+"\',\'"+jsonBuyOrders+"\')");
            st.close(); 
        }
        catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    private void addMarketUpdateToBatch(String marketLabel, double lastTradePrice, Cryptsy.MarketSellOrder[] sellOrders, Cryptsy.MarketBuyOrder[] buyOrders, Statement st) {
        String jsonSellOrders = "{\"sellOrders\":[";
        String jsonBuyOrders = "{\"buyOrders\":[";
        if(sellOrders != null) {
            for(Cryptsy.MarketSellOrder sellOrder: sellOrders) {
                jsonSellOrders += "["+sellOrder.getPrice()+","+sellOrder.getQuantity()+","+sellOrder.getTotal()+"], ";
            }
            jsonSellOrders = jsonSellOrders.substring(0, jsonSellOrders.length()-2);
        }
        jsonSellOrders += "]}";
        
        if(buyOrders != null) {
            for(Cryptsy.MarketBuyOrder buyOrder: buyOrders) {
                jsonBuyOrders += "["+buyOrder.getPrice()+","+buyOrder.getQuantity()+","+buyOrder.getTotal()+"], ";
            }
            jsonBuyOrders = jsonBuyOrders.substring(0, jsonBuyOrders.length()-2);
        }
        jsonBuyOrders += "]}";
        
        try {
            st.addBatch("INSERT INTO \"Market_Updates\" (\"MID\", \"Last_Trade_Price\", \"Sell_Orders\", \"Buy_Orders\") VALUES ((SELECT \"MID\" FROM \"Markets\" WHERE (\"Markets\".\"Market_Label\" = '"+marketLabel+"')), \'"+lastTradePrice+"\',\'"+jsonSellOrders+"\',\'"+jsonBuyOrders+"\')");
        }
        catch(Exception e) {
            System.err.println(e.toString());
            System.err.println(jsonSellOrders);
            System.err.println(jsonBuyOrders);
        }
    }
    
    public void addAllMarketUpdates() {
        Cryptsy c = new Cryptsy();

        try {
            Cryptsy.PublicMarket[] markets = c.getPublicMarketData();
            Statement st = db.createStatement();
            for(Cryptsy.PublicMarket m : markets) {
                addMarketUpdateToBatch(m.label,m.lasttradeprice,m.sellorders,m.buyorders, st);
            }
            st.executeBatch();
            st.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void addTrade(long id, String marketLabel, java.util.Date time, double price, double quantity) {
        try {
            Statement st = db.createStatement();
            st.execute("INSERT INTO \"Cryptsy_Trades\" (\"TID\", \"MID\", \"Time\", \"Price\", \"Quantity\") VALUES (\'"+id+"\',(SELECT \"MID\" FROM \"Markets\" WHERE (\"Markets\".\"Market_Label\" = '"+marketLabel+"')), \'"+time+"\',\'"+price+"\',\'"+quantity+"\')");
            st.close(); 
        }
        catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    private void addTradeToBatch(long id, String marketLabel, java.util.Date time, double price, double quantity, Statement st) {
        try {
            st.addBatch("INSERT INTO \"Cryptsy_Trades\" (\"TID\", \"MID\", \"Time\", \"Price\", \"Quantity\") VALUES (\'"+id+"\',(SELECT \"MID\" FROM \"Markets\" WHERE (\"Markets\".\"Market_Label\" = '"+marketLabel+"')), \'"+time+"\',\'"+price+"\',\'"+quantity+"\')");
        }
        catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void addAllTrades() {
        Cryptsy c = new Cryptsy();

        try {
            Cryptsy.PublicMarket[] markets = c.getPublicMarketData();
            Statement st = db.createStatement();
            for(Cryptsy.PublicMarket m : markets) {
                for(Cryptsy.PublicTrade trade : m.recenttrades) {
                    addTradeToBatch(trade.id,m.label, trade.time, trade.price,trade.quantity, st);
                }
            }
            st.executeBatch();
            st.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public ArrayList<Cryptsy.PublicTrade> getTrades(String marketLabel, long startTime, long endTime) {
        ArrayList<Cryptsy.PublicTrade> trades = new ArrayList<>();;
        try {
            Statement st = db.createStatement();
            st.executeQuery("SELECT * FROM \"Cryptsy_Trades\" WHERE \"MID\" = (SELECT \"MID\" FROM \"Markets\" WHERE (\"Markets\".\"Market_Label\" = '"+marketLabel+"')) AND \"Time\" >= (SELECT TIMESTAMP WITH TIME ZONE 'epoch' + "+startTime+" * INTERVAL '1 second') AND \"Time\" <= (SELECT TIMESTAMP WITH TIME ZONE 'epoch' + "+endTime+" * INTERVAL '1 second')");
            ResultSet rs = st.getResultSet();
            while(rs.next()) {
                Cryptsy.PublicTrade trade = new Cryptsy.PublicTrade();
                trade.id = rs.getLong(1);
                trade.price = rs.getDouble(4);
                trade.quantity = rs.getDouble(5);
                trade.time = rs.getDate(3);
                trade.total = trade.price * trade.quantity;
                trades.add(trade);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return trades;
    }
    
    public static void main(String args[]) {
        DatabaseConnection conn = new DatabaseConnection("jdbc:postgresql://99.***.186.203:****/cryptotrade");        //conn.insertAllCurrencies();
        //conn.insertAllMarkets();
        conn.addAllMarketUpdates();
    }
}
