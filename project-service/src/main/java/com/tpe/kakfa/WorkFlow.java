package com.tpe.kakfa;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WorkFlow {

    class FileIngessionService {
        //private final KafkaTemplate<String, OrderDto> kafkaTemplate;
        static void ignestFileToS3(InputStream inputStream) {
            // logic to ingest file to S3
            // if successful, send message to kafka topic "file-ingestion-topic"
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("datasourceId", 1L);
            dataMap.put("filename", "data.csv");
            String topic = "file-ingested";
            String partitionKey = "jobId";
            //kafkaTemplate.send(topic, partitionKey , dataMap);

        }
    }


    class SnowflakeDataService {

        //@KafkaListener(topics = "orders", groupId = "order-group")
        public void consume(
                 Map<String, Object> dataMap,
                //@Header(KafkaHeaders.RECEIVED_KEY)
                String partitionKey) {

            //System.out.println("Processing order ID: " + order.getId());
            System.out.println("Received from partition via key: " + partitionKey);
            importData("","","" );
        }
         static void importData(String schema, String tableName , String s3filePath) {
             // logic to get table details from database using dataSourceId
        }

    }

     static void startWorkFlow() {
         InputStream inputStream = null; // get input stream from file
         FileIngessionService.ingestFileToS3(null);
    }

}
