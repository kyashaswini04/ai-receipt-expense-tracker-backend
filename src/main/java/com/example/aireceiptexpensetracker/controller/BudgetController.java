package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Expense;
import com.example.aireceiptexpensetracker.entity.MonthlyBudget;
import com.example.aireceiptexpensetracker.entity.MoneyTransfer;
import com.example.aireceiptexpensetracker.entity.Payment;

import com.example.aireceiptexpensetracker.repository.ExpenseRepository;
import com.example.aireceiptexpensetracker.repository.MonthlyBudgetRepository;
import com.example.aireceiptexpensetracker.repository.MoneyTransferRepository;
import com.example.aireceiptexpensetracker.repository.PaymentRepository;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/budget")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final MonthlyBudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final MoneyTransferRepository moneyTransferRepository;
    private final PaymentRepository paymentRepository;

    public BudgetController(
            MonthlyBudgetRepository budgetRepository,
            ExpenseRepository expenseRepository,
            MoneyTransferRepository moneyTransferRepository,
            PaymentRepository paymentRepository) {

        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.moneyTransferRepository = moneyTransferRepository;
        this.paymentRepository = paymentRepository;
    }

    // ==========================================
    // SAVE MONTHLY BUDGET
    // ==========================================

    @PostMapping
    public MonthlyBudget saveBudget(
            @RequestBody MonthlyBudget budget) {

        return budgetRepository.save(budget);
    }

    // ==========================================
    // GET MONTHLY BUDGET
    // ==========================================

    @GetMapping("/{year}/{month}")
    public MonthlyBudget getBudget(
            @PathVariable Integer year,
            @PathVariable Integer month) {

        return budgetRepository
                .findByMonthAndYear(month, year)
                .orElse(null);
    }

    // ==========================================
    // GET MONTHLY BUDGET SUMMARY
    // ==========================================

    @GetMapping("/summary/{year}/{month}")
    public Map<String, Object> getBudgetSummary(
            @PathVariable Integer year,
            @PathVariable Integer month) {

        // ------------------------------------------
        // GET BUDGET
        // ------------------------------------------

        MonthlyBudget budget = budgetRepository
                .findByMonthAndYear(month, year)
                .orElseThrow(() -> new RuntimeException(
                        "Budget not found"));

        // ------------------------------------------
        // MONTH START AND END
        // ------------------------------------------

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate = startDate.withDayOfMonth(
                startDate.lengthOfMonth());

        // ==========================================
        // 1. NORMAL EXPENSES
        // ==========================================

        List<Expense> expenses = expenseRepository
                .findByExpenseDateBetween(
                        startDate,
                        endDate);

        double shoppingSpent = 0;
        double groceriesSpent = 0;
        double foodSpent = 0;
        double transportSpent = 0;
        double personalCareSpent = 0;
        double otherSpent = 0;

        for (Expense expense : expenses) {

            if (expense.getAmount() == null) {
                continue;
            }

            String category = expense.getCategory();

            if (category == null) {

                otherSpent += expense.getAmount();

                continue;
            }

            switch (category.toLowerCase()) {

                case "shopping":

                    shoppingSpent += expense.getAmount();

                    break;

                case "groceries":

                    groceriesSpent += expense.getAmount();

                    break;

                case "food":

                    foodSpent += expense.getAmount();

                    break;

                case "transportation":

                    transportSpent += expense.getAmount();

                    break;

                case "personal care":

                    personalCareSpent += expense.getAmount();

                    break;

                default:

                    otherSpent += expense.getAmount();

                    break;
            }
        }

        // ==========================================
        // 2. MONEY GIVEN
        // ==========================================

        List<MoneyTransfer> moneyTransfers = moneyTransferRepository.findAll();

        double moneyGiven = 0;

        for (MoneyTransfer transfer : moneyTransfers) {

            if (transfer.getAmount() == null) {
                continue;
            }

            if (transfer.getType() != MoneyTransfer.TransferType.GIVEN) {

                continue;
            }

            LocalDate date = transfer.getTransactionDate();

            if (date == null) {
                continue;
            }

            if (!date.isBefore(startDate)
                    && !date.isAfter(endDate)) {

                moneyGiven += transfer.getAmount();
            }
        }

        // ==========================================
        // 3. LOAN EMI + CHIT FUND PAYMENTS
        // ==========================================

        List<Payment> payments = paymentRepository.findAll();

        double loanPayments = 0;
        double chitFundPayments = 0;

        for (Payment payment : payments) {

            if (payment.getAmount() == null) {
                continue;
            }

            if (payment.getPaymentDate() == null) {
                continue;
            }

            // Check payment date
            if (payment.getPaymentDate()
                    .isBefore(startDate)
                    ||
                    payment.getPaymentDate()
                            .isAfter(endDate)) {

                continue;
            }

            // Only count PAID payments
            if (payment.getStatus() != null
                    &&
                    !payment.getStatus()
                            .equalsIgnoreCase("PAID")) {

                continue;
            }

            // Bank Loan EMI
            if (payment.getPaymentType() == Payment.PaymentType.EMI) {

                loanPayments += payment.getAmount();
            }

            // Chit Fund
            if (payment.getPaymentType() == Payment.PaymentType.CHIT_FUND) {

                chitFundPayments += payment.getAmount();
            }
        }

        // ==========================================
        // 4. NORMAL EXPENSE TOTAL
        // ==========================================

        double normalExpenses = shoppingSpent
                + groceriesSpent
                + foodSpent
                + transportSpent
                + personalCareSpent
                + otherSpent;

        // ==========================================
        // 5. TOTAL OUTGOING
        // ==========================================

        double totalOutgoing = normalExpenses
                + moneyGiven
                + loanPayments
                + chitFundPayments;

        // ==========================================
        // 6. REMAINING SALARY
        // ==========================================

        double remainingSalary = budget.getSalary()
                - totalOutgoing;

        // ==========================================
        // 7. CREATE RESPONSE
        // ==========================================

        Map<String, Object> result = new HashMap<>();

        // Salary

        result.put(
                "salary",
                budget.getSalary());

        // Total outgoing

        result.put(
                "totalSpent",
                totalOutgoing);

        // Remaining salary

        result.put(
                "remainingSalary",
                remainingSalary);

        // ==========================================
        // SHOPPING
        // ==========================================

        result.put(
                "shoppingBudget",
                budget.getShoppingBudget());

        result.put(
                "shoppingSpent",
                shoppingSpent);

        result.put(
                "shoppingRemaining",
                budget.getShoppingBudget()
                        - shoppingSpent);

        // ==========================================
        // GROCERIES
        // ==========================================

        result.put(
                "groceriesBudget",
                budget.getGroceriesBudget());

        result.put(
                "groceriesSpent",
                groceriesSpent);

        result.put(
                "groceriesRemaining",
                budget.getGroceriesBudget()
                        - groceriesSpent);

        // ==========================================
        // FOOD
        // ==========================================

        result.put(
                "foodBudget",
                budget.getFoodBudget());

        result.put(
                "foodSpent",
                foodSpent);

        result.put(
                "foodRemaining",
                budget.getFoodBudget()
                        - foodSpent);

        // ==========================================
        // TRANSPORT
        // ==========================================

        result.put(
                "transportBudget",
                budget.getTransportBudget());

        result.put(
                "transportSpent",
                transportSpent);

        result.put(
                "transportRemaining",
                budget.getTransportBudget()
                        - transportSpent);

        // ==========================================
        // PERSONAL CARE
        // ==========================================

        result.put(
                "personalCareBudget",
                budget.getPersonalCareBudget());

        result.put(
                "personalCareSpent",
                personalCareSpent);

        result.put(
                "personalCareRemaining",
                budget.getPersonalCareBudget()
                        - personalCareSpent);

        // ==========================================
        // OTHER
        // ==========================================

        result.put(
                "otherBudget",
                budget.getOtherBudget());

        result.put(
                "otherSpent",
                otherSpent);

        result.put(
                "otherRemaining",
                budget.getOtherBudget()
                        - otherSpent);

        // ==========================================
        // MONEY GIVEN
        // ==========================================

        result.put(
                "moneyGiven",
                moneyGiven);

        // ==========================================
        // LOAN EMI
        // ==========================================

        result.put(
                "loanPayments",
                loanPayments);

        // ==========================================
        // CHIT FUND
        // ==========================================

        result.put(
                "chitFundPayments",
                chitFundPayments);

        // ==========================================
        // RETURN RESULT
        // ==========================================

        return result;
    }
}