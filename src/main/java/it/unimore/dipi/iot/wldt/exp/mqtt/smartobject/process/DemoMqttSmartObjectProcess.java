package it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.unimore.dipi.iot.wldt.exp.exception.MqttSmartObjectConfigurationException;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.device.DemoMqttSmartObject;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.device.MqttSmartObjectConfiguration;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource.DummyStringResource;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource.SmartObjectResource;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.HashMap;

/**
 * Author: Marco Picone, Ph.D. (picone.m@gmail.com)
 * Date: 24/04/2020
 * Project: MQTT BOT Smart Object (mqtt-bot-smartobject)
 */
public class DemoMqttSmartObjectProcess {

    private static final Logger logger = LoggerFactory.getLogger(DemoMqttSmartObjectProcess.class);

    private static final String TAG = "[MQTT-SMARTOBJECT]";

    private static final String MQTT_SMARTOBJECT_CONFIGURATION_FILE = "mqtt_so_conf.yaml";

    private static MqttSmartObjectConfiguration mqttSmartObjectConfiguration;

    public static void main(String[] args) {

        try {

            logger.info("MQTT SmartObject Started !");

            readConfigurationFile();
            startSmartObject();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void startSmartObject(){

        try{

            //Generate a random device ID
            //String uuid = UUID.randomUUID().toString();
            String uuid = "dummyMqttDevice001";
            String deviceId = String.format("%s:%s", mqttSmartObjectConfiguration.getDeviceNameSpace(), uuid);

            logger.info("DEVICE ID: {}", deviceId);

            //Create Mqtt Client with a Memory Persistence
            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d",
                    mqttSmartObjectConfiguration.getMqttBrokerAddress(),
                    mqttSmartObjectConfiguration.getMqttBrokerPort()),
                    deviceId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            mqttClient.connect(options);

            logger.info("MQTT Client Connected ! Client Id: {}", deviceId);

            DemoMqttSmartObject vehicleMqttSmartObject = new DemoMqttSmartObject();
            vehicleMqttSmartObject.init(mqttSmartObjectConfiguration,
                    mqttClient,
                    deviceId,
                    String.format("%s/%s", mqttSmartObjectConfiguration.getBasicTopic(), deviceId),
                    new HashMap<String, SmartObjectResource<?>>(){
                        {
                            put("dummy_string_resource", new DummyStringResource(mqttSmartObjectConfiguration.getUpdatePeriodMs()));
                        }
                    });

            vehicleMqttSmartObject.start();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void readConfigurationFile() throws MqttSmartObjectConfigurationException {

        try{

            //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            //File file = new File(classLoader.getResource(WLDT_CONFIGURATION_FILE).getFile());
            File file = new File(MQTT_SMARTOBJECT_CONFIGURATION_FILE);

            ObjectMapper om = new ObjectMapper(new YAMLFactory());

            mqttSmartObjectConfiguration = om.readValue(file, MqttSmartObjectConfiguration.class);

            logger.info("{} MQTT Configuration Loaded ! Conf: {}", TAG, mqttSmartObjectConfiguration);

        }catch (Exception e){
            e.printStackTrace();
            String errorMessage = String.format("ERROR LOADING CONFIGURATION FILE ! Error: %s", e.getLocalizedMessage());
            logger.error("{} {}", TAG, errorMessage);
            throw new MqttSmartObjectConfigurationException(errorMessage);
        }
    }

}
