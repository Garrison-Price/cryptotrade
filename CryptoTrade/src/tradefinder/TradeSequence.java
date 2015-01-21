package tradefinder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class TradeSequence {
    private ArrayList<String> sequence = new ArrayList<>();
    
    public TradeSequence() {}
    public TradeSequence(ArrayList<String> sequence) {this.sequence = sequence;}
    public TradeSequence(String s) {
        String[] st = s.split(">");
        sequence.addAll(Arrays.asList(st));
    }
    
    public void addToSequence(String curr) {sequence.add(curr);}
    
    public double computeGains(double funds) {return computeValue()*funds-funds;}
    public double computeValue() {
        double v = 1;
        for(int i = 0; i < sequence.size()-1; i++)
            v *= TradeMaster.getTradeRate(sequence.get(i), sequence.get(i+1));
        return v < 0 ? -1 : v;
    }

    public boolean isValid() {
        for(int i = 0; i < sequence.size()-1; i++)
            if(TradeMaster.getTradeRate(sequence.get(i), sequence.get(i+1)) == -1) return false;
        return true;
    }
    
    int counter = 0;
    public void resetCounter() {counter = 0;}
    public String getNext() {counter++; return sequence.get(counter-1);}
    public String get(int i) {return sequence.get(i);}
    
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        return this.sequence.toString() + "; @ " + df.format((this.computeValue()-1)*100.0) + "% : " + this.counter;
    }
}
