package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoan_User_EmailOrderByPaymentDateDesc(String email);
    List<Payment> findByLoan_IdAndLoan_User_Email(Long loanId, String email);
    Optional<Payment> findByIdAndLoan_User_Email(Long id, String email);
}
