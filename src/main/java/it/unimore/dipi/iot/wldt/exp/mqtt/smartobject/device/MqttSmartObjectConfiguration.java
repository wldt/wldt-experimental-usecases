package it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.device;

/**
 * Author: Marco Picone, Ph.D. (picone.m@gmail.com)
 * Date: 24/04/2020
 * Project: MQTT BOT Smart Object (mqtt-bot-smartobject)
 */
public class MqttSmartObjectConfiguration {

    private String deviceUUID;

    private String mqttBrokerAddress;

    private int mqttBrokerPort;

    private String deviceNameSpace;

    private int startUpDelayMs = 5000;

    private int mqttOutgoingClientQoS = 0;

    private String basicTopic;

    private long updatePeriodMs;

    public MqttSmartObjectConfiguration() {
    }

    public MqttSmartObjectConfiguration(String deviceUUID, String mqttBrokerAddress, int mqttBrokerPort, String deviceNameSpace, int startUpDelayMs, int mqttOutgoingClientQoS, String basicTopic, long updatePeriodMs) {
        this.deviceUUID = deviceUUID;
        this.mqttBrokerAddress = mqttBrokerAddress;
        this.mqttBrokerPort = mqttBrokerPort;
        this.deviceNameSpace = deviceNameSpace;
        this.startUpDelayMs = startUpDelayMs;
        this.mqttOutgoingClientQoS = mqttOutgoingClientQoS;
        this.basicTopic = basicTopic;
        this.updatePeriodMs = updatePeriodMs;
    }

    public String getMqttBrokerAddress() {
        return mqttBrokerAddress;
    }

    public void setMqttBrokerAddress(String mqttBrokerAddress) {
        this.mqttBrokerAddress = mqttBrokerAddress;
    }

    public int getMqttBrokerPort() {
        return mqttBrokerPort;
    }

    public void setMqttBrokerPort(int mqttBrokerPort) {
        this.mqttBrokerPort = mqttBrokerPort;
    }

    public String getDeviceNameSpace() {
        return deviceNameSpace;
    }

    public void setDeviceNameSpace(String deviceNameSpace) {
        this.deviceNameSpace = deviceNameSpace;
    }

    public int getStartUpDelayMs() {
        return startUpDelayMs;
    }

    public void setStartUpDelayMs(int startUpDelayMs) {
        this.startUpDelayMs = startUpDelayMs;
    }

    public int getMqttOutgoingClientQoS() {
        return mqttOutgoingClientQoS;
    }

    public void setMqttOutgoingClientQoS(int mqttOutgoingClientQoS) {
        this.mqttOutgoingClientQoS = mqttOutgoingClientQoS;
    }

    public String getBasicTopic() {
        return basicTopic;
    }

    public void setBasicTopic(String basicTopic) {
        this.basicTopic = basicTopic;
    }

    public long getUpdatePeriodMs() {
        return updatePeriodMs;
    }

    public void setUpdatePeriodMs(long updatePeriodMs) {
        this.updatePeriodMs = updatePeriodMs;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MqttSmartObjectConfiguration{");
        sb.append("deviceUUID='").append(deviceUUID).append('\'');
        sb.append(", mqttBrokerAddress='").append(mqttBrokerAddress).append('\'');
        sb.append(", mqttBrokerPort=").append(mqttBrokerPort);
        sb.append(", deviceNameSpace='").append(deviceNameSpace).append('\'');
        sb.append(", startUpDelayMs=").append(startUpDelayMs);
        sb.append(", mqttOutgoingClientQoS=").append(mqttOutgoingClientQoS);
        sb.append(", basicTopic='").append(basicTopic).append('\'');
        sb.append(", updatePeriodMs=").append(updatePeriodMs);
        sb.append('}');
        return sb.toString();
    }
}
