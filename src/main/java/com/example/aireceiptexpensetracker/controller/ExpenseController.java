package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Expense;
import com.example.aireceiptexpensetracker.entity.User;
import com.example.aireceiptexpensetracker.repository.ExpenseRepository;
import com.example.aireceiptexpensetracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public List<Expense> getAllExpenses(@AuthenticationPrincipal String email) {
        return expenseRepository.findByUser_EmailOrderByExpenseDateDesc(email);
    }

    @PostMapping
    public Expense addExpense(@RequestBody Expense expense, @AuthenticationPrincipal String email) {
        expense.setId(null);
        expense.setUser(currentUser(email));
        return expenseRepository.save(expense);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id, @AuthenticationPrincipal String email) {
        return expenseRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long id, @AuthenticationPrincipal String email) {
        Expense expense = expenseRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        expenseRepository.delete(expense);
    }
}
