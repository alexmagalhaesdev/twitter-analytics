import org.apache.spark.sql.SparkSession

object HashtagConsolidation {
  
  // Função responsável por consolidar os dados de tweet por hashtag, agrupá-los e contar quantos tweets cada hashtag tem, e salvar o resultado em um novo arquivo Parquet
  def consolidateData(): Unit = {
    val spark = SparkSession.builder()
    .appName(sys.env("EXPORT_APP_NAME"))
    .getOrCreate()

    val df = spark.read.parquet(s"s3a://${sys.env("EXPORT_AWS_S3_BUCKET")}/${sys.env("EXPORT_AWS_S3_PATH")}")

    val consolidatedDf = df.groupBy("hashtag").count()

    // Escreve o resultado em um novo arquivo Parquet no caminho especificado no .env, com o sufixo "consolidated"
    consolidatedDf.write.parquet(s"s3a://${sys.env("EXPORT_AWS_S3_BUCKET")}/${sys.env("EXPORT_AWS_S3_PATH")}/consolidated")
  }
}
