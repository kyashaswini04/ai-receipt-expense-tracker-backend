package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Expense;
import com.example.aireceiptexpensetracker.repository.ExpenseRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final ExpenseRepository expenseRepository;
    public DashboardController(ExpenseRepository expenseRepository) { this.expenseRepository = expenseRepository; }

    @GetMapping("/summary")
    public Map<String, Object> getSummary(@AuthenticationPrincipal String email) {
        List<Expense> expenses = expenseRepository.findByUser_EmailOrderByExpenseDateDesc(email);
        double total = expenses.stream().filter(e -> e.getAmount() != null).mapToDouble(Expense::getAmount).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("totalExpenses", expenses.size());
        result.put("totalSpending", total);
        return result;
    }

    @GetMapping("/categories")
    public Map<String, Double> getCategorySpending(@AuthenticationPrincipal String email) {
        Map<String, Double> totals = new HashMap<>();
        for (Expense e : expenseRepository.findByUser_EmailOrderByExpenseDateDesc(email)) {
            String category = e.getCategory() == null ? "Unknown" : e.getCategory();
            totals.merge(category, e.getAmount() == null ? 0.0 : e.getAmount(), Double::sum);
        }
        return totals;
    }

    @GetMapping("/expenses")
    public List<Expense> getExpenseHistory(@AuthenticationPrincipal String email) {
        return expenseRepository.findByUser_EmailOrderByExpenseDateDesc(email);
    }

    @GetMapping("/monthly")
    public Map<String, Double> getMonthlySpending(@AuthenticationPrincipal String email) {
        Map<String, Double> totals = new HashMap<>();
        for (Expense e : expenseRepository.findByUser_EmailOrderByExpenseDateDesc(email)) {
            LocalDate date = e.getExpenseDate();
            if (date != null && e.getAmount() != null) {
                totals.merge(YearMonth.from(date).toString(), e.getAmount(), Double::sum);
            }
        }
        return totals;
    }
}
