package com.tpe.service;

import com.tpe.model.Rule;
import com.tpe.repository.RuleRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@Service
public class CsvExportService {

    private final RuleRepository recordRepository;

    public CsvExportService(RuleRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Transactional(readOnly = true)
    public void writeRecordsToCsv(OutputStream outputStream) {
        // Use .build() or .get() depending on your commons-csv version
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("Rule ID") // Adjust headers to match your Rule fields
                .build();

        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
                // Notice the stream type matches your repository stream (Stream<Rule>)
                Stream<Rule> ruleStream = recordRepository.streamAllRecords()
        ) {
            ruleStream.forEach(rule -> {
                try {
                    csvPrinter.printRecord(
                            // Replace these with actual getter methods available on your Rule class
                            rule.getId()
                    );
                } catch (IOException e) {
                    throw new RuntimeException("Error while writing CSV row", e);
                }
            });
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to CSV", e);
        }
    }

    /*
    // If you are using MyBatis, you can implement a similar method using a Cursor to stream records. Here's an example:
    @Transactional(readOnly = true) // Crucial: Keeps the database connection open during iteration
    public void writeRecordsToCsv(OutputStream outputStream) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("Rule ID", "Rule Name")
                .build();

        // Cursor implements AutoCloseable, so try-with-resources handles cleanup safely
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
                Cursor<Rule> ruleCursor = ruleMapper.streamAllRules()
        ) {
            for (Rule rule : ruleCursor) {
                csvPrinter.printRecord(
                        rule.getRuleId()
                        // rule.getName() -> add your actual fields here
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to CSV via MyBatis", e);
        }
    }

     */
}