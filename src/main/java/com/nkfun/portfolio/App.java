package com.nkfun.portfolio;

//import javax.sound.sampled.Port;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println("\n--------------Portfolio Management System-------------------");
        
        // Creating the services needed for api fetch:
        MarketDataService marketDataService = new AlphaVantageService();
        PortfolioStorageService storageService = new PortfolioStorageService();

        Portfolio myPortfolio = storageService.loadPortfolio(marketDataService);
        myPortfolio.setMarketDataService(marketDataService);

        System.out.println("Current state of the portfolio: ");
        System.out.println(myPortfolio);

        // Creating hardcoded Stocks for testing purposes:
        // (tickerSymbol, companyName, quantity, purchasePrice)
        Stock appleStock = new Stock("AAPL", "Apple Inc", 20, 1.65);
        Stock googleStock = new Stock("GOOGL", "Alphabet Inc", 50, 1.80);
        Stock teslaStock = new Stock("TSLA", "Tesla Inc", 100, 1.55);
        

        // appending the stocks to myPortfolio:
        myPortfolio.addStock(appleStock);
        myPortfolio.addStock(googleStock);
        myPortfolio.addStock(teslaStock);

        Stock applStock = new Stock("AAPL", "Apple Inc", 50, 90);
        myPortfolio.addStock(applStock);
        // Saving the portfolio onto file:
        storageService.savePortfolio(myPortfolio);

        // Displaying the stocks in myPortfolio:
        // System.out.println("\n Final Portfolio State: ");
        // System.out.println(myPortfolio); // here the program will directly call toString which was overriden
        System.out.println("\nCalculating portfolio's total value: ");
        double totalValue = myPortfolio.calculateTotalValue();
        
        System.out.println("---------*******-----------");
        System.out.printf("Total Portfolio value is : $%.2f\n",totalValue);
        System.out.println("---------*******-----------");
    }
}
