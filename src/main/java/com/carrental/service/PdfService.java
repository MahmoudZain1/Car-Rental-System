package com.carrental.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.carrental.entity.PersonalInformation;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {
    private final UserServiceImpl userService;

    public PdfService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public byte[] generatePdf() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            PdfFont font = PdfFontFactory.createFont();

            Paragraph title = new Paragraph("Car Rental - Users Report")
                    .setFont(font)
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setItalic();
            document.add(date);

            document.add(new Paragraph("\n"));

            List<PersonalInformation> users = userService.getAllUsers();
            if (users == null || users.isEmpty()) {
                document.add(new Paragraph("No users found").setTextAlignment(TextAlignment.CENTER).setBold());
            } else {
                Table table = new Table(new float[]{1, 2, 2, 3, 2, 2})
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginBottom(20)
                        .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

                table.addHeaderCell(createStyledHeaderCell("ID"));
                table.addHeaderCell(createStyledHeaderCell("First Name"));
                table.addHeaderCell(createStyledHeaderCell("Last Name"));
                table.addHeaderCell(createStyledHeaderCell("Email"));
                table.addHeaderCell(createStyledHeaderCell("Phone"));
                table.addHeaderCell(createStyledHeaderCell("Birth Date"));

                for (PersonalInformation user : users) {
                    table.addCell(createStyledCell(String.valueOf(user.getId())));
                    table.addCell(createStyledCell(user.getFirstName()));
                    table.addCell(createStyledCell(user.getLastName()));
                    table.addCell(createStyledCell(user.getEmail()));
                    table.addCell(createStyledCell(user.getPhone()));
                    table.addCell(createStyledCell(user.getDateOfBirth().toString()));
                }
                document.add(table);
            }

            document.add(new Paragraph("\n\n"));
            Paragraph signature = new Paragraph("Report generated by: Mahmoud Zain")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBold()
                    .setFontSize(12);
            document.add(signature);

            document.close();
            pdf.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    public void generateAndSavePdf() {
        byte[] pdfBytes = generatePdf();
        try (FileOutputStream fos = new FileOutputStream("users-report.pdf")) {
            fos.write(pdfBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Cell createStyledHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));
    }

    private Cell createStyledCell(String text) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "N/A"))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f));
    }
}
