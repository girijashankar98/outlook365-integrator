package org.integrator.office365.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jwt.JWTParser;
import org.integrator.office365.document.ChannelIntegrationDetails;
import org.integrator.office365.repo.ChannelIntegrationDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Configuration
public class ServiceUtility {

    @Value("${spring.security.oauth2.client.provider.azure.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.registration.azure.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.azure.client-secret}")
    private String clientSecret;
    @Value("${outlook.api.fetch-mails}")
    private String outlookApiFetchMail;
    @Value("${outlook.api.post-mails}")
    private String outlookApiPostMail;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ChannelIntegrationDetailsRepo integrationDetailsRepo;

    Map getAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            // Handle error, e.g., log and return null
            return null;
        }
    }
    public ChannelIntegrationDetails validateAccessToken(ChannelIntegrationDetails channelIntegrationDetails) throws ParseException {

        Date expDate = JWTParser.parse(channelIntegrationDetails.getAccessToken()).getJWTClaimsSet().getExpirationTime();
        if (expDate.before(new Date())) {
            String accessToken = getAccessToken(channelIntegrationDetails.getRefreshToken()).get("access_token").toString();
            channelIntegrationDetails.setAccessToken(accessToken);
            channelIntegrationDetails = integrationDetailsRepo.save(channelIntegrationDetails);
            return channelIntegrationDetails;
        }
        return channelIntegrationDetails;
    }

    public List<LinkedHashMap<String, Object>> getOutlookEmails(ChannelIntegrationDetails channelIntegrationDetails) throws URISyntaxException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        RequestEntity<Void> request;
        headers.setBearerAuth(channelIntegrationDetails.getAccessToken());
        Instant instant = Instant.ofEpochSecond(channelIntegrationDetails.getLastSyncTime());
        String lastSync = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        request = new RequestEntity<>(headers, HttpMethod.GET, new URI(outlookApiFetchMail + "filter=createdDateTime+gt+"+ lastSync));
        ResponseEntity<HashMap> response = restTemplate.exchange(request, HashMap.class);
        return (List<LinkedHashMap<String, Object>>) response.getBody().get("value");
    }

    public String postOutlookEmails(String accessToken,String lastMessageId, String content) throws URISyntaxException, JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(content,headers);

        System.out.println(request);

        String response  = restTemplate.postForObject(outlookApiPostMail.replace("emailId",lastMessageId),request,String.class);

        return response;
    }
}
