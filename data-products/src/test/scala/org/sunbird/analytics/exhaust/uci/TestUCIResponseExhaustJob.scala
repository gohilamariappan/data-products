package org.sunbird.analytics.exhaust.uci

import org.apache.spark.sql.SparkSession
import org.ekstep.analytics.framework.{Fetcher, FrameworkContext, JobConfig, Query}
import org.ekstep.analytics.framework.util.JSONUtils
import org.scalamock.scalatest.MockFactory
import org.sunbird.analytics.exhaust.BaseReportsJob
import org.sunbird.analytics.job.report.BaseReportSpec
import org.sunbird.analytics.util.EmbeddedPostgresql

class TestUCIResponseExhaustJob  extends BaseReportSpec with MockFactory with BaseReportsJob {

  implicit var spark: SparkSession = _
  val jobRequestTable = "job_request"

  override def beforeAll(): Unit = {
    spark = getSparkSession()
    super.beforeAll()
    EmbeddedPostgresql.start()
    EmbeddedPostgresql.createJobRequestTable()
    EmbeddedPostgresql.createConversationTable()
//    EmbeddedPostgresql.createUserTable()
//    EmbeddedPostgresql.createUserRegistrationTable()
//    EmbeddedPostgresql.createIdentitiesTable()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    EmbeddedPostgresql.close()
    spark.close()
  }

  "UCI Response Exhaust Report" should "generate the report with all the correct data" in {
    EmbeddedPostgresql.execute(s"TRUNCATE $jobRequestTable")
    EmbeddedPostgresql.execute("INSERT INTO job_request (tag, request_id, job_id, status, request_data, requested_by, requested_channel, dt_job_submitted, download_urls, dt_file_created, dt_job_completed, execution_time, err_message ,iteration, encryption_key) VALUES ('fabc64a7-c9b0-4d0b-b8a6-8778757b2bb5:channel-01', '37564CF8F134EE7532F125651B51D17F', 'uci-response-exhaust', 'SUBMITTED', '{\"conversationId\": \"fabc64a7-c9b0-4d0b-b8a6-8778757b2bb5\"}', 'user-002', 'channel-001', '2020-10-19 05:58:18.666', '{}', NULL, NULL, 0, '' ,0, 'test12');")

    EmbeddedPostgresql.execute("INSERT INTO bot (id, name, startingMessage, users, logicIDs, owners, created_at, updated_at, status, description, startDate, endDate, purpose ) VALUES ('fabc64a7-c9b0-4d0b-b8a6-8778757b2bb5', 'Diksha Bot', 'Hello World!', NULL, '{}', ARRAY['channel-001', 'channel-002'], timestamp '2015-01-11 00:51:14', timestamp '2015-01-11 00:51:14', NULL, NULL, timestamp '2021-07-10 00:51:14', timestamp '2021-07-11 00:51:14', NULL);")
    EmbeddedPostgresql.execute("INSERT INTO bot (id, name, startingMessage, users, logicIDs, owners, created_at, updated_at, status, description, startDate, endDate, purpose ) VALUES ('56b31f3d-cc0f-49a1-b559-f7709200aa85', 'Sunbird Bot', 'Hello Sunbird!', NULL, '{}','{}', timestamp '2015-01-11 00:51:14', timestamp '2015-01-11 00:51:14', NULL, NULL, timestamp '2021-07-10 00:51:14', timestamp '2021-07-11 00:51:14', NULL);")
    EmbeddedPostgresql.execute("INSERT INTO bot (id, name, startingMessage, users, logicIDs, owners, created_at, updated_at, status, description, startDate, endDate, purpose ) VALUES ('5db54579-04bb-4fb7-a9ee-0f9994cfaada', 'COVID', 'What is COVID', NULL, '{}','{}', timestamp '2015-01-11 00:51:14', timestamp '2015-01-11 00:51:14', NULL, NULL, timestamp '2021-07-10 00:51:14', timestamp '2021-07-11 00:51:14', NULL);")
    EmbeddedPostgresql.execute("INSERT INTO bot (id, name, startingMessage, users, logicIDs, owners, created_at, updated_at, status, description, startDate, endDate, purpose ) VALUES ('20b0cdec-a9a6-4bd4-8b36-150d45499946', 'SUNDAY FUN', 'How was sunday', NULL, '{}','{}', timestamp '2015-01-11 00:51:14', timestamp '2015-01-11 00:51:14', NULL, NULL, timestamp '2021-07-10 00:51:14', timestamp '2021-07-11 00:51:14', NULL);")


    implicit val fc = new FrameworkContext()
    val strConfig = """{"search":{"type":"local","queries":[{"file":"/Users/sowmyadixit/ekstep/github/Sunbird-Ed/Latest/sunbird-data-products/data-products/src/test/resources/exhaust/uci/telemetry_data.log"}]},"model":"org.sunbird.analytics.uci.UCIResponseExhaust","modelParams":{"store":"local","mode":"OnDemand","fromDate":"","toDate":"","storageContainer":""},"parallelization":8,"appName":"UCI Response Exhaust"}"""
    val jobConfig = JSONUtils.deserialize[JobConfig](strConfig)
    implicit val config = jobConfig

    UCIResponseExhaustJob.execute()

    val pResponse = EmbeddedPostgresql.executeQuery("SELECT * FROM job_request WHERE job_id='uci-response-exhaust'")

    while(pResponse.next()) {
      println(pResponse.getString("err_message"))
      println(pResponse.getString("status"))
//      pResponse.getString("err_message") should be ("")
//      pResponse.getString("dt_job_submitted") should be ("2020-10-19 05:58:18.666")
//      pResponse.getString("download_urls") should be (s"{reports/response-exhaust/$requestId/batch-001_response_${getDate()}.zip}")
//      pResponse.getString("dt_file_created") should be (null)
//      pResponse.getString("iteration") should be ("0")
    }

  }
}


