package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.MoneyTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, Long> {
    List<MoneyTransfer> findByUser_EmailOrderByTransactionDateDesc(String email);
    Optional<MoneyTransfer> findByIdAndUser_Email(Long id, String email);
}
