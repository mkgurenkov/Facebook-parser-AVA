package sunBrowser.utils;

import adsPower.data.Profile;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import sunBrowser.exceptions.AccessFailureException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

public class FacebookAccessManager {
    private final JavascriptExecutor jsExecutor;
    private final WebDriver driver;
    private final Profile profile;
    private String accessToken;
    private LocalTime accessTokenExpiresAt;

    public FacebookAccessManager(WebDriver driver, Profile profile) {
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;
        this.profile = profile;
    }

    public String getAccessToken() {
        return getAccessToken(1);
    }

    private String getAccessToken(int attempts) {
        try {
            if (accessToken == null || Duration.between(LocalTime.now(), accessTokenExpiresAt).getSeconds() < 50) {
                if (!switchToAdsManagerTab()) {
                    driver.get("https://adsmanager.facebook.com/adsmanager");
                }

                if (driver.getCurrentUrl().contains("/loginpage")) {
                    if (attempts < 2) {
                        processLoginPage();
                        attempts ++;
                        return getAccessToken(attempts);
                    } else {
                        throw new AccessFailureException("Failed to get access token");
                    }
                } else if (driver.getCurrentUrl().contains("/accountonboarding")) {
                    driver.get("https://www.facebook.com/adsmanager/manage/campaigns");
                }

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                try {
                    wait.until(webDriver -> (Boolean) jsExecutor.executeScript("return (typeof window.__accessToken !== 'undefined') && (typeof window.__accessTokenExpirySecondsRemaining !== 'undefined');"));
                } catch (TimeoutException e) {
                    if (!proxyIsOk()) {
                        throw new AccessFailureException("Proxy failure");
                    } else {
                        throw new AccessFailureException("Failed to get access token");
                    }
                }

                accessToken = (String) jsExecutor.executeScript("return window.__accessToken;");
                accessTokenExpiresAt = LocalTime.now().plusSeconds((long) jsExecutor.executeScript("return window.__accessTokenExpirySecondsRemaining;"));
            }
            return accessToken;
        } catch (WebDriverException e) {
            throw new AccessFailureException("Failed to get access token: " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }

    private void processLoginPage() {
        try {
            System.out.println("Processing login page...");
            WebDriverWait waitPage = new WebDriverWait(driver, Duration.ofSeconds(40));
            WebDriverWait waitElement = new WebDriverWait(driver, Duration.ofSeconds(10));

            driver.get("https://facebook.com/login");
            waitPage.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));

            if (!driver.findElements(By.id("loginbutton")).isEmpty()) { //login page 1
                driver.findElement(By.id("pass")).sendKeys("0");
                driver.findElement(By.id("pass")).clear();
                driver.findElement(By.id("pass")).sendKeys(profile.getPassword());
                driver.findElement(By.id("loginbutton")).click();
            } else if (!driver.findElements(By.xpath("//*[text()=\"Continue\"]")).isEmpty()) { //login page 2
                driver.findElement(By.xpath("//*[text()=\"Continue\"]")).click();

                waitElement.until(ExpectedConditions.presenceOfElementLocated(By.name("pass")));
                if (driver.findElement(By.name("pass")).getAttribute("value").isEmpty()) {
                    driver.findElement(By.name("pass")).sendKeys(profile.getPassword());
                }
                driver.findElement(By.xpath("//span[text()=\"Log in\"]")).click();
            }

            if (driver.getCurrentUrl().contains("two_step_verification")) {
                System.out.println("Two-factor authentication is required");
                String currentWindowHandle = driver.getWindowHandle();
                driver.switchTo().newWindow(WindowType.TAB);

                driver.get("https://start.adspower.net");
                waitPage.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));

                String code = driver.findElement(By.xpath("//*[text()=\"2FA Code\"]/following-sibling::div")).getText().split("\\n")[0];
                driver.switchTo().window(currentWindowHandle);

                waitPage.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));
                driver.findElement(By.xpath("//input")).sendKeys(code);
                if (!driver.findElements(By.xpath("//*[text()=\"Continue\"]")).isEmpty()) {
                    driver.findElement(By.xpath("//*[text()=\"Continue\"]")).click();
                }
                waitPage.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));
                driver.findElement(By.xpath("//*[text()=\"Trust this device\"]")).click();
            }

            waitPage.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));
            if (driver.getCurrentUrl().contains("/loginpage") || driver.getCurrentUrl().contains("/login/")) {
                throw new AccessFailureException("Failed to log in");
            } else {
                System.out.println("Successfully logged in");
            }
        } catch (WebDriverException e) {
            throw new AccessFailureException("Failed to log in: " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }

    private boolean switchToAdsManagerTab() {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            if (driver.getCurrentUrl().contains("https://adsmanager.facebook.com/adsmanager")) {
                return true;
            }
            driver.switchTo().window(windowHandle);
        }
        return false;
    }

    private boolean proxyIsOk() {
        String currentWindow = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://start.adspower.net");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[text()=\"Proxy failure\"]")));
            return false;
        } catch (TimeoutException ex) {
            return true;
        } finally {
            driver.close();
            driver.switchTo().window(currentWindow);
        }
    }
}
