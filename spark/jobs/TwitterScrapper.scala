object TwitterScraper {
  def extractAndSaveTweets(): Unit = {
    // Importando bibliotecas necessárias
    import org.apache.spark.sql.SparkSession
    import org.jsoup.Jsoup
    import org.jsoup.nodes.Document
    import org.jsoup.select.Elements
    

    val spark = SparkSession.builder().appName(sys.env("EXPORT_APP_NAME")).getOrCreate()

    // Definindo as configurações do Twitter e o URL de busca
    val twitterBaseUrl = sys.env("EXPORT_TWITTER_BASEURL")
    val hashtag = sys.env("HASHTAG")
    val count = sys.env("COUNT")
    val searchUrl = twitterBaseUrl + hashtag + "&src=typed_query&f=live&count=" + count
    
    val userAgent = sys.env("EXPORT_TWITTER_USERAGENT")
    val requestTimeout = sys.env("EXPORT_TWITTER_REQUESTTIMEOUTMS").toInt

    val doc: Document = Jsoup.connect(searchUrl).userAgent(userAgent).timeout(requestTimeout).get()

    // Selecionando os tweets da página
    val tweets: Elements = doc.select(".css-1dbjc4n.r-18u37iz.r-1wtj0ep.r-1s2bzr4.r-1mdbhws")

    val tweetsDF = spark.createDataFrame(Seq(tweets.html())).toDF("tweet")

    // Salvando o DataFrame em formato Parquet no S3
    val s3Bucket = sys.env("EXPORT_AWS_S3_BUCKET")
    val s3Path = sys.env("EXPORT_AWS_S3_PATH")
    tweetsDF.write.mode("append").parquet(s"s3a://$s3Bucket/$s3Path")
    

    spark.stop()
  }
}
