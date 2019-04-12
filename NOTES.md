#### Confluent exercise

The purpose of the following take-home assignment is to demonstrate a candidate's:
* Understanding of customer use cases
* Confluent Platform value proposition
* Technical depth in products and end-to-end solutions

Using the Confluent Platform quickstart at https://docs.confluent.io/current/quickstart/index.html  (either for local install or Docker) as the context for your analysis, 

at a high level design a follow-up tutorial that

(a) integrates the UDF capabilities of KSQL (https://docs.confluent.io/current/ksql/docs/developer-guide/udf.html, https://www.confluent.io/blog/build-udf-udaf-ksql-5-0)
(b) provides a compelling use case 
(c) integrates any other relevant components of the Confluent Platform

Explain the technical components—
* how does it work
* the value proposition
* how does it address real customer use cases.
* It is not required to code, implement, or validate the solution, but you certainly may!

Please limit to 1-2 hours
Timebox
* 1 hour for proposal authoring
* 1 hour for sample project / code creation including UDF examples and (hopefully tests)

#### Idea: Stock Market Pricer
Sample market data prices, JSON format to make it simpler to start

Create GitHub repository
Write tutorial proposal in Markdown in README.md
Add code to repository if time

Add Stock prices to sample data generator
* https://github.com/confluentinc/avro-random-generator
* https://github.com/confluentinc/kafka-connect-datagen/blob/master/src/main/resources/users_schema.avro

build a datagen schema, stockquote_schema.avro
ksql-datagen schema=./stockquote_schema.avro format=json topic=stockquotes key=ticker

From stockquote stream
* UDF  to extract a custom price (Bid/Ask Mid | VWAP)
* UDAF to extract stateful Exponential Moving Average

EMA=Price(t)×k+EMA(y)×(1−k)
where:
t=today
y=yesterday
N=number of days in EMA
k=2÷(N+1) 
https://www.dummies.com/personal-finance/investing/stocks-trading/how-to-calculate-exponential-moving-average-in-trading/

#### Feedback to Product Teams
KSQL UDF jar deployment documentation is confusing around the ext folder, where it should be, if it’s a default installation it’s not clear where it should be created or what the config should be modified to.  Investigation of the sql-server log verified it was the installdir/ext

Stateful aggregate function (UDAF)
An aggregate function that takes N input rows and returns one output value. During the function call, state is retained for all input records, which enables aggregating results. When you implement a custom aggregate function, it's called a User-Defined Aggregate Function (UDAF). 
* Documentation is unclear on how an aggregate function takes N records or shows samples of parameterizing (just on the SQL command itself i guess)?

#### Further ideas for tutorial
* Make a Kafka Connector to generate sample data instead of asking reader to use CLI data generator on the console
* Potentially materialize the price and the VWAP and then materialize into a Table of signals, key by SYMBOL, expensive or cheap stock price
* Diagram to show components, connections, and dataflows


#### Tools involved 
* Confluent data generator with custom schema and generation rules
* Kafka
* KSQL
* UDF/UDAF

