package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.Expense;
import com.example.aireceiptexpensetracker.repository.ExpenseRepository;
import com.example.aireceiptexpensetracker.repository.UserRepository;
import com.example.aireceiptexpensetracker.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.example.aireceiptexpensetracker.service.CategoryService;
import com.example.aireceiptexpensetracker.service.OcrService;
import com.example.aireceiptexpensetracker.service.ReceiptParserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final OcrService ocrService;
    private final ReceiptParserService receiptParserService;
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public ReceiptController(
            OcrService ocrService,
            ReceiptParserService receiptParserService,
            ExpenseRepository expenseRepository,
            CategoryService categoryService,
            UserRepository userRepository) {
        this.ocrService = ocrService;
        this.receiptParserService = receiptParserService;
        this.expenseRepository = expenseRepository;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReceipt(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String email) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please select a receipt image."));
        }

        Path tempFile = null;

        try {
            String originalName = file.getOriginalFilename();
            String extension = ".png";

            if (originalName != null && originalName.lastIndexOf('.') >= 0) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
                if (!extension.matches("\\.[A-Za-z0-9]{1,10}")) {
                    extension = ".png";
                }
            }

            tempFile = Files.createTempFile("receipt-", extension);
            file.transferTo(tempFile);

            String ocrText = ocrService.extractText(tempFile);

            if (ocrText == null || ocrText.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Could not read text from the receipt."));
            }

            String storeName = receiptParserService.extractStoreName(ocrText);
            LocalDate date = receiptParserService.extractDate(ocrText);
            Double total = receiptParserService.extractTotal(ocrText);
            List<String> items = receiptParserService.extractItems(ocrText);

            if (total == null || total <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Could not identify a valid total. Please check the receipt image.",
                                "ocrText",
                                ocrText));
            }

            if (date == null) {
                date = LocalDate.now();
            }

            String category = categoryService.detectCategory(items);

            Expense expense = new Expense();
            expense.setStoreName(storeName);
            expense.setExpenseDate(date);
            expense.setAmount(total);
            expense.setCategory(category);
            expense.setDescription(
                    items.isEmpty() ? "Receipt expense" : String.join(", ", items));

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
            expense.setUser(user);
            Expense savedExpense = expenseRepository.save(expense);

            return ResponseEntity.ok(Map.of(
                    "message", "Receipt processed and saved successfully.",
                    "expenseId", savedExpense.getId(),
                    "storeName", storeName == null ? "Unknown Store" : storeName,
                    "expenseDate", date.toString(),
                    "amount", total,
                    "category", category,
                    "items", items,
                    "ocrText", ocrText));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message", "Receipt processing failed.",
                            "error", e.getMessage() == null
                                    ? "Unknown error" : e.getMessage()));
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception ignored) {
                    // Temporary-file cleanup should not hide the main result.
                }
            }
        }
    }
}
