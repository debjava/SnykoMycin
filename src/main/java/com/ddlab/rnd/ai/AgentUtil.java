package com.ddlab.rnd.ai;

import com.ddlab.rnd.ai.output.model.LLmModel;
import com.ddlab.rnd.ai.output.model.OAuthTokenModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AgentUtil {

    public static String getAIBearerToken(String clientId, String clientSecret, String tokenUrl) throws RuntimeException {
        String accessToken = getAccessToken(clientId, clientSecret, tokenUrl);
        String bearerToken = "Bearer " + accessToken;
        return bearerToken;
    }

    public static String getAccessToken(String clientId, String clientSecret, String tokenUrl) throws RuntimeException {
        OAuthTokenModel model = null;
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String authHeaderValue = "Basic " + encodedCredentials;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded").header("Authorization", authHeaderValue)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            log.debug("Response Body: " + responseBody);
            ObjectMapper mapper = new ObjectMapper();
            model = mapper.readValue(responseBody, OAuthTokenModel.class);
        } catch (Exception e) {
            log.error("Error while getting access token", e);
            e.printStackTrace();
            throw new RuntimeException("Unable to receive the token. Please check your input details.");
        }
        return model.getAccessToken();
    }

    public static List<String> getAllLLMModels(String bearerToken, String aiAPIUrl) throws Exception {
        aiAPIUrl = aiAPIUrl + "/models";
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(aiAPIUrl);
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .header("Content-Type", "application/json").header("Authorization", bearerToken)
                    .GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        String responseBody = response.body();
        log.debug("Model Response Body: " + responseBody);
        LLmModel model = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            model = objectMapper.readValue(responseBody, LLmModel.class);
        } catch (Exception e) {
            log.error("Error while getting models", e);
            e.printStackTrace();
        }
        List<String> llmModelList = model.getData().stream()
                .map(data -> {
                    String modelName = data.getModel();
                    String modelType = data.getType().get(0);
                    String maxModelLength = data.getMaxModelLength();
                    return modelName + "~" + modelType + "~" + maxModelLength;
                }).collect(Collectors.toList());
        log.debug("Model List: " + llmModelList);

        return llmModelList;
    }
}
