package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.Loan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository
        extends JpaRepository<Loan, Long> {
}