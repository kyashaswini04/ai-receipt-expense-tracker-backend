package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseDateBetween(
            LocalDate startDate,
            LocalDate endDate);
}