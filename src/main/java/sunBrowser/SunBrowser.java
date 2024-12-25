package sunBrowser;

import adsPower.data.Profile;
import application.configuration.Configuration;
import org.openqa.selenium.*;
import sunBrowser.data.Account;
import sunBrowser.data.FacebookResponse;
import sunBrowser.data.Report;
import sunBrowser.data.WebUrl;
import sunBrowser.exceptions.*;
import sunBrowser.utils.AjaxSender;
import sunBrowser.utils.FacebookAccessManager;
import sunBrowser.utils.FacebookResponseConverter;
import utils.Reports;
import utils.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SunBrowser {
    private final WebDriver driver;
    private final FacebookAccessManager accessManager;
    private final String apiUrl = "https://graph.facebook.com/v17.0";
    private final String getAccountsPath = "/me/adaccounts";
    private final String insightsApiPathTemplate = "/act_<account_id>/insights";
    private final String adsApiPathTemplate = "/act_<account_id>/ads";
    public SunBrowser(WebDriver driver, Profile profile) {
        this.driver = driver;
        accessManager = new FacebookAccessManager(driver, profile);
    }

    public List<Account> getAccounts() {
        try {
            URL url = new URL(apiUrl)
                    .add(getAccountsPath)
                    .addParameter("access_token", accessManager.getAccessToken())
                    .addParameter("limit", "2000")
                    .addParameter("fields", "timezone_name,currency,account_id,account_status,adspaymentcycle");
            AjaxSender ajaxSender = new AjaxSender(driver);
            FacebookResponse response = ajaxSender.send(url.getValue(), "GET");
            return FacebookResponseConverter.toAccounts(response);
        } catch (WebDriverException | AjaxException | FacebookAPIResponseException | FacebookResponseConvertingException | ScriptParsingException e) {
            throw new SunBrowserException("Failed to get the profile accounts: " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }

    public Report generateReport(Account account, String type) {
        if (type.equals("new")) {
            return generateReportNew(account);
        } else {
            return generateReportOld(account);
        }
    }

    private Report generateReportNew(Account account) {
        try {
            String regex = "<account_id>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(insightsApiPathTemplate);
            String insightsApiPath = matcher.replaceAll(account.getAccountId());

            URL url = new URL(apiUrl)
                    .add(insightsApiPath)
                    .addParameter("access_token", accessManager.getAccessToken())
                    .addParameter("fields", "ad_name,ad_id,spend")
                    .addParameter(Configuration.report.date.mode, Configuration.report.date.getValue())
                    .addParameter("level", "ad")
                    .addParameter("time_increment", "1")
                    .addParameter("limit", "2000")
                    .addParameter("sort", "date_start_descending");

            AjaxSender ajaxSender = new AjaxSender(driver);
            FacebookResponse response = ajaxSender.send(url.getValue(), "GET");
            List<Object> data = (List<Object>) response.getData();

            Set<String> adIds = new HashSet<>();
            for (Object element : data) {
                adIds.add((String) ((Map<String, Object>) element).get("ad_id"));
            }

            List<WebUrl> webUrls = getWebUrls(account, new ArrayList<>(adIds));

//            System.out.println(RED + "debug: webUrls.size == adIds.size (must be true) :" + (webUrls.size() == adIds.size()) + RESET);
            return Reports.formReportNew(data, webUrls, account);
        } catch (WebDriverException | AjaxException | FacebookAPIResponseException | FacebookResponseConvertingException | ScriptParsingException e) {
            throw new SunBrowserException("Failed to generate report for the account " + account.getAccountId() + ": " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }

    private Report generateReportOld(Account account) {
        try {
            String regex = "<account_id>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(insightsApiPathTemplate);
            String insightsApiPath = matcher.replaceAll(account.getAccountId());

            URL url = new URL(apiUrl)
                    .add(insightsApiPath)
                    .addParameter("access_token", accessManager.getAccessToken())
                    .addParameter("fields", "spend")
                    .addParameter("breakdowns", "country")
                    .addParameter(Configuration.report.date.mode, Configuration.report.date.getValue())
                    .addParameter("time_increment", "1")
                    .addParameter("limit", "2000")
                    .addParameter("sort", "date_start_descending");

            AjaxSender ajaxSender = new AjaxSender(driver);
            FacebookResponse response = ajaxSender.send(url.getValue(), "GET");
            List<Object> data = (List<Object>) response.getData();

            return Reports.formReportOld(data, account);
        } catch (WebDriverException | AjaxException | FacebookAPIResponseException | FacebookResponseConvertingException | ScriptParsingException e) {
            throw new SunBrowserException("Failed to generate report for the account " + account.getAccountId() + ": " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }

    private List<WebUrl> getWebUrls(Account account, List<String> adIds) {
        String regex = "<account_id>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(adsApiPathTemplate);
        String adsApiPath = matcher.replaceAll(account.getAccountId());

        URL url = new URL(apiUrl)
                .add(adsApiPath)
                .addParameter("access_token", accessManager.getAccessToken())
                .addParameter("limit", "2000")
                .addParameter("fields", "creative{object_story_spec,asset_feed_spec},name")
                //Запрос может возвращать не все объявления, так как что-то может быть не указано в фильтрации по effective status. Если не указывать фильтрацию, то по деволту возвращаются тоже не совсем все объявления
                //Чтобы проверить все ли окей, достаточно сравнить количество элементов в webUrls и в первом запросе
                .addParameter("filtering", "[{\"field\":\"effective_status\",\"operator\":\"IN\",\"value\":[\"ACTIVE\",\"PAUSED\",\"DELETED\"," +
                        "\"PENDING_REVIEW\",\"DISAPPROVED\",\"PREAPPROVED\",\"PENDING_BILLING_INFO\",\"CAMPAIGN_PAUSED\",\"ARCHIVED\",\"ADSET_PAUSED\"," +
                        "\"WITH_ISSUES\"]},{\"field\":\"id\",\"operator\":\"IN\",\"value\":" + adIds.toString() + "}]");

        AjaxSender ajaxSender = new AjaxSender(driver);
        FacebookResponse response = ajaxSender.send(url.getValue(), "GET");
        return FacebookResponseConverter.toWebUrls(response);
    }
    public void close() {
        driver.quit();
    }
}
