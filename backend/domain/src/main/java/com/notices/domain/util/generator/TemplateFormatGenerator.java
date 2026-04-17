package com.notices.domain.util.generator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

@Slf4j
@AllArgsConstructor
public class TemplateFormatGenerator {

    public String getPageTitle(String pageTitle, String layoutTitle) {
        if (StringUtils.hasText(pageTitle)) {
            return layoutTitle + " - " + pageTitle;
        }
        return layoutTitle;
    }

    public String formatAddressOccupant(String addressOccupant) {
        if (StringUtils.hasText(addressOccupant) && !addressOccupant.contains("/")) {
            addressOccupant = "C/O " + addressOccupant;
        }
        return addressOccupant;
    }

    public String formatAddress(String address) {
        if (StringUtils.hasText(address)) {
            String[] addressList = address.split(",");
            int totalLength = addressList.length;
            StringBuilder newAddress = new StringBuilder();
            int notice = 0;
            for (int i = 0; i < totalLength; i++) {
                newAddress.append(addressList[i]);
                if (StringUtils.hasText(addressList[i])) {
                    newAddress.append(",");
                    notice++;
                }
                if ((notice + 1) % 5 == 0) {
                    newAddress.append("<br/>");
                }
            }
            address = newAddress.toString();
        }
        return address;
    }

    public String formatDate(Object dateValue, String format) {
        if (dateValue == null) {
            return "";
        }
        if (dateValue instanceof String) {
            if (dateValue.toString().contains("T")) {

                OffsetDateTime odt = OffsetDateTime.parse(dateValue.toString());
                Instant instantValue = odt.toInstant();
                Date date = Date.from(instantValue);
                SimpleDateFormat sFormat = new SimpleDateFormat(format);
                return sFormat.format(date);
            }
            return dateValue.toString();
        } else if (dateValue instanceof Date) {
            SimpleDateFormat sFormat = new SimpleDateFormat(format);
            return sFormat.format(dateValue);
        } else if (dateValue instanceof Instant) {
            var instantValue = (Instant) dateValue;
            Date date = Date.from(instantValue);
            SimpleDateFormat sFormat = new SimpleDateFormat(format);
            return sFormat.format(date);
        }
        return "";

    }

}
