package application.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;


public class Configuration {
    public static String reportsFolder;
    public static Search search;
    public static Report report;
    @JsonProperty("reports_folder")
    private String tempReportsFolder;
    @JsonProperty("search")
    private Search tempSearch;
    @JsonProperty("report")
    private Report tempReport;
    static {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Configuration configuration = objectMapper.readValue(new File("configuration.json"), Configuration.class);
            Configuration.reportsFolder = configuration.tempReportsFolder;
            Configuration.search = configuration.tempSearch;
            Configuration.report = configuration.tempReport;
        } catch (JsonProcessingException e) {
            System.err.println("Failed to load the configuration: not valid data in configuration file");
        } catch (IOException e) {
            System.err.println("Failed to load the configuration: " + e.getMessage());
        }
    }
}
