package com.ddlab.rnd.ai;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ddlab.rnd.ai.input.model.AIPromptModel;
import com.ddlab.rnd.ai.input.model.PromptMessageModel;
import com.ddlab.rnd.ai.output.model.AIResponseModel;
import com.ddlab.rnd.ai.output.model.LLmModel;
import com.ddlab.rnd.ai.output.model.OAuthTokenModel;
import com.ddlab.rnd.common.util.CallApiType;
import com.ddlab.rnd.common.util.Constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class AIAssistant {

    private String clientId;
    private String clientSecret;
    private String tokenUrl;

    private String bearerToken;

    public AIAssistant(String clientId, String clientSecret, String tokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
        bearerToken = getAIBearerToken(clientId, clientSecret, tokenUrl);
    }

    public String getAIBearerToken(String clientId, String clientSecret, String tokenUrl) throws RuntimeException {
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
    public String getAccessToken(String clientId, String clientSecret, String tokenUrl) throws RuntimeException {
        OAuthTokenModel model = null;
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String authHeaderValue = Constants.BASIC_SPC +  encodedCredentials;
        Map<String,String> xtraHeadersMap = Map.of(
                Constants.CONTENT_TYPE, Constants.URL_ENCODED_TYPE,
                Constants.AUTHORIZATION, authHeaderValue
        );

        String responseBody = CallApiType.POST.perform(tokenUrl, Constants.CLIENT_CREDENTIALS, xtraHeadersMap);
        ObjectMapper mapper = new ObjectMapper();
        model = mapper.readValue(responseBody, OAuthTokenModel.class);

        return model.getAccessToken();
    }

    public Map<String,String> getAllLLMModelsMap(String bearerToken, String aiAPIUrl) throws RuntimeException {
        Map<String,String> modelsMap = null;
        aiAPIUrl = aiAPIUrl + Constants.MODEL_PATH;
        LLmModel model = null;
        try {
            String responseBody = CallApiType.GET.perform(aiAPIUrl, bearerToken);
            ObjectMapper objectMapper = new ObjectMapper();
            model = objectMapper.readValue(responseBody, LLmModel.class);

            modelsMap = model.getData().stream().collect(Collectors.toMap(
                    data -> data.getModel(),
                    data -> data.getType().get(0)+"~"+data.getMaxModelLength()));

        } catch (Exception e) {
            log.error("Error while getting models", e);
            throw new RuntimeException("UnExpected Error while fetching the LLM models. \nPlease contact the developer.");
        }

        return modelsMap;
    }

    public List<String> getAllLLMModels(String bearerToken, String aiAPIUrl) throws RuntimeException {
        List<String> llmModelList = null;
        aiAPIUrl = aiAPIUrl + Constants.MODEL_PATH;
        LLmModel model = null;
        try {
            String responseBody = CallApiType.GET.perform(aiAPIUrl, bearerToken);
            ObjectMapper objectMapper = new ObjectMapper();
            model = objectMapper.readValue(responseBody, LLmModel.class);
            llmModelList = model.getData().stream()
                    .map(data -> {
                        String modelName = data.getModel();
                        String modelType = data.getType().get(0);
                        String maxModelLength = data.getMaxModelLength();
                        return modelName + "~" + modelType + "~" + maxModelLength;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while getting models", e);
            throw new RuntimeException("UnExpected Error while fetching the LLM models. \nPlease contact the developer.");
        }

        return llmModelList;
    }

    public String getAnswerFromAIAsJsonText(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
        String responseBody = CallApiType.POST.perform(aiAPIUrl, bearerToken, promptString);
        return responseBody;
    }

    public String getOnlyAnswerFromAI(String aiAPIUrl, String bearerToken, String promptString) throws Exception {
        String responseBody = getAnswerFromAIAsJsonText(aiAPIUrl, bearerToken, promptString);
        return getActualAIAnswer(responseBody);
    }

    public String getActualAIAnswer(String jsonResponse) throws RuntimeException{
        ObjectMapper mapper = new ObjectMapper();
        AIResponseModel apiResponseModel = mapper.readValue(jsonResponse, AIResponseModel.class);
        return apiResponseModel.getChoices().get(0).getMessage().getContent();
    }

    public String getFormedPrompt(String inputText, String modelName) {
        PromptMessageModel promptMessageModel = new PromptMessageModel();
        promptMessageModel.setRole(Constants.USER);
        promptMessageModel.setContent(inputText);
        AIPromptModel aiPromptModel = new AIPromptModel();
        aiPromptModel.setModel(modelName);
        //aiPromptModel.setTemperature(0.0);
        aiPromptModel.setMessages(Arrays.asList(promptMessageModel));

        ObjectMapper mapper = new ObjectMapper();
        String aiInputModelMsg = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(aiPromptModel);

        return aiInputModelMsg;
    }

    public String getFormedAIApiUrl(String aiApiUrl) {
        return aiApiUrl = aiApiUrl.endsWith("/") ?
                aiApiUrl + Constants.AI_CHAT_COMPLETIONS
                : aiApiUrl + "/" + Constants.AI_CHAT_COMPLETIONS;
    }
}
