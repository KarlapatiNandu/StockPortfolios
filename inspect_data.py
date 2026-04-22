import yfinance as yf
import pandas as pd

ticker = "AAPL"
data = yf.download(ticker, period="1mo")
print("Data columns:", data.columns)
print("Data head:\n", data.head())
print("Close column type:", type(data['Close']))
print("Close column values:\n", data['Close'].head())
