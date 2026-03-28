package com.esspbackend.service;

import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final WorkRepository workRepository;
    private final SchoolRepository schoolRepository;
    private final TalukaRepository talukaRepository;
    private final MaterialRepository materialRepository;
    private final SchoolInventoryRepository schoolInventoryRepository;
    private final BlockerRepository blockerRepository;
    private final QuotationRepository quotationRepository;

    public ReportService(WorkRepository workRepository, SchoolRepository schoolRepository, 
                         TalukaRepository talukaRepository, MaterialRepository materialRepository, 
                         SchoolInventoryRepository schoolInventoryRepository, BlockerRepository blockerRepository, 
                         QuotationRepository quotationRepository) {
        this.workRepository = workRepository;
        this.schoolRepository = schoolRepository;
        this.talukaRepository = talukaRepository;
        this.materialRepository = materialRepository;
        this.schoolInventoryRepository = schoolInventoryRepository;
        this.blockerRepository = blockerRepository;
        this.quotationRepository = quotationRepository;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==================== WORK REPORTS ====================

    public void exportWorkReport(Long talukaId, Long schoolId, String status, String format, HttpServletResponse response) throws IOException {
        List<Work> works;
        if (schoolId != null) {
            works = (status != null && !status.equals("ALL")) 
                ? workRepository.findBySchoolIdAndStatus(schoolId, status)
                : workRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
        } else if (talukaId != null) {
            List<School> schools = schoolRepository.findByTalukaId(talukaId);
            List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
            works = new ArrayList<>();
            for (Long sId : schoolIds) {
                works.addAll(workRepository.findBySchoolId(sId));
            }
            if (status != null && !status.equals("ALL")) {
                works = works.stream().filter(w -> w.getStatus().equals(status)).collect(Collectors.toList());
            }
        } else {
            works = (status != null && !status.equals("ALL"))
                ? workRepository.findByStatusOrderByCreatedAtDesc(status)
                : workRepository.findAll();
        }

        if (format.equalsIgnoreCase("excel")) {
            exportWorksToExcel(works, response);
        } else if (format.equalsIgnoreCase("csv")) {
            exportWorksToCsv(works, response);
        } else {
            exportWorksToPdf(works, response);
        }
    }

    private void exportWorksToPdf(List<Work> works, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=works_report.pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph p = new Paragraph("Work Progress Report", fontTitle);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 1.5f, 1.5f, 2.0f, 2.0f});
        table.setSpacingBefore(10);

        writeWorkTableHeader(table);
        writeWorkTableData(table, works);

        document.add(table);
        document.close();
    }

    private void writeWorkTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(10);

        cell.setPhrase(new Phrase("Work Code", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Title", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("School", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Status", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Progress", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Sanctioned Amt", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Utilized Amt", font));
        table.addCell(cell);
    }

    private void writeWorkTableData(PdfPTable table, List<Work> works) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(9);

        for (Work work : works) {
            table.addCell(new Phrase(work.getWorkCode(), font));
            table.addCell(new Phrase(work.getTitle(), font));
            
            String schoolName = "N/A";
            School s = schoolRepository.findById(work.getSchoolId()).orElse(null);
            if (s != null) schoolName = s.getName();
            table.addCell(new Phrase(schoolName, font));
            
            table.addCell(new Phrase(work.getStatus(), font));
            table.addCell(new Phrase(work.getProgressPercentage() + "%", font));
            table.addCell(new Phrase("₹" + work.getSanctionedAmount(), font));
            table.addCell(new Phrase("₹" + work.getTotalUtilized(), font));
        }
    }

    private void exportWorksToExcel(List<Work> works, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=works_report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Works");

        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
        String[] columns = {"Work Code", "Title", "School", "Status", "Progress", "Sanctioned Amount", "Utilized Amount"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Work work : works) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(work.getWorkCode());
            row.createCell(1).setCellValue(work.getTitle());
            
            School s = schoolRepository.findById(work.getSchoolId()).orElse(null);
            row.createCell(2).setCellValue(s != null ? s.getName() : "N/A");
            
            row.createCell(3).setCellValue(work.getStatus());
            row.createCell(4).setCellValue(work.getProgressPercentage() + "%");
            row.createCell(5).setCellValue(work.getSanctionedAmount());
            row.createCell(6).setCellValue(work.getTotalUtilized());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void exportWorksToCsv(List<Work> works, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=works_report.csv");

        PrintWriter writer = response.getWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Work Code", "Title", "School", "Status", "Progress", "Sanctioned Amount", "Utilized Amount"));

        for (Work work : works) {
            School s = schoolRepository.findById(work.getSchoolId()).orElse(null);
            csvPrinter.printRecord(
                work.getWorkCode(),
                work.getTitle(),
                s != null ? s.getName() : "N/A",
                work.getStatus(),
                work.getProgressPercentage() + "%",
                work.getSanctionedAmount(),
                work.getTotalUtilized()
            );
        }
        csvPrinter.flush();
    }

    // ==================== INVENTORY REPORTS ====================

    public void exportInventoryReport(Long schoolId, String format, HttpServletResponse response) throws IOException {
        List<SchoolInventory> inventory;
        if (schoolId != null) {
            inventory = schoolInventoryRepository.findBySchoolId(schoolId);
        } else {
            inventory = schoolInventoryRepository.findAll();
        }

        if (format.equalsIgnoreCase("excel")) {
            exportInventoryToExcel(inventory, response);
        } else if (format.equalsIgnoreCase("csv")) {
            exportInventoryToCsv(inventory, response);
        } else {
            exportInventoryToPdf(inventory, response);
        }
    }

    private void exportInventoryToPdf(List<SchoolInventory> inventory, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=inventory_report.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph p = new Paragraph("Inventory Stock Report", fontTitle);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        writeInventoryTableHeader(table);
        writeInventoryTableData(table, inventory);

        document.add(table);
        document.close();
    }

    private void writeInventoryTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(10);

        table.addCell(new Phrase("School", font));
        table.addCell(new Phrase("Material", font));
        table.addCell(new Phrase("Current Stock", font));
        table.addCell(new Phrase("Min Level", font));
        table.addCell(new Phrase("Status", font));
    }

    private void writeInventoryTableData(PdfPTable table, List<SchoolInventory> inventory) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(9);

        for (SchoolInventory item : inventory) {
            School s = schoolRepository.findById(item.getSchoolId()).orElse(null);
            table.addCell(new Phrase(s != null ? s.getName() : "N/A", font));
            
            Material m = materialRepository.findById(item.getMaterialId()).orElse(null);
            table.addCell(new Phrase(m != null ? m.getName() : "N/A", font));
            
            table.addCell(new Phrase(item.getCurrentQuantity() + " " + (m != null ? m.getUnitOfMeasurement() : ""), font));
            table.addCell(new Phrase(m != null ? m.getMinStockLevel().toString() : "0", font));
            
            String status = "Normal";
            if (m != null) {
                if (item.getCurrentQuantity() <= 0) status = "OUT OF STOCK";
                else if (item.getCurrentQuantity() <= m.getMinStockLevel()) status = "LOW STOCK";
            }
            table.addCell(new Phrase(status, font));
        }
    }

    private void exportInventoryToExcel(List<SchoolInventory> inventory, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventory_report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory");

        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
        String[] columns = {"School", "Material", "Current Stock", "Min Level", "Status"};
        
        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (SchoolInventory item : inventory) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            School s = schoolRepository.findById(item.getSchoolId()).orElse(null);
            row.createCell(0).setCellValue(s != null ? s.getName() : "N/A");
            
            Material m = materialRepository.findById(item.getMaterialId()).orElse(null);
            row.createCell(1).setCellValue(m != null ? m.getName() : "N/A");
            
            row.createCell(2).setCellValue(item.getCurrentQuantity());
            row.createCell(3).setCellValue(m != null ? m.getMinStockLevel() : 0);
            
            String status = "Normal";
            if (m != null) {
                if (item.getCurrentQuantity() <= 0) status = "OUT OF STOCK";
                else if (item.getCurrentQuantity() <= m.getMinStockLevel()) status = "LOW STOCK";
            }
            row.createCell(4).setCellValue(status);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void exportInventoryToCsv(List<SchoolInventory> inventory, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=inventory_report.csv");

        PrintWriter writer = response.getWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("School", "Material", "Current Stock", "Min Level", "Status"));

        for (SchoolInventory item : inventory) {
            School s = schoolRepository.findById(item.getSchoolId()).orElse(null);
            Material m = materialRepository.findById(item.getMaterialId()).orElse(null);
            
            String status = "Normal";
            if (m != null) {
                if (item.getCurrentQuantity() <= 0) status = "OUT OF STOCK";
                else if (item.getCurrentQuantity() <= m.getMinStockLevel()) status = "LOW STOCK";
            }
            
            csvPrinter.printRecord(
                s != null ? s.getName() : "N/A",
                m != null ? m.getName() : "N/A",
                item.getCurrentQuantity(),
                m != null ? m.getMinStockLevel() : 0,
                status
            );
        }
        csvPrinter.flush();
    }

    // ==================== FINANCIAL REPORTS ====================

    public void exportFinancialReport(Long talukaId, Long schoolId, String format, HttpServletResponse response) throws IOException {
        List<Work> works;
        if (schoolId != null) {
            works = workRepository.findBySchoolId(schoolId);
        } else if (talukaId != null) {
            List<School> schools = schoolRepository.findByTalukaId(talukaId);
            List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
            works = new ArrayList<>();
            for (Long sId : schoolIds) {
                works.addAll(workRepository.findBySchoolId(sId));
            }
        } else {
            works = workRepository.findAll();
        }

        if (format.equalsIgnoreCase("excel")) {
            exportFinancialToExcel(works, response);
        } else if (format.equalsIgnoreCase("csv")) {
            exportFinancialToCsv(works, response);
        } else {
            exportFinancialToPdf(works, response);
        }
    }

    private void exportFinancialToPdf(List<Work> works, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=financial_report.pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph p = new Paragraph("Financial Expenditure Report", fontTitle);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        writeFinancialTableHeader(table);
        
        double totalSanctioned = 0;
        double totalUtilized = 0;

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(9);

        for (Work work : works) {
            table.addCell(new Phrase(work.getWorkCode(), font));
            table.addCell(new Phrase(work.getTitle(), font));
            
            School s = schoolRepository.findById(work.getSchoolId()).orElse(null);
            table.addCell(new Phrase(s != null ? s.getName() : "N/A", font));
            
            table.addCell(new Phrase("₹" + work.getSanctionedAmount(), font));
            table.addCell(new Phrase("₹" + work.getTotalUtilized(), font));
            table.addCell(new Phrase("₹" + (work.getSanctionedAmount() - work.getTotalUtilized()), font));
            
            totalSanctioned += work.getSanctionedAmount();
            totalUtilized += work.getTotalUtilized();
        }

        // Add summary row
        PdfPCell summaryCell = new PdfPCell(new Phrase("TOTAL", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        summaryCell.setColspan(3);
        summaryCell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(summaryCell);
        
        table.addCell(new Phrase("₹" + totalSanctioned, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        table.addCell(new Phrase("₹" + totalUtilized, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        table.addCell(new Phrase("₹" + (totalSanctioned - totalUtilized), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));

        document.add(table);
        document.close();
    }

    private void writeFinancialTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(10);

        table.addCell(new Phrase("Code", font));
        table.addCell(new Phrase("Work Title", font));
        table.addCell(new Phrase("School", font));
        table.addCell(new Phrase("Sanctioned", font));
        table.addCell(new Phrase("Utilized", font));
        table.addCell(new Phrase("Balance", font));
    }

    private void exportFinancialToExcel(List<Work> works, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=financial_report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial");

        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
        String[] columns = {"Code", "Work Title", "School", "Sanctioned", "Utilized", "Balance"};
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Work work : works) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(work.getWorkCode());
            row.createCell(1).setCellValue(work.getTitle());
            School s = schoolRepository.findById(work.getSchoolId()).orElse(null);
            row.createCell(2).setCellValue(s != null ? s.getName() : "N/A");
            row.createCell(3).setCellValue(work.getSanctionedAmount());
            row.createCell(4).setCellValue(work.getTotalUtilized());
            row.createCell(5).setCellValue(work.getSanctionedAmount() - work.getTotalUtilized());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void exportFinancialToCsv(List<Work> works, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=financial_report.csv");

        PrintWriter writer = response.getWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Code", "Work Title", "School", "Sanctioned", "Utilized", "Balance"));

        for (Work work : works) {
            School s = schoolRepository.findById(work.getSchoolId()).orElse(null);
            csvPrinter.printRecord(
                work.getWorkCode(),
                work.getTitle(),
                s != null ? s.getName() : "N/A",
                work.getSanctionedAmount(),
                work.getTotalUtilized(),
                work.getSanctionedAmount() - work.getTotalUtilized()
            );
        }
        csvPrinter.flush();
    }

    // ==================== BLOCKER REPORTS ====================

    public void exportBlockerReport(Long talukaId, Long schoolId, String format, HttpServletResponse response) throws IOException {
        List<Blocker> blockers = blockerRepository.findAll();
        
        if (schoolId != null) {
            blockers = blockers.stream().filter(b -> b.getSchoolId().equals(schoolId)).collect(Collectors.toList());
        } else if (talukaId != null) {
            List<School> schools = schoolRepository.findByTalukaId(talukaId);
            List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
            blockers = blockers.stream().filter(b -> schoolIds.contains(b.getSchoolId())).collect(Collectors.toList());
        }

        if (format.equalsIgnoreCase("excel")) {
            exportBlockersToExcel(blockers, response);
        } else if (format.equalsIgnoreCase("csv")) {
            exportBlockersToCsv(blockers, response);
        } else {
            exportBlockersToPdf(blockers, response);
        }
    }

    private void exportBlockersToPdf(List<Blocker> blockers, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=blockers_report.pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph p = new Paragraph("Work Blockers Report", fontTitle);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        writeBlockerTableHeader(table);
        
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(9);

        for (Blocker blocker : blockers) {
            table.addCell(new Phrase(blocker.getTitle(), font));
            
            String workCode = "N/A";
            Work w = workRepository.findById(blocker.getWorkId()).orElse(null);
            if (w != null) workCode = w.getWorkCode();
            table.addCell(new Phrase(workCode, font));
            
            School s = schoolRepository.findById(blocker.getSchoolId()).orElse(null);
            table.addCell(new Phrase(s != null ? s.getName() : "N/A", font));
            
            table.addCell(new Phrase(blocker.getType(), font));
            table.addCell(new Phrase(blocker.getPriority(), font));
            table.addCell(new Phrase(blocker.getStatus(), font));
        }

        document.add(table);
        document.close();
    }

    private void writeBlockerTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(10);

        table.addCell(new Phrase("Blocker Title", font));
        table.addCell(new Phrase("Work Code", font));
        table.addCell(new Phrase("School", font));
        table.addCell(new Phrase("Type", font));
        table.addCell(new Phrase("Priority", font));
        table.addCell(new Phrase("Status", font));
    }

    private void exportBlockersToExcel(List<Blocker> blockers, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=blockers_report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Blockers");

        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
        String[] columns = {"Blocker Title", "Work Code", "School", "Type", "Priority", "Status"};
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Blocker blocker : blockers) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(blocker.getTitle());
            Work w = workRepository.findById(blocker.getWorkId()).orElse(null);
            row.createCell(1).setCellValue(w != null ? w.getWorkCode() : "N/A");
            School s = schoolRepository.findById(blocker.getSchoolId()).orElse(null);
            row.createCell(2).setCellValue(s != null ? s.getName() : "N/A");
            row.createCell(3).setCellValue(blocker.getType());
            row.createCell(4).setCellValue(blocker.getPriority());
            row.createCell(5).setCellValue(blocker.getStatus());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void exportBlockersToCsv(List<Blocker> blockers, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=blockers_report.csv");

        PrintWriter writer = response.getWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Blocker Title", "Work Code", "School", "Type", "Priority", "Status"));

        for (Blocker blocker : blockers) {
            Work w = workRepository.findById(blocker.getWorkId()).orElse(null);
            School s = schoolRepository.findById(blocker.getSchoolId()).orElse(null);
            csvPrinter.printRecord(
                blocker.getTitle(),
                w != null ? w.getWorkCode() : "N/A",
                s != null ? s.getName() : "N/A",
                blocker.getType(),
                blocker.getPriority(),
                blocker.getStatus()
            );
        }
        csvPrinter.flush();
    }

    // ==================== PERFORMANCE REPORTS ====================

    public void exportPerformanceReport(Long talukaId, String format, HttpServletResponse response) throws IOException {
        List<School> schools;
        if (talukaId != null) {
            schools = schoolRepository.findByTalukaId(talukaId);
        } else {
            schools = schoolRepository.findAll();
        }

        if (format.equalsIgnoreCase("excel")) {
            exportPerformanceToExcel(schools, response);
        } else if (format.equalsIgnoreCase("csv")) {
            exportPerformanceToCsv(schools, response);
        } else {
            exportPerformanceToPdf(schools, response);
        }
    }

    private void exportPerformanceToPdf(List<School> schools, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=performance_report.pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph p = new Paragraph("School Performance Ranking Report", fontTitle);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        writePerformanceTableHeader(table);
        
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(9);

        for (School school : schools) {
            table.addCell(new Phrase(school.getName(), font));
            table.addCell(new Phrase(school.getCode(), font));
            
            List<Work> schoolWorks = workRepository.findBySchoolId(school.getId());
            int total = schoolWorks.size();
            long completed = schoolWorks.stream().filter(w -> w.getStatus().equals("COMPLETED")).count();
            
            table.addCell(new Phrase(String.valueOf(total), font));
            table.addCell(new Phrase(String.valueOf(completed), font));
            
            double rate = total > 0 ? (completed * 100.0 / total) : 0;
            table.addCell(new Phrase(String.format("%.1f%%", rate), font));
            
            // Simplified quality score based on blockers
            long blockers = blockerRepository.findAll().stream().filter(b -> b.getSchoolId().equals(school.getId())).count();
            double qualityScore = 100 - (blockers * 5); // Example logic
            if (qualityScore < 0) qualityScore = 0;
            table.addCell(new Phrase(String.format("%.1f", qualityScore), font));
        }

        document.add(table);
        document.close();
    }

    private void writePerformanceTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(10);

        table.addCell(new Phrase("School Name", font));
        table.addCell(new Phrase("Code", font));
        table.addCell(new Phrase("Total Works", font));
        table.addCell(new Phrase("Completed", font));
        table.addCell(new Phrase("Comp. Rate", font));
        table.addCell(new Phrase("Quality Score", font));
    }

    private void exportPerformanceToExcel(List<School> schools, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=performance_report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Performance");

        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
        String[] columns = {"School Name", "Code", "Total Works", "Completed", "Comp. Rate", "Quality Score"};
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (School school : schools) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(school.getName());
            row.createCell(1).setCellValue(school.getCode());
            
            List<Work> schoolWorks = workRepository.findBySchoolId(school.getId());
            int total = schoolWorks.size();
            long completed = schoolWorks.stream().filter(w -> w.getStatus().equals("COMPLETED")).count();
            
            row.createCell(2).setCellValue(total);
            row.createCell(3).setCellValue(completed);
            
            double rate = total > 0 ? (completed * 100.0 / total) : 0;
            row.createCell(4).setCellValue(String.format("%.1f%%", rate));
            
            long blockers = blockerRepository.findAll().stream().filter(b -> b.getSchoolId().equals(school.getId())).count();
            double qualityScore = 100 - (blockers * 5);
            row.createCell(5).setCellValue(Math.max(0, qualityScore));
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void exportPerformanceToCsv(List<School> schools, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=performance_report.csv");

        PrintWriter writer = response.getWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("School Name", "Code", "Total Works", "Completed", "Comp. Rate", "Quality Score"));

        for (School school : schools) {
            List<Work> schoolWorks = workRepository.findBySchoolId(school.getId());
            int total = schoolWorks.size();
            long completed = schoolWorks.stream().filter(w -> w.getStatus().equals("COMPLETED")).count();
            double rate = total > 0 ? (completed * 100.0 / total) : 0;
            long blockers = blockerRepository.findAll().stream().filter(b -> b.getSchoolId().equals(school.getId())).count();
            double qualityScore = Math.max(0, 100 - (blockers * 5));
            
            csvPrinter.printRecord(
                school.getName(),
                school.getCode(),
                total,
                completed,
                String.format("%.1f%%", rate),
                qualityScore
            );
        }
        csvPrinter.flush();
    }
}
