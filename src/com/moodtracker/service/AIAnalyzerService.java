package com.moodtracker.service;

import com.moodtracker.model.MoodEntry;
import com.moodtracker.ui.UIConstants; 
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AIAnalyzerService {

    private static final String JAN_API_BASE_URL = "http://127.0.0.1:1337";

    private static final int CONNECT_TIMEOUT = 10000; 
    private static final int READ_TIMEOUT = 20000; 

    private String apiKey = "";
    private String endpointPath = "/v1/chat/completions"; 
    private String modelName = "Jan-V1-4B-GGUF"; 

    public void setApiKey(String key) {
        this.apiKey = key;
    }

    public void setEndpointPath(String path) {

        if (path == null || path.isEmpty()) {
            this.endpointPath = "/v1/chat/completions";
        } else if (!path.startsWith("/")) {
            this.endpointPath = "/" + path;
        } else {
            this.endpointPath = path;
        }
    }

    public void setModelName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.modelName = "Jan-V1-4B-GGUF"; 
        } else {
            this.modelName = name.trim();
        }
    }

    public String analyzeMoodEntry(MoodEntry entry) {
        try {
            String prompt = buildPrompt(entry);

            boolean useChatFormat = this.endpointPath.contains("chat");
            String jsonPayload;
            if (useChatFormat) {
                jsonPayload = buildChatPayload(prompt);
            } else {
                jsonPayload = buildCompletionsPayload(prompt);
            }

            String jsonResponse = sendJanRequest(jsonPayload);

            String aiContent;
            if (useChatFormat) {
                aiContent = parseChatResponse(jsonResponse);
            } else {
                aiContent = parseCompletionsResponse(jsonResponse);
            }

            return formatAsHtml(aiContent.trim()); 

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return "<html><body><p style='color: red;'><b>Error: Connection timed out.</b><br>Is Jan running and not busy? Check server timeout setting.</p></body></html>";
        } catch (java.io.IOException e) {
            e.printStackTrace();
             return "<html><body><p style='color: red;'><b>Error: Could not connect to AI service.</b><br>Is Jan running at " + JAN_API_BASE_URL + "?</p></body></html>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><p style='color: red;'><b>Error: " + e.getMessage() + "</b></p></body></html>";
        }
    }

    private String sendJanRequest(String jsonPayload) throws Exception {

        URL url = new URL(JAN_API_BASE_URL + this.endpointPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        if (apiKey != null && !apiKey.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        }

        connection.setDoOutput(true);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT); 

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        String errorBody = ""; 

         if (responseCode != HttpURLConnection.HTTP_OK) {
             try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                 StringBuilder response = new StringBuilder();
                 String responseLine;
                 while ((responseLine = br.readLine()) != null) {
                     response.append(responseLine.trim());
                 }
                 errorBody = response.toString();
             } catch (Exception readEx) {

             }
         }

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new Exception("Authorization Error (401). Is your API Key correct?");
        }
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new Exception("Error: Server responded with 404 Not Found. Is the API Endpoint Path correct? (Tried: " + this.endpointPath + ")");
        }
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP Error: " + responseCode + (errorBody.isEmpty() ? "" : " - " + errorBody));
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    private String buildChatPayload(String prompt) {
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a kind, empathetic, and supportive AI companion acting as a mini-therapist for mood tracking. Your goal is to help the user reflect on their day and feel encouraged. Analyze the user's daily log (rating, feelings, notes). \n1. Start by summarizing the day in one sentence, based *specifically* on the notes provided.\n2. Acknowledge and validate *all* the feelings the user listed, connecting them gently to their rating and notes. Show understanding.\n3. If the rating is low (1-2) or negative feelings (sad, angry, anxious, stressed) are present, offer specific, gentle encouragement and a simple, positive thought or coping suggestion for the next day. Use supportive emojis like 🙏, ✨, 🌱, or 😊.\n4. If the rating is high (4-5) and feelings are positive, celebrate their good day and offer a brief positive affirmation for maintaining that momentum. Use celebratory emojis like 🎉, 👍, or ⭐.\n5. Keep the entire response thoughtful, complete, and around 3-5 sentences total.");

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        JSONArray messages = new JSONArray();
        messages.put(systemMessage);
        messages.put(userMessage);

        JSONObject payload = new JSONObject();
        payload.put("model", this.modelName); 
        payload.put("messages", messages);
        payload.put("temperature", 0.7);
        payload.put("stream", false);

        payload.put("max_tokens", 500);

        return payload.toString();
    }

    private String buildCompletionsPayload(String prompt) {

        String systemPrompt = "You are a kind, empathetic, and supportive AI companion acting as a mini-therapist for mood tracking. Your goal is to help the user reflect on their day and feel encouraged. Analyze the user's daily log (rating, feelings, notes). \n1. Start by summarizing the day in one sentence, based *specifically* on the notes provided.\n2. Acknowledge and validate *all* the feelings the user listed, connecting them gently to their rating and notes. Show understanding.\n3. If the rating is low (1-2) or negative feelings (sad, angry, anxious, stressed) are present, offer specific, gentle encouragement and a simple, positive thought or coping suggestion for the next day. Use supportive emojis like 🙏, ✨, 🌱, or 😊.\n4. If the rating is high (4-5) and feelings are positive, celebrate their good day and offer a brief positive affirmation for maintaining that momentum. Use celebratory emojis like 🎉, 👍, or ⭐.\n5. Keep the entire response thoughtful, complete, and around 3-5 sentences total.\n\n";
        String fullPrompt = "USER: " + systemPrompt + prompt + "\nASSISTANT: ";

        JSONObject payload = new JSONObject();
        payload.put("model", this.modelName); 
        payload.put("prompt", fullPrompt); 
        payload.put("temperature", 0.7);
        payload.put("stop", "\nUSER:");
        payload.put("stream", false);

        payload.put("max_tokens", 500);

        return payload.toString();
    }

    private String buildPrompt(MoodEntry entry) {

        return String.format(
            "Here is my mood log for the day:\n- Rating: %d out of 5\n- I felt: %s\n- Notes: \"%s\"",
            entry.getRating(),
            entry.getFeelings().isEmpty() ? "None specified" : entry.getFeelings(),
            entry.getNotes().isEmpty() ? "No notes." : entry.getNotes()
        );
    }

    private String parseChatResponse(String jsonResponse) throws Exception {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);
            JSONArray choices = responseObj.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);

                String finishReason = firstChoice.optString("finish_reason", "unknown");
                 JSONObject message = firstChoice.getJSONObject("message");
                 String content = message.getString("content");

                if ("length".equalsIgnoreCase(finishReason)) {

                     return content + "... (response might be cut short due to length limit)";
                }
                return content;

            } else {

                 if (responseObj.has("error")) {
                    JSONObject errorObj = responseObj.getJSONObject("error");
                    throw new Exception("AI Server Error: " + errorObj.optString("message", "Unknown error from server."));
                 }
                throw new Exception("Invalid AI response: 'choices' array is empty.");
            }
        } catch (org.json.JSONException e) {
            throw new Exception("Error parsing AI response: " + e.getMessage());
        }
    }

    private String parseCompletionsResponse(String jsonResponse) throws Exception {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);
            JSONArray choices = responseObj.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);

                String finishReason = firstChoice.optString("finish_reason", "unknown");
                 String content = firstChoice.getString("text");
                if ("length".equalsIgnoreCase(finishReason)) {

                     return content + "... (response might be cut short due to length limit)";
                }
                return content;
            } else {

                 if (responseObj.has("error")) {
                    JSONObject errorObj = responseObj.getJSONObject("error");
                    throw new Exception("AI Server Error: " + errorObj.optString("message", "Unknown error from server."));
                 }
                throw new Exception("Invalid AI response: 'choices' array is empty.");
            }
        } catch (org.json.JSONException e) {
            throw new Exception("Error parsing AI response: " + e.getMessage());
        }
    }

    private String formatAsHtml(String rawText) {

        String htmlContent = rawText.replaceAll("\n", "<br>");

        htmlContent = htmlContent.replaceAll("([\uD83C-\uDBFF\uDC00-\uDFFF]+)", "<span style='font-size: 1.1em;'>$1</span>");

        return "<html><body style='font-family: " + UIConstants.MAIN_FONT.getFamily() + "; font-size: 11pt;'>" +
               htmlContent +
               "</body></html>";
    }
}