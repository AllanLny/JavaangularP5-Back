# Yoga App

## Description

Yoga App is a full-stack application designed to help users manage and participate in yoga sessions. The application provides functionalities for user management, session management, and participation tracking.

## Features

- User registration and authentication
- Create, update, and delete yoga sessions
- Participate in and withdraw from sessions
- View all available sessions

## Installation

To install and run the project locally, follow these steps:

1. **Clone the repository:**
    ```sh
    git clone https://github.com/AllanLny/JavaangularP5-Back
    cd YogaApp
    ```


2. **Build the project using Maven:**
    ```sh
    mvn clean install
    ```

3. **Run the Spring Boot application:**
    ```sh
    mvn spring-boot:run
    ```

## Running Tests

### Unit Tests

To run the unit tests, use the following command:
```sh
mvn test
 ```

### Integration Tests

To run the integration tests, use the following command:

```sh
mvn verify
```


### Code Coverage
to generate the code coverage report, use the following command:
```sh
mvn clean test
```
The code coverage report will be generated in the target/site/jacoco directory.

- Dependencies
- Spring Boot
- Spring Data JPA
- H2 Database (for testing)
- JUnit 5
- Mockito
- JaCoCo