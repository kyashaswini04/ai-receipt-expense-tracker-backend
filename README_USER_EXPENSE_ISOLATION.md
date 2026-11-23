# Backend update: per-user expense isolation

## What changed
- Expenses now have a nullable `user_id` relationship to `users`.
- `GET /expenses`, `GET /expenses/{id}`, `POST /expenses`, and `DELETE /expenses/{id}` are scoped to the authenticated JWT email.
- Dashboard expense summary, category totals, history, and monthly totals are scoped to the authenticated JWT email.
- Receipt uploads save the expense under the authenticated user.

## Important existing-data note
This change does NOT guess which account owns existing rows. Existing rows in `expenses` receive `user_id = NULL` after Hibernate adds the nullable column and will not appear in either user's filtered expense list until ownership is assigned.
Back up the database before starting. To preserve old expenses, determine the correct owner for each legacy row and update `expenses.user_id` using the matching `users.id`. Do not assign all old rows to the newest account.

Example inspection SQL:
```sql
SELECT id, name, email FROM users ORDER BY id;
SELECT id, store_name, expense_date, amount, user_id FROM expenses ORDER BY id;
```
Only after you have identified the correct owner for a particular legacy expense:
```sql
UPDATE expenses SET user_id = <correct_users_id> WHERE id = <expense_id>;
```

## Remaining multi-user work
Money transfers, loans, budgets, and payments are NOT yet tenant-isolated in this ZIP. Do not deploy this as a fully multi-user-secure application until those controllers/entities are also scoped and ownership-checked.


## Multi-user ownership for all finance modules

This version scopes expenses, monthly budgets, money transfers, loans, and loan/chit payments to the authenticated JWT email.

- `expenses`, `monthly_budgets`, `money_transfers`, and `loans` have a `user_id` owner column.
- Payments inherit ownership through their associated loan; a user can only read or modify payments belonging to their own loan.
- Create operations set the owner from the authenticated account. Client-supplied owner IDs are not trusted.
- Cross-account record IDs return `404` rather than exposing whether another user's record exists.

### Existing database migration

With `spring.jpa.hibernate.ddl-auto=update`, Hibernate adds the new nullable `user_id` columns without deleting existing records. Existing rows in the newly scoped tables will have `user_id = NULL` until you deliberately assign them.

Before assigning old records, inspect them in MySQL:

```sql
SELECT id, email FROM users ORDER BY id;
SELECT id, user_id FROM monthly_budgets ORDER BY id;
SELECT id, user_id FROM money_transfers ORDER BY id;
SELECT id, user_id FROM loans ORDER BY id;
```

If the existing budgets/transfers/loans belong to your original account, first verify that account's actual ID from the `users` query, then update only those rows. For example, replace `1` below with the verified owner ID:

```sql
UPDATE monthly_budgets SET user_id = 1 WHERE user_id IS NULL;
UPDATE money_transfers SET user_id = 1 WHERE user_id IS NULL;
UPDATE loans SET user_id = 1 WHERE user_id IS NULL;
```

Do not run those updates if records belong to different users. Assign each row to its correct owner. Payments are automatically scoped through `loans.user_id`; no separate payment owner column is needed.

### Important validation

Test with at least two accounts:
1. Create a budget, loan, money transfer, and payment while logged in as account A.
2. Log in as account B and verify those records are not returned.
3. Try account A's record IDs while logged in as account B; requests should return `404`.
4. Verify account B can create and see its own records.

Do not deploy to Render until the local build and these tests pass. Keep a database backup before any production migration.
