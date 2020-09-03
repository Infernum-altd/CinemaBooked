package com.altynnikov.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class QRcodeGenerator {
    public static byte[] generateQRCodeImage(String text, int width, int height)
            throws IOException {

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        }catch (WriterException e){
            e.printStackTrace();
        }

        return pngOutputStream.toByteArray();
    }
}
