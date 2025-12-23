package com.ddlab.rnd.common.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum CallApiType {

	GET {
		public String perform(String uri, String authToken) throws RuntimeException {
			String responseBody = null;
			HttpResponse<String> response = null;
			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri))
					.header("Content-Type", "application/json").header("Authorization", authToken).GET().build();
			try {
				response = client.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() != 200) {
					throw new RuntimeException("Unable to get response from API : HTTP error code : " + response.statusCode());
				}
				responseBody = response.body();

			} catch (IOException | InterruptedException e) {
                logError(response, e, "IOException/InterruptedException while performing GET API call");
				throw new RuntimeException(e);
			} catch(Exception ex) {
                logError(response, ex, "Unexpected exception while performing GET API call");
                throw new RuntimeException(ex);
            }
			return responseBody;
		}

	},
	POST {
		public String perform(String uri, String authToken, String bodyContent) {
			String responseBody = null;
			HttpResponse<String> response = null;
			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri))
					.header("Content-Type", "application/json").header("Authorization", authToken)
					.POST(HttpRequest.BodyPublishers.ofString(bodyContent)).build();
			try {
				response = client.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
                logError(response, e, "IOException/InterruptedException while performing GET API call");
                throw new RuntimeException(e);
			} catch(Exception ex) {
                logError(response, ex, "Unexpected exception while performing POST API call");
                throw new RuntimeException(ex);
            }
			responseBody = response.body();
			return responseBody;
		}


        public String perform(String uri,String bodyContent, Map<String, String> xtraHeadersMap) {
            String responseBody = null;
            HttpResponse<String> response = null;
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(uri))
                    .POST(HttpRequest.BodyPublishers.ofString(bodyContent));

            if (xtraHeadersMap != null && !xtraHeadersMap.isEmpty()) {
                xtraHeadersMap.forEach(requestBuilder::header);
            }
            HttpRequest request = requestBuilder.build();

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                logError(response, e, "IOException/InterruptedException while performing POST API call");
                throw new RuntimeException(e);
            } catch(Exception ex) {
                logError(response, ex, "Unexpected exception while performing POST API call");
                throw new RuntimeException(ex);
            }
            responseBody = response.body();
            return responseBody;
        }

	};

    public String perform(String uri, String authToken) {
        throw new UnsupportedOperationException("Not supported for " + this);
    }

    public String perform(String uri, String authToken, String bodyContent) {
        throw new UnsupportedOperationException("Not supported for " + this);
    }

    public String perform(String uri,String bodyContent, Map<String, String> xtraHeadersMap) {
        throw new UnsupportedOperationException("Not supported for " + this);
    }

    private static void logError(HttpResponse<String> response, Exception ex, String errMsg) {
        log.error("Error Message: {}", errMsg);
        if(response != null) {
            log.error("Response code: {}, Error Message:{}", response.statusCode(), ex.getMessage());
        } else {
            log.error("Propagated Error Message: {}, Technical Error Message:{}", errMsg, ex.getMessage());
        }
    }

}
