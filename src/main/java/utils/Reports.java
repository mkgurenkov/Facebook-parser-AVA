package utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sunBrowser.data.Account;
import sunBrowser.data.Report;
import sunBrowser.data.WebUrl;

public class Reports {
    public static Report formReportNew(List<Object> mainData, List<WebUrl> webUrls, Account account) {
        String[] headers = new String[]{"Day", "Ad name", "Amount spent", "Currency", "Account ID", "Website URL", "Timezone", "Reporting starts", "Reporting ends", "Threshold", "In grace period"};
        List<List<String>> rows = new ArrayList<>();
        for (Object element : mainData) {
            Map<String, Object> elementMap = (Map<String, Object>) element;
            List<String> row = new ArrayList<>();

            row.add((String) elementMap.get("date_start"));
            row.add((String) elementMap.get("ad_name"));
            row.add((String) elementMap.get("spend"));
            row.add(account.getCurrency());
            row.add(account.getAccountId());
            row.add(getWebUrlByAdId((String) elementMap.get("ad_id"), webUrls));
            row.add(account.getTimezoneName());
            row.add((String) elementMap.get("date_start"));
            row.add((String) elementMap.get("date_stop"));
            row.add(account.getAccountStatus() != 2 ? String.valueOf(account.getThreshold()) : "");
            row.add(account.getAccountStatus() == 9 ? String.valueOf(true) : "");

            rows.add(row);
        }

        Report report = new Report();
        report.setName(account.getAccountId());
        report.setHeaders(headers);
        report.setRows(rows);
        return report;
    }
    public static Report formReportOld(List<Object> mainData, Account account) {
        String[] headers = new String[]{"Day", "Country", "Amount spent", "Currency", "Reporting starts", "Reporting ends", "Account ID", "Threshold", "In grace period"};
        List<List<String>> rows = new ArrayList<>();
        for (Object element : mainData) {
            Map<String, Object> elementMap = (Map<String, Object>) element;
            List<String> row = new ArrayList<>();

            row.add((String) elementMap.get("date_start"));
            row.add((String) elementMap.get("country"));
            row.add((String) elementMap.get("spend"));
            row.add(account.getCurrency());
            row.add((String) elementMap.get("date_start"));
            row.add((String) elementMap.get("date_stop"));
            row.add(account.getAccountId());
            row.add(account.getAccountStatus() != 2 ? String.valueOf(account.getThreshold()) : "");
            row.add(account.getAccountStatus() == 9 ? String.valueOf(true) : "");

            rows.add(row);
        }

        Report report = new Report();
        report.setName(account.getAccountId());
        report.setHeaders(headers);
        report.setRows(rows);
        return report;
    }
    public static void download(Report report, String path) throws IOException {
        File reportsFolder = new File(path + "\\" + LocalDate.now());
        reportsFolder.mkdirs();
        try (FileWriter writer = new FileWriter(reportsFolder + "\\" + report.getName() + ".csv");
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(report.getHeaders()))) {

            for (List<String> row : report.getRows()) {
                printer.printRecord(row);
            }
        }
    }
    private static String getWebUrlByAdId(String adId, List<WebUrl> webUrls) {
        for (WebUrl webUrl : webUrls) {
            if (webUrl.getAdId().equals(adId)) {
                return webUrl.getValue();
            }
        }
        return null;
    }
}