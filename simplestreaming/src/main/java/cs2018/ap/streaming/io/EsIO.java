package cs2018.ap.streaming.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.auto.value.AutoValue;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import javax.annotation.Nullable;
import org.apache.beam.sdk.annotations.Experimental;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.elasticsearch.Version;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.ShortClassName", "PMD.TooManyMethods", "PMD.GodClass"})
@Experimental(Experimental.Kind.SOURCE_SINK)
public class EsIO {
  private static final Logger LOG = LoggerFactory.getLogger(EsIO.class);

  // Recommended in ES docs (1000 doc or 5MB)
  // This bulk size is used for production ES
  // We need to have fast ES sinking
  public static final int BATCH_SIZE = 1000;

  private static final String SUPP_VER_MAJOR = "1.";

  private EsIO() {}

  public static Write write() {
    return new AutoValue_EsIO_Write.Builder()
        .setMaxBatchSize(BATCH_SIZE)
        .setIgnoredIfError(false)
        .build();
  }

  /* A {@link PTransform} writing data to Elasticsearch. */
  @SuppressWarnings("PMD.AbstractNaming")
  @AutoValue
  public abstract static class Write extends PTransform<PCollection<EsDoc>, PDone> {
    private static void checkVersion(final EsConfiguration connConfig) {
      try (TransportClient transportClient = connConfig.createClient()) {
        final ImmutableList<DiscoveryNode> nodes = transportClient.connectedNodes();
        final DiscoveryNode firstNode = nodes.get(0);
        final Version version = firstNode.getVersion();
        final boolean isVersion1x = version.toString().startsWith(SUPP_VER_MAJOR);
        checkArgument(
            isVersion1x,
            String.format(
                    "The Elasticsearch version to connect to is different of %sx. ", SUPP_VER_MAJOR)
                + String.format(
                    "This version of the EsIO is only compatible with Elasticsearch v%sx",
                    SUPP_VER_MAJOR));
      } catch (IOException e) {
        throw new IllegalArgumentException("Cannot check Elasticsearch version", e);
      }
    }

    @Nullable
    abstract EsConfiguration getConnectionConfiguration();

    abstract int getMaxBatchSize();

    abstract boolean isIgnoredIfError();

    abstract Builder builder();

    public Write withConfig(final EsConfiguration connConfig) {
      checkArgument(
          connConfig != null,
          "EsIO.write()" + ".withConfig(configuration) called with null configuration");
      return builder().setConnectionConfiguration(connConfig).build();
    }

    public Write withIgnoredIfError() {
      return builder().setIgnoredIfError(true).build();
    }

    public Write withMaxBatchSize(final int batchSize) {
      checkArgument(
          batchSize > 0,
          "EsIO.write()" + ".setBatchSize(configuration) called with invalid batch size");
      return builder().setMaxBatchSize(batchSize).build();
    }

    @Override
    public void validate(final PipelineOptions options) {
      final EsConfiguration connConfig = getConnectionConfiguration();
      checkState(
          connConfig != null,
          "EsIO.write() requires a connection configuration"
              + " to be set via withConfig(configuration)");
      checkVersion(connConfig);
    }

    @Override
    public PDone expand(final PCollection<EsDoc> input) {
      input.apply(getName(), ParDo.of(new WriteFn(this)));
      return PDone.in(input.getPipeline());
    }

    @AutoValue.Builder
    abstract static class Builder {
      abstract Builder setConnectionConfiguration(EsConfiguration connConfig);

      abstract Builder setMaxBatchSize(int maxBatchSize);

      abstract Builder setIgnoredIfError(boolean ignored);

      abstract Write build();
    }

    @SuppressWarnings("PMD.BeanMembersShouldSerialize")
    static class WriteFn extends DoFn<EsDoc, Void> {
      private static final Logger LOG = LoggerFactory.getLogger(WriteFn.class);

      private static final long serialVersionUID = 466439313690167856L;

      private final Write spec;
      private List<EsDoc> batch;

      private transient TransportClient client;

      WriteFn(final Write spec) {
        this.spec = spec;
      }

      @DoFn.Setup
      public void createClient() throws UnknownHostException {
        client = spec.getConnectionConfiguration().createClient();
      }

      @DoFn.StartBundle
      public void startBundle(final StartBundleContext context) throws IOException {
        this.batch = new ArrayList<>();
      }

      @ProcessElement
      public void processElement(final ProcessContext context) throws IOException {
        batch.add(context.element());
        LOG.debug("{} write: {}/{}", spec.getName(), batch.size(), spec.getMaxBatchSize());
        if (batch.size() >= spec.getMaxBatchSize()) {
          flushBatch();
        }
      }

      @DoFn.FinishBundle
      public void finishBundle(final FinishBundleContext context) throws IOException {
        flushBatch();
      }

      private void flushBatch() throws IOException {
        LOG.debug("{} flush bash: {}/{}", spec.getName(), batch.size(), spec.getMaxBatchSize());

        if (batch.isEmpty()) {
          return;
        }
        final List<EsDoc> bulkRequest = ImmutableList.copyOf(batch);
        batch.clear();

        final BulkRequestBuilder builder = client.prepareBulk();
        for (final EsDoc doc : bulkRequest) {
          LOG.debug(
              "About to index message: {} to {}",
              doc.getId(),
              spec.getConnectionConfiguration().getIndex());
          builder.add(
              client
                  .prepareIndex(
                      spec.getConnectionConfiguration().getIndex(),
                      spec.getConnectionConfiguration().getType(),
                      doc.getId())
                  .setSource(doc.getSource()));
        }
        final BulkResponse bulkResponse = builder.get();
        if (bulkResponse.hasFailures()) {
          throw new IOException("Cannot insert to ES");
        }
      }

      @DoFn.Teardown
      public void closeClient() {
        if (client != null) {
          client.close();
        }
      }
    }
  }
}
