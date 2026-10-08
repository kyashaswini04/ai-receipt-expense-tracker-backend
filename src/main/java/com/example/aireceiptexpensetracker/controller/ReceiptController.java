package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.service.CategoryService;
import com.example.aireceiptexpensetracker.service.OcrService;
import com.example.aireceiptexpensetracker.service.ReceiptParserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/receipts")
@CrossOrigin(origins = "http://localhost:5173")
public class ReceiptController {

    private final OcrService ocrService;
    private final ReceiptParserService receiptParserService;
    private final CategoryService categoryService;

    public ReceiptController(
            OcrService ocrService,
            ReceiptParserService receiptParserService,
            CategoryService categoryService) {

        this.ocrService = ocrService;
        this.receiptParserService = receiptParserService;
        this.categoryService = categoryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReceipt(
            @RequestParam("file") MultipartFile file) {

        try {

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Please select a receipt image."));
            }

            String ocrText = ocrService.extractText(file);

            if (ocrText == null ||
                    ocrText.trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Could not read text from the receipt."));
            }

            Map<String, Object> result = receiptParserService.parseReceipt(ocrText);

            String category = categoryService.detectCategory(
                    ocrText);

            result.put(
                    "category",
                    category);

            result.put(
                    "ocrText",
                    ocrText);

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message",
                            "Receipt processing failed."));
        }
    }
}