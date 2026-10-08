package com.example.aireceiptexpensetracker.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

@Service
public class OcrService {

    public String extractText(Path imagePath) {

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "tesseract",
                    imagePath.toString(),
                    "stdout");

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder text = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("OCR failed");
            }

            return text.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Could not process receipt image",
                    e);
        }
    }
}