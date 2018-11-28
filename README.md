# ChatMicroService


## Prerequisits

- MySQL installed
- DB named db_chat
- DB user with name: ```springuser``` and password: ```12345White!``` 

## Build & Run

#### Build and Run
You can build and run the application using:
```
./mvnw spring-boot:run 
```
#### Build
Or you can build the JAR file with:

```
./mvnw clean package
```
#### Run
Then you can run the JAR file:
```
java -jar target/<Name of the jar file>.jar
```


## Using REST service

### Messeges

#### To get chat messeges with specific service id and customer id:
```
GET http://localhost:8000/dialog/aquire/service/{servise_id}/customer/{customer_id}
```

The response should have this structure:
```
[  
   {
        "id": 1487
        "serviceId": 18,
        "customerId": 6,
        "time": "1998-06-18T10:40:35",
        "fromServiceProvider": true,
        "messegeBody": "Hello, I can do any dirty job for 5$, no more and no less"
    }, 
    {
        "id": 19980,
        "serviceId": 17,
        "customerId": 6,
        "time": "2015-11-05T02:17:00",
        "fromServiceProvider": false,
        "messegeBody": "Can you help me to wash my dirty kitten, I`ve tried almost everything"
    }
]
```

#### To save chat messege:
```
POST http://localhost:8000/dialog/save
```
The request body should have this structure:
```
{
    "serviceId" :  "1997",
    "customerId" : "1112",
    "messegeBody" : "Hello, I would like to clean my shoes, mister. Do you still provide this service?",
    "fromServiceProvider" : "false"
}
```
```fromServiceProvider``` means that messege was sent from service owner, if true, and vice versa otherwise. 

#### To get all dialogs' last message of the specific service provider:
```
GET http://dialog/aquire/service/{service_id}
```

#### To get all messages:
```
GET http://localhost:8000/dialog/aquire/all
```

### Comments

#### To get comments with specific service id 
```
GET http://localhost:8000/comments/aquire/service/{servise_id}
```
#### To save comment: 
```
POST http://localhost:8000/comments/save
```
The request body should have this structure:
```
{
    "serviceId" :  "1997",
    "customerId" : "1112",
    "commentBody" : "This individual does her job perfectly)0))00",
    "rating" : "1.487999"
}
```
#### To get all comments: 
```
GET http://localhost:8000/comments/aquire/all
```

