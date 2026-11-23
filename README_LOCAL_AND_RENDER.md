# AI Receipt Expense Tracker — Backend (JWT + Render)

## Requirements
- Java 17
- MySQL 8+ (local) or a reachable managed MySQL database (deployment)

## Run locally on macOS
1. Start MySQL and create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS ai_expense_tracker;
   ```
2. In Terminal, from this project folder, set the connection values (use the password that works for your Homebrew MySQL server):
   ```bash
   export SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/ai_expense_tracker'
   export SPRING_DATASOURCE_USERNAME='root'
   export SPRING_DATASOURCE_PASSWORD='YOUR_MYSQL_PASSWORD'
   export JWT_SECRET="$(openssl rand -base64 32)"
   ```
3. Build and run:
   ```bash
   chmod +x mvnw
   ./mvnw clean package -DskipTests
   java -jar target/ai-receipt-expense-tracker-0.0.1-SNAPSHOT.jar
   ```
   Or run `./mvnw spring-boot:run`.
4. The server listens on `http://localhost:8080`. Keep the Terminal window open.

## Render deployment
- Runtime: Java 17
- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/ai-receipt-expense-tracker-0.0.1-SNAPSHOT.jar`
- Set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, and `JWT_SECRET` in Render. `JWT_SECRET` must be a strong random value of at least 32 bytes. Set `DDL_AUTO=update` and `SHOW_SQL=false`.
- Use your database provider's external hostname in the JDBC URL; do not use localhost for a separately hosted database.
- In Vercel, set `VITE_API_URL` to the exact public Render service URL, then redeploy the frontend.

## Authentication
- `POST /auth/register` and `POST /auth/login` are public.
- Login responds with a JWT `token` and `tokenType: Bearer`.
- Other API routes require `Authorization: Bearer <token>`.

## Important data isolation note
The existing expense, budget, loan, payment, money-transfer, and receipt data models are not scoped to an individual user. JWT authentication gates API access but does not make records private per account. Do not use this as a multi-user financial system until user ownership fields and per-user repository/controller filtering are implemented.

## Frontend/local development
The companion frontend should use `http://localhost:8080` locally. For Vercel, configure `VITE_API_URL` with the Render URL. The configured CORS allowlist includes localhost ports 5173/5174 and the named Vercel project/preview pattern; adjust it if your deployed frontend has a different domain.


## Allowing the frontend (CORS)

The backend allows the local Vite origins `http://localhost:5173` and `http://localhost:5174`, plus the configured Vercel frontend project and its matching preview deployments. If your Vercel project uses a different domain, set this Render environment variable to a comma-separated allowlist of exact origins/patterns (no trailing slash):

`CORS_ALLOWED_ORIGINS=https://YOUR-FRONTEND.vercel.app,https://YOUR-FRONTEND-*.vercel.app,http://localhost:5173`

After changing it, redeploy the Render service. Do not use `*` as an unrestricted origin when credentials are enabled.
