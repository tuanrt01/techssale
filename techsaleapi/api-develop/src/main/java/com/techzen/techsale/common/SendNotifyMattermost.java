package com.techzen.techsale.common;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class SendNotifyMattermost {

    @Value("${mattermost.mattermost-api-url}")
    private String mattermostApiUrl;
    @Value("${mattermost.auth-token}")
    private String authToken;
    @Value("${mattermost.notify-channel}")
    private String notifyChannel;

    public void sendNotify(String notifyMsg) {
        log.info("Start send notify with: " + notifyMsg);
        JSONObject json = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        try {
            json.put("message", notifyMsg);
            json.put("channel_id", notifyChannel);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authToken);
            headers.set("Accept-Charset", "UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
            restTemplate.exchange(mattermostApiUrl, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("Exceptions happen!: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
