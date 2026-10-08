package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.MoneyTransfer;
import com.example.aireceiptexpensetracker.repository.MoneyTransferRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/money")
@CrossOrigin(origins = "*")
public class MoneyTransferController {

    private final MoneyTransferRepository repository;

    public MoneyTransferController(
            MoneyTransferRepository repository) {

        this.repository = repository;
    }

    // Get all money transactions
    @GetMapping
    public List<MoneyTransfer> getAll() {

        return repository.findAll();
    }

    // Add money given/taken
    @PostMapping
    public MoneyTransfer addMoney(
            @RequestBody MoneyTransfer moneyTransfer) {

        if (moneyTransfer.getStatus() == null) {
            moneyTransfer.setStatus("PENDING");
        }

        if (moneyTransfer.getPaidAmount() == null) {
            moneyTransfer.setPaidAmount(0.0);
        }

        return repository.save(moneyTransfer);
    }

    // Get one transaction
    @GetMapping("/{id}")
    public MoneyTransfer getById(
            @PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Money transaction not found"));
    }

    // Delete transaction
    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id) {

        repository.deleteById(id);

        return "Money transaction deleted successfully";
    }
}