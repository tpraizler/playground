import java.net.InetAddress

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.concurrent.ExecutionContext.Implicits.global

object Example {
  def main(args: Array[String]): Unit = {
    val client = initClient("username", "password", "eu-west-1", "clusterId", 9343)

    val firstIndex = createIndex(client)
    client.execute(index into firstIndex / "my_type" fields "name"->"coldplay").await
    client.execute(index into firstIndex / "my_type" fields "name"->"coldplay").await
    client.execute(index into firstIndex / "my_type" fields "name"->"coldplay").await

    Thread.sleep(2000)
    val searchResponse = client.execute(search in firstIndex / "my_type" query matchAllQuery).await
    println(s"- Number of documents: ${searchResponse.totalHits}")
    println(s"- Trying to reindex into new index")
    val secondIndex = createIndex(client)

    client.execute(reindex(firstIndex, secondIndex)).await
  }

  def createIndex(client:ElasticClient): String = {
    val newIndexName = "myindex" + "_" + DateTime.now().toString(DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss-SS"))
    println(s"- Creating new index - $newIndexName")
    client.execute(create index newIndexName).await
    newIndexName
  }

  def initClient(username: String, password: String, region: String, clusterId: String, port: Int): ElasticClient = {
    val hostname = clusterId + "." + region + ".aws.found.io"
    val settings = Settings.settingsBuilder()
      .put("transport.ping_schedule", "5s")
      .put("cluster.name", clusterId)
      .put("shield.transport.ssl", true)
      .put("request.headers.X-Found-Cluster", clusterId)
      .put("shield.user", s"$username:$password")
      .put("plugin.types", "org.elasticsearch.shield.ShieldPlugin")
      .build();
    val client: TransportClient = TransportClient.builder().settings(settings).build().
      addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), port))

    ElasticClient.fromClient(client)
  }
}