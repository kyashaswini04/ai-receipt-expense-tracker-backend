# AI Receipt Expense Tracker – Backend

A RESTful backend application developed using Spring Boot and Java for an AI-powered expense tracking system. It provides secure user authentication, expense management, monthly budget and salary tracking, loan management, and financial transaction management.

## Features

- **User Authentication:** Secure registration and login using JWT authentication.
- **Expense Management:** Create, retrieve, and manage personal expense records.
- **Receipt Management:** Backend support for receipt-related operations.
- **Monthly Salary Management:** Save and retrieve salary information separately for each month.
- **Budget Management:** Manage monthly budgets across different spending categories.
- **Financial Summary:** Calculate monthly expenses and remaining salary.
- **Money Transfers:** Manage money transfer records.
- **Payment Management:** Maintain payment-related records.
- **Loan Management:** Store and manage loan information.
- **Multi-User Data Isolation:** Ensure authenticated users access only their own financial records.

## Tech Stack

- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- MySQL
- JWT Authentication
- Maven
- REST APIs

## Project Structure

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/aireceiptexpensetracker/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── AiReceiptExpenseTrackerApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── Dockerfile
└── README.md
```

## Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/kyashaswini04/ai-receipt-expense-tracker-backend.git
```

### 2. Navigate to the project directory

```bash
cd ai-receipt-expense-tracker-backend
```

### 3. Configure MySQL

Create the database:

```sql
CREATE DATABASE ai_expense_tracker;
```

### 4. Configure environment variables

Configure the following environment variables:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ai_expense_tracker
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_mysql_password
JWT_SECRET=your_secure_jwt_secret
PORT=8080
```

Use your own database credentials and a secure JWT secret. Do not commit passwords or secrets to GitHub.

### 5. Run the application

On macOS/Linux:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend runs locally at:

```text
http://localhost:8080
```

## API Modules

| Module | Base route |
|---|---|
| Authentication | `/auth` |
| Expenses | `/expenses` |
| Receipts | `/receipts` |
| Monthly Budgets | `/budget` |
| Money Transfers | `/money` |
| Loans | `/loans` |
| Payments | `/payments` |

Protected API requests use JWT Bearer token authentication.

### Monthly Budget APIs

- `POST /budget` – Save monthly salary and category budgets.
- `GET /budget/{year}/{month}` – Retrieve the selected month's budget.
- `GET /budget/summary/{year}/{month}` – Retrieve the monthly financial summary.

## Database

The application uses MySQL with Spring Data JPA for database operations.

Important data entities include:

- User
- Expense
- MonthlyBudget
- Loan
- Payment
- MoneyTransfer

Monthly salary and budget information is associated with the authenticated user and the selected month and year.

## Security

- JWT-based authentication.
- Spring Security for API protection.
- Authenticated user identification.
- User-specific financial data access.
- Secure database configuration through environment variables.

## Testing

Run the Maven test suite:

```bash
./mvnw test
```

Build the application:

```bash
./mvnw clean package
```

## Deployment

The backend is deployed using Render.

Production backend URL:

https://ai-receipt-expense-tracker-backend-2.onrender.com

Configure the required database and JWT environment variables in the Render dashboard before deployment.

## Frontend Integration

Frontend repository:

https://github.com/kyashaswini04/ai-receipt-expense-tracker-frontend

The React frontend communicates with this backend through REST APIs.

## Author

**Yashaswini Kooroju**

GitHub: https://github.com/kyashaswini04

## License

This project is developed for educational and practical application purposes.
