package it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource.DummyStringResource;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource.ResourceDataListener;
import it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource.SmartObjectResource;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Author: Marco Picone, Ph.D. (picone.m@gmail.com)
 * Date: 24/04/2020
 * Project: MQTT BOT Smart Object (mqtt-bot-smartobject)
 */
public class DemoMqttSmartObject implements IMqttSmartObjectDevice {

    private static final Logger logger = LoggerFactory.getLogger(DemoMqttSmartObject.class);

    private static final String TELEMETRY_TOPIC = "resource";

    private static final String EVENT_TOPIC = "event";

    private static final String CONTROL_TOPIC = "control";

    private static final String COMMAND_TOPIC = "command";

    private final ObjectMapper objectMapper;

    private MqttSmartObjectConfiguration smartObjectConfiguration;

    private IMqttClient mqttClient;

    private String deviceId;

    private Map<String, SmartObjectResource<?>> resourceMap;

    private String deviceBasicTopic;

    public DemoMqttSmartObject(){
        //Jackson Object Mapper + Ignore Null Fields in order to properly generate the SenML Payload
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void init(MqttSmartObjectConfiguration smartObjectConfiguration,
                     IMqttClient mqttClient,
                     String deviceId,
                     String baseTopic,
                     Map<String, SmartObjectResource<?>> resourceMap) {

        this.smartObjectConfiguration = smartObjectConfiguration;
        this.mqttClient = mqttClient;
        this.deviceId = deviceId;
        this.resourceMap = resourceMap;
        this.deviceBasicTopic = baseTopic;

        logger.info("Smart Object correctly initialized ! Resource Number: {}", this.resourceMap.keySet().size());
    }

    @Override
    public void start() {

        try {

            if(this.smartObjectConfiguration != null &&
                    this.mqttClient != null &&
                    this.deviceId != null &&
                    this.resourceMap != null &&
                    this.deviceBasicTopic != null){

                logger.info("Waiting {} ms before starting ...", this.smartObjectConfiguration.getStartUpDelayMs()) ;

                Thread.sleep(this.smartObjectConfiguration.getStartUpDelayMs());

                subscribeToIncomingCommands(String.format("%s/%s", COMMAND_TOPIC, deviceId));

                //Register to listen on available resources
                this.resourceMap.entrySet().forEach(resourceEntry -> {
                    if(resourceEntry != null){

                        //ADD LISTENER FOR GPS AND BATTERY RESOURCES
                        if(resourceEntry.getValue().getType().equals(DummyStringResource.RESOURCE_TYPE)){

                            DummyStringResource dummyStringResource = (DummyStringResource)resourceEntry.getValue();
                            dummyStringResource.addDataListener(new ResourceDataListener<String>() {
                                @Override
                                public void onDataChanged(SmartObjectResource<String> resource, String updatedValue) {
                                    try {
                                        String topic = String.format("%s/%s/%s", deviceBasicTopic, TELEMETRY_TOPIC, resourceEntry.getKey());
                                        publishTelemetryData(topic, updatedValue);
                                        logger.debug("Published data on topic: {} -> Value: {}", topic, updatedValue);
                                    } catch (MqttException e) {
                                        logger.error("Error Sending Location Update ! Msg: {}", e.getLocalizedMessage());
                                    }
                                }
                            });
                        }
                    }
                });
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        //TODO Implement a proper stop ... :)
    }

    private void subscribeToIncomingCommands(String targetTopic){

        try{

            mqttClient.subscribe(targetTopic, (topic, msg) -> {
                byte[] payload = msg.getPayload();
                logger.info("Command Message Received -> Topic: {} - Payload: {}", topic, new String(payload));
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void publishTelemetryData(String topic, String stringPayload) throws MqttException {

        //logger.info("Publishing to Topic: {} Smart Object: {}", topic, stringPayload);

        if (mqttClient.isConnected() && stringPayload != null && topic != null) {

            MqttMessage msg = new MqttMessage(stringPayload.getBytes());
            msg.setQos(this.smartObjectConfiguration.getMqttOutgoingClientQoS());

            mqttClient.publish(topic,msg);

            //logger.info("Data Correctly Published to topic: {}", topic);
        }
        else{
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
        }

    }

    public void setMqttClient(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public IMqttClient getMqttClient() {
        return mqttClient;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}