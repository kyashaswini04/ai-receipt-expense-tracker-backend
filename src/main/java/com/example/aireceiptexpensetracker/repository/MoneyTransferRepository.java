package com.example.aireceiptexpensetracker.repository;

import com.example.aireceiptexpensetracker.entity.MoneyTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyTransferRepository
        extends JpaRepository<MoneyTransfer, Long> {
}