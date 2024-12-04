package sunBrowser;

import adsPower.data.Profile;
import application.configuration.Configuration;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import sunBrowser.data.Account;
import sunBrowser.data.Report;
import sunBrowser.data.WebUrl;
import sunBrowser.exceptions.*;
import utils.Reports;
import utils.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static application.Main.RED;
import static application.Main.RESET;


public class SunBrowser {
    private final WebDriver driver;
    private final String apiUrl = "https://graph.facebook.com/v17.0";
    private final String getAccountsPath = "/me/adaccounts";
    private final String insightsApiPathTemplate = "/act_<account_id>/insights";
    private final String adsApiPathTemplate = "/act_<account_id>/ads";
    private String accessToken;
    private LocalTime accessTokenExpiresAt;
    @Setter
    private Profile profile;
    public SunBrowser(WebDriver driver) {
        this.driver = driver;
    }

    private String getAccessToken(int attempts) {
        if (accessToken == null || Duration.between(LocalTime.now(), accessTokenExpiresAt).getSeconds() < 50) {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

            Set<String> windowHandles = driver.getWindowHandles();
            boolean tabWithAdsManagerFound = false;
            for (String windowHandle : windowHandles) {
                if (driver.getCurrentUrl().contains("https://adsmanager.facebook.com/adsmanager")) {
                    tabWithAdsManagerFound = true;
                    break;
                }
                driver.switchTo().window(windowHandle);
            }
            if (!tabWithAdsManagerFound) {
                driver.get("https://adsmanager.facebook.com/adsmanager");
            }

            if (driver.getCurrentUrl().contains("/loginpage")) {
                if (attempts < 2) {
                    processLoginPage();
                    attempts ++;
                    return getAccessToken(attempts);
                } else {
                    throw new SunBrowserException("Failed to get access token");
                }
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                wait.until(webDriver -> (Boolean) jsExecutor.executeScript("return (typeof window.__accessToken !== 'undefined') && (typeof window.__accessTokenExpirySecondsRemaining !== 'undefined');"));
            } catch (TimeoutException e) {
                throw new SunBrowserException("Failed to get access token");
            }
            accessToken = (String) jsExecutor.executeScript("return window.__accessToken;");
            accessTokenExpiresAt = LocalTime.now().plusSeconds((long) jsExecutor.executeScript("return window.__accessTokenExpirySecondsRemaining;"));
        }
        return accessToken;
    }
    private String getAccessToken() {
        return getAccessToken(1);
    }
    public List<Account> getAccounts() {
        try {
            URL url = new URL(apiUrl)
                    .add(getAccountsPath)
                    .addParameter("access_token", getAccessToken())
                    .addParameter("limit", "2000")
                    .addParameter("fields", "timezone_name,currency,account_id");
            AjaxSender ajaxSender = new AjaxSender(driver);
            FacebookResponse response = ajaxSender.send(url.getValue(), "GET");
            return FacebookResponseConverter.toAccounts(response);
        } catch (AjaxException | FacebookAPIResponseException | FacebookResponseConvertingException | ScriptParsingException e) {
            throw new SunBrowserException("Failed to get the profile accounts: " + e.getMessage());
        }
    }

    public Report generateReport(Account account) {
        try {
            String regex = "<account_id>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(insightsApiPathTemplate);
            String insightsApiPath = matcher.replaceAll(account.getAccountId());

            URL url = new URL(apiUrl)
                    .add(insightsApiPath)
                    .addParameter("access_token", getAccessToken())
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
            Report report = Reports.formReport(data, webUrls, account);
            return report;
        } catch (AjaxException | FacebookAPIResponseException | FacebookResponseConvertingException | ScriptParsingException e) {
            throw new SunBrowserException("Failed to generate report for the account " + account.getAccountId() + ": " + e.getMessage());
        }
    }

    private List<WebUrl> getWebUrls(Account account, List<String> adIds) {
        String regex = "<account_id>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(adsApiPathTemplate);
        String adsApiPath = matcher.replaceAll(account.getAccountId());

        URL url = new URL(apiUrl)
                .add(adsApiPath)
                .addParameter("access_token", getAccessToken())
                .addParameter("limit", "2000")
                .addParameter("fields", "creative{object_story_spec,asset_feed_spec},name")
                //Запрос может возвращать не все объявления, так как что-то может быть не указано в фильтрации по effective status. Если не указывать фильтрацию, то по деволту возвращаются тоже не совсем все объявления
                //Чтобы проверить все ли окей, достаточно сравнить количество элементов в webUrls и в первом запросе
                .addParameter("filtering", "[{\"field\":\"effective_status\",\"operator\":\"IN\",\"value\":[\"ACTIVE\",\"PAUSED\",\"DELETED\"," +
                        "\"PENDING_REVIEW\",\"DISAPPROVED\",\"PREAPPROVED\",\"PENDING_BILLING_INFO\",\"CAMPAIGN_PAUSED\",\"ARCHIVED\",\"ADSET_PAUSED\"," +
                        "\"WITH_ISSUES\"]},{\"field\":\"id\",\"operator\":\"IN\",\"value\":" + adIds.toString() + "}]");

        AjaxSender ajaxSender = new AjaxSender(driver);
        FacebookResponse response = ajaxSender.send(url.getValue(), "GET");
        List<WebUrl> webUrls = FacebookResponseConverter.toWebUrls(response);
        return webUrls;
    }

    private void processLoginPage() {
        System.out.println("Processing login page...");
        driver.get("https://facebook.com/login");
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(webDriver -> (Boolean) jsExecutor.executeScript("return document.readyState").equals("complete"));
        driver.findElement(By.id("loginbutton")).click();
        if (driver.getCurrentUrl().contains("two_step_verification")) {
            System.out.println("Two-factor authentication is required");
            String currentWindowHandle = driver.getWindowHandle();
            driver.switchTo().newWindow(WindowType.TAB);
            driver.get("https://start.adspower.net");
            String code = driver.findElement(By.xpath("//*[text()=\"2FA Code\"]/following-sibling::div")).getText().split("\\n")[0];
            driver.switchTo().window(currentWindowHandle);
            wait.until(webDriver -> (Boolean) jsExecutor.executeScript("return document.readyState").equals("complete"));
            driver.findElement(By.xpath("//input")).sendKeys(code);
            wait.until(webDriver -> (Boolean) jsExecutor.executeScript("return document.readyState").equals("complete"));
            driver.findElement(By.xpath("//*[text()=\"Trust this device\"]")).click();
        } else if (driver.getCurrentUrl().contains("/login/")) {
            driver.findElement(By.id("pass")).sendKeys(profile.getPassword());
            driver.findElement(By.id("loginbutton")).click();
        }
        System.out.println("Successfully logged in");
    }
}
