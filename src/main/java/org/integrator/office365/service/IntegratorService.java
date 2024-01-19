package org.integrator.office365.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

public interface IntegratorService {
    String saveChannelIntegrationDetail(String channelId, OAuth2AuthorizedClient client);
}
