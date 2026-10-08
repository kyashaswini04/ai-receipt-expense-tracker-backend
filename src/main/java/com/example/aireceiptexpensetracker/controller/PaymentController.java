package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Loan;
import com.example.aireceiptexpensetracker.entity.Payment;
import com.example.aireceiptexpensetracker.repository.LoanRepository;
import com.example.aireceiptexpensetracker.repository.PaymentRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;

    public PaymentController(
            PaymentRepository paymentRepository,
            LoanRepository loanRepository) {

        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
    }

    // Get all payments
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payments for one loan/chit fund
    @GetMapping("/loan/{loanId}")
    public List<Payment> getLoanPayments(
            @PathVariable Long loanId) {

        return paymentRepository.findByLoanId(loanId);
    }

    // Add payment
    @PostMapping("/loan/{loanId}")
    public Payment addPayment(
            @PathVariable Long loanId,
            @RequestBody Payment payment) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Loan or Chit Fund not found"));

        payment.setLoan(loan);

        if (payment.getStatus() == null) {
            payment.setStatus("PAID");
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Update loan payment information
        int paymentsMade = loan.getPaymentsMade() == null
                ? 0
                : loan.getPaymentsMade();

        double amountPaid = loan.getAmountPaid() == null
                ? 0.0
                : loan.getAmountPaid();

        double paymentAmount = payment.getAmount() == null
                ? 0.0
                : payment.getAmount();

        loan.setPaymentsMade(
                paymentsMade + 1);

        loan.setAmountPaid(
                amountPaid + paymentAmount);

        if (loan.getTenureMonths() != null) {

            loan.setPaymentsRemaining(
                    Math.max(
                            0,
                            loan.getTenureMonths()
                                    - loan.getPaymentsMade()));
        }

        if (loan.getPrincipalAmount() != null) {

            loan.setAmountRemaining(
                    Math.max(
                            0,
                            loan.getPrincipalAmount()
                                    - loan.getAmountPaid()));
        }

        if (loan.getPaymentsRemaining() != null
                && loan.getPaymentsRemaining() == 0) {

            loan.setStatus("COMPLETED");
        }

        loanRepository.save(loan);

        return savedPayment;
    }

    // Delete payment
    @DeleteMapping("/{id}")
    public String deletePayment(
            @PathVariable Long id) {

        paymentRepository.deleteById(id);

        return "Payment deleted successfully";
    }
}