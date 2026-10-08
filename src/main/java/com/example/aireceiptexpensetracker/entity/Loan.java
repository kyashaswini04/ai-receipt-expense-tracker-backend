package com.example.aireceiptexpensetracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    private String loanName;

    private String providerName;

    private String contactPerson;

    private Double principalAmount;

    private Double amountReceived;

    private Double interestRate;

    private Integer tenureMonths;

    private Double monthlyPayment;

    private Integer paymentsMade;

    private Integer paymentsRemaining;

    private Double amountPaid;

    private Double amountRemaining;

    private LocalDate startDate;

    private LocalDate nextPaymentDate;

    private String status;

    private String notes;

    public Loan() {
    }

    public enum LoanType {
        BANK_LOAN,
        CHITTI
    }

    public Long getId() {
        return id;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(
            LoanType loanType) {
        this.loanType = loanType;
    }

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(
            String loanName) {
        this.loanName = loanName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(
            String providerName) {
        this.providerName = providerName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(
            String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(
            Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Double getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(
            Double amountReceived) {
        this.amountReceived = amountReceived;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(
            Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(
            Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(
            Double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public Integer getPaymentsMade() {
        return paymentsMade;
    }

    public void setPaymentsMade(
            Integer paymentsMade) {
        this.paymentsMade = paymentsMade;
    }

    public Integer getPaymentsRemaining() {
        return paymentsRemaining;
    }

    public void setPaymentsRemaining(
            Integer paymentsRemaining) {
        this.paymentsRemaining = paymentsRemaining;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(
            Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getAmountRemaining() {
        return amountRemaining;
    }

    public void setAmountRemaining(
            Double amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(
            LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {
        this.notes = notes;
    }
}
