package org.integrator.office365.repo;

import org.integrator.office365.document.ChannelIntegrationDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelIntegrationDetailsRepo extends MongoRepository<ChannelIntegrationDetails,String> {
    List<ChannelIntegrationDetails> findAllByChannelNameAndIntegrated(String channelName,boolean integrated);

    Optional<ChannelIntegrationDetails> findByIntegratedId(String integratedId);
    Optional<ChannelIntegrationDetails> findByTenantId(String tenantId);
}

