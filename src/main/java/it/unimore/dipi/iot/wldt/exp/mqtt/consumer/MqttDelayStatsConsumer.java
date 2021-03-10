package it.unimore.dipi.iot.wldt.exp.mqtt.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import it.unimore.dipi.iot.wldt.exp.utils.SenMLRecord;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Simple MQTT Consumer using the library Eclipse Paho
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-playground
 * @created 14/10/2020 - 09:19
 */
public class MqttDelayStatsConsumer {

    private final static Logger logger = LoggerFactory.getLogger(MqttDelayStatsConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1884;

    private static final String TARGET_TOPIC = "#";

    private static ObjectMapper objectMapper;

    private static final int MESSAGE_NUMBER_LIMIT = 10000;

    private static Map<Long, String> messageMap = new HashMap<>();

    private static int receivedMessageCount = 0;

    private static long startTime;

    public static void main(String [ ] args) {

    	logger.info("MQTT Consumer Tester Started ...");

        try{

            objectMapper = new ObjectMapper();

            //Generate a random MQTT client ID using the UUID class
            String clientId = UUID.randomUUID().toString();

            //Represents a persistent data store, used to store outbound and inbound messages while they
            //are in flight, enabling delivery to the QoS specified. In that case use a memory persistence.
            //When the application stops all the temporary data will be deleted.
            MqttClientPersistence persistence = new MemoryPersistence();

            //The the persistence is not passed to the constructor the default file persistence is used.
            //In case of a file-based storage the same MQTT client UUID should be used
            IMqttClient client = new MqttClient(
                    String.format("tcp://%s:%d", BROKER_ADDRESS, BROKER_PORT), //Create the URL from IP and PORT
                    clientId,
                    persistence);

            //Define MQTT Connection Options such as reconnection, persistent/clean session and connection timeout
            //Authentication option can be added -> See AuthProducer example
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);

            logger.info("Connected ! Client Id: {}", clientId);

            startTime = System.currentTimeMillis();

            //Subscribe to the target topic #. In that case the consumer will receive (if authorized) all the message
            //passing through the broker
            client.subscribe(TARGET_TOPIC, (topic, msg) -> {

                //The topic variable contain the specific topic associated to the received message. Using MQTT wildcards
                //messaged from multiple and different topic can be received with the same subscription
                //The msg variable is a MqttMessage object containing all the information about the received message
                receivedMessageCount ++;

                if(messageMap.size() == MESSAGE_NUMBER_LIMIT || receivedMessageCount == MESSAGE_NUMBER_LIMIT || (System.currentTimeMillis() - startTime) > (1000*60*5)) {
                    client.unsubscribe(TARGET_TOPIC);
                    startStatsThread();
                }
                else {
                    logger.info("Message Processed: {}", receivedMessageCount);
                    messageMap.put(System.currentTimeMillis(), new String(msg.getPayload()));
                }

            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void startStatsThread(){

       logger.info("Starting Statistics Thread ...");

        CollectionType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, SenMLRecord.class);

       new Thread(new Runnable() {
           @Override
           public void run() {

               logger.info("Total Received Messages: {}/{}", receivedMessageCount, MESSAGE_NUMBER_LIMIT);

               List<Long> delayList = new ArrayList<>();

               messageMap.entrySet().forEach(messageEntry -> {
                    try{

                        long incomingTimestamp = messageEntry.getKey();
                        String payloadString = messageEntry.getValue();
                        List<SenMLRecord> asList = objectMapper.readValue(payloadString, javaType);
                        SenMLRecord record = asList.get(0);

                        long diff = incomingTimestamp - record.getT().longValue();

                        delayList.add(diff);

                        logger.info("Delay: {}", diff);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
               });

               long sum = 0;
               for(long delay: delayList)
                   sum = sum + delay;

               long averageDelay = sum / (long)delayList.size();

               logger.info("AVERAGE DELAY: {}", averageDelay);
           }
       }).start();

    }

}
