package com.ddlab.rnd.ai;

import com.ddlab.rnd.ai.input.model.AIPromptModel;
import com.ddlab.rnd.ai.input.model.PromptMessageModel;
import com.ddlab.rnd.ai.output.model.AIResponseModel;
import com.ddlab.rnd.ai.output.model.LLmModel;
import com.ddlab.rnd.ai.output.model.OAuthTokenModel;
import com.ddlab.rnd.common.util.Constants;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AgentUtil {

    public static String getAIBearerToken(String clientId, String clientSecret, String tokenUrl) throws RuntimeException {
        String accessToken = getAccessToken(clientId, clientSecret, tokenUrl);
        String bearerToken = Constants.BEARER_SPC + accessToken;
        return bearerToken;
    }

    public static String getAccessToken(String clientId, String clientSecret, String tokenUrl) throws RuntimeException {
        OAuthTokenModel model = null;
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String authHeaderValue = Constants.BASIC_SPC +  encodedCredentials;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tokenUrl))
                .header(Constants.CONTENT_TYPE, Constants.URL_ENCODED_TYPE).header(Constants.AUTHORIZATION, authHeaderValue)
                .POST(HttpRequest.BodyPublishers.ofString(Constants.CLIENT_CREDENTIALS)).build();
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
        HttpClient client = HttpClient.newHttpClient();
//        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(aiAPIUrl);
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, bearerToken)
                    .GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        }
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

    public static String askAI(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
//        log.debug("****************** Calling AI API ******************");
//        log.debug("aiAPIUrl: " + aiAPIUrl);
//        log.debug("bearerToken: " + bearerToken);
//        log.debug("promptString: " + promptString);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(aiAPIUrl)) // Target URI
//                .header("Content-Type", "application/json").header("Authorization", bearerToken)
                .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, bearerToken)
                .POST(HttpRequest.BodyPublishers.ofString(promptString)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println("Response Body: " + responseBody);

        responseBody = getActualAIAnswer(responseBody);
        return responseBody;
    }

    public static String getAnswerFromAIAsJsonText(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(aiAPIUrl)) // Target URI
//                .header("Content-Type", "application/json").header("Authorization", bearerToken)
                .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, bearerToken)
                .POST(HttpRequest.BodyPublishers.ofString(promptString)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println("Response Body: " + responseBody);
        return responseBody;
    }

    public static String getOnlyAnswerFromAI(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
        String responseBody = getAnswerFromAIAsJsonText(aiAPIUrl, bearerToken, promptString);
        return getActualAIAnswer(responseBody);
    }

    public static String getActualAIAnswer(String jsonResponse) {
        ObjectMapper mapper = new ObjectMapper();
        AIResponseModel apiResponseModel = mapper.readValue(jsonResponse, AIResponseModel.class);
        return apiResponseModel.getChoices().get(0).getMessage().getContent();
    }

    public static String getFormedPrompt(String inputText, String modelName) {

        PromptMessageModel promptMessageModel = new PromptMessageModel();
        promptMessageModel.setRole(Constants.USER);
        promptMessageModel.setContent(inputText);
        AIPromptModel aiPromptModel = new AIPromptModel();
        aiPromptModel.setModel(modelName);
        aiPromptModel.setMessages(Arrays.asList(promptMessageModel));

        ObjectMapper mapper = new ObjectMapper();
        String aiInputModelMsg = mapper.writeValueAsString(aiPromptModel);
        log.debug("JSON: \n" + aiInputModelMsg);
        return aiInputModelMsg;
    }


}
