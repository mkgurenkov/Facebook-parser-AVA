package adsPower.responseConverter;

import adsPower.exceptions.ConvertingException;
import adsPower.responseParser.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            List<Object> list = (List<Object>) response.getData().get("list");;
            for (Object v : list) {
                profiles.add(objectMapper.convertValue(v, Profile.class));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ConvertingException("Failed to convert the response");
        }
        return profiles;
    }

}
