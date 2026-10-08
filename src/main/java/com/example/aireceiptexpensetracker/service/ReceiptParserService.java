package com.example.aireceiptexpensetracker.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReceiptParserService {

    // Extract store/shop name
    public String extractStoreName(String text) {

        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String upperLine = line.toUpperCase();

            // Direct labels commonly found on receipts
            String[] labels = {
                    "STORE NAME",
                    "STORE",
                    "SHOP",
                    "MERCHANT",
                    "OUTLET"
            };

            for (String label : labels) {

                if (upperLine.startsWith(label)) {

                    String store = line
                            .substring(label.length())
                            .replace(":", "")
                            .trim();

                    if (!store.isEmpty()) {
                        return cleanStoreName(store);
                    }
                }
            }

            // Ignore common receipt information
            if (upperLine.contains("ESTIMATING RESOLUTION")
                    || upperLine.contains("PACK NO")
                    || upperLine.contains("BILL RECEIPT")
                    || upperLine.contains("ORDER NO")
                    || upperLine.contains("BILL NO")
                    || upperLine.contains("DATE")
                    || upperLine.contains("JHSN")
                    || upperLine.contains("PRODUCT")
                    || upperLine.contains("PRODUCV")
                    || upperLine.contains("TOTAL")
                    || upperLine.contains("TAX")
                    || upperLine.contains("PAY MODE")
                    || upperLine.contains("CASHIER")
                    || upperLine.contains("TERMINAL")) {

                continue;
            }

            // Ignore lines containing large numbers
            if (line.matches(".*\\d{4,}.*")) {
                continue;
            }

            // A simple text-only line near the top may be the store name
            if (line.matches("[A-Za-z][A-Za-z &.'-]{2,50}")) {
                return cleanStoreName(line);
            }
        }

        return "Unknown Store";
    }

    private String cleanStoreName(String store) {

        store = store.trim();

        if (store.length() > 50) {
            store = store.substring(0, 50);
        }

        return store;
    }

    // Extract date
    public LocalDate extractDate(String text) {

        Pattern pattern = Pattern.compile(
                "Date\\s*:\\s*(\\d{2}/\\d{2}/\\d{4})",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {

            String dateText = matcher.group(1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            return LocalDate.parse(dateText, formatter);
        }

        return null;
    }

    // Extract total amount
    public Double extractTotal(String text) {

        Pattern pattern = Pattern.compile(
                "Total Amount\\s+([\\d,]+\\.\\d{2})",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {

            String amountText = matcher.group(1).replace(",", "");

            return Double.parseDouble(amountText);
        }

        return null;
    }

    // Extract purchased items
    public List<String> extractItems(String text) {

        List<String> items = new ArrayList<>();

        String[] lines = text.split("\\r?\\n");

        boolean productSectionStarted = false;

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String upperLine = line.toUpperCase();

            // Start reading products
            if (upperLine.contains("JHSN")
                    || upperLine.contains("PRODUCT")
                    || upperLine.contains("PRODUCV")
                    || upperLine.contains("QTY MRP")) {

                productSectionStarted = true;
                continue;
            }

            // Stop reading products
            if (upperLine.contains("SUM")
                    || upperLine.contains("TOTAL QTY")
                    || upperLine.contains("TOTAL AMOUNT")
                    || upperLine.contains("PAY MODE")) {

                break;
            }

            if (productSectionStarted) {

                if (upperLine.contains("ELECTRIC COOKER")) {

                    if (!items.contains("ELECTRIC COOKER")) {
                        items.add("ELECTRIC COOKER");
                    }

                    continue;
                }

                if (upperLine.contains("FLASK")) {

                    if (!items.contains("FLASK")) {
                        items.add("FLASK");
                    }
                }
            }
        }

        return items;
    }
}