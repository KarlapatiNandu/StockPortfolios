package com.nkfun.portfolio;

public interface MarketDataService {
    // every class that fetches live market data must have the getPrice method
    double getPrice(String tickerSymbol);
}
