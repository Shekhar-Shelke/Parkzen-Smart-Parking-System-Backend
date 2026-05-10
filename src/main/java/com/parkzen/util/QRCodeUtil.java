package com.parkzen.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class QRCodeUtil {

    public String generateQRCodeBase64(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            byte[] qrBytes = outputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrBytes);
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code: {}", e.getMessage());
            return "";
        }
    }

    public String buildBookingQRContent(Long bookingId, String userName, String slotNumber,
                                         String parkingAreaName, String startTime, String endTime) {
        return String.format(
                "PARKZEN_BOOKING\nBookingID: %d\nUser: %s\nSlot: %s\nParking: %s\nStart: %s\nEnd: %s",
                bookingId, userName, slotNumber, parkingAreaName, startTime, endTime
        );
    }
}
