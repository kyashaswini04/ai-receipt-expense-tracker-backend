package com.example.aireceiptexpensetracker.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    public String detectCategory(List<String> items) {

        for (String item : items) {

            String product = item.toLowerCase();

            // Food
            if (product.contains("pizza")
                    || product.contains("burger")
                    || product.contains("restaurant")
                    || product.contains("food")
                    || product.contains("cake")
                    || product.contains("snack")
                    || product.contains("biryani")) {

                return "Food";
            }

            // Groceries
            if (product.contains("rice")
                    || product.contains("milk")
                    || product.contains("bread")
                    || product.contains("vegetable")
                    || product.contains("fruit")
                    || product.contains("grocery")
                    || product.contains("dal")
                    || product.contains("flour")) {

                return "Groceries";
            }

            // Transportation
            if (product.contains("petrol")
                    || product.contains("diesel")
                    || product.contains("fuel")
                    || product.contains("metro")
                    || product.contains("bus")
                    || product.contains("taxi")
                    || product.contains("uber")
                    || product.contains("ola")) {

                return "Transportation";
            }

            // Personal Care
            if (product.contains("shampoo")
                    || product.contains("soap")
                    || product.contains("toothpaste")
                    || product.contains("face wash")
                    || product.contains("cosmetic")
                    || product.contains("cream")
                    || product.contains("lotion")) {

                return "Personal Care";
            }
        }

        // If no specific category is detected
        return "Shopping";
    }
}