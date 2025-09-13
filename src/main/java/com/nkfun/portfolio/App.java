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
        
        // loading the portfolio from the file is already present, if not then creating a new portfolio
        Portfolio myPortfolio = storageService.loadPortfolio(marketDataService);
        myPortfolio.setMarketDataService(marketDataService); // set a new marketService


        System.out.println("Current state of the portfolio: ");
        System.out.println(myPortfolio);
        storageService.savePortfolio(myPortfolio); // saving initial state
        System.out.println("---------------*************---------------");


        AnalysisEngine analysisEngine = new AnalysisEngine();
        analysisEngine.runAnalysis("AAPL");


        // Creating hardcoded Stocks for testing purposes:
        // (tickerSymbol, companyName, quantity, purchasePrice)
        // purchase price implying at what price the person bought the shares, whereas the total value of the portfolio is the current amount 
        // to be earned by the portfolio if he sells all his shares 
        
    }
}
