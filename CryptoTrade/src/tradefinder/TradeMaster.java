package tradefinder;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeMaster {
    private static HashMap<String, Double> tradeRates = new HashMap<>();
    private static ArrayList<String> currencies = new ArrayList<>();
    
    public static void addTradeRate(String currA, String currB, double rate) {
        tradeRates.put(currA+">"+currB,   rate);
        //tradeRates.put(currB+">"+currA, 1/rate);
        if(!currencies.contains(currA)) currencies.add(currA);
        if(!currencies.contains(currB)) currencies.add(currB);
    }
    
    public static void showCurrencies() {
        for(String s1 : currencies)
            for(String s2 : currencies) 
                if(tradeRates.containsKey(s1+">"+s2))
                    System.out.println(s1+">"+s2+" = "+tradeRates.get(s1+">"+s2));
    }
    
    public static double getTradeRate(String currA, String currB) {
        if(!tradeRates.containsKey(currA+">"+currB)) return -1;
        return tradeRates.get(currA+">"+currB);
    }
    
    public static ArrayList<TradeSequence> getValidTrades(String baseCurr) {
        ArrayList<TradeSequence> ret = new ArrayList<>();
        
        for(String s1 : currencies) {
            if(baseCurr.equals(s1)) continue;
            for(String s2 : currencies) {
                if(baseCurr.equals(s2) || s1.equals(s2)) continue;
                TradeSequence t = new TradeSequence(baseCurr+">"+s1+">"+s2+">"+baseCurr);
                if(t.isValid()) ret.add(t);
                if(t.isValid()) System.out.println(t);
            }
        }
        
        return ret;
    }
    
    public static TradeSequence getBestTrade() {
        TradeSequence max = new TradeSequence();
        for(String st : TradeMaster.currencies) {
            TradeSequence tr = getBestTrade(st);
            if(!max.isValid() || tr.computeValue() > max.computeValue()) max = tr;
        }
        
        return max;
    }
    
    public static TradeSequence getBestTrade(String baseCurr) {
        if(!currencies.contains(baseCurr)) throw new Error("TradeMaster does not contain currency " + baseCurr);

        TradeSequence max = new TradeSequence();
        for(TradeSequence tr : getValidTrades(baseCurr))
            if(!max.isValid() || tr.computeValue() > max.computeValue())
                max = tr;
        
        return max;
    }
    
    private TradeMaster() {}
}
