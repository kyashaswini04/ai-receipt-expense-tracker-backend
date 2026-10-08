package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Expense;
import com.example.aireceiptexpensetracker.repository.ExpenseRepository;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ExpenseRepository expenseRepository;

    public DashboardController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // ==========================================
    // DASHBOARD SUMMARY
    // ==========================================

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {

        List<Expense> expenses = expenseRepository.findAll();

        double totalSpending = 0;

        for (Expense expense : expenses) {

            if (expense.getAmount() != null) {
                totalSpending += expense.getAmount();
            }
        }

        Map<String, Object> result = new HashMap<>();

        result.put("totalExpenses", expenses.size());
        result.put("totalSpending", totalSpending);

        return result;
    }

    // ==========================================
    // CATEGORY-WISE SPENDING
    // ==========================================

    @GetMapping("/categories")
    public Map<String, Double> getCategorySpending() {

        List<Expense> expenses = expenseRepository.findAll();

        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {

            String category = expense.getCategory();

            Double amount = expense.getAmount();

            if (category == null) {
                category = "Unknown";
            }

            if (amount == null) {
                amount = 0.0;
            }

            categoryTotals.put(
                    category,
                    categoryTotals.getOrDefault(category, 0.0)
                            + amount);
        }

        return categoryTotals;
    }

    // ==========================================
    // EXPENSE HISTORY
    // ==========================================

    @GetMapping("/expenses")
    public List<Expense> getExpenseHistory() {

        return expenseRepository.findAll();
    }

    // ==========================================
    // MONTHLY SPENDING
    // ==========================================

    @GetMapping("/monthly")
    public Map<String, Double> getMonthlySpending() {

        List<Expense> expenses = expenseRepository.findAll();

        Map<String, Double> monthlyTotals = new HashMap<>();

        for (Expense expense : expenses) {

            LocalDate date = expense.getExpenseDate();

            Double amount = expense.getAmount();

            if (date == null || amount == null) {
                continue;
            }

            YearMonth month = YearMonth.from(date);

            String monthName = month.toString();

            monthlyTotals.put(
                    monthName,
                    monthlyTotals.getOrDefault(monthName, 0.0)
                            + amount);
        }

        return monthlyTotals;
    }
}