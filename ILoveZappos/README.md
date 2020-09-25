## I Love Zappos - A Bitcoin Application

A native Android app written in Java that displays cryptocurrency price information from https://www.bitstamp.net. The application focuses on demonstrating the Use of various API's and prasing required data, use of recycler view and creating tasks and notification. 


I Love Zappos             |  Transaction History    |  Order Book  |  Price Alert Notification  
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
<img src="https://github.com/ssarfare/Android-MiniProjects/blob/master/ILoveZappos/media/Screenshot_20200925-182015.jpg" width="300" height="300" /> | <img src="https://github.com/ssarfare/Android-MiniProjects/blob/master/ILoveZappos/media/Screenshot_20200925-194153.jpg" width="300" height="300" /> |  <img src="https://github.com/ssarfare/Android-MiniProjects/blob/master/ILoveZappos/media/Screenshot_20200925-182024.jpg" width="300" height="300"/> | <img src="https://github.com/ssarfare/Android-MiniProjects/blob/master/ILoveZappos/media/Screenshot_20200925-182109.jpg" width="300" height="300"/>


Features:

- Transaction History
1. Displays a graph of recent transaction histrory.
2. API to used: https://www.bitstamp.net/api/v2/transactions/btcusd/
3. Used mpAndroidChart to create the graph.

- Order Book
1. Display 2 tables using RecyclerViews; Bids and Asks.
2. API to used: https://www.bitstamp.net/api/v2/order_book/btcusd/

- Price Alert
1. Take price input from the user and store it using some sort of storing mechanism. Hit the api mentioned below every hour (in the background using a Service) and if the current bitcoin price has fallen below the specified price entered by the user, the app sends a notification which would open the app when clicked.
2. API to used: https://www.bitstamp.net/api/v2/ticker_hour/btcusd/