package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Expense;
import com.example.aireceiptexpensetracker.repository.ExpenseRepository;
import com.example.aireceiptexpensetracker.service.CategoryService;
import com.example.aireceiptexpensetracker.service.OcrService;
import com.example.aireceiptexpensetracker.service.ReceiptParserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/receipts")
@CrossOrigin(origins = "*")
public class ReceiptController {

    private final OcrService ocrService;
    private final ReceiptParserService receiptParserService;
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;

    private final String uploadDir = "uploads/";

    public ReceiptController(
            OcrService ocrService,
            ReceiptParserService receiptParserService,
            ExpenseRepository expenseRepository,
            CategoryService categoryService) {

        this.ocrService = ocrService;
        this.receiptParserService = receiptParserService;
        this.expenseRepository = expenseRepository;
        this.categoryService = categoryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadReceipt(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Please select a receipt image.");
        }

        try {

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(file.getOriginalFilename());

            Files.write(filePath, file.getBytes());

            // OCR
            String extractedText = ocrService.extractText(filePath);

            // Extract store name
            String storeName = receiptParserService.extractStoreName(extractedText);

            // Extract date
            LocalDate date = receiptParserService.extractDate(extractedText);

            // Extract total
            Double total = receiptParserService.extractTotal(extractedText);

            // Extract items
            List<String> items = receiptParserService.extractItems(extractedText);

            // Detect category
            String category = categoryService.detectCategory(items);

            // Create expense
            Expense expense = new Expense();

            expense.setStoreName(storeName);
            expense.setExpenseDate(date);
            expense.setAmount(total);
            expense.setCategory(category);
            expense.setDescription(
                    String.join(", ", items));

            // Save to MySQL
            Expense savedExpense = expenseRepository.save(expense);

            return ResponseEntity.ok(

                    "Receipt processed successfully!\n\n"

                            + "Expense ID: "
                            + savedExpense.getId() + "\n"

                            + "Store: "
                            + storeName + "\n"

                            + "Date: "
                            + date + "\n"

                            + "Total: ₹"
                            + total + "\n"

                            + "Items: "
                            + items + "\n"

                            + "Category: "
                            + category + "\n"

                            + "Saved to MySQL: YES");

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body("Failed to upload receipt.");

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(
                            "Receipt processing failed: "
                                    + e.getMessage());
        }
    }
}