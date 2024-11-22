package application;

import adsPower.APIService;
import adsPower.exceptions.HttpException;
import adsPower.exceptions.OperationFailedException;
import adsPower.responseConverter.Group;
import adsPower.responseConverter.ResponseConverter;
import adsPower.responseConverter.Profile;
import adsPower.responseParser.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String GROUP_NAME = "Share-0625-honpcy-AHaEsc";
    public static void main(String[] args) {
        try {
            APIService adsPower = new APIService(true);
            List<Group> groups = ResponseConverter.toGroups(adsPower.getGroups());
            Long groupId = getGroupId(groups);
            if (groupId == null) {
                System.err.println("Группа " + GROUP_NAME + " не найдена");
                return;
            }

            List<Profile> profiles = ResponseConverter.toProfiles(adsPower.getProfiles(groupId));
            printProfiles(profiles);

            List<Profile> skipped = new ArrayList<>();
            for (Profile profile : profiles) {
                try {
                    Response response = adsPower.openProfile(profile.getUserId());
                    try {
                        //TODO работа с селениум
                    } finally {
                        adsPower.closeProfile(profile.getUserId());
                    }
                } catch (HttpException | OperationFailedException e) {
                    System.err.println("Не удалось открыть профиль " + profile.getName());
                    skipped.add(profile);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка установления соединения с AdsPower Browser: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Текущий поток был прерван: " + e.getMessage());
        }
    }

    private static void printProfiles(List<Profile> profiles) {
        int profilesSize = profiles.size();
        System.out.println(profilesSize + " profile" + (profilesSize == 1 ? "" : "s") + " detected" + (profilesSize > 0 ? ":" : ""));
        for (int i = 0; i < profilesSize; i ++) {
            System.out.print(profiles.get(i).getName() + (i != profilesSize - 1 ? ", " : ""));
        }
    }

    private static Long getGroupId(List<Group> groups) {
        Long groupId = null;
        for (Group group : groups) {
            if (group.getGroupName().equals(GROUP_NAME)) {
                groupId = group.getGroupId();
                break;
            }
        }
        return groupId;
    }
}
