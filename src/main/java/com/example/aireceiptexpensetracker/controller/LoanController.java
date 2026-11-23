package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Loan;
import com.example.aireceiptexpensetracker.entity.User;
import com.example.aireceiptexpensetracker.repository.LoanRepository;
import com.example.aireceiptexpensetracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public LoanController(LoanRepository loanRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public List<Loan> getAllLoans(@AuthenticationPrincipal String email) {
        return loanRepository.findByUser_EmailOrderByStartDateDesc(email);
    }

    @PostMapping
    public Loan addLoan(@RequestBody Loan loan, @AuthenticationPrincipal String email) {
        loan.setUser(currentUser(email));
        if (loan.getLoanType() == null) loan.setLoanType(Loan.LoanType.BANK_LOAN);
        if (loan.getPaymentsMade() == null) loan.setPaymentsMade(0);
        if (loan.getPaymentsRemaining() == null) loan.setPaymentsRemaining(loan.getTenureMonths());
        if (loan.getAmountPaid() == null) loan.setAmountPaid(0.0);
        if (loan.getAmountRemaining() == null) loan.setAmountRemaining(loan.getPrincipalAmount());
        if (loan.getStatus() == null) loan.setStatus("ACTIVE");
        return loanRepository.save(loan);
    }

    @GetMapping("/{id}")
    public Loan getLoanById(@PathVariable Long id, @AuthenticationPrincipal String email) {
        return loanRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }

    @DeleteMapping("/{id}")
    public String deleteLoan(@PathVariable Long id, @AuthenticationPrincipal String email) {
        Loan loan = loanRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
        loanRepository.delete(loan);
        return "Loan deleted successfully";
    }
}
