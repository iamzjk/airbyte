/*
 * Copyright (c) 2021 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.oauth.flows;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.airbyte.commons.json.Jsons;
import io.airbyte.config.SourceOAuthParameter;
import io.airbyte.config.persistence.ConfigNotFoundException;
import io.airbyte.config.persistence.ConfigRepository;
import io.airbyte.oauth.OAuthFlowImplementation;
import io.airbyte.validation.json.JsonValidationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SurveymonkeyOAuthFlowIntegrationTest extends OAuthFlowIntegrationTest {

  protected static final Path CREDENTIALS_PATH = Path.of("secrets/surveymonkey.json");
  protected static final String REDIRECT_URL = "http://localhost:3000/auth_flow";

  @Override
  protected Path get_credentials_path() {
    return CREDENTIALS_PATH;
  }

  @Override
  protected OAuthFlowImplementation getFlowObject(ConfigRepository configRepository) {
    return new SurveymonkeyOAuthFlow(configRepository);
  }

  @BeforeEach
  public void setup() throws IOException {
    super.setup();
  }

  @Override
  protected int getServerListeningPort() {
    return 3000;
  }

  @Test
  public void testFullSurveymonkeyOAuthFlow() throws InterruptedException, ConfigNotFoundException, IOException, JsonValidationException {
    final UUID workspaceId = UUID.randomUUID();
    final UUID definitionId = UUID.randomUUID();
    final String fullConfigAsString = new String(Files.readAllBytes(CREDENTIALS_PATH));
    final JsonNode credentialsJson = Jsons.deserialize(fullConfigAsString);
    when(configRepository.listSourceOAuthParam()).thenReturn(List.of(new SourceOAuthParameter()
        .withOauthParameterId(UUID.randomUUID())
        .withSourceDefinitionId(definitionId)
        .withWorkspaceId(workspaceId)
        .withConfiguration(Jsons.jsonNode(ImmutableMap.builder()
            .put("client_id", credentialsJson.get("client_id").asText())
            .put("client_secret", credentialsJson.get("client_secret").asText())
            .build()))));
    final String url = flow.getSourceConsentUrl(workspaceId, definitionId, REDIRECT_URL);
    LOGGER.info("Waiting for user consent at: {}", url);
    waitForResponse(20);
    assertTrue(serverHandler.isSucceeded(), "Failed to get User consent on time");
    final Map<String, Object> params = flow.completeSourceOAuth(workspaceId, definitionId,
        Map.of("code", serverHandler.getParamValue()), REDIRECT_URL);
    LOGGER.info("Response from completing OAuth Flow is: {}", params.toString());
    assertTrue(params.containsKey("access_token"));
    assertTrue(params.get("access_token").toString().length() > 0);
  }

}
