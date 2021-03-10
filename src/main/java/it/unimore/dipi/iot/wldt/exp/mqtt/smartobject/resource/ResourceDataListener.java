package it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project edt-sdn-experiments
 * @created 04/11/2020 - 15:39
 */
public interface ResourceDataListener<T> {

    public void onDataChanged(SmartObjectResource<T> resource, T updatedValue);

}
