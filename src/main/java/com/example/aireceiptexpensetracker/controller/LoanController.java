package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Loan;
import com.example.aireceiptexpensetracker.repository.LoanRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanRepository loanRepository;

    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @GetMapping
    public List<Loan> getAllLoans() {

        return loanRepository.findAll();

    }

    @PostMapping
    public Loan addLoan(
            @RequestBody Loan loan) {

        if (loan.getLoanType() == null) {
            loan.setLoanType(
                    Loan.LoanType.BANK_LOAN);
        }

        if (loan.getPaymentsMade() == null) {
            loan.setPaymentsMade(0);
        }

        if (loan.getPaymentsRemaining() == null) {

            loan.setPaymentsRemaining(
                    loan.getTenureMonths());

        }

        if (loan.getAmountPaid() == null) {
            loan.setAmountPaid(0.0);
        }

        if (loan.getAmountRemaining() == null) {

            loan.setAmountRemaining(
                    loan.getPrincipalAmount());

        }

        if (loan.getStatus() == null) {
            loan.setStatus("ACTIVE");
        }

        return loanRepository.save(loan);
    }

    @GetMapping("/{id}")
    public Loan getLoanById(
            @PathVariable Long id) {

        return loanRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Loan not found"));

    }

    @DeleteMapping("/{id}")
    public String deleteLoan(
            @PathVariable Long id) {

        loanRepository.deleteById(id);

        return "Loan deleted successfully";
    }

}