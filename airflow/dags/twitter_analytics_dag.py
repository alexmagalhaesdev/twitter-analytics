from datetime import datetime, timedelta
from airflow import DAG
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from dotenv import load_dotenv
import os

load_dotenv()

dag = DAG(
    'twitter_scraper',
    default_args=default_args,
    description='DAG para executar scraping de tweets do Twitter com Spark e Jsoup',
    schedule_interval='@daily',
    catchup=False
)

job1 = SparkSubmitOperator(
    task_id='baixar_tweets',
    application='jobs/twitter_scraper_job_1.jar',
    name='Twitter Scraper - Job 1',
    conn_id='spark_default',
    dag=dag,
)

job1 >> job2 >> job3