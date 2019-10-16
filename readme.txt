This file contains clear step-by-step instructions on how to build, deployment, test and configure the application.

It includes below sections :

>> Introduction
>> Software requirements
>> Build steps
>> Launch application with Maven command
>> Deploy and run through .jar file
>> Test setup
>> Configurations
>> Logging



*******************  INTRODUCTION  *******************

In an Application submission center, the applicant visits one of token generation counters, where after successful
verification of applicant's document a token number is generated and assigned to a service counter.

This application starts a RESTful webservice which accepts requests from users to generate token after successful document
verification. It assigns a service counter to generated token. The assigned service counter is dedicated to serve either
NORMAL or PREMIUM category tokens. The token category is passed in token generation request by user.

Internally, each service counter is subscribed to its corresponding queue name(a kafka topic) and the generated token message is
published to its assigned service counter's queue name.
Note - You can also get more detailed action, requests and responses in application.log file. See Logging section in this file.

Through below settings you can provide the required information to application:

#  To provide the number of token generation counters
        token.generation.counters.size : 4

#  To provide the service counter ID and the token category to be served by it
        service.counter.id.category.pairs : SC1:NORMAL,SC2:NORMAL,SC3:PREMIUM,SC4:PREMIUM

#  Through below setting you can provide the queue name for each service counter, The same queue name will be used as
   kafka topic from where the Service counter can subscribe the tokens to be served.
   The queue names must be in same order the service counters are being defined in "service.counter.id.category.pairs"
   setting

        service.counter.queue.names : SC1,SC2,SC3,SC4




******************* SOFTWARE  REQUIREMENTS *******************

* Java - jdk 1.8 or higher
* Apache Maven - 3.3.9 or higher

   -- To install Java you can follow below link:
         https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html
   -- To install maven you can follow below link:
         https://maven.apache.org/install.html

* Kafka server - 2.3.0

   -- Kafka uses ZooKeeper so you need to first start a ZooKeeper server if you don't already have one
   -- To install and run zookeeper and kafka you can follow below link:
         https://kafka.apache.org/quickstart

   -- By default, zookeeper runs on port no. 2181 and kafka server runs on port 9092.
      So ensure that these ports are free otherwise you can also change the these default ports from the properties files
      of zookeeper and kafka which are present under config folder.






*******************  BUILD  STEPS  **********************

-- Open command prompt and navigate to project folder where pom.xml file is present
-- Run command:
            mvn clean package
   This command will clean and build project. You can find distributable jar file under target folder.




******************* LAUNCH APPLICATION WITH MAVEN COMMAND ***********************

-- Start zookeeper and kafka server
-- Open command prompt and navigate to project folder where pom.xml file is present
-- You can configure the kafka server port in application.properties file present at below location

        src/main/resources/application.properties

   Default value: localhost:9092

-- Run below command to launch the Token management application

       mvn spring-boot:run




******************* DEPLOY AND RUN THROUGH JAR FILE ***********************

-- Build project
         mvn clean package
-- Copy the tms-0.0.1.jar file present under <PROJECT_HOME>/target/ folder
-- Paste the jar file in a folder(say "run" directory) from where you want to launch the application.
-- If you want to launch application with custom configurations, You can create a config folder inside the "run" directory
    and put application.properties file inside it (You can find application.properties file under Project's resources directory).
-- Start zookeeper and kafka server
-- Open command line and navigate to "run folder"
-- Run below command
               java  -jar  tms-0.0.1.jar

-- You can verify the application configurations and actions in log files, See Logging section in this file.





******************* TEST  SETUP  **********************

-- You need a REST client to test the webservice, You can use Postman client to send HTTP POST request.


   Endpoint:  http://localhost:8080/brillio/tms/generateToken
   Sample Request body json:

        {
          "applicant": {
            "name": "Prashant"
          },
          "document": {
            "applicantName": "Prashant",
            "documentNum": 1
          },
          "tokenCategory": "PREMIUM"
        }

   Response:

       {
         "token": {
           "tokenNumber": 5,
           "tokenCategory": "PREMIUM"
         },
         "applicant": {
           "name": "Prashant"
         },
         "serviceCounter": "SC4",
         "errorMessage": ""
       }

-- For successful document verification, the applicant name and the applicant name in document must be same(case insensitive).

   Example:

   Endpoint:  http://localhost:8080/brillio/tms/generateToken
   Sample Request body json:

           {
             "applicant": {
               "name": "Prashant"
             },
             "document": {
               "applicantName": "Prashan",
               "documentNum": 1
             },
             "tokenCategory": "PREMIUM"
           }

   Response:

        {
          "token": null,
          "applicant": {
            "name": "Prashant"
          },
          "serviceCounter": null,
          "errorMessage": "Invalid documents, applicant name mismatch"
        }

  For more detailed info. you can see application.log file, See Logging section in this file.






******************* CONFIGURATIONS **********************

You can configure the application and kafka server properties in application.properties file.

        src/main/resources/application.properties

Note- Below properties are present with their default values

File- application.properties : >>>>>>>>

-- To configure the number of token generation counters
       token.generation.counters.size : 4
-- To configure the service counter ID and its category
       service.counter.id.category.pairs : SC1:NORMAL,SC2:NORMAL,SC3:PREMIUM,SC4:PREMIUM
-- To configure service counter queue name (kafka topic)
       service.counter.queue.names : SC1,SC2,SC3,SC4


-- kafka server's address <HOST>:<PORT>
       bootstrap.servers=localhost:9092
-- A unique string that identifies the consumer group
       group.id=brillio
-- If true the consumer's offset will be periodically committed in the background.
       enable.auto.commit=false
-- The frequency in milliseconds that the consumer offsets are auto-committed to Kafka
       auto.commit.interval.ms=1000
-- The timeout used to detect consumer failures
       session.timeout.ms=30000
-- The expected time between heartbeats to the consumer
       heartbeat.interval.ms=10000
-- The maximum number of records returned in a single poll when subscribed to a kafka topic
       max.poll.records=100

-- To monitor kafka server running status
    server.monitor.interval.millis=5000





******************* LOGGING **********************

-- When you run the application, It will create two log files under "LOGS" directory. You can use these for debugging
   purposes.

   - application.log : Traces the application actions like- application configurations, HTTP request, token information,
                     assigned service counter information. All the traces can be related with each other through the requestId
                     token number.
   - monitor.log     : Traces the kafka server availability status.