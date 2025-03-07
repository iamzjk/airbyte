/*
 * Copyright (c) 2021 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.s3;

import static io.airbyte.integrations.destination.s3.S3DestinationConstants.NAME_TRANSFORMER;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.airbyte.commons.io.IOs;
import io.airbyte.commons.jackson.MoreMappers;
import io.airbyte.commons.json.Jsons;
import io.airbyte.integrations.destination.s3.util.S3OutputPathHelper;
import io.airbyte.integrations.standardtest.destination.DestinationAcceptanceTest;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When adding a new S3 destination acceptance test, extend this class and do the following:
 * <li>Implement {@link #getFormatConfig} that returns a {@link S3FormatConfig}</li>
 * <li>Implement {@link #retrieveRecords} that returns the Json records for the test</li>
 *
 * Under the hood, a {@link S3DestinationConfig} is constructed as follows:
 * <li>Retrieve the secrets from "secrets/config.json"</li>
 * <li>Get the S3 bucket path from the constructor</li>
 * <li>Get the format config from {@link #getFormatConfig}</li>
 */
public abstract class S3DestinationAcceptanceTest extends DestinationAcceptanceTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(S3DestinationAcceptanceTest.class);
  protected static final ObjectMapper MAPPER = MoreMappers.initMapper();

  protected final String secretFilePath = "secrets/config.json";
  protected final S3Format outputFormat;
  protected JsonNode configJson;
  protected S3DestinationConfig config;
  protected AmazonS3 s3Client;

  protected S3DestinationAcceptanceTest(final S3Format outputFormat) {
    this.outputFormat = outputFormat;
  }

  protected JsonNode getBaseConfigJson() {
    return Jsons.deserialize(IOs.readFile(Path.of(secretFilePath)));
  }

  @Override
  protected String getImageName() {
    return "airbyte/destination-s3:dev";
  }

  @Override
  protected JsonNode getConfig() {
    return configJson;
  }

  @Override
  protected JsonNode getFailCheckConfig() {
    final JsonNode baseJson = getBaseConfigJson();
    final JsonNode failCheckJson = Jsons.clone(baseJson);
    // invalid credential
    ((ObjectNode) failCheckJson).put("access_key_id", "fake-key");
    ((ObjectNode) failCheckJson).put("secret_access_key", "fake-secret");
    return failCheckJson;
  }

  /**
   * Helper method to retrieve all synced objects inside the configured bucket path.
   */
  protected List<S3ObjectSummary> getAllSyncedObjects(final String streamName, final String namespace) {
    final String outputPrefix = S3OutputPathHelper
        .getOutputPrefix(config.getBucketPath(), namespace, streamName);
    final List<S3ObjectSummary> objectSummaries = s3Client
        .listObjects(config.getBucketName(), outputPrefix)
        .getObjectSummaries()
        .stream()
        .filter(o -> o.getKey().contains(NAME_TRANSFORMER.convertStreamName(streamName) + "/"))
        .sorted(Comparator.comparingLong(o -> o.getLastModified().getTime()))
        .collect(Collectors.toList());
    LOGGER.info(
        "All objects: {}",
        objectSummaries.stream().map(o -> String.format("%s/%s", o.getBucketName(), o.getKey())).collect(Collectors.toList()));
    return objectSummaries;
  }

  protected abstract JsonNode getFormatConfig();

  /**
   * This method does the following:
   * <li>Construct the S3 destination config.</li>
   * <li>Construct the S3 client.</li>
   */
  @Override
  protected void setup(final TestDestinationEnv testEnv) {
    final JsonNode baseConfigJson = getBaseConfigJson();
    // Set a random s3 bucket path for each integration test
    final JsonNode configJson = Jsons.clone(baseConfigJson);
    final String testBucketPath = String.format(
        "%s_test_%s",
        outputFormat.name().toLowerCase(Locale.ROOT),
        RandomStringUtils.randomAlphanumeric(5));
    ((ObjectNode) configJson)
        .put("s3_bucket_path", testBucketPath)
        .set("format", getFormatConfig());
    this.configJson = configJson;
    this.config = S3DestinationConfig.getS3DestinationConfig(configJson);
    LOGGER.info("Test full path: {}/{}", config.getBucketName(), config.getBucketPath());

    this.s3Client = config.getS3Client();
  }

  /**
   * Remove all the S3 output from the tests.
   */
  @Override
  protected void tearDown(final TestDestinationEnv testEnv) {
    final List<KeyVersion> keysToDelete = new LinkedList<>();
    final List<S3ObjectSummary> objects = s3Client
        .listObjects(config.getBucketName(), config.getBucketPath())
        .getObjectSummaries();
    for (final S3ObjectSummary object : objects) {
      keysToDelete.add(new KeyVersion(object.getKey()));
    }

    if (keysToDelete.size() > 0) {
      LOGGER.info("Tearing down test bucket path: {}/{}", config.getBucketName(),
          config.getBucketPath());
      final DeleteObjectsResult result = s3Client
          .deleteObjects(new DeleteObjectsRequest(config.getBucketName()).withKeys(keysToDelete));
      LOGGER.info("Deleted {} file(s).", result.getDeletedObjects().size());
    }
  }

}
