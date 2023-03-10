object TwitterConsolidator {
  def consolidateData(): Unit = {
    import org.apache.spark.sql.SparkSession

    val spark = SparkSession.builder().appName(sys.env("EXPORT_APP_NAME")).getOrCreate()

    val s3Bucket = sys.env("EXPORT_AWS_S3_BUCKET")
    val s3Path = sys.env("EXPORT_AWS_S3_PATH")
    val tweetsDF = spark.read.parquet(s"s3a://$s3Bucket/$s3Path")

    val hashtagsDF = tweetsDF.groupBy("hashtag").count()

    val consolidatedS3Path = s3Path + "/consolidated"
    hashtagsDF.write.mode("append").parquet(s"s3a://$s3Bucket/$consolidatedS3Path")

    spark.stop()
  }
}
