package application.configuration;

import application.Main;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Configuration {
    public static String reportsFolder;
    public static Search search;
    public static Report report;
    public static List<String> consoleInput;
    @JsonProperty("reports_folder")
    private String tempReportsFolder;
    @JsonProperty("search")
    private Search tempSearch;
    @JsonProperty("report")
    private Report tempReport;
    @JsonProperty("console_input")
    private List<String> tempConsoleInput;

    public static boolean init(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Configuration configuration = objectMapper.readValue(new File(path), Configuration.class);
            Configuration.reportsFolder = configuration.tempReportsFolder;
            Configuration.search = configuration.tempSearch;
            Configuration.report = configuration.tempReport;
            Configuration.consoleInput = configuration.tempConsoleInput;

            if (consoleInput != null) {
                consoleInput();
            }
            return true;
        } catch (JsonProcessingException e) {
            System.out.println(Main.RED + "Failed to load the configuration: not valid data in configuration file" + Main.RESET);
            return false;
        } catch (IOException e) {
            System.out.println(Main.RED + "Failed to load the configuration: " + e.getMessage() + Main.RESET);
            return false;
        }
    }

    private static void consoleInput() {
        Scanner scanner = new Scanner(System.in);
        for (String field : consoleInput) {
            switch (field) {
                case "reports_folder":
                    System.out.print("Enter path to reports folder: ");
                    reportsFolder = scanner.nextLine().strip();
                    break;
                case "search.mode":
                    System.out.print("Enter search mode (ids/group): ");
                    search.mode = scanner.nextLine().strip();
                    break;
                case "search.group":
                    System.out.print("Enter group: ");
                    search.group = scanner.nextLine().strip();
                    break;
                case "search.ids":
                    System.out.print("Enter ids (ex: kp19h5h kpvoqqs kjbcd6j): ");
                    String line = scanner.nextLine().strip();
                    if (!line.isBlank()) {
                        search.ids = List.of(line.split(" "));
                    }
                    break;
                case "report.type":
                    System.out.print("Enter report type: ");
                    report.type = scanner.nextLine().strip();
                    break;
                case "report.date.mode":
                    System.out.print("Enter date mode (date_preset/time_range): ");
                    report.date.mode = scanner.nextLine().strip();
                    break;
                case "report.date.date_preset":
                    System.out.print("Enter date_preset: ");
                    report.date.datePreset = scanner.nextLine().strip();
                    break;
                case "report.date.time_range":
                    System.out.println("Enter time_range: ");
                    System.out.print("Enter since (YYYY-MM-DD): ");
                    report.date.timeRange.since = scanner.nextLine().strip();
                    System.out.println("Enter until (YYYY-MM-DD): ");
                    report.date.timeRange.until = scanner.nextLine().strip();
                    break;
            }
        }
    }
}
