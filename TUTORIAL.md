### Using KSQL User Defined Functions for custom stock market pricing

#### Summary
Confluent KSQL User Defined Functions (UDF & UDAF) provide KSQL developers the opportunity to implement custom business 
logic that can be exposed to KSQL users via an aparent built in function.

* todo: link to ksql built-ins and udf documentation
* todo: very brief discussion on stock prices and technical analysis

Using a topic of streaming _simulated_ stock market prices, we will use KSQL and custom UDF functions to convert the prices
to a volume weighted, and exponetially averaged, price stream.  This materialized stream of "smoothed" prices could be 
further processed by risk or trading applications. 

todo: Briely explain Volume Weighted Average Price (VWAP) and Exponential Moving Average (EMA).

#### Prerequistes
Details on Confluent Platform Local, links to documentation
Kafkacat installation

##### Step 1: Download and Start Confluent Platform
https://docs.confluent.io/current/quickstart/ce-quickstart.html#ce-quickstart

##### Step 2: Simulate Market Data
The Confluent platform ships with a handy data generation tool we will use to simulate market data prices.
Custom data formats and rules around simulated data generation can be specified with an Avro schema. 
Create a local avro schema file named: `stockquote_schema.avro` with the following contents:
```
{
  "namespace": "ksql",
  "name": "stockquote",
  "type": "record",
  "fields": [{
      "name": "ticker",
      "type": {
        "type": "string",
        "arg.properties": {
          "options": [
            "TICKER_A",
            "TICKER_B",
            "TICKER_C",
            "TICKER_D",
            "TICKER_E"
          ]}}},
    {
      "name": "bid",
      "type": {
        "type": "int",
        "arg.properties": {
          "range": {
            "min": 1,
            "max": 50
          }}}},
    {
      "name": "ask",
      "type": {
        "type": "int",
        "arg.properties": {
          "range": {
            "min": 51,
            "max": 100
          }}}},
    {
      "name": "bidQty",
      "type": {
        "type": "int",
        "arg.properties": {
          "range": {
            "min": 1,
            "max": 100
          }}}},
    {
      "name": "askQty",
      "type": {
        "type": "int",
        "arg.properties": {
          "range": {
            "min": 1,
            "max": 100
          }}}}
  ]
}
``` 
Execute the simulated market data producer 
```
ksql-datagen schema=./stockquote_schema.avro format=json topic=stockquotes key=ticker
```

View the topic of streaming prices (Ctrl-C to stop)
```
kafkacat -b localhost:9092 -t stockquotes -C
```

##### Step 3: Build and deploy the UDF VWAP Function
Create a Java project using your preferred build tool and IDE or editor.  Add the following file to the project, 
this is our custom UDF function to calculate VWAP.

```$xslt
package io.confluent.tutorials.udf;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

@UdfDescription(name = "vwap", description = "Volume weighted average price")
public class VwapUdf {

    @Udf(description = "vwap for market prices as integers, returns double")
    public double vwap(
        @UdfParameter(value = "bid")
        final int bid,
        @UdfParameter(value = "bidQty")
        final int bidQty,
        @UdfParameter(value = "ask")
        final int ask,
        @UdfParameter(value = "askQty")
        final int askQty) {
        return ((ask * askQty) + (bid * bidQty)) / (bidQty + askQty);
    }

    @Udf(description = "vwap for market prices as integers, returns double")
    public double vwap(
        @UdfParameter(value = "bid")
        final double bid,
        @UdfParameter(value = "bidQty")
        final int bidQty,
        @UdfParameter(value = "ask")
        final double ask,
        @UdfParameter(value = "askQty")
        final int askQty) {
        return ((ask * askQty) + (bid * bidQty)) / (bidQty + askQty);
    }
}
```

* Using your build tool create a JAR that includes the VWAP UDF function and any depenendencies.
* Copy the JAR to the Confluent Platform extensions folder in the Confluent install root (default: `<confluent-install-path>\ext`)
* Restart KSQL
  * `confluent stop ksql-server`
  * `confluent start ksq-server` 
  
##### Step 4: Transform the Stream with KSQL
Launch the KSQL CLI
```$xslt
➜ ksql

                  ===========================================
                  =        _  __ _____  ____  _             =
                  =       | |/ // ____|/ __ \| |            =
                  =       | ' /| (___ | |  | | |            =
                  =       |  <  \___ \| |  | | |            =
                  =       | . \ ____) | |__| | |____        =
                  =       |_|\_\_____/ \___\_\______|       =
                  =                                         =
                  =  Streaming SQL Engine for Apache Kafka® =
                  ===========================================

Copyright 2017-2018 Confluent Inc.

CLI v5.2.1, Server v5.2.1 located at http://localhost:8088

Having trouble? Type 'help' (case-insensitive) for a rundown of how things work!

ksql>
```

Show the available topics
```$xslt
ksql> show topics;

 Kafka Topic                                                                                   | Registered | Partitions | Partition Replicas | Consumers | ConsumerGroups
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
...
 stockquotes                                                                                   | false      | 1          | 1                  | 0         | 0
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
```

View the 'raw' stock quote data
```$xslt
ksql> print 'stockquotes';
Format:JSON
{"ROWTIME":1555086127774,"ROWKEY":"TICKER_E","ticker":"TICKER_E","bid":15,"ask":72,"bidQty":70,"askQty":46}
{"ROWTIME":1555086128165,"ROWKEY":"TICKER_E","ticker":"TICKER_E","bid":30,"ask":61,"bidQty":86,"askQty":95}
{"ROWTIME":1555086128223,"ROWKEY":"TICKER_D","ticker":"TICKER_D","bid":4,"ask":71,"bidQty":88,"askQty":35}
{"ROWTIME":1555086128639,"ROWKEY":"TICKER_D","ticker":"TICKER_D","bid":5,"ask":94,"bidQty":51,"askQty":19}
^CTopic printing ceased
```

Create a stock quote stream specifying the `stockquote` topic and that our stream will be in JSON format
```$xslt
ksql> create stream quote_stream(ticker varchar, bid int, ask int, bidqty int, askqty int) with (kafka_topic='stockquotes', value_format='json');

 Message
----------------
 Stream created
----------------
```

View the quote_stream stream:
```$xslt
ksql> select * from quote_stream;
1555086272719 | TICKER_E | TICKER_E | 45 | 89 | 18 | 92
1555086273140 | TICKER_E | TICKER_E | 4 | 82 | 84 | 33
1555086273458 | TICKER_A | TICKER_A | 3 | 64 | 57 | 72
1555086273479 | TICKER_E | TICKER_E | 49 | 90 | 53 | 57
^C1555086273508 | TICKER_C | TICKER_C | 13 | 67 | 34 | 20
Query terminated
```

Transform the prices into VWAP with the custom UDF function
```$xslt
ksql> select TICKER, vwap(BID, ASK, BIDQTY, ASKQTY) FROM quote_stream LIMIT 5;
TICKER_B | 24.0
TICKER_B | 38.0
TICKER_E | 18.0
TICKER_C | 11.0
TICKER_D | 33.0
Limit Reached
Query terminated
```

##### Step 5: Implement a stateful UDAF function
todo: do this


#### Next Steps
KSQL and UDF provide a wonderful high level abstraction for rapid building of custom streaming applications.  For
applications that require even more flexibility dig into Kafka Streams (link).
todo: link to deeper documentation on KSQL, UDF, and KStreams
