/*
 * ============================================================================
 *  Copyright (c) 2025 DDLABS INC. All Rights Reserved.
 *  Snykomycin is a plugin from Tornado Application maintained
 *  by DDLABS INC (Debadatta Mishra).
 *  Contact me in deba.java@gmail.com.
 *
 *  Description: Code for Snykomycin product from Tornado
 *  Author: Debadatta Mishra
 *  Version: 1.0
 * ============================================================================
 */
package com.ddlab.rnd.ai;

import com.ddlab.rnd.ai.input.model.AIPromptModel;
import com.ddlab.rnd.ai.input.model.PromptMessageModel;
import com.ddlab.rnd.ai.output.model.AIResponseModel;
import com.ddlab.rnd.ai.output.model.LLmModel;
import com.ddlab.rnd.ai.output.model.OAuthTokenModel;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.InvalidTokenException;
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

/**
 * The Class AgentUtil.
 * @author Debadatta Mishra
 */
@Slf4j
public class AgentUtil {

    /**
     * Gets the AI bearer token.
     *
     * @param clientId the client id
     * @param clientSecret the client secret
     * @param tokenUrl the token url
     * @return the AI bearer token
     * @throws RuntimeException the runtime exception
     */
    public static String getAIBearerToken(String clientId, String clientSecret, String tokenUrl) throws RuntimeException {
        return  Constants.BEARER_SPC + getAccessToken(clientId, clientSecret, tokenUrl);
    }

    /**
     * Gets the access token.
     *
     * @param clientId the client id
     * @param clientSecret the client secret
     * @param tokenUrl the token url
     * @return the access token
     * @throws RuntimeException the runtime exception
     */
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
//            log.debug("What is the Response Code for getting AI access token: " + response.statusCode());
            if (response.statusCode() != 200) {
                throw new InvalidTokenException("Unable to receive the token. Please check client id, client secret and token uri.");
            }
            String responseBody = response.body();
//            log.debug("Response Body: " + responseBody);

            ObjectMapper mapper = new ObjectMapper();
            model = mapper.readValue(responseBody, OAuthTokenModel.class);
        } catch (InvalidTokenException ie) {
            throw ie;
        }
        catch (Exception e) {
            log.error("Error while getting access token", e);
            throw new RuntimeException("UnExpected Error: Unable to receive/process the AI bearer token. \nPlease contact the developer.");
        }
        return model.getAccessToken();
    }

    /**
     * Gets the all LLM models.
     *
     * @param bearerToken the bearer token
     * @param aiAPIUrl the ai API url
     * @return the all LLM models
     * @throws Exception the exception
     */
    public static List<String> getAllLLMModels(String bearerToken, String aiAPIUrl) throws RuntimeException {
        List<String> llmModelList = null;
        aiAPIUrl = aiAPIUrl + Constants.MODEL_PATH;
        HttpResponse<String> response;
        HttpClient client = HttpClient.newHttpClient();
        LLmModel model = null;
        try {
            URI uri = URI.create(aiAPIUrl);
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, bearerToken)
                    .GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
//            log.debug("Model Response Body: " + responseBody);
            ObjectMapper objectMapper = new ObjectMapper();
            model = objectMapper.readValue(responseBody, LLmModel.class);
            llmModelList = model.getData().stream()
                    .map(data -> {
                        String modelName = data.getModel();
                        String modelType = data.getType().get(0);
                        String maxModelLength = data.getMaxModelLength();
                        return modelName + "~" + modelType + "~" + maxModelLength;
                    }).collect(Collectors.toList());
//            log.debug("Model List: " + llmModelList);
        } catch (Exception e) {
            log.error("Error while getting models", e);
            throw new RuntimeException("UnExpected Error while fetching the LLM models. \nPlease contact the developer.");
        }

        return llmModelList;
    }

    /**
     * Ask AI.
     *
     * @param aiAPIUrl the ai API url
     * @param bearerToken the bearer token
     * @param promptString the prompt string
     * @return the string
     * @throws Exception the exception
     */
    @Deprecated
    public static String askAI(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(aiAPIUrl)) // Target URI
                .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, bearerToken)
                .POST(HttpRequest.BodyPublishers.ofString(promptString)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        //TODO: Handle Response code

        responseBody = getActualAIAnswer(responseBody);
        return responseBody;
    }

    /**
     * Gets the answer from AI as json text.
     *
     * @param aiAPIUrl the ai API url
     * @param bearerToken the bearer token
     * @param promptString the prompt string
     * @return the answer from AI as json text
     * @throws Exception the exception
     */
    public static String getAnswerFromAIAsJsonText(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(aiAPIUrl)) // Target URI
                .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, bearerToken)
                .POST(HttpRequest.BodyPublishers.ofString(promptString)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new RuntimeException("Unable to receive/process the answer from AI. " +
                    "\nPleaase try after sometime." +
                    "\nIf the issues persist,Please contact the engineer.");
        String responseBody = response.body();
        return responseBody;
    }

    /**
     * Gets the only answer from AI.
     *
     * @param aiAPIUrl the ai API url
     * @param bearerToken the bearer token
     * @param promptString the prompt string
     * @return the only answer from AI
     * @throws Exception the exception
     */
    public static String getOnlyAnswerFromAI(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
        String responseBody = getAnswerFromAIAsJsonText(aiAPIUrl, bearerToken, promptString);
        return getActualAIAnswer(responseBody);
    }

    /**
     * Gets the actual AI answer.
     *
     * @param jsonResponse the json response
     * @return the actual AI answer
     */
    public static String getActualAIAnswer(String jsonResponse) throws RuntimeException{
        ObjectMapper mapper = new ObjectMapper();
        AIResponseModel apiResponseModel = mapper.readValue(jsonResponse, AIResponseModel.class);
        return apiResponseModel.getChoices().get(0).getMessage().getContent();
    }

    /**
     * Gets the formed prompt.
     *
     * @param inputText the input text
     * @param modelName the model name
     * @return the formed prompt
     */
    public static String getFormedPrompt(String inputText, String modelName) {
        PromptMessageModel promptMessageModel = new PromptMessageModel();
        promptMessageModel.setRole(Constants.USER);
        promptMessageModel.setContent(inputText);
        AIPromptModel aiPromptModel = new AIPromptModel();
        aiPromptModel.setModel(modelName);
        aiPromptModel.setMessages(Arrays.asList(promptMessageModel));

        ObjectMapper mapper = new ObjectMapper();
        String aiInputModelMsg = mapper.writeValueAsString(aiPromptModel);
//        log.debug("JSON: \n" + aiInputModelMsg);
        return aiInputModelMsg;
    }

    public static String getFormedAIApiUrl(String aiApiUrl) {
        return aiApiUrl = aiApiUrl.endsWith("/") ?
                aiApiUrl + Constants.AI_CHAT_COMPLETIONS
                : aiApiUrl + "/" + Constants.AI_CHAT_COMPLETIONS;
    }
}
