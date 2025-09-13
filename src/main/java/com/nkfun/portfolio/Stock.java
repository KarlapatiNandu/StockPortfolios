package com.nkfun.portfolio;

public class Stock {

    // Attributes of a stock
    private final String tickerSymbol;
    private final String companyName;
    private int quantity;
    private double purchasePrice;

    Stock(String tickerSymbol, String companyName, int quantity, double purchasePrice){
        this.tickerSymbol = tickerSymbol;
        this.companyName = companyName;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
    }

    // getter functions:
    public String getTickerSymbol(){
        return tickerSymbol;
    }

    public String getCompanyName(){
        return companyName;
    }

    public int getQuantity(){
        return quantity;
    }

    public double getPurchasePrice(){
        return purchasePrice;
    }

    // overriding toString to give meaningful description about our objects:
    @Override
    public String toString(){
        return "Stock [ticker = " + tickerSymbol + ", quantity = " + quantity + ", companyName = " + companyName + ", purchasePrice = " + purchasePrice + "]\n";
    
    }
}
