package com.tpe.controller;

import com.tpe.service.CsvExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/exports")
public class CsvExportController {

    private final CsvExportService csvExportService;

    public CsvExportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<StreamingResponseBody> downloadCsv() {
        StreamingResponseBody stream = outputStream -> {
            csvExportService.writeRecordsToCsv(outputStream);
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=large-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(stream);
    }
}
