package com.example.aireceiptexpensetracker.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "money_transfers")
public class MoneyTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GIVEN = money you gave to someone
    // TAKEN = money you took from someone
    @Enumerated(EnumType.STRING)
    private TransferType type;

    private String personName;

    private Double amount;

    private LocalDate transactionDate;

    private String purpose;

    private LocalDate dueDate;

    // PENDING / PARTIALLY_PAID / PAID
    private String status;

    private Double paidAmount;

    private String notes;

    public MoneyTransfer() {
    }

    public Long getId() {
        return id;
    }

    public TransferType getType() {
        return type;
    }

    public void setType(TransferType type) {
        this.type = type;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public enum TransferType {
        GIVEN,
        TAKEN
    }
}