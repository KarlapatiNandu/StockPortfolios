package com.nkfun.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    
    private List<Stock> stocks;

    @JsonIgnore
    private MarketDataService marketDataService;
    // Initialising a portfolio -> implies initialising a array list of stocks
    
    // No argument constructor for jackson for deserialization
    private Portfolio(){
        this.stocks = new ArrayList<>();
    }

    Portfolio(MarketDataService marketDataService){
        this.marketDataService = marketDataService;
        this.stocks = new ArrayList<>();
    }

    // setting a marketDataService after loading a portfolio for live access of the shares
    public void setMarketDataService(MarketDataService marketDataService){
        this.marketDataService = marketDataService;
    }
    // Adding a new stock to our current list:
    public void addStock(Stock stock){
        for(Stock existingStock : stocks){
            if(existingStock.getTickerSymbol().equals(stock.getTickerSymbol())){
                // Since the stock already exists, updating the quanitity:
                existingStock.addShares(stock.getQuantity(),stock.getPurchasePrice());
                System.out.println("Updated : " + stock.getQuantity() + " shares of " + stock.getTickerSymbol());
                return;
            }
        }
        this.stocks.add(stock);
        System.out.println("Added : " + stock.getQuantity() + " shares of " + stock.getTickerSymbol());
    }

    public double calculateTotalValue(){
        if(marketDataService == null){
            System.err.println("MarketDataService is not set, value cannot be computed");
            return 0.0;
        }
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

    public List<Stock> getStocks(){
        return stocks;
    }

    @Override
    public String toString(){
        return "Portfolio stocks = [" + stocks + "]\n";
    }
}
