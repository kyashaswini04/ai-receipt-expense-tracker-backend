package com.example.aireceiptexpensetracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "monthly_budgets")
public class MonthlyBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer month;
    private Integer year;

    private Double salary;

    private Double shoppingBudget;
    private Double groceriesBudget;
    private Double foodBudget;
    private Double transportBudget;
    private Double personalCareBudget;
    private Double otherBudget;

    public MonthlyBudget() {
    }

    public Long getId() {
        return id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Double getShoppingBudget() {
        return shoppingBudget;
    }

    public void setShoppingBudget(Double shoppingBudget) {
        this.shoppingBudget = shoppingBudget;
    }

    public Double getGroceriesBudget() {
        return groceriesBudget;
    }

    public void setGroceriesBudget(Double groceriesBudget) {
        this.groceriesBudget = groceriesBudget;
    }

    public Double getFoodBudget() {
        return foodBudget;
    }

    public void setFoodBudget(Double foodBudget) {
        this.foodBudget = foodBudget;
    }

    public Double getTransportBudget() {
        return transportBudget;
    }

    public void setTransportBudget(Double transportBudget) {
        this.transportBudget = transportBudget;
    }

    public Double getPersonalCareBudget() {
        return personalCareBudget;
    }

    public void setPersonalCareBudget(Double personalCareBudget) {
        this.personalCareBudget = personalCareBudget;
    }

    public Double getOtherBudget() {
        return otherBudget;
    }

    public void setOtherBudget(Double otherBudget) {
        this.otherBudget = otherBudget;
    }
}