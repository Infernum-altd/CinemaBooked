package com.altynnikov.model;

import com.altynnikov.service.DbInteraction;
import com.altynnikov.service.QRcodeGenerator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TicketFile {
    private static final int FONT_SIZE_SMALL = 16;
    private static final int FONT_SIZE_BIG = 32;
    private static String cinemaName;
    private static String cinemaAddress;
    private static String seanceDate;
    private static String row;
    private static String place;
    private static String cost;



    public static File createFile(Ticket ticket) throws IOException, DocumentException {
        Document document = new Document();

        Font font1 = new Font(BaseFont.createFont("c:/windows/fonts/arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED),
                FONT_SIZE_BIG, Font.BOLD);
        Font font2 = new Font(BaseFont.createFont("c:/windows/fonts/arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED),
                FONT_SIZE_SMALL, Font.ITALIC);

        PdfWriter.getInstance(document,
                new FileOutputStream("билет.pdf"));

        document.open();

        Paragraph title = new Paragraph("Билет", font1);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(FONT_SIZE_BIG);
        document.add(title);
        Paragraph purpose = new Paragraph(new Chunk("Название фильма: " + DbInteraction.getFilmNameBySeanceId(ticket.getSeanceId()), font2));
        document.add(purpose);
        document.add(new Paragraph(new Chunk("Кинотеатр: " + ticket.getCinemaName(), font2)));
        document.add(new Paragraph(new Chunk("Адрес: " + ticket.getAddress(), font2)));
        document.add(new Paragraph(new Chunk("Дата сеанса: " + ticket.getDate(), font2)));
        document.add(new Paragraph(new Chunk("Ряд: " + ticket.getPlace().getRowNumber(), font2)));
        document.add(new Paragraph(new Chunk("Место: " + ticket.getPlace().getPlaceNumber(), font2)));
        document.add(new Paragraph(new Chunk("Ціна: " + ticket.getCost(), font2)));

        String textToQR = "Row number: " + ticket.getPlace().getRowNumber() + "\nPlace number: " + ticket.getPlace().getPlaceNumber();
        Image stamp = Image.getInstance(QRcodeGenerator.generateQRCodeImage(textToQR, 150, 150));
        stamp.setAlignment(Element.ALIGN_CENTER);
        document.add(stamp);

        document.close();

        return new File("билет.pdf");
    }
}
