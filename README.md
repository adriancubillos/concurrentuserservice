# Concurrent User Service

This project is a high-performance, non-blocking REST API for managing a collection of users. It is built using Vert.x for the web layer and Spring Boot for dependency injection, providing a robust and scalable solution for handling concurrent requests.

The service uses an in-memory, thread-safe data store.

## Architecture

The application follows a layered architecture:

-   **Web Layer (Vert.x)**: The `MainVerticle` sets up a non-blocking HTTP server and a router. It defines all API endpoints and forwards requests to the appropriate handler.
-   **Handler Layer**: The `UserHandler` class contains the logic for handling HTTP requests, parsing request bodies, validating data, and calling the service layer.
-   **Service Layer**: The `UserService` interface defines the business logic contract, and `InMemoryUserService` provides the implementation, orchestrating calls to the data store.
-   **Data Store Layer**: The `UserDatastore` interface defines the contract for data storage. `InMemoryUserDatastore` provides a thread-safe, in-memory implementation using `ConcurrentHashMap` and `AtomicLong` for ID generation.

## API Endpoints

The following endpoints are available:

| Method   | Path                | Description                          |
| :------- | :------------------ | :----------------------------------- |
| `GET`    | `/users`            | Retrieves a list of all users.       |
| `POST`   | `/users`            | Creates a new user.                  |
| `GET`    | `/users/{id}`       | Retrieves a single user by their ID. |
| `PUT`    | `/users/{id}`       | Updates a user's name and email.     |
| `PUT`    | `/users/{id}/email` | Updates a user's email only.         |
| `DELETE` | `/users/{id}`       | Deletes a user by their ID.          |

## Validation and Error Handling

The API includes robust validation and error handling:

-   **Input Validation**: Incoming data for user creation and updates is validated to ensure that `name` and `email` fields are not blank and that the `email` has a valid format.
-   **Duplicate Email Check**: The service prevents the creation of users with duplicate emails and also prevents a user from updating their email to one that is already in use by another user (`409 Conflict`).
-   **Not Found Errors**: Accessing, updating, or deleting a non-existent user will result in a `404 Not Found` error.
-   **Malformed JSON**: Requests with invalid JSON will be rejected with a `400 Bad Request`.

## Getting Started

To get started with this project, clone the repository to your local machine:

```sh
git clone https://github.com/adriancubillos/concurrentuserservice.git
cd concurrentuserservice
```

## How to Run

### Prerequisites

-   Java 21
-   Maven

### Running Locally

1.  Open a terminal in the project root.
2.  Run the application using the Maven wrapper:

    ```sh
    ./mvnw spring-boot:run
    ```

The service will be available at `http://localhost:8080`.

## How to Test

### Unit Tests

To run the unit tests for this project, open a terminal in the project root and use the Maven wrapper:

```sh
./mvnw test
```

Alternatively, if you have Maven installed globally on your system, you can use the following command:

```sh
mvn test
```

### End-to-End API Tests

The project includes two ways to perform end-to-end testing of the API.

#### Using the Test Script

The project includes a comprehensive shell script for end-to-end testing of the API.

### Prerequisites

-   `curl`
-   `jq`

### Running the Test Script

1.  Make sure the application is running.
2.  In a new terminal, make the script executable:

    ```sh
    chmod +x src/test/java/com/amex/assesment/concusers/apirequests/test-api.sh
    ```

3.  Run the script:

    ```sh
    ./src/test/java/com/amex/assesment/concusers/apirequests/test-api.sh
    ```

#### Using the .http file

As an alternative to the shell script, you can use the `users.http` file located at `src/test/java/com/amex/assesment/concusers/apirequests/users.http` to send requests directly from your IDE.

**Prerequisites:**

- An IDE with a REST client extension that supports `.http` files (e.g., the [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) extension for Visual Studio Code).

**Running the Requests:**

1.  Make sure the application is running.
2.  Open the `src/test/java/com/amex/assesment/concusers/apirequests/users.http` file in your IDE.
3.  Your IDE should display a "Send Request" button or link above each HTTP request block. Click it to execute a request and see the response directly in the editor.

## Containerization with Docker

This application is configured to be built and run as a Docker container.

**Prerequisites:**
- Docker must be installed and running on your system.

**Building the Docker Image:**

Open a terminal in the project root directory and run the following command:

```sh
docker build -t concurrentuserservice:latest .
```

This command will build a Docker image named `concurrentuserservice` with the `latest` tag.

**Running the Docker Container:**

Once the image is built, you can run the application in a container with this command:

```sh
docker run -p 8080:8080 concurrentuserservice:latest
```

This will start the container and map port 8080 on your local machine to port 8080 inside the container. The service will be accessible at `http://localhost:8080`.
