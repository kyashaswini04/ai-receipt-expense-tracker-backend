package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByLoanId(Long loanId);
}