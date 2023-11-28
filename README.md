# Crypto Stats

A java web application that gathers statistics of cryptocurrency.

## CSV Format
* In the CRYPTO_NAME_values.csv (e.g. BTC_values.csv) you can find one month’s prices for
  one crypto in USD.
* The file has the following format:
    timestamp, symbol, price

## Requirements for the recommendation service
* Reads all the prices from the csv files
* Calculates oldest/newest/min/max for each crypto for the whole month
* Exposes an endpoint that will return a descending sorted list of all the cryptos,
comparing the normalized range (i.e. (max-min)/min)
* Exposes an endpoint that will return the oldest/newest/min/max values for a requested
crypto
* Exposes an endpoint that will return the crypto with the highest normalized range for a
specific day

## Extra mile for recommendation service (optional):
* Malicious users will always exist, so it will be really beneficial if at least we can rate limit
them (based on IP)

## Installations needed

* Java 17
* Spring 3
* Gradle

## Running

### Build & run 

* Clone the repo


* Run
```
$ ./gradlew bootRun
```

* Run with spring profile as "dev" (default)
```
$ ./gradlew bootRun --args='--spring.profiles.active=dev'
```

* Run with spring profile "prod"
```
$ ./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Spring Cloud Config server

* App makes use of spring cloud config server in order to load configuration dynamically.
* In case you dont want to use config server, just populate application.yml with supported currency.

### Build & run using Docker

* Build docker image
```
$ docker build -t crypto-stats .
```

* Run the container to expose app on port 9090
```
$ docker run -it -p 9090:9090 crypto-stats
```

### Access the app

App will be accessible at: 
```
http://localhost:9090
```

### Documentation

Open API documentation accessible at: 
```
http://localhost:9090/api-docs
```

Swagger UI accessible at: 
```
http://localhost:9090/swagger-ui/index.html
```

