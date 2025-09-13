package com.nkfun.portfolio;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    
    private List<Stock> stocks;

    private final MarketDataService marketDataService;
    // Initialising a portfolio -> implies initialising a array list of stocks
    Portfolio(MarketDataService marketDataService){
        this.marketDataService = marketDataService;
        this.stocks = new ArrayList<>();
    }

    // Adding a new stock to our current list:
    public void addStock(Stock stock){
        this.stocks.add(stock);
        System.out.println("Added: " + stock.getQuantity() + " shares of " + stock.getTickerSymbol());

    }

    public double calculateTotalValue(){
        double totalValue = 0.0;
        for(Stock stock : stocks){
            double currentPrice = marketDataService.getPrice(stock.getTickerSymbol());
            totalValue+=currentPrice*stock.getQuantity();
            System.out.println("Stock = [" + stock.getTickerSymbol() + ", currentPrice = " + currentPrice + "]");
            // Adding a delay for fetching the data from the api -> 5 calls for minute
            try {
                Thread.sleep(15000); // 15 seconds delay between each call
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return totalValue;
    }

    public List<Stock> geStocks(){
        return stocks;
    }

    @Override
    public String toString(){
        return "Portfolio stocks = [" + stocks + "]\n";
    }
}
