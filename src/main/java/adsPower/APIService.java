package adsPower;

import adsPower.exceptions.HttpException;
import adsPower.exceptions.OperationFailedException;
import utils.URL;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class APIService {
    private final HttpClient client;
    private final String apiUrl ="http://local.adspower.net:50325";
    private final String openProfilePath = "/api/v1/browser/start";
    private final String closeProfilePath = "/api/v1/browser/stop";
    private final String getProfilesPath = "/api/v1/user/list";
    private final String getGroupsPath = "/api/v1/group/list";
    private final boolean throwExceptionIfOperationFailed;

    public APIService(boolean throwExceptionIfOperationFailed) {
        this.throwExceptionIfOperationFailed = throwExceptionIfOperationFailed;
        this.client = HttpClient.newHttpClient();
    }
    public Response openProfile(String profileId) throws IOException,
            InterruptedException {
        URL url = new URL(apiUrl)
                .add(openProfilePath)
                .addParameter("user_id", profileId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.getValue()))
                .GET()
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new HttpException("Http error occurred: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
        Response adsPowerResponse = ResponseParser.parse(httpResponse.body());
        if (adsPowerResponse.getCode() == -1 && throwExceptionIfOperationFailed) {
            throw new OperationFailedException("Operation failed: " + adsPowerResponse.getMsg());
        }
        return adsPowerResponse;
    }

    public Response closeProfile(String profileId) throws IOException,
            InterruptedException {
        URL url = new URL(apiUrl)
                .add(closeProfilePath)
                .addParameter("user_id", profileId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.getValue()))
                .GET()
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new HttpException("Http error occurred: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
        Response adsPowerResponse = ResponseParser.parse(httpResponse.body());
        if (adsPowerResponse.getCode() == -1 && throwExceptionIfOperationFailed) {
            throw new OperationFailedException("Operation failed: " + adsPowerResponse.getMsg());
        }
        return adsPowerResponse;
    }

    public Response getProfilesByUserIds(List<String> userIds) throws IOException, InterruptedException {
        if (userIds == null) {
            return getProfiles("user_id", "-1");
        }
        StringBuilder value = new StringBuilder();
        boolean first = true;
        for (String userId : userIds) {
            if (!first) {
                value.append(",");
            }
            value.append(userId);
            first = false;
        }
        return getProfiles("user_id", value.toString());
    }

    public Response getProfilesByGroupId(Long groupId) throws IOException, InterruptedException {
        return getProfiles("group_id", String.valueOf(groupId));
    }
    private Response getProfiles(String parameter, String value) throws IOException,
            InterruptedException {
        URL url = new URL(apiUrl)
                .add(getProfilesPath)
                .addParameter(parameter, value)
                .addParameter("page_size", "1000"); //max page size for profiles (page size = number of profiles)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.getValue()))
                .GET()
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new HttpException("Http error occurred: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
        Response adsPowerResponse = ResponseParser.parse(httpResponse.body());
        if (adsPowerResponse.getCode() == -1 && throwExceptionIfOperationFailed) {
            throw new OperationFailedException("Operation failed: " + adsPowerResponse.getMsg());
        }
        return adsPowerResponse;
    }

    public Response getGroups() throws IOException,
            InterruptedException {
        URL url = new URL(apiUrl)
                .add(getGroupsPath)
                .addParameter("page_size", "2000"); //max page size for groups (page size = number of groups)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.getValue()))
                .GET()
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new HttpException("Http error occurred: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
        Response adsPowerResponse = ResponseParser.parse(httpResponse.body());
        if (adsPowerResponse.getCode() == -1 && throwExceptionIfOperationFailed) {
            throw new OperationFailedException("Operation failed: " + adsPowerResponse.getMsg());
        }
        return adsPowerResponse;
    }
}
