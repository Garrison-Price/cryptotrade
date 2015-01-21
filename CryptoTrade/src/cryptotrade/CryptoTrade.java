package cryptotrade;

import api.Cryptsy;
import api.Cryptsy.PublicMarket;
import org.json.simple.parser.ParseException;
import tradefinder.TradeMaster;

public class CryptoTrade {
    private static final String PUBKEY = "992b6ea0f7f499821d8292f6df9cf482ff21a688";
    private static final String PRIVKEY = "*************f43ddb3b340cbfec9cbd005932081b98f4962172b45b6b9b357f564d94aa0dbf5a";
    
    public static void addMarket(PublicMarket r) {
        if(r == null) {System.out.print("failed "); return;}
        TradeMaster.addTradeRate(r.primarycode, r.secondarycode, (r.buyorders == null?0:(0.998*r.buyorders[0].getPrice())));
        TradeMaster.addTradeRate(r.secondarycode, r.primarycode, (r.sellorders == null?0:(0.997*(1.0/r.sellorders[0].getPrice()))));
    }
    
    public static int findMarketID(Cryptsy c, String label) throws Cryptsy.CryptsyException {
        Cryptsy.Market[] t = c.getMarkets();
        for(Cryptsy.Market m : t) 
            if(m.label.equals(label)) return m.marketid;
        return -1;
    }
    
    public static void main(String[] args) throws Cryptsy.CryptsyException, ParseException {
        Cryptsy crypt = new Cryptsy();
        crypt.setAuthKeys(PUBKEY, PRIVKEY);
        
        PublicMarket[] mark = crypt.getPublicMarketData();
        //System.out.println(mark[0].buyorders[0].toString());
        for(PublicMarket m : mark){
            addMarket(m);
        }
        
        System.out.println("\n"+TradeMaster.getBestTrade("BTC"));
    }
}
