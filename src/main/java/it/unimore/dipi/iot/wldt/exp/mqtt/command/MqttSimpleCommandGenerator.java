package it.unimore.dipi.iot.wldt.exp.mqtt.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.wldt.exp.mqtt.consumer.MqttSimpleConsumer;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource.DummyStringResource;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource.SmartObjectResource;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project wldt-experimental-usecases
 * @created 10/03/2021 - 10:47
 */
public class MqttSimpleCommandGenerator {

    private final static Logger logger = LoggerFactory.getLogger(MqttSimpleConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1884;

    //DT TOPIC WITH PREFIX
    private static final String TARGET_TOPIC = "wldt/command/com:iot:dummy:dummyMqttDevice001";

    //private static final String TARGET_TOPIC = "command/com:iot:dummy:dummyMqttDevice001";

    private static ObjectMapper objectMapper;

    private static final int COMMAND_MESSAGE_LIMIT = 1000;

    private static final long COMMAND_DELAY_MS = 10000;

    public static void main(String [ ] args) {

        logger.info("MQTT Simple Command Generator Started ...");

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

            for(int i=0; i<COMMAND_MESSAGE_LIMIT; i++){

                MqttMessage msg = new MqttMessage(objectMapper.writeValueAsBytes(new CommandMessage(CommandMessage.COMMAND_TYPE_DEMO, System.currentTimeMillis(), new HashMap<String, Object>(){
                    {
                        put("test_key", "test");
                    }
                })));

                msg.setQos(0);
                msg.setRetained(false);
                client.publish(TARGET_TOPIC,msg);

                logger.debug("Command Correctly Published ! Topic: {}", TARGET_TOPIC);

                Thread.sleep(COMMAND_DELAY_MS);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
