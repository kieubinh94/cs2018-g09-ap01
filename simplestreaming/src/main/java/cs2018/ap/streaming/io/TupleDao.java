package cs2018.ap.streaming.io;

import java.util.Optional;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TupleDao {
  private static final Logger LOG = LoggerFactory.getLogger(TupleDao.class);

  private final transient TransportClient client;
  private final transient String index;

  public TupleDao(final Client client, final String index) {
    this.client = (TransportClient) client;
    this.index = index;
  }

  public void destroy() {
    if (client != null) {
      client.close();
      LOG.debug("Releasing resource of Elastic search client");
    }
  }

  public Optional<Tuple> loadByKey(final String key, final String esIndexType) {
    final SearchResponse response =
        client.prepareSearch(index).setQuery(QueryBuilders.idsQuery(esIndexType).ids(key)).get();

    if (response.getHits().getTotalHits() == 0) {
      LOG.debug("Cannot find publisher from index {} with key '{}'", index, key);
      return Optional.empty();
    }

    final SearchHit hit = response.getHits().getHits()[0];
    return Optional.of(new Tuple(hit.getSource()));
  }
}
