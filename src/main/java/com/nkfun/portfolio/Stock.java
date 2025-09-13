package com.nkfun.portfolio;

public class Stock {

    // Attributes of a stock
    private final String tickerSymbol;
    private final String companyName;
    private int quantity;
    private double purchasePrice;

    private Stock() {} // for jackson

    Stock(String tickerSymbol, String companyName, int quantity, double purchasePrice){
        this.tickerSymbol = tickerSymbol;
        this.companyName = companyName;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
    }


    // updating the shares of a stock which is already present:
    public void addShares(int additionalShares,double priceOfNewShares){
        double totalCost = (this.quantity * this.purchasePrice) + (additionalShares * priceOfNewShares);
        this.quantity += additionalShares;
        this.purchasePrice = totalCost/this.quantity; // average price of each share is stored as purchaseprice
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
