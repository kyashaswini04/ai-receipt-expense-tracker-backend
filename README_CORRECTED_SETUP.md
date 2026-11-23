# AI Receipt Expense Tracker Backend - corrected package

## Local setup (macOS)

1. Make sure MySQL is running and the database exists:
   `CREATE DATABASE IF NOT EXISTS ai_expense_tracker;`
2. Open Terminal in this project directory and set your local database password (do not put it in source code):
   `export SPRING_DATASOURCE_PASSWORD='YOUR_MYSQL_PASSWORD'`
3. Generate a local JWT secret:
   `export JWT_SECRET="$(openssl rand -base64 32)"`
4. Run:
   `chmod +x mvnw && ./mvnw spring-boot:run`
5. Keep the backend running at `http://localhost:8080`. Frontend origin `http://localhost:5174` is allowed by CORS.

The SecurityConfig references JwtAuthenticationFilter (not JwtAuthFilter); both source files are included under the same config package.

## Render environment variables

Set SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD, JWT_SECRET, and CORS_ALLOWED_ORIGINS in the Render service environment. CORS_ALLOWED_ORIGINS should contain your exact Vercel frontend origin (comma-separated if more than one). Do not commit secrets.

This archive preserves the existing application controllers/entities/services and adds JWT authentication and CORS configuration. Verify your Render database URL and frontend API URL in their respective dashboards before deployment.
