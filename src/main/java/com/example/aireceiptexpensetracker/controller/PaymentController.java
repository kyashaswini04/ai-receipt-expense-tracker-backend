package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Loan;
import com.example.aireceiptexpensetracker.entity.Payment;
import com.example.aireceiptexpensetracker.repository.LoanRepository;
import com.example.aireceiptexpensetracker.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;

    public PaymentController(PaymentRepository paymentRepository, LoanRepository loanRepository) {
        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping
    public List<Payment> getAllPayments(@AuthenticationPrincipal String email) {
        return paymentRepository.findByLoan_User_EmailOrderByPaymentDateDesc(email);
    }

    @GetMapping("/loan/{loanId}")
    public List<Payment> getLoanPayments(@PathVariable Long loanId, @AuthenticationPrincipal String email) {
        // Verify the loan belongs to this user before returning its payments.
        loanRepository.findByIdAndUser_Email(loanId, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan or Chit Fund not found"));
        return paymentRepository.findByLoan_IdAndLoan_User_Email(loanId, email);
    }

    @PostMapping("/loan/{loanId}")
    @Transactional
    public Payment addPayment(@PathVariable Long loanId, @RequestBody Payment payment,
                              @AuthenticationPrincipal String email) {
        Loan loan = loanRepository.findByIdAndUser_Email(loanId, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan or Chit Fund not found"));

        payment.setLoan(loan);
        if (payment.getStatus() == null) payment.setStatus("PAID");
        Payment savedPayment = paymentRepository.save(payment);

        int paymentsMade = loan.getPaymentsMade() == null ? 0 : loan.getPaymentsMade();
        double amountPaid = loan.getAmountPaid() == null ? 0.0 : loan.getAmountPaid();
        double paymentAmount = payment.getAmount() == null ? 0.0 : payment.getAmount();

        loan.setPaymentsMade(paymentsMade + 1);
        loan.setAmountPaid(amountPaid + paymentAmount);
        if (loan.getTenureMonths() != null) {
            loan.setPaymentsRemaining(Math.max(0, loan.getTenureMonths() - loan.getPaymentsMade()));
        }
        if (loan.getPrincipalAmount() != null) {
            loan.setAmountRemaining(Math.max(0, loan.getPrincipalAmount() - loan.getAmountPaid()));
        }
        if (loan.getPaymentsRemaining() != null && loan.getPaymentsRemaining() == 0) {
            loan.setStatus("COMPLETED");
        }
        loanRepository.save(loan);
        return savedPayment;
    }

    @DeleteMapping("/{id}")
    public String deletePayment(@PathVariable Long id, @AuthenticationPrincipal String email) {
        Payment payment = paymentRepository.findByIdAndLoan_User_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        paymentRepository.delete(payment);
        return "Payment deleted successfully";
    }
}
