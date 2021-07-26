# Foreword

While the code is functional, there are some requirements not implemented due to the time constraint. 
Before diving in, a short list of what isn't included:
 * Exposing instruments as a stream. Currently, only available as a JSON response.
 * Current price of an instrument. All quotes are exposed per instrument with an associated time.
 * Hot instruments stream. 

# First Steps

## Prerequisites

 * Docker & Docker Compose

Failing that:

 * JDK 16
 * Maven 3.8.1
 * PostgreSQL 13.3
    * Username/password set to `postgres`
    * Database named `data`
    * Standard port exposed; `5432`

## Running

If Docker Compose is available, simply run the compose file:

```shell
trade-republic$ docker-compose up
```

If Docker Compose is not available, try the following commands:

```shell
trade-republic$ java -jar ./server/partner-service-1.0-all.jar &
trade-republic$ cd server
trade-republic/server$ mvn clean spring-boot:run
```

## Playing

The server is exposed on port `8081` instead of the default.

Swagger has been configured to expose instruments and related characteristics. 
Navigate to the [localhost URL](http://localhost:8081/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)
to get an overview.

# Design

## Instruments

 * When instruments are received as `DELETED`, a delete date is added to the associated instrument. 
   This allows ISINs to be reused.
 * Attempting to add a duplicate instrument will result in a no-op, and a warning will be logged.   


## Structure

The system is basically split in two:

 * Reactive websocket client reading from the `partner-service` and writing to a DB.
   
   Much of this is handled by configuring spring and telling it how to map from a websocket to a DB entity.
   
 * A rest controller exposing instruments, quotes and candlesticks.
   
   The point of interest here is probably the `CandlestickService`, which has quite a bit of logic.

I decided to skip the typical service layer when exposing simple instrument data. 
The candlestick transformation is the only point where a service actually made sense.

To separate concerns, there are several models included:
 * Incoming DTOs from the `partner-service`, generally with a `*DTO` suffix.
 * Application model, no suffix e.g. `Instrument`.
 * DB entity model, generally with a `*Entity` suffix.
 * Response DTOs, generally with a `*Response` suffix.

## API

The API is quite simple, only GET operations are exposed `instrument`s and related details:

 * `.../instruments`
 * `.../instruments/{isin}`
 * `.../instruments/{isin}/quotes`
 * `.../instruments/{isin}/candlesticks`

There is no pagination or limits imposed on data here, which there certainly should be.

## Testing

There are several unit tests giving good coverage of the core services.
Didn't get to a level of coverage that I would be happy with, but what's here will have to do.

It's probably worth pointing out that the tests provided validate not only happy paths, but also business logic where
required. 

Next steps would be to create an integration test covering the controller, related services and repositories. I had
planned to try out [Test Containers](https://github.com/testcontainers/) here, but ran out of time.

## Design Decisions

It would have been nice to go fully-reactive here, but I lack knowledge about reactive databases and producing event
streams for consumers. 

