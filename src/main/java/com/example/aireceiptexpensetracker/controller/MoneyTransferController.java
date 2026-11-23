package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.MoneyTransfer;
import com.example.aireceiptexpensetracker.entity.User;
import com.example.aireceiptexpensetracker.repository.MoneyTransferRepository;
import com.example.aireceiptexpensetracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/money")
public class MoneyTransferController {
    private final MoneyTransferRepository repository;
    private final UserRepository userRepository;

    public MoneyTransferController(MoneyTransferRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public List<MoneyTransfer> getAll(@AuthenticationPrincipal String email) {
        return repository.findByUser_EmailOrderByTransactionDateDesc(email);
    }

    @PostMapping
    public MoneyTransfer addMoney(@RequestBody MoneyTransfer moneyTransfer,
                                  @AuthenticationPrincipal String email) {
        // Never trust an owner or ID supplied by the client.
        moneyTransfer.setUser(currentUser(email));
        if (moneyTransfer.getStatus() == null) moneyTransfer.setStatus("PENDING");
        if (moneyTransfer.getPaidAmount() == null) moneyTransfer.setPaidAmount(0.0);
        return repository.save(moneyTransfer);
    }

    @GetMapping("/{id}")
    public MoneyTransfer getById(@PathVariable Long id, @AuthenticationPrincipal String email) {
        return repository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Money transaction not found"));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal String email) {
        MoneyTransfer transfer = repository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Money transaction not found"));
        repository.delete(transfer);
        return "Money transaction deleted successfully";
    }
}
