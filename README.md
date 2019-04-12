### Planning document for an example KSQL UDF tutorial based on Stock Prices and basic technical analysis

Timebox:
* 1 Hour for tutorial planning
* 1 Hour for building

### Tutorial Layout
1. Give a summary of the tutorial including the use of KSQL and UDF with the example application a simple financial application using market prices and technical analysis for the UDF component
2. List prerequisites with links for reader to follow to fulfill them
3. Give a short list of instructions for the reader to bootstrap the environment with links to the existing documents to resolve issues.
4. Tutorial Content
5. Further Reading, References links, etc...

### Tutorial Content
1. Introduce Confluent KSQL data generator, explain custom data capabilities
1. Show the example data gen stock quote schema (see stockquote_scheam.avro)
1. Instruct how to run stock quote data generator in a free terminal
```ksql-datagen schema=./stockquote_schema.avro format=json topic=stockquotes key=ticker```
1. Show generated data topic, maybe use `kafkacat`
1. Explain the idea of KSQL functions, link to 'out of the box functions' as well as existing documentation on UDF & UDAF.
1. Briefly introduce VWAP as an alternative way of looking at stock price
1. Link to and explain the example VWAP calculation java code (Use github.com link, never done this?)
1. Provide instructions on compiling the VWAP java code and deploying to the KSQL server, with links to existing documentation.  Clarify where to actually deploy and explain the KSQL server extension property (i got hung up on this).
1. Instruct opening KSQL CLI in a free terminal
1. Show new topic and data in KSQL
1. Exemplify `CREATE STREAM stockquote_stream FROM (...` to show conversion of the raw TOPIC into a STREAM
1. Show how to use the `vwap` UDF function from a SELECT Statement
1. Briefly introduce technical analysis and different types of moving averages.
1. Revist the Java Project and introduce the UDAF EMA function that maintains statefulness between invocations and
1. Explain how to redeploy the new Java code with KSQL restart.
1. Show how to use the UDAF EMA function which, in turn, calls the VWAP function, to create an exponential moving average of the volume weight average of the stock prices.
1. Show a diagram of the components and their interactions with maybe a brief explanation of how the KSQL commands compile down into KStreams applications.
1. Provide links to existing KSQL documentation and explain how the Stock Ticker keyed data could be used in a TABLE to build a stateful store of current VWAP prices by ticker

### Value of Tutorial
* The aim of this tutorial is to get a reader from zero to functioning UDF example in 30 minutes or less with a realistic use case.  This provides value by curating developer productivity and aims to reduce developer frustration with the tools. 
* This tutorial narrows in on the UDF functions in KSQL which provides deep value to customers with extraordinary business logic.  This excercise shows that value by 'stringing' together both the UDF and UDAF capabilities into a single 'SELECT' statement using custom functions which Confluent would likely never build or deploy on their own.
* Financial companies, in particular, have a lot of proprietary or custom business logic.  This tutorial 'speaks the language' of developers in this space, more so than the typical 'web developer' tutorials in the current Confluent asset list.  This is valuable as a use case for a wide set of potential customers which, in my experience, have been slow to adopt event stream processing technologies.
* Simulated data in the financial space is critical for test systems.  The existing data generation tools are a compelling tool for a very rapid prototype of how a financial company could utilize the confluent platform.  I have some ideas on how the Avro generation tooling could be improved to more closely mimic things like financial transactions.

### Tool exposure
* This tutorial exposes the reader to Confluents efforts to build developer friendly tools.  Bootstrapping an environment with 'life like' data is key in my opinion.  This tutorial aims to exemplify the possiblity of adding that to their workflow w/ the Confluent tools by showcasing the data generation tool _with_ a custom schema.
* The bulk of this tutorial is mean to showcase the UDF and UDAF functions in KSQL which add obvious value add to any business that has non-trival business logic use cases.
* Expose the reader further to the KSQL command line. 

