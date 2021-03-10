package it.unimore.dipi.iot.wldt.exp.mqtt.command;

import java.util.Map;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project wldt-experimental-usecases
 * @created 10/03/2021 - 10:54
 */
public class CommandMessage {

    public static final String COMMAND_TYPE_DEMO = "demo_command";

    private String type;

    private long timestamp;

    private Map<String, Object> metadata;

    public CommandMessage() {
    }

    public CommandMessage(String type, long timestamp, Map<String, Object> metadata) {
        this.type = type;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    public static String getCommandTypeDemo() {
        return COMMAND_TYPE_DEMO;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommandMessage{");
        sb.append("type='").append(type).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", metadata=").append(metadata);
        sb.append('}');
        return sb.toString();
    }
}
