import java.net.InetAddress

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Example {
  def main(args: Array[String]): Unit = {
    val client = initClient("username", "password", "eu-west-1", "cluserId", 9343)
    val newIndexName = "myindex" + "_" + DateTime.now().toString(DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss-SS"))

    println(s"- Creating new index - $newIndexName")
    Await.result(client.execute(create index newIndexName), 5 seconds)

    println(System.currentTimeMillis())
    Await.result(client.execute(reindex("current_index_name", newIndexName)), 3 minutes)
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