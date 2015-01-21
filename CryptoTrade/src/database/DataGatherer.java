package database;

import java.util.Timer;
import java.util.TimerTask;

//Author: garrison Price

public class DataGatherer {
    DatabaseConnection conn;
    public DataGatherer(long timeBetweenUpdates, long timeBetweenCurrencyUpdates) {
        conn = new DatabaseConnection("jdbc:postgresql://127.0.0.1:5432/cryptotrade");
        //conn = new DatabaseConnection("jdbc:postgresql://99.***.186.203:****/cryptotrade");
        Timer tUpdates = new Timer();
        tUpdates.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                conn.addAllMarketUpdates();
                System.out.println("Market Updates Added.");
                conn.addAllTrades();
                System.out.println("All New Trades Added.");
            }
        }, timeBetweenUpdates, timeBetweenUpdates);
        
        Timer tCurrencyUpdates = new Timer();
        tCurrencyUpdates.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                conn.insertAllCurrencies();
                conn.insertAllMarkets();
                System.out.println("Currencies and Markets Updated.");
            }
        }, timeBetweenCurrencyUpdates, timeBetweenCurrencyUpdates);
    }
    
    public static void main(String[] args) {
        long timeBetweenUpdates = 60000;
        long timeBetweenCurrencyUpdates = 600000;
        try {
            timeBetweenUpdates = Long.getLong(args[0]);
            timeBetweenCurrencyUpdates = Long.getLong(args[1]);
        }
        catch(Exception e) {
            System.err.println(e);
        }
        
        new DataGatherer(timeBetweenUpdates, timeBetweenCurrencyUpdates);
    }
}
