package ru.blackart.dsi.infopanel.utils.snmpc.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.*;
import ru.blackart.dsi.infopanel.services.DeviceManager;
import ru.blackart.dsi.infopanel.services.DevcapsuleService;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataImpl;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;
import ru.blackart.dsi.infopanel.utils.snmpc.storage.Storage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class DownTrapsHandler extends Thread {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private RequestDataImpl requestData;
    private Storage storage;

    private DeviceManager deviceManager = DeviceManager.getInstance();
    private DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();
    private DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    private AccessService accessService = AccessService.getInstance();
    private TroubleService troubleService = TroubleService.getInstance();
    private TroubleListService troubleListService = TroubleListService.getInstance();
    private TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();

    private Device device;
    private Devcapsule devcapsule;
    private Trouble trouble;
    private ThreadPoolExecutor threadPoolExecutor;

    public DownTrapsHandler(RequestDataImpl requestData, Storage storage, ThreadPoolExecutor threadPoolExecutor) { //Конструктор потока
        this.requestData = requestData;                                                 //передаём потоку данные запроса
        this.storage = storage;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    private Device deviceLevelOperations() {
        Device device = null;

        synchronized (deviceManager) {
            device = deviceManager.getDevice(this.requestData.getDevice());
            if (device == null) {                                   //если даннх о узле в DB нет, записываем.
                deviceManager.addNewDevice(this.requestData.getDevice(), this.requestData.getGroup(), this.requestData.getDesc());
//                log.info("Save info about new device - " + device);
            } else {                                                //если данные о узле в DB есть
                if (true) {                                         //и включен режим обучения
                    //сохраняем новые данные в DB и возвращаем объект
                    deviceManager.updateDevice(device, this.requestData.getDevice(), this.requestData.getGroup(), this.requestData.getDesc());
//                    log.info("ENABLED MOD LEARN INFO DEVICE. Update info about new device - " + device);
                } else {                                            //если режим обучения не включен
//                    log.info("Get info about " + device + " from DB");
                }
            }
            device = deviceManager.getDevice(this.requestData.getDevice());
        }
        return device;
    }

    private Devcapsule devcapsuleLevelOperations() {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        Date downDate = this.parse(this.requestData.getDate(), this.requestData.getTime());
//        log.info("Saving devcapsule for " + device.getName() + " down date - " + format.format(downDate));

        synchronized (dataModelConstructor) {
    //        log.info("Find in DB devcapsule with + " + device.getName() + " and null timedown and not null timeup");
            List<Devcapsule> devcapsules = dataModelConstructor.getDevcWithOpenUpDateForDevice(device);
    //        log.info("Count devcapsule for device " + device.getName() + " - " + devcapsules.size());

            Boolean lock = false;
            Devcapsule return_devcapsule = null;

                if ((devcapsules != null) && (devcapsules.size() > 0)) {
        //            log.info("Sorting list of devcapsules for device " + device.getName());
                    devcapsules = dataModelConstructor.sortDevcapsuleByTime(devcapsules);    //Сортируем по убыванию
                    Devcapsule devcapsule = devcapsules.get(0);

                    Date downDate_last = new Date(Long.valueOf(devcapsule.getTimedown()));

                    if (downDate.before(downDate_last)) {
                        devcapsule.setTimedown(String.valueOf(downDate.getTime()));
                        synchronized (devcapsuleService)  {
                            devcapsuleService.update(devcapsule);
                        }
                        Trouble trouble = dataModelConstructor.getTroubleForDevcapsule(devcapsule);
                        Date time_down_min = new Date(Long.valueOf(trouble.getDate_in()));
                        for (Devcapsule devc : trouble.getDevcapsules()) {
                            Date time_down_another = new Date(Long.valueOf(devc.getTimedown()));
                            if (time_down_min.after(time_down_another)) {
                                time_down_min = time_down_another;
                            }
                        }
                        if (Long.valueOf(trouble.getDate_in()) > time_down_min.getTime()) {
                            trouble.setDate_in(String.valueOf(time_down_min.getTime()));
                            troubleService.update(trouble);
                        }
        //                log.info("Find devcapsule and update.");
                    }
                    lock = true;
                }

                if (!lock) {
                    return_devcapsule = new Devcapsule();
                    return_devcapsule.setDevice(device);
                    return_devcapsule.setComplete(false);
                    return_devcapsule.setTimedown(String.valueOf(downDate.getTime()));
                    synchronized (devcapsuleService)  {
                        devcapsuleService.save(return_devcapsule);
                    }
        //            log.info("Save devcapsule for device " + device.getName());
                }

                return return_devcapsule;
        }
    }

    private Trouble troubleLevelOperations() {
        User systemUser;
        synchronized (accessService) {
            systemUser = accessService.getUser("system");
        }

        Trouble trouble = new Trouble();
//        log.info("Create new trouble for device " + devcapsule.getDevice().getName());

        ArrayList<Devcapsule> devcapsules = new ArrayList<Devcapsule>();
        devcapsules.add(devcapsule);

        trouble.setDevcapsules(devcapsules);
        trouble.setTitle(devcapsule.getDevice().getName() + ", " + devcapsule.getDevice().getDescription());
        trouble.setDate_in(devcapsule.getTimedown());
        trouble.setAuthor(systemUser);
        trouble.setComments(new ArrayList<Comment>());
        trouble.setActualProblem("");
        trouble.setCrm(false);
        trouble.setClose(devcapsule.getComplete());

        synchronized (troubleService){
            troubleService.save(trouble);
        }

        return trouble;
    }

    private Date parse(String str_date, String str_time) {
        String[] str_date_mas = str_date.split("\\/");
        String[] str_time_mas = str_time.split(":");

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(str_time_mas[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(str_time_mas[1]));
        calendar.set(Calendar.SECOND, Integer.valueOf(str_time_mas[2]));

        calendar.set(Calendar.YEAR, Integer.valueOf(str_date_mas[2]));
        calendar.set(Calendar.MONTH, Integer.valueOf(str_date_mas[1]) - 1);
        calendar.set(Calendar.DATE, Integer.valueOf(str_date_mas[0]));

        return calendar.getTime();
    }

    public void run() {
//        log.info("Check device - " + this.requestData.getDevice() + " in store");
        this.device = this.deviceLevelOperations();
        if (this.device != null) this.devcapsule = this.devcapsuleLevelOperations();
        if (this.devcapsule != null) this.trouble = this.troubleLevelOperations();

        if (this.trouble != null) {
            synchronized (dataModelConstructor) {
                TroubleList troubleList = this.dataModelConstructor.getTargetTroubleListForTrouble(this.trouble);
                troubleList.getTroubles().add(this.trouble);

                troubleListsManager.sortTroubleList(troubleList);
                synchronized (troubleListService) {
                    troubleListService.update(troubleList);
                }
            }
        }

        if (this.storage.getUpDevcList().containsKey(this.device.getName())) {
            RequestDataImpl requestData = this.storage.getUpDevcList().get(device.getName());
            Date upDate = this.parse(requestData.getDate(), requestData.getTime());
            Date downDate = this.parse(this.requestData.getDate(), this.requestData.getTime());

            if (upDate.after(downDate)) {
                Thread exUpThread = new UpTrapsHandler(requestData, this.storage, this.threadPoolExecutor);
                threadPoolExecutor.execute(exUpThread);
                this.storage.getUpDevcList().remove(device.getName());
            }
        }
    }
}
