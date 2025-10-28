import sys
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.preprocessing import PolynomialFeatures
import yfinance as yf
import pandas as pd
import matplotlib.pyplot as plt
import warnings

try:
    # statsmodels used for ARIMA implementation
    from statsmodels.tsa.stattools import adfuller
    from statsmodels.tsa.arima.model import ARIMA
except Exception:
    adfuller = None
    ARIMA = None
    # we'll print a helpful message later if these aren't available

def analyze_stock(ticker):
    #Fetching historical data for the ticker symbol entered
    try:
        stock_data = yf.download(ticker, period="2y")  # Use 2 years of data for better ARIMA stability

        if stock_data.empty:
            print(f"Error, no data found for the tickerSymbol '{ticker}', might be an invalid symbol")
            return
        #Simple mean average - sma of closing prices over 50days and 200 days
        stock_data['SMA_50'] = stock_data['Close'].rolling(window=50).mean()
        stock_data['SMA_200'] = stock_data['Close'].rolling(window=200).mean()
        # We'll perform a backtest: train on data up to one month before the last available date
        # then forecast that final month and compare two methods: polynomial (existing) and ARIMA.
        df = stock_data[['Close']].copy()

        # Use last 30 trading days as the test period
        test_horizon = 30
        if len(df) < test_horizon + 10:
            print("Not enough data to perform a 30-day backtest. Need at least 40 trading days.")
            return

        train = df.iloc[:-test_horizon].copy()
        test = df.iloc[-test_horizon:].copy()

        # ---------------- Polynomial baseline (similar to original, but trained on window before cutoff)
        # We'll use up to last 60 days of training data to fit the polynomial for fairness with original code
        poly_window = 60
        poly_train = train.tail(poly_window).copy()
        poly_train = poly_train.reset_index(drop=True)
        poly_train['day_index'] = np.arange(len(poly_train))

        X_poly_train = poly_train[['day_index']]
        y_poly_train = poly_train[['Close']]

        degree = 2
        poly_features = PolynomialFeatures(degree=degree)
        Xp = poly_features.fit_transform(X_poly_train)
        poly_model = LinearRegression()
        poly_model.fit(Xp, y_poly_train)

        # prepare future indices for prediction: these correspond to the test period
        future_indices = np.arange(len(poly_train), len(poly_train) + len(test))
        X_future = poly_features.transform(future_indices.reshape(-1, 1))
        poly_preds = poly_model.predict(X_future).flatten()
        poly_pred_series = pd.Series(poly_preds, index=test.index)

        # ---------------- ARIMA model
        if ARIMA is None or adfuller is None:
            print("statsmodels is required for ARIMA forecasting. Please install it: pip install statsmodels")
            arima_pred_series = None
            arima_order = None
        else:
            # Convert prices to log returns which are more suitable for ARIMA
            log_prices = np.log(train['Close'])
            returns = log_prices.diff()  # Calculate log returns using pandas diff
            series_for_arima = returns.dropna()  # Remove the first NaN from diff
            d = 0  # Returns are usually stationary, so no differencing needed
            
            # Verify we have enough data (minimum 252 trading days) for reliable ARIMA
            if len(series_for_arima) < 252:
                print("Warning: Less than 1 year of training data. ARIMA estimates may be unstable.")
            
            try:
                # Verify stationarity of returns
                adf_p = adfuller(series_for_arima.dropna())[1]
                if adf_p > 0.05:
                    print("Warning: Returns may not be stationary (p={:.4f})".format(adf_p))
                # Use simple ARIMA(1,1,1) model which is typically sufficient for stock prices
                # and more numerically stable than higher orders
                best_order = (1, d, 1)  # p=1, d=1, q=1
                warnings.filterwarnings('ignore')

                arima_order = best_order
                arima_model = ARIMA(series_for_arima, order=arima_order)
                arima_res = arima_model.fit()
                # Forecast returns
                return_forecast = arima_res.forecast(steps=len(test))
                
                # Convert returns forecast back to prices
                last_price = train['Close'].iloc[-1]
                forecasted_prices = []
                current_price = last_price
                
                for ret in return_forecast:
                    # Convert log return to price multiplier and apply
                    price_multiplier = np.exp(ret)
                    next_price = current_price * price_multiplier
                    forecasted_prices.append(next_price)
                    current_price = next_price
                
                arima_pred_series = pd.Series(forecasted_prices, index=test.index)
            except Exception as e:
                print(f"ARIMA fit failed: {e}")
                arima_pred_series = None
                arima_order = None

        # ---------------- Evaluation metrics
        def metrics(true, pred):
            true = np.array(true)
            pred = np.array(pred)
            mae = np.mean(np.abs(true - pred))
            rmse = np.sqrt(np.mean((true - pred) ** 2))
            with np.errstate(divide='ignore', invalid='ignore'):
                mape = np.mean(np.abs((true - pred) / true)) * 100
            return mae, rmse, mape

        poly_metrics = metrics(test['Close'].values, poly_pred_series.values)
        arima_metrics = None
        if arima_pred_series is not None:
            arima_metrics = metrics(test['Close'].values, arima_pred_series.values)

        # ---------------- Plotting
        plt.style.use('fivethirtyeight')
        plt.figure(figsize=(14, 7))
        plt.plot(train.index, train['Close'], label='Train (historical)')
        plt.plot(test.index, test['Close'], label='Test (actual)', color='black')
        plt.plot(poly_pred_series.index, poly_pred_series.values, '--', label='Polynomial forecast', color='red')
        if arima_pred_series is not None:
            plt.plot(arima_pred_series.index, arima_pred_series.values, '--', label=f'ARIMA{arima_order} forecast', color='green')

        plt.plot(stock_data['SMA_50'], label='50-Day SMA')
        plt.plot(stock_data['SMA_200'], label='200-Day SMA')

        plt.title(f'{ticker} — Backtest: Forecasting last {test_horizon} trading days')
        plt.xlabel('Date')
        plt.ylabel('Price (USD)')
        plt.legend()
        plt.grid(True)
        plt.tight_layout()

        output_filename = f'{ticker}_backtest_comparison.png'
        plt.savefig(output_filename)

        # Print metrics summary
        print(f"Backtest saved to {output_filename}")
        print("Polynomial (baseline) — MAE: {:.4f}, RMSE: {:.4f}, MAPE: {:.2f}%".format(*poly_metrics))
        if arima_metrics is not None:
            print("ARIMA{} — MAE: {:.4f}, RMSE: {:.4f}, MAPE: {:.2f}%".format(arima_order, *arima_metrics))
            # Decide which is better by RMSE primarily
            if arima_metrics[1] < poly_metrics[1]:
                print("-> ARIMA performs better by RMSE on this backtest.")
            else:
                print("-> Polynomial baseline performs better by RMSE on this backtest.")
        else:
            print("ARIMA forecast not available (statsmodels missing or fit failed).")
    
    except Exception as e:
        print(f"An error occured: {e}")

# If the file is run directly from the command line then the default __name__ is set to __main__ 
if __name__ == "__main__":
    if len(sys.argv) > 1:
        ticker_symbol = sys.argv[1].upper() # i.e if the ticker symbol is entered directly in the command prompt
        analyze_stock(ticker_symbol)
    else:
        print("Usage: python analyzer.py <TICKER_SYMBOL>")
