package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.MonthlyBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyBudgetRepository
        extends JpaRepository<MonthlyBudget, Long> {

    Optional<MonthlyBudget> findByMonthAndYear(
            Integer month,
            Integer year);
}