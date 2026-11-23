package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser_EmailOrderByExpenseDateDesc(String email);
    Optional<Expense> findByIdAndUser_Email(Long id, String email);
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    List<Expense> findByUser_EmailAndExpenseDateBetween(String email, LocalDate startDate, LocalDate endDate);
}
