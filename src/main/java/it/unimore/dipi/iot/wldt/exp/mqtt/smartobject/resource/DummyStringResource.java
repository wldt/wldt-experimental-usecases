package it.unimore.dipi.iot.wldt.exp.mqtt.smartobject.resource;


import it.unimore.dipi.iot.wldt.exp.utils.StringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project edt-sdn-experiments
 * @created 04/11/2020 - 14:39
 */
public class DummyStringResource extends SmartObjectResource<String> {

    private static final Logger logger = LoggerFactory.getLogger(DummyStringResource.class);

    private static final int DUMMY_STRING_LENGHT = 10;

    private static final long DEFAULT_UPDATE_PERIOD = 200; //5 Seconds

    private static final long TASK_DELAY_TIME = 5000; //Seconds before starting the periodic update task

    public static final String RESOURCE_TYPE = "iot:resource:dummy:string";

    private String updatedStringLevel;

    private Timer updateTimer = null;

    private long updatePeriod;

    public DummyStringResource(long updatePeriod) {
        //super(UUID.randomUUID().toString(), DummyStringResource.RESOURCE_TYPE);
        super("rs1", DummyStringResource.RESOURCE_TYPE);
        init(updatePeriod);
    }

    public DummyStringResource() {
        super(UUID.randomUUID().toString(), DummyStringResource.RESOURCE_TYPE);
        init(DEFAULT_UPDATE_PERIOD);
    }

    public DummyStringResource(String id, String type) {
        super(id, type);
        init(DEFAULT_UPDATE_PERIOD);
    }

    /**
     * Init internal random battery level in th range [MIN_BATTERY_LEVEL, MAX_BATTERY_LEVEL]
     */
    private void init(long updatePeriod){
        try{
            this.updatePeriod = updatePeriod;
            this.updatedStringLevel = String.format("%d:%s", System.currentTimeMillis(), StringGenerator.generateRandomAlphanumericString(DUMMY_STRING_LENGHT));
            startPeriodicEventValueUpdateTask();
        }catch (Exception e){
            logger.error("Error init Battery Resource Object ! Msg: {}", e.getLocalizedMessage());
        }
    }

    private void startPeriodicEventValueUpdateTask(){

        try{

            logger.info("Starting periodic Update Task with Period: {} ms -> Rate: {} [msg/sec]", updatePeriod, (1000/updatePeriod));

            this.updateTimer = new Timer();
            this.updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updatedStringLevel = String.format("%d:%s", System.currentTimeMillis(), StringGenerator.generateRandomAlphanumericString(DUMMY_STRING_LENGHT));
                    notifyUpdate(updatedStringLevel);
                }
            }, TASK_DELAY_TIME, updatePeriod);

        }catch (Exception e){
            logger.error("Error executing periodic resource value ! Msg: {}", e.getLocalizedMessage());
        }
    }

    @Override
    public String loadUpdatedValue() {
        return this.updatedStringLevel;
    }

    public long getUpdatePeriod() {
        return updatePeriod;
    }

    public void setUpdatePeriod(long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public static void main(String[] args) {

        DummyStringResource dummyStringResource = new DummyStringResource();
        logger.info("New {} Resource Created with Id: {} ! String Value: {}",
                dummyStringResource.getType(),
                dummyStringResource.getId(),
                dummyStringResource.loadUpdatedValue());

        //Add Resource Listener
        dummyStringResource.addDataListener(new ResourceDataListener<String>() {
            @Override
            public void onDataChanged(SmartObjectResource<String> resource, String updatedValue) {
                if(resource != null && updatedValue != null)
                    logger.info("Device: {} -> New String Value Received: {}", resource.getId(), updatedValue);
                else
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
            }
        });
    }

}
