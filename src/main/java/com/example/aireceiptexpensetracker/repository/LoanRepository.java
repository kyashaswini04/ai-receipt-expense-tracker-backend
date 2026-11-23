package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser_EmailOrderByStartDateDesc(String email);
    Optional<Loan> findByIdAndUser_Email(Long id, String email);
}
