package com.notices.domain.util;

import com.notices.domain.dao.notification.SendSmsDao;
import com.notices.domain.dao.notification.SendWhatsappDao;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExportUtil {

    public byte[] buildSmsCsv(List<SendSmsDao> items, boolean isErrorLog) {
        StringBuilder sb = new StringBuilder();
        if (isErrorLog) {
            sb.append("sendTo,sendAt,errorMessage,message\n");
            for (SendSmsDao r : items)
                sb.append(row(r.getSendTo(), r.getSendAt(), r.getErrorMessage(), r.getMessage()));
        } else {
            sb.append("sendTo,sendAt,ackId,status,receivedAt,message\n");
            for (SendSmsDao r : items)
                sb.append(row(r.getSendTo(), r.getSendAt(), r.getAckId(), r.getReceivedStatus(), r.getReceivedAt(), r.getMessage()));
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] buildWhatsappCsv(List<SendWhatsappDao> items, boolean isErrorLog) {
        StringBuilder sb = new StringBuilder();
        if (isErrorLog) {
            sb.append("sendTo,sendAt,errorMessage,message\n");
            for (SendWhatsappDao r : items)
                sb.append(row(r.getSendTo(), r.getSendAt(), r.getErrorMessage(), r.getMessage()));
        } else {
            sb.append("sendTo,sendAt,ackId,status,receivedAt,message\n");
            for (SendWhatsappDao r : items)
                sb.append(row(r.getSendTo(), r.getSendAt(), r.getAckId(), r.getReceivedStatus(), r.getReceivedAt(), r.getMessage()));
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String row(Object... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(fields[i]));
        }
        return sb.append('\n').toString();
    }

    private String escape(Object value) {
        if (value == null)
            return "";
        String s = value.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
