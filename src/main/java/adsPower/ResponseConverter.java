package adsPower;

import adsPower.exceptions.ConvertingException;
import adsPower.data.Group;
import adsPower.data.Profile;
import adsPower.data.SeleniumData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ResponseConverter {
    public static List<Group> toGroups(Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Group> groups = new ArrayList<>();
        try {
            List<Object> list = (List<Object>) response.getData().get("list");
            for (Object v : list) {
                groups.add(objectMapper.convertValue(v, Group.class));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ConvertingException("Failed to convert the response");
        }
        return groups;
    }
    public static List<Profile> toProfiles(Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Profile> profiles = new ArrayList<>();
        try {
            List<Object> list = (List<Object>) response.getData().get("list");
            for (Object v : list) {
                profiles.add(objectMapper.convertValue(v, Profile.class));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ConvertingException("Failed to convert the response");
        }
        return profiles;
    }
    public static SeleniumData toSeleniumData(Response response) {
        SeleniumData seleniumData = new SeleniumData();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            seleniumData = objectMapper.convertValue(response.getData(), SeleniumData.class);
        } catch (IllegalArgumentException e) {
            throw new ConvertingException("Failed to convert the response");
        }
        return seleniumData;
    }

}
