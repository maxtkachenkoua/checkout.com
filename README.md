# Payment Processing Application [Back End Server]

This project is a Spring Boot based rest api server that handles user authentication and payment processing. The
application integrates with a frontend client for payment status tracking and utilizes the Checkout.com API for payment
processing.

Frontend source code: https://github.com/maxtkachenkoua/checkout.com-client

## Tech Stack

- **Backend**: Java 17, Spring Boot, Spring Security, Postgres DB, flyway
- **Backend Integration**: RESTful API (Rect based client)
- **Payment Gateway**: Checkout.com

## Features

- **User Authentication**: Users can log in to the application using their username and password.
- **Payment Processing**: Users can submit payment details, which are processed via the Checkout.com API.
- **Tokenizing card info**: Users can choose an option to process payment request using tokenized card information
- **Payment Status Tracking**: The application tracks the payment status and provides real-time updates to the user.
- **Failure Handling**: If a payment fails, the user is notified and provided with relevant details.
- **Payment status tracking scheduler**: As far as payment process is asynchronous by its nature, scheduler allows to
  protect payment status tracking in case of checkout.com callback notification failures

## Environment setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/maxtkachenkoua/checkout.com
2. **Install postgres db locally, set up an empty checkout.com schema**
3. **In case of using default TEST profile, it is very important to specify the URL of the client application, as checkout.com will use that ip address for success and failure url redirects**
   ```bash
   client:
     allowed-origin: ${ENV_CLIENT_ALLOWED_ORIGIN:http://95.216.224.218:3000}
4. **Use IDE of your choice (Intellij IDEA etc) or maven to build and application**
   ```bash
   mvn clean install
5. **Application supports TEST and PROD environments by exposing environment-specific configurations through
   appliction-profile.yml and environment variables**
6. **Start React based front end client**


