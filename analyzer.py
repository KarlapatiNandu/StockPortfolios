import sys
import yfinance as yf
import pandas as pd
import matplotlib.pyplot as plt

def analyze_stock(ticker):
    #Fetching historical data for the ticker symbol entered
    try:
        stock_data = yf.download(ticker,period="1y")

        if stock_data.empty:
            print(f"Error, no data found for the tickerSymbol '{ticker}', might be an invalid symbol")
            return
        #Simple mean average - sma of closing prices over 50days and 200 days
        stock_data['SMA_50'] = stock_data['Close'].rolling(window=50).mean()
        stock_data['SMA_200'] = stock_data['Close'].rolling(window=200).mean()

        plt.style.use('fivethirtyeight')
        plt.figure(figsize=(12,6)) # (width,height)

        #plotting the data:
        plt.plot(stock_data['Close'],label = 'Close Price')
        plt.plot(stock_data['SMA_50'], label = '50-Day SMA')
        plt.plot(stock_data['SMA_200'],label = '200-Day SMA')

        plt.title(f'{ticker} Stock Price and Moving Averages')
        plt.xlabel('Data')
        plt.ylabel('Price (USD)')
        plt.legend()
        plt.grid(True)
        plt.tight_layout()

        #Saving the plot to a file:
        output_filename = f'{ticker}_chart.png'
        plt.savefig(output_filename)

        print(f"Success : Chart saved as {output_filename}")
    
    except Exception as e:
        print(f"An error occured: {e}")

# If the file is run directly from the command line then the default __name__ is set to __main__ 
if __name__ == "__main__":
    if len(sys.argv) > 1:
        ticker_symbol = sys.argv[1].upper() # i.e if the ticker symbol is entered directly in the command prompt
        analyze_stock(ticker_symbol)
    else:
        print("Usage: python analyzer.py <TICKER_SYMBOL>")
