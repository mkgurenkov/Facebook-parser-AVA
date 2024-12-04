package sunBrowser;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SunBrowserConnector {
    public static SunBrowser connect(int debugPort, String pathToWebDriver) {
        System.setProperty("webdriver.chrome.driver", pathToWebDriver);
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER); //чтобы driver.get() не ждал полную загрузку страницы
        options.setExperimentalOption("debuggerAddress", "localhost:" + debugPort);
        WebDriver driver = new ChromeDriver(options); //иногда по какой-то причине зависает в бесконечном цикле, когда профиль уже был открыт вручную
        return new SunBrowser(driver);
    }
}
