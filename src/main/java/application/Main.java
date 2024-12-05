package application;

import adsPower.APIService;
import adsPower.exceptions.*;
import adsPower.data.Group;
import adsPower.ResponseConverter;
import adsPower.data.Profile;
import adsPower.data.SeleniumData;
import application.configuration.Configuration;
import sunBrowser.data.Report;
import sunBrowser.exceptions.SunBrowserException;
import sunBrowser.SunBrowserConnector;
import sunBrowser.SunBrowser;
import sunBrowser.data.Account;
import utils.Reports;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static final String RESET = "\033[0m";
    public static final String GREEN = "\033[92m";
    public static final String RED = "\033[91m";
    public static final String YELLOW = "\033[93m";
    public static void main(String[] args) {
        if (!Configuration.init(args[0])) {
            return;
        }

        try {
            APIService adsPower = new APIService(true);

            List<Profile> profiles;
            if (Configuration.search.mode.equals("group")) {
                List<Group> groups = ResponseConverter.toGroups(adsPower.getGroups());
                long groupId = getGroupId(groups);
                profiles = ResponseConverter.toProfiles(adsPower.getProfilesByGroupId(groupId));
            } else {
                profiles = ResponseConverter.toProfiles(adsPower.getProfilesByUserIds(Configuration.search.ids));
            }

            printProfiles(profiles, "detected", "detected");

            List<Profile> skipped = processProfiles(profiles, adsPower);

            printProfiles(skipped, "was skipped", "were skipped");

            if (!skipped.isEmpty()) {
                System.out.println("Trying to process skipped profiles:");
                if (skipped.size() == 1) {
                    Thread.sleep(3000); //когда профиль закрывается и сразу же открывается ads power не успевает понять что происходит, поэтом ждем
                }
                skipped = processProfiles(skipped, adsPower);
                printProfiles(skipped, "was skipped", "were skipped");
            }
        } catch (IOException e) {
            System.out.println(RED + "Ошибка установления соединения с AdsPower Browser: " + Objects.requireNonNullElse(e.getMessage(), "no system message") + RESET);
        } catch (InterruptedException e) {
            System.out.println(RED + "Текущий поток был прерван: " + Objects.requireNonNullElse(e.getMessage(), "no system message") + "RESET");
        }
    }

    private static List<Profile> processProfiles(List<Profile> profiles, APIService adsPower) throws IOException, InterruptedException {
        List<Profile> skipped = new ArrayList<>();
        for (int i = 0; i < profiles.size(); i ++) {
            Profile profile = profiles.get(i);
            try {
                System.out.println();
                System.out.println("Opening profile " + profile.getUserId() + "...");
                SeleniumData seleniumData = ResponseConverter.toSeleniumData(adsPower.openProfile(profile.getUserId()));
                try {
                    SunBrowser sunBrowser = SunBrowserConnector.connect(seleniumData.getDebugPort(), seleniumData.getPathToWebDriver(), profile);
                    List<Account> accounts = sunBrowser.getAccounts();
                    List<Report> profileReports = new ArrayList<>();
                    for (Account account : accounts) {
                        System.out.print("Generating report " + account.getAccountId() + " ");
                        try {
                            Report report = sunBrowser.generateReport(account, Configuration.report.type);
                            if (report.isEmpty()) {
                                System.out.println(YELLOW + "EMPTY" + RESET);
                            } else {
                                profileReports.add(report);
                                System.out.println(GREEN + "OK" + RESET);
                            }
                        } catch (SunBrowserException e) {
                            System.out.println(RED + "FAILED" + RESET);
                            throw new SunBrowserException(e.getMessage());
                        }
                    }

                    if (!profileReports.isEmpty()) {
                        System.out.println("Downloading reports...");
                    }
                    for (Report report : profileReports) {
                        try {
                            Reports.download(report, Configuration.reportsFolder);
                            System.out.println("Report " + report.getName() + " was downloaded");
                        } catch (IOException e) {
                            System.out.println(RED + "Failed to download the report " + report.getName() + RESET);
                        }
                    }
                } finally {
                    System.out.println("Closing profile " + profile.getUserId());
                    adsPower.closeProfile(profile.getUserId());
                }
            } catch (AdsPowerException | SunBrowserException e) {
                System.out.println(RED + e.getMessage() + RESET);
                skipped.add(profile);
            } catch (Exception e) {
                System.out.println(RED + "Unexpected error: " + Objects.requireNonNullElse(e.getMessage(), "no system message") + RESET);
                for (int j = i; j < profiles.size(); j ++) {
                    skipped.add(profiles.get(j));
                }
                break;
            }
        }
        System.out.println();
        return skipped;
    }

    private static void printProfiles(List<Profile> profiles, String phraseIfOne, String phraseIfMany) {
        int profilesSize = profiles.size();
        System.out.println(profilesSize + " profile" + (profilesSize == 1 ? "" : "s") + " " + (profilesSize == 1 ? phraseIfOne : phraseIfMany) + (profilesSize > 0 ? ":" : ""));
        for (int i = 0; i < profilesSize; i ++) {
            System.out.println(i + 1 + ") "  + profiles.get(i).getName().trim().replaceAll("\\s+", " ") + " (" + profiles.get(i).getUserId() + ")");
        }
        System.out.println();
    }

    private static long getGroupId(List<Group> groups) {
        long groupId = -1;
        for (Group group : groups) {
            if (group.getGroupName().equals(Configuration.search.group)) {
                groupId = group.getGroupId();
                break;
            }
        }
        return groupId;
    }
}
