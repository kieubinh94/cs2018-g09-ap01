/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.elasticsearch;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.value.AutoValue;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import org.apache.beam.sdk.annotations.Experimental;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms for reading and writing data from/to Elasticsearch.
 *
 * <h3>Reading from Elasticsearch</h3>
 *
 * <p>{@link ElasticsearchIO#read ElasticsearchIO.read()} returns a bounded {@link PCollection
 * PCollection&lt;String&gt;} representing JSON documents.
 *
 * <p>To configure the {@link ElasticsearchIO#read}, you have to provide a connection configuration
 * containing the HTTP address of the instances, an index name and a type. The following example
 * illustrates options for configuring the source:
 *
 * <pre>{@code
 * pipeline.apply(ElasticsearchIO.read().withConnectionConfiguration(
 *    ElasticsearchIO.ConnectionConfiguration.create("http://host:9200", "my-index", "my-type")
 * )
 *
 * }</pre>
 *
 * <p>The connection configuration also accepts optional configuration: {@code withUsername()} and
 * {@code withPassword()}.
 *
 * <p>You can also specify a query on the {@code read()} using {@code withQuery()}.
 *
 * <h3>Writing to Elasticsearch</h3>
 *
 * <pre>{@code
 * pipeline
 *   .apply(...)
 *   .apply(ElasticsearchIO.write().withConnectionConfiguration(
 *      ElasticsearchIO.ConnectionConfiguration.create("http://host:9200", "my-index", "my-type")
 *   )
 *
 * }</pre>
 *
 * <p>Optionally, you can provide {@code withBatchSize()} and {@code withBatchSizeBytes()} to
 * specify the size of the write batch in number of documents or in bytes.
 */
@Experimental(Experimental.Kind.SOURCE_SINK)
public class ElasticsearchIO {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchIO.class);

  public static Read read() {
    // default scrollKeepalive = 5m as a majorant for un-predictable time between 2 start/read calls
    // default batchSize to 100 as recommended by ES dev team as a safe value when dealing
    // with big documents and still a good compromise for performances
    return new AutoValue_ElasticsearchIO_Read.Builder()
        .setWithMetadata(false)
        .setScrollKeepalive("5m")
        .setBatchSize(100L)
        .build();
  }

  private ElasticsearchIO() {}

  private static final ObjectMapper mapper = new ObjectMapper();

  @VisibleForTesting
  static JsonNode parseResponse(Response response) throws IOException {
    return mapper.readValue(response.getEntity().getContent(), JsonNode.class);
  }

  static void checkForErrors(Response response, int backendVersion) throws IOException {
    JsonNode searchResult = parseResponse(response);
    boolean errors = searchResult.path("errors").asBoolean();
    if (errors) {
      StringBuilder errorMessages =
          new StringBuilder("Error writing to Elasticsearch, some elements could not be inserted:");
      JsonNode items = searchResult.path("items");
      //some items present in bulk might have errors, concatenate error messages
      for (JsonNode item : items) {

        String errorRootName = "";
        if (backendVersion == 2) {
          errorRootName = "create";
        } else if (backendVersion == 5 || backendVersion == 6) {
          errorRootName = "index";
        }
        JsonNode errorRoot = item.path(errorRootName);
        JsonNode error = errorRoot.get("error");
        if (error != null) {
          String type = error.path("type").asText();
          String reason = error.path("reason").asText();
          String docId = errorRoot.path("_id").asText();
          errorMessages.append(String.format("%nDocument id %s: %s (%s)", docId, reason, type));
          JsonNode causedBy = error.get("caused_by");
          if (causedBy != null) {
            String cbReason = causedBy.path("reason").asText();
            String cbType = causedBy.path("type").asText();
            errorMessages.append(String.format("%nCaused by: %s (%s)", cbReason, cbType));
          }
        }
      }
      throw new IOException(errorMessages.toString());
    }
  }

  /** A POJO describing a connection configuration to Elasticsearch. */
  @AutoValue
  public abstract static class ConnectionConfiguration implements Serializable {

    public abstract List<String> getAddresses();

    @Nullable
    public abstract String getUsername();

    @Nullable
    public abstract String getPassword();

    @Nullable
    public abstract String getKeystorePath();

    @Nullable
    public abstract String getKeystorePassword();

    public abstract String getIndex();

    public abstract String getType();

    abstract Builder builder();

    @AutoValue.Builder
    abstract static class Builder {
      abstract Builder setAddresses(List<String> addresses);

      abstract Builder setUsername(String username);

      abstract Builder setPassword(String password);

      abstract Builder setKeystorePath(String keystorePath);

      abstract Builder setKeystorePassword(String password);

      abstract Builder setIndex(String index);

      abstract Builder setType(String type);

      abstract ConnectionConfiguration build();
    }

    /**
     * Creates a new Elasticsearch connection configuration.
     *
     * @param addresses list of addresses of Elasticsearch nodes
     * @param index the index toward which the requests will be issued
     * @param type the document type toward which the requests will be issued
     * @return the connection configuration object
     */
    public static ConnectionConfiguration create(String[] addresses, String index, String type) {
      checkArgument(addresses != null, "addresses can not be null");
      checkArgument(addresses.length > 0, "addresses can not be empty");
      checkArgument(index != null, "index can not be null");
      checkArgument(type != null, "type can not be null");
      ConnectionConfiguration connectionConfiguration =
          new AutoValue_ElasticsearchIO_ConnectionConfiguration.Builder()
              .setAddresses(Arrays.asList(addresses))
              .setIndex(index)
              .setType(type)
              .build();
      return connectionConfiguration;
    }

    /**
     * If Elasticsearch authentication is enabled, provide the username.
     *
     * @param username the username used to authenticate to Elasticsearch
     * @return a {@link ConnectionConfiguration} describes a connection configuration to
     *     Elasticsearch.
     */
    public ConnectionConfiguration withUsername(String username) {
      checkArgument(username != null, "username can not be null");
      checkArgument(!username.isEmpty(), "username can not be empty");
      return builder().setUsername(username).build();
    }

    /**
     * If Elasticsearch authentication is enabled, provide the password.
     *
     * @param password the password used to authenticate to Elasticsearch
     * @return a {@link ConnectionConfiguration} describes a connection configuration to
     *     Elasticsearch.
     */
    public ConnectionConfiguration withPassword(String password) {
      checkArgument(password != null, "password can not be null");
      checkArgument(!password.isEmpty(), "password can not be empty");
      return builder().setPassword(password).build();
    }

    /**
     * If Elasticsearch uses SSL/TLS with mutual authentication (via shield), provide the keystore
     * containing the client key.
     *
     * @param keystorePath the location of the keystore containing the client key.
     * @return a {@link ConnectionConfiguration} describes a connection configuration to
     *     Elasticsearch.
     */
    public ConnectionConfiguration withKeystorePath(String keystorePath) {
      checkArgument(keystorePath != null, "keystorePath can not be null");
      checkArgument(!keystorePath.isEmpty(), "keystorePath can not be empty");
      return builder().setKeystorePath(keystorePath).build();
    }

    /**
     * If Elasticsearch uses SSL/TLS with mutual authentication (via shield), provide the password
     * to open the client keystore.
     *
     * @param keystorePassword the password of the client keystore.
     * @return a {@link ConnectionConfiguration} describes a connection configuration to
     *     Elasticsearch.
     */
    public ConnectionConfiguration withKeystorePassword(String keystorePassword) {
      checkArgument(keystorePassword != null, "keystorePassword can not be null");
      return builder().setKeystorePassword(keystorePassword).build();
    }

    private void populateDisplayData(DisplayData.Builder builder) {
      builder.add(DisplayData.item("address", getAddresses().toString()));
      builder.add(DisplayData.item("index", getIndex()));
      builder.add(DisplayData.item("type", getType()));
      builder.addIfNotNull(DisplayData.item("username", getUsername()));
      builder.addIfNotNull(DisplayData.item("keystore.path", getKeystorePath()));
    }

    @VisibleForTesting
    RestClient createClient() throws IOException {
      HttpHost[] hosts = new HttpHost[getAddresses().size()];
      int i = 0;
      for (String address : getAddresses()) {
        URL url = new URL(address);
        hosts[i] = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        i++;
      }
      RestClientBuilder restClientBuilder = RestClient.builder(hosts);
      if (getUsername() != null) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY, new UsernamePasswordCredentials(getUsername(), getPassword()));
        restClientBuilder.setHttpClientConfigCallback(
            httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
      }
      if (getKeystorePath() != null && !getKeystorePath().isEmpty()) {
        try {
          KeyStore keyStore = KeyStore.getInstance("jks");
          try (InputStream is = new FileInputStream(new File(getKeystorePath()))) {
            String keystorePassword = getKeystorePassword();
            keyStore.load(is, (keystorePassword == null) ? null : keystorePassword.toCharArray());
          }
          final SSLContext sslContext =
              SSLContexts.custom()
                  .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                  .build();
          final SSLIOSessionStrategy sessionStrategy = new SSLIOSessionStrategy(sslContext);
          restClientBuilder.setHttpClientConfigCallback(
              httpClientBuilder ->
                  httpClientBuilder.setSSLContext(sslContext).setSSLStrategy(sessionStrategy));
        } catch (Exception e) {
          throw new IOException("Can't load the client certificate from the keystore", e);
        }
      }
      return restClientBuilder.build();
    }
  }

  /** A {@link PTransform} reading data from Elasticsearch. */
  @AutoValue
  public abstract static class Read extends PTransform<PBegin, PCollection<String>> {

    private static final long MAX_BATCH_SIZE = 10000L;

    @Nullable
    abstract ConnectionConfiguration getConnectionConfiguration();

    @Nullable
    abstract String getQuery();

    abstract boolean isWithMetadata();

    abstract String getScrollKeepalive();

    abstract long getBatchSize();

    abstract Builder builder();

    @AutoValue.Builder
    abstract static class Builder {
      abstract Builder setConnectionConfiguration(ConnectionConfiguration connectionConfiguration);

      abstract Builder setQuery(String query);

      abstract Builder setWithMetadata(boolean withMetadata);

      abstract Builder setScrollKeepalive(String scrollKeepalive);

      abstract Builder setBatchSize(long batchSize);

      abstract Read build();
    }

    /**
     * Provide the Elasticsearch connection configuration object.
     *
     * @param connectionConfiguration a {@link ConnectionConfiguration} describes a connection
     *     configuration to Elasticsearch.
     * @return a {@link PTransform} reading data from Elasticsearch.
     */
    public Read withConnectionConfiguration(ConnectionConfiguration connectionConfiguration) {
      checkArgument(connectionConfiguration != null, "connectionConfiguration can not be null");
      return builder().setConnectionConfiguration(connectionConfiguration).build();
    }

    /**
     * Provide a query used while reading from Elasticsearch.
     *
     * @param query the query. See <a
     *     href="https://www.elastic.co/guide/en/elasticsearch/reference/2.4/query-dsl.html">Query
     *     DSL</a>
     * @return a {@link PTransform} reading data from Elasticsearch.
     */
    public Read withQuery(String query) {
      checkArgument(query != null, "query can not be null");
      checkArgument(!query.isEmpty(), "query can not be empty");
      return builder().setQuery(query).build();
    }

    /**
     * Include metadata in result json documents. Document source will be under json node _source.
     *
     * @return a {@link PTransform} reading data from Elasticsearch.
     */
    public Read withMetadata() {
      return builder().setWithMetadata(true).build();
    }

    /**
     * Provide a scroll keepalive. See <a
     * href="https://www.elastic.co/guide/en/elasticsearch/reference/2.4/search-request-scroll.html">scroll
     * API</a> Default is "5m". Change this only if you get "No search context found" errors.
     *
     * @param scrollKeepalive keepalive duration of the scroll
     * @return a {@link PTransform} reading data from Elasticsearch.
     */
    public Read withScrollKeepalive(String scrollKeepalive) {
      checkArgument(scrollKeepalive != null, "scrollKeepalive can not be null");
      checkArgument(!"0m".equals(scrollKeepalive), "scrollKeepalive can not be 0m");
      return builder().setScrollKeepalive(scrollKeepalive).build();
    }

    /**
     * Provide a size for the scroll read. See <a
     * href="https://www.elastic.co/guide/en/elasticsearch/reference/2.4/search-request-scroll.html">
     * scroll API</a> Default is 100. Maximum is 10 000. If documents are small, increasing batch
     * size might improve read performance. If documents are big, you might need to decrease
     * batchSize
     *
     * @param batchSize number of documents read in each scroll read
     * @return a {@link PTransform} reading data from Elasticsearch.
     */
    public Read withBatchSize(long batchSize) {
      checkArgument(
          batchSize > 0 && batchSize <= MAX_BATCH_SIZE,
          "batchSize must be > 0 and <= %s, but was: %s",
          MAX_BATCH_SIZE,
          batchSize);
      return builder().setBatchSize(batchSize).build();
    }

    @Override
    public PCollection<String> expand(PBegin input) {
      ConnectionConfiguration connectionConfiguration = getConnectionConfiguration();
      checkState(connectionConfiguration != null, "withConnectionConfiguration() is required");
      return input.apply(
          org.apache.beam.sdk.io.Read.from(new BoundedElasticsearchSource(this, null, null, null)));
    }

    @Override
    public void populateDisplayData(DisplayData.Builder builder) {
      super.populateDisplayData(builder);
      builder.addIfNotNull(DisplayData.item("query", getQuery()));
      builder.addIfNotNull(DisplayData.item("withMetadata", isWithMetadata()));
      builder.addIfNotNull(DisplayData.item("batchSize", getBatchSize()));
      builder.addIfNotNull(DisplayData.item("scrollKeepalive", getScrollKeepalive()));
      getConnectionConfiguration().populateDisplayData(builder);
    }
  }

  /** A {@link BoundedSource} reading from Elasticsearch. */
  @VisibleForTesting
  public static class BoundedElasticsearchSource extends BoundedSource<String> {

    private int backendVersion;

    private final Read spec;
    // shardPreference is the shard id where the source will read the documents
    @Nullable private final String shardPreference;
    @Nullable private final Integer numSlices;
    @Nullable private final Integer sliceId;

    //constructor used in split() when we know the backend version
    private BoundedElasticsearchSource(
        Read spec,
        @Nullable String shardPreference,
        @Nullable Integer numSlices,
        @Nullable Integer sliceId,
        int backendVersion) {
      this.backendVersion = backendVersion;
      this.spec = spec;
      this.shardPreference = shardPreference;
      this.numSlices = numSlices;
      this.sliceId = sliceId;
    }

    @VisibleForTesting
    BoundedElasticsearchSource(
        Read spec,
        @Nullable String shardPreference,
        @Nullable Integer numSlices,
        @Nullable Integer sliceId) {
      this.spec = spec;
      this.shardPreference = shardPreference;
      this.numSlices = numSlices;
      this.sliceId = sliceId;
    }

    @Override
    public List<? extends BoundedSource<String>> split(
        long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
      ConnectionConfiguration connectionConfiguration = spec.getConnectionConfiguration();
      this.backendVersion = getBackendVersion(connectionConfiguration);
      List<BoundedElasticsearchSource> sources = new ArrayList<>();
      if (backendVersion == 2) {
        // 1. We split per shard :
        // unfortunately, Elasticsearch 2.x doesn't provide a way to do parallel reads on a single
        // shard.So we do not use desiredBundleSize because we cannot split shards.
        // With the slice API in ES 5.x+ we will be able to use desiredBundleSize.
        // Basically we will just ask the slice API to return data
        // in nbBundles = estimatedSize / desiredBundleSize chuncks.
        // So each beam source will read around desiredBundleSize volume of data.

        JsonNode statsJson = BoundedElasticsearchSource.getStats(connectionConfiguration, true);
        JsonNode shardsJson =
            statsJson.path("indices").path(connectionConfiguration.getIndex()).path("shards");

        Iterator<Map.Entry<String, JsonNode>> shards = shardsJson.fields();
        while (shards.hasNext()) {
          Map.Entry<String, JsonNode> shardJson = shards.next();
          String shardId = shardJson.getKey();
          sources.add(new BoundedElasticsearchSource(spec, shardId, null, null, backendVersion));
        }
        checkArgument(!sources.isEmpty(), "No shard found");
      } else if (backendVersion == 5 || backendVersion == 6) {
        long indexSize = BoundedElasticsearchSource.estimateIndexSize(connectionConfiguration);
        float nbBundlesFloat = (float) indexSize / desiredBundleSizeBytes;
        int nbBundles = (int) Math.ceil(nbBundlesFloat);
        // ES slice api imposes that the number of slices is <= 1024 even if it can be overloaded
        if (nbBundles > 1024) {
          nbBundles = 1024;
        }
        // split the index into nbBundles chunks of desiredBundleSizeBytes by creating
        // nbBundles sources each reading a slice of the index
        // (see https://goo.gl/MhtSWz)
        // the slice API allows to split the ES shards
        // to have bundles closer to desiredBundleSizeBytes
        for (int i = 0; i < nbBundles; i++) {
          sources.add(new BoundedElasticsearchSource(spec, null, nbBundles, i, backendVersion));
        }
      }
      return sources;
    }

    @Override
    public long getEstimatedSizeBytes(PipelineOptions options) throws IOException {
      return estimateIndexSize(spec.getConnectionConfiguration());
    }

    @VisibleForTesting
    static long estimateIndexSize(ConnectionConfiguration connectionConfiguration)
        throws IOException {
      // we use indices stats API to estimate size and list the shards
      // (https://www.elastic.co/guide/en/elasticsearch/reference/2.4/indices-stats.html)
      // as Elasticsearch 2.x doesn't not support any way to do parallel read inside a shard
      // the estimated size bytes is not really used in the split into bundles.
      // However, we implement this method anyway as the runners can use it.
      // NB: Elasticsearch 5.x+ now provides the slice API.
      // (https://www.elastic.co/guide/en/elasticsearch/reference/5.0/search-request-scroll.html
      // #sliced-scroll)
      JsonNode statsJson = getStats(connectionConfiguration, false);
      JsonNode indexStats =
          statsJson.path("indices").path(connectionConfiguration.getIndex()).path("primaries");
      JsonNode store = indexStats.path("store");
      return store.path("size_in_bytes").asLong();
    }

    @Override
    public void populateDisplayData(DisplayData.Builder builder) {
      spec.populateDisplayData(builder);
      builder.addIfNotNull(DisplayData.item("shard", shardPreference));
      builder.addIfNotNull(DisplayData.item("numSlices", numSlices));
      builder.addIfNotNull(DisplayData.item("sliceId", sliceId));
    }

    @Override
    public BoundedReader<String> createReader(PipelineOptions options) {
      return new BoundedElasticsearchReader(this);
    }

    @Override
    public void validate() {
      spec.validate(null);
    }

    @Override
    public Coder<String> getOutputCoder() {
      return StringUtf8Coder.of();
    }

    private static JsonNode getStats(
        ConnectionConfiguration connectionConfiguration, boolean shardLevel) throws IOException {
      HashMap<String, String> params = new HashMap<>();
      if (shardLevel) {
        params.put("level", "shards");
      }
      String endpoint = String.format("/%s/_stats", connectionConfiguration.getIndex());
      try (RestClient restClient = connectionConfiguration.createClient()) {
        return parseResponse(restClient.performRequest("GET", endpoint, params));
      }
    }
  }

  private static class BoundedElasticsearchReader extends BoundedSource.BoundedReader<String> {

    private final BoundedElasticsearchSource source;

    private RestClient restClient;
    private String current;
    private String scrollId;
    private ListIterator<String> batchIterator;

    private BoundedElasticsearchReader(BoundedElasticsearchSource source) {
      this.source = source;
    }

    @Override
    public boolean start() throws IOException {
      restClient = source.spec.getConnectionConfiguration().createClient();

      String query = source.spec.getQuery();
      if (query == null) {
        query = "{\"query\": { \"match_all\": {} }}";
      }
      if ((source.backendVersion == 5 || source.backendVersion == 6)
          && source.numSlices != null
          && source.numSlices > 1) {
        //if there is more than one slice, add the slice to the user query
        String sliceQuery =
            String.format("\"slice\": {\"id\": %s,\"max\": %s}", source.sliceId, source.numSlices);
        query = query.replaceFirst("\\{", "{" + sliceQuery + ",");
      }
      Response response;
      String endPoint =
          String.format(
              "/%s/%s/_search",
              source.spec.getConnectionConfiguration().getIndex(),
              source.spec.getConnectionConfiguration().getType());
      Map<String, String> params = new HashMap<>();
      params.put("scroll", source.spec.getScrollKeepalive());
      if (source.backendVersion == 2) {
        params.put("size", String.valueOf(source.spec.getBatchSize()));
        if (source.shardPreference != null) {
          params.put("preference", "_shards:" + source.shardPreference);
        }
      }
      HttpEntity queryEntity = new NStringEntity(query, ContentType.APPLICATION_JSON);
      response = restClient.performRequest("GET", endPoint, params, queryEntity);
      JsonNode searchResult = parseResponse(response);
      updateScrollId(searchResult);
      return readNextBatchAndReturnFirstDocument(searchResult);
    }

    private void updateScrollId(JsonNode searchResult) {
      scrollId = searchResult.path("_scroll_id").asText();
    }

    @Override
    public boolean advance() throws IOException {
      if (batchIterator.hasNext()) {
        current = batchIterator.next();
        return true;
      } else {
        String requestBody =
            String.format(
                "{\"scroll\" : \"%s\",\"scroll_id\" : \"%s\"}",
                source.spec.getScrollKeepalive(), scrollId);
        HttpEntity scrollEntity = new NStringEntity(requestBody, ContentType.APPLICATION_JSON);
        Response response =
            restClient.performRequest(
                "GET", "/_search/scroll", Collections.emptyMap(), scrollEntity);
        JsonNode searchResult = parseResponse(response);
        updateScrollId(searchResult);
        return readNextBatchAndReturnFirstDocument(searchResult);
      }
    }

    private boolean readNextBatchAndReturnFirstDocument(JsonNode searchResult) {
      //stop if no more data
      JsonNode hits = searchResult.path("hits").path("hits");
      if (hits.size() == 0) {
        current = null;
        batchIterator = null;
        return false;
      }
      // list behind iterator is empty
      List<String> batch = new ArrayList<>();
      boolean withMetadata = source.spec.isWithMetadata();
      for (JsonNode hit : hits) {
        if (withMetadata) {
          batch.add(hit.toString());
        } else {
          final String document = hit.path("_source").toString();
          final String id = String.format("\"_id\":%s", hit.get("_id").toString());
          final String newDoc =
              String.format("{%s, %s}", id, document.substring(1, document.length() - 1));
          batch.add(newDoc);
        }
      }
      batchIterator = batch.listIterator();
      current = batchIterator.next();
      return true;
    }

    @Override
    public String getCurrent() throws NoSuchElementException {
      if (current == null) {
        throw new NoSuchElementException();
      }
      return current;
    }

    @Override
    public void close() throws IOException {
      // remove the scroll
      String requestBody = String.format("{\"scroll_id\" : [\"%s\"]}", scrollId);
      HttpEntity entity = new NStringEntity(requestBody, ContentType.APPLICATION_JSON);
      try {
        restClient.performRequest("DELETE", "/_search/scroll", Collections.emptyMap(), entity);
      } finally {
        if (restClient != null) {
          restClient.close();
        }
      }
    }

    @Override
    public BoundedSource<String> getCurrentSource() {
      return source;
    }
  }
  /**
   * A POJO encapsulating a configuration for retry behavior when issuing requests to ES. A retry
   * will be attempted until the maxAttempts or maxDuration is exceeded, whichever comes first, for
   * 429 TOO_MANY_REQUESTS error.
   */
  @AutoValue
  public abstract static class RetryConfiguration implements Serializable {
    @VisibleForTesting
    static final RetryPredicate DEFAULT_RETRY_PREDICATE = new DefaultRetryPredicate();

    abstract int getMaxAttempts();

    abstract Duration getMaxDuration();

    abstract RetryPredicate getRetryPredicate();

    abstract Builder builder();

    @AutoValue.Builder
    abstract static class Builder {
      abstract ElasticsearchIO.RetryConfiguration.Builder setMaxAttempts(int maxAttempts);

      abstract ElasticsearchIO.RetryConfiguration.Builder setMaxDuration(Duration maxDuration);

      abstract ElasticsearchIO.RetryConfiguration.Builder setRetryPredicate(
          RetryPredicate retryPredicate);

      abstract ElasticsearchIO.RetryConfiguration build();
    }

    /**
     * Creates RetryConfiguration for {@link ElasticsearchIO} with provided maxAttempts,
     * maxDurations and exponential backoff based retries.
     *
     * @param maxAttempts max number of attempts.
     * @param maxDuration maximum duration for retries.
     * @return {@link RetryConfiguration} object with provided settings.
     */
    public static RetryConfiguration create(int maxAttempts, Duration maxDuration) {
      checkArgument(maxAttempts > 0, "maxAttempts must be greater than 0");
      checkArgument(
          maxDuration != null && maxDuration.isLongerThan(Duration.ZERO),
          "maxDuration must be greater than 0");
      return new AutoValue_ElasticsearchIO_RetryConfiguration.Builder()
          .setMaxAttempts(maxAttempts)
          .setMaxDuration(maxDuration)
          .setRetryPredicate(DEFAULT_RETRY_PREDICATE)
          .build();
    }

    // Exposed only to allow tests to easily simulate server errors
    @VisibleForTesting
    RetryConfiguration withRetryPredicate(RetryPredicate predicate) {
      checkArgument(predicate != null, "predicate must be provided");
      return builder().setRetryPredicate(predicate).build();
    }

    /**
     * An interface used to control if we retry the Elasticsearch call when a {@link Response} is
     * obtained. If {@link RetryPredicate#test(Object)} returns true, {@link Write} tries to resend
     * the requests to the Elasticsearch server if the {@link RetryConfiguration} permits it.
     */
    @FunctionalInterface
    interface RetryPredicate extends Predicate<Response>, Serializable {}

    /**
     * This is the default predicate used to test if a failed ES operation should be retried. A
     * retry will be attempted until the maxAttempts or maxDuration is exceeded, whichever comes
     * first, for TOO_MANY_REQUESTS(429) error.
     */
    @VisibleForTesting
    static class DefaultRetryPredicate implements RetryPredicate {

      private int errorCode;

      DefaultRetryPredicate(int code) {
        this.errorCode = code;
      }

      DefaultRetryPredicate() {
        this(429);
      }

      /** Returns true if the response has the error code for any mutation. */
      private static boolean errorCodePresent(Response response, int errorCode) {
        try {
          JsonNode json = parseResponse(response);
          if (json.path("errors").asBoolean()) {
            for (JsonNode item : json.path("items")) {
              if (item.findValue("status").asInt() == errorCode) {
                return true;
              }
            }
          }
        } catch (IOException e) {
          LOG.warn("Could not extract error codes from response {}", response);
        }
        return false;
      }

      @Override
      public boolean test(Response response) {
        return errorCodePresent(response, errorCode);
      }
    }
  }

  static int getBackendVersion(ConnectionConfiguration connectionConfiguration) {
    try (RestClient restClient = connectionConfiguration.createClient()) {
      Response response = restClient.performRequest("GET", "");
      JsonNode jsonNode = parseResponse(response);
      int backendVersion =
          Integer.parseInt(jsonNode.path("version").path("number").asText().substring(0, 1));
      checkArgument(
          (backendVersion == 2 || backendVersion == 5 || backendVersion == 6),
          "The Elasticsearch version to connect to is %s.x. "
              + "This version of the ElasticsearchIO is only compatible with "
              + "Elasticsearch v6.x, v5.x and v2.x",
          backendVersion);
      return backendVersion;

    } catch (IOException e) {
      throw (new IllegalArgumentException("Cannot get Elasticsearch version"));
    }
  }
}
