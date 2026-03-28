package com.esspbackend.controller;

import com.esspbackend.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/works/export")
    public void exportWorkReport(
            @RequestParam(required = false) Long talukaId,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "pdf") String format,
            HttpServletResponse response
    ) throws IOException {
        reportService.exportWorkReport(talukaId, schoolId, status, format, response);
    }

    @GetMapping("/inventory/export")
    public void exportInventoryReport(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(defaultValue = "pdf") String format,
            HttpServletResponse response
    ) throws IOException {
        reportService.exportInventoryReport(schoolId, format, response);
    }

    @GetMapping("/financial/export")
    public void exportFinancialReport(
            @RequestParam(required = false) Long talukaId,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(defaultValue = "pdf") String format,
            HttpServletResponse response
    ) throws IOException {
        reportService.exportFinancialReport(talukaId, schoolId, format, response);
    }

    @GetMapping("/blockers/export")
    public void exportBlockerReport(
            @RequestParam(required = false) Long talukaId,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(defaultValue = "pdf") String format,
            HttpServletResponse response
    ) throws IOException {
        reportService.exportBlockerReport(talukaId, schoolId, format, response);
    }

    @GetMapping("/performance/export")
    public void exportPerformanceReport(
            @RequestParam(required = false) Long talukaId,
            @RequestParam(defaultValue = "pdf") String format,
            HttpServletResponse response
    ) throws IOException {
        reportService.exportPerformanceReport(talukaId, format, response);
    }
}
