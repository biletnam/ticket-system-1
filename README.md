# ticket-system
Ticket System that provides different ticket services to hold and reserve seats

It exposes the ticket service functionality through rest api's which provides a JSON response

This is a SpringBoot application which can be run using the below maven comand mvn spring-boot:run

setAdminValues API (POST)

This api let's the admin set the totalNumberOfSeats in the venue,  expiryTimeInMinutes for seatHold and bestSeatCriteria for which valid values are 
("TOPTOBOTTOM", "BOTTOMTOTOP")

TOPTOBOTTOM which corresponds to higher seat number seat's are the best seats
BOTTOMTOTOP which corresponds to lower seat number seat's are the best seats

API can be reached with the below path 

http://localhost:8080/ticketSystem/setAdminValues

sample input for the service in body is 

{
	"totalNumberOfSeats" : 100,
	"expiryTimeInMinutes" : 1,
	"bestSeatCriteria" : "TOPTOBOTTOM"
} 

response is 201 created usually returns a succesful creation of the record

{
    "id": 50,
    "totalNumberOfSeats": 100,
    "expiryTimeInMinutes": 1,
    "bestSeatCriteria": "TOPTOBOTTOM"
}

addHold API(POST)

This api let's the user addHold in the venue to hold seats takes numberofSeats to hold and the customer email as input parameters 

API can be reached with the below path 
http://localhost:8080/ticketSystem/addHold 

sample input for the service in request body is
{
	"numSeats" : 3,
	"customerEmail" : "abcd@gmail.com"
} 

response is 201 created usually returns a succesful creation of the hold for the requested number of tickets
{
    "id": 50,
    "customerEmail": "abcd@gmail.com",
    "numberOfSeats": 3,
    "status": "HOLD",
    "holdPlacedTime": {
        "epochSecond": 1516140569,
        "nano": 235000000
    },
    "expired": false
}

reserve API(POST)
This api let's the user reserve the seats that they have put on hold  

API can be reached with the below path 
http://localhost:8080/ticketSystem/reserve 

sample input for the service in request body is

{
	"seatHoldId" : 50,
	"customerEmail" : "abcd@gmail.com"
} 

response is 201 created usually returns a succesful creation of reservation with a reservation code
{
    c29e458b-a9f2-49a7-a58d-9036cd3f997a
}


