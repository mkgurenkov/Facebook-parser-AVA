package sunBrowser.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import sunBrowser.data.FacebookResponse;
import sunBrowser.exceptions.AjaxException;
import sunBrowser.exceptions.ScriptParsingException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

public class AjaxSender {
    private final WebDriver driver;
    public AjaxSender(WebDriver driver) {
        this.driver = driver;
    }
    public FacebookResponse send(String url, String method, String body) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        Map<String, Object> response = (Map<String, Object>) jsExecutor.executeAsyncScript(parseScript("ajax.js"), url, method, body);
        if (!((boolean) response.get("success"))) {
            throw new AjaxException("Error: " + response.get("error"));
        } else {
            Map<String, Object> jsonFacebookResponse = (Map<String, Object>) response.get("response");
            return FacebookResponseParser.parse(jsonFacebookResponse);
        }
    }

    public FacebookResponse send(String url, String method) {
        return send(url, method, null);
    }
    private String parseScript(String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line = reader.readLine();
            StringBuilder script = new StringBuilder();
            while (line != null) {
                script.append(line);
                line = reader.readLine();
            }
            return script.toString();
        } catch (IOException e) {
            throw new ScriptParsingException("Failed to parse " + path + ": " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }
}
