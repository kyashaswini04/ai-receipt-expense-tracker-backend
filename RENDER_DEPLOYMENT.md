# Render deployment settings

Use the repository root as the Render service root directory (the folder containing `pom.xml`).

- Runtime: Java 17
- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/ai-receipt-expense-tracker-0.0.1-SNAPSHOT.jar`

Set these environment variables in Render:

- `SPRING_DATASOURCE_URL` = your managed MySQL JDBC URL, e.g. `jdbc:mysql://HOST:3306/DATABASE?useSSL=true&serverTimezone=UTC`
- `SPRING_DATASOURCE_USERNAME` = database username
- `SPRING_DATASOURCE_PASSWORD` = database password
- `JWT_SECRET` = a randomly generated secret of at least 32 characters (do not commit it)
- `DDL_AUTO` = `update`
- `SHOW_SQL` = `false`

Render's ephemeral filesystem is not a database. Use a managed MySQL service and its externally reachable host/port. Do not use `localhost` for a separately hosted database.

## Frontend login integration

A successful `POST /auth/login` now returns `token` and `tokenType: "Bearer"` alongside `name` and `email`. Store the token using your app's existing auth mechanism and send it on every protected API request:

`Authorization: Bearer <token>`

`/auth/register` and `/auth/login` remain public. All other routes require a valid, unexpired JWT.

## Important data-ownership limitation

The current entities and repositories do not associate expenses, budgets, loans, payments, money transfers, or receipts with a user. JWT now gates API access, but this change does not provide per-user data isolation: authenticated users can still access the shared records. Add user ownership fields and scope every repository/controller query before using this for multiple independent accounts or real personal financial data.
