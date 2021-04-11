# crud-java
Create, update, read and delete operations for Stocks

## CREATE
URL: http://localhost:8080/stock 

HTTP Method: POST

BODY:
`{
 "name": "PETR3",
 "quotes": [22.2,22.37,22.11]
} `
## READ ONE
URL: http://localhost:8080/stock?name=PETR3

KEY: name

VALUE:PETR3

HTTP Method: GET 
## READ ALL
URL: http://localhost:8080/stock 

HTTP Method: GET 

## UPDATE
URL: http://localhost:8080/stock/PETR3

HTTP Method: PATCH

BODY: 
`{
 "quotes": [21]
} `

## DELETE
URL: http://localhost:8080/stock/PETR3

HTTP Method: DELETE 
