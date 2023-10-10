# Read Me, I'm useful.
## Title of the Application 
This application is Poseidon Capital Resources.
It serves as an entity handling tool.

## Description of the Application
### What does this Application do?
This application was created by Poseidon Inc. to handle financial entities (bids, trades, ratings, etc...).
It respects the MVC approach as it has controller layers for all entities as well repository layers to handle data persistence.
Indeed, it uses the CRUD methods to handle the entities.

### Which features will we implement in the future?
We intend to develop several features to improve and expend the use of this Application.
- To create different pages for regular users and admins.
- To implement different services based on what profile is using the Application.
- To improve the front-end visuals for better clarity.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
See deployment for notes on how to deploy the project on a live system.

## Prerequisites
What software you need to use the app and how to install them:

- Java 11
- Maven 3.11.0
- Mysql 8.0.34

### Installing
A step by step series of examples that tell you how to get a development environment running:

#### 1 > Install Java:
https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

#### 2 > Install Maven:
https://maven.apache.org/install.html

#### 3 > Install MySql:
https://dev.mysql.com/downloads/mysql/

After downloading the mysql 8.0.34 installer and installing it, you will be asked to configure the password for the default root account.

This code uses the default <b>root</b> account to connect, and the password can be set as <b>rootroot</b>.

## Running App

### Creating the Database and Data
Post installation of MySQL, Java and Maven, you will have to set up the tables and data in the data base.

To create the database, please run the sql commands present in the data.sql file under the \doc folder in the code base.

A database named "demo" should now be created with all the tables needed.
Three users are already created for example purposes.

### Import the code into your IDE
Finally, you need to import the code into an IDE of your choice and run the Application.java to launch the application.

### Change database user credentials
If you add another user or modify the current one make sure to change the same in the code.
Code to change if user's credentials are updated or if a new user is created:
- code file location : src/main/resources/<b>application-properties</b>
- username to change : spring.datasource.username=your_username
- passsword to change : spring.datasource.password=your_password

## Using the App
To create you own user go to the home page (example : if you're using localhost it is http://localhost:8080/).
Then go to user management and click on <b>Add New</b>.
You will then be able to login with your own credentials.

Consult the javadoc to understand how to use the requests to handle the different entities (bids, trades, etc...)

## Tests : Application viability
### Cover rate
We have implemented unit tests and integation tests to ensure the quality and robustness of our application.

With more than 130 tests covering our codes and scenarios:
- We have a cover rate of 100% for the controller and repository layers.
- We have taken into account the nominal cases but also the borderline ones to ensure that Users
will be accompanied even when an error shows up.

### How to run the tests

The existing tests need to be triggered from maven-surefire and maven-failsafe plugin while we try to generate the final executable jar file.

To run the tests from maven, go to the folder that contains the pom.xml file and execute the below command.
- <i>mvn clean test</i> in order to run the unit tests and generate the unit-test cover report located in
<b>/target/site/jacoco-unit-test-coverage-report</b>
- <i>mvn clean verify</i> in order to run the unit and integration tests and generate different reports
(located in <b>/target/site</b>):
    - the unit-test cover report
    - the integration-test cover report
    - the merged-test cover report (both unit and integration test merge together)

### How to read the test reports
When in a test report file (for example <b>/target/site/jacoco-unit-test-coverage-report</b>), click on the index file to open a web page showing:
- the global cover rate
- the the cover rate associated with each class and methods (if you need to go into details).
