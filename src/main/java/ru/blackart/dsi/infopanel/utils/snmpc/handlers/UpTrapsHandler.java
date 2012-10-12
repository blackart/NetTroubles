package ru.blackart.dsi.infopanel.utils.snmpc.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.device.DeviceManager;
import ru.blackart.dsi.infopanel.services.DevcapsuleService;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.services.UserService;
import ru.blackart.dsi.infopanel.utils.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataImpl;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;
import ru.blackart.dsi.infopanel.utils.snmpc.storage.Storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class UpTrapsHandler extends Thread {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private RequestDataImpl requestData;
    private Storage storage;
    private ThreadPoolExecutor threadPoolExecutor;

    private DeviceManager deviceManager = DeviceManager.getInstance();
    private DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();
    private DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    private UserService userService = UserService.getInstance();
    private TroubleService troubleService = TroubleService.getInstance();
    private TroubleListService troubleListService = TroubleListService.getInstance();
    private TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();

    Date upDate;

    public UpTrapsHandler(RequestDataImpl requestData, Storage storage, ThreadPoolExecutor threadPoolExecutor) { //Конструктор потока
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

    private void devcapsuleLevelOperations(Devcapsule devcapsule) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        Date downDate = new Date(Long.valueOf(devcapsule.getTimedown()));

        if (downDate.before(upDate)) {
            if ((upDate.getTime() - downDate.getTime()) / (60 * 1000) >= this.storage.getTrueDownInterval()) {
                devcapsule.setTimeup(String.valueOf(upDate.getTime()));
                devcapsule.setComplete(true);

                synchronized (this.devcapsuleService) {
                    this.devcapsuleService.update(devcapsule);
                }

                Trouble trouble = this.dataModelConstructor.getTroubleForDevcapsule(devcapsule);

                boolean move = true;
                for (Devcapsule d_c : trouble.getDevcapsules()) {
                    move = d_c.getComplete() && move;
                }

                if (move) {
                    trouble.setClose(true);
                    trouble.setDate_out(String.valueOf(upDate.getTime()));
                    if ((trouble.getTimeout() == null) || (trouble.getTimeout().trim().equals(""))) {
                        trouble.setTimeout(String.valueOf(upDate.getTime()));
                    }

                    synchronized (this.troubleService) {
                        this.troubleService.update(trouble);
                    }

                    TroubleList targetTrList = this.dataModelConstructor.getTargetTroubleListForTrouble(trouble);
                    TroubleList sourceTrList = this.dataModelConstructor.getTroubleListForTrouble(trouble);
                    this.dataModelConstructor.moveTroubleList(trouble, sourceTrList, targetTrList);

                    synchronized (this.troubleListService) {
                        this.troubleListService.update(targetTrList);
                        this.troubleListService.update(sourceTrList);
                    }

                    if (trouble.getCrm()) {
                        CrmTrouble crmTrouble = new CrmTrouble(trouble, "2");
                        crmTrouble.send();
                    }
                }

            } else {
                Trouble trouble = this.dataModelConstructor.getTroubleForDevcapsule(devcapsule);

                int index = -1;
                for (Devcapsule d : trouble.getDevcapsules()) {
                    if (d.getId() == devcapsule.getId()) {
                        index = trouble.getDevcapsules().indexOf(d);
                    }
                }

                trouble.getDevcapsules().remove(index);

                if (trouble.getDevcapsules().size() > 0) {
                    synchronized (troubleService) {
                        troubleService.update(trouble);
                    }
                } else {
                    TroubleList troubleList = this.dataModelConstructor.getTroubleListForTrouble(trouble);
                    index = -1;
                    for (Trouble t : troubleList.getTroubles()) {
                        if (t.getId() == trouble.getId()) {
                            index = troubleList.getTroubles().indexOf(t);
                        }
                    }
                    if (trouble.getCrm()) {
                        CrmTrouble crmTrouble = new CrmTrouble(trouble, "3");
                        crmTrouble.send();
                    }
                    troubleList.getTroubles().remove(index);

                    synchronized (troubleListService) {
                        troubleListService.update(troubleList);
                    }
                    synchronized (troubleService) {
                        troubleService.delete(trouble);
                    }
                    synchronized (devcapsuleService) {
                        devcapsuleService.delete(devcapsule);
                    }
                }
            }
        }
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
        synchronized (dataModelConstructor) {
            Device device = this.deviceLevelOperations();
            this.upDate = this.parse(this.requestData.getDate(), this.requestData.getTime());
            List<Devcapsule> devcapsules = dataModelConstructor.sortDevcapsuleByTime(dataModelConstructor.getDevcWithOpenUpDateForDevice(device));

            if (devcapsules.size() > 0) {
                for (Devcapsule devcapsule : devcapsules) {
                    this.devcapsuleLevelOperations(devcapsule);
                }
            } else if (devcapsules.size() == 0) {
                if (this.storage.getUpDevcList().containsKey(device.getName())) {
                    RequestDataImpl requestData = this.storage.getUpDevcList().get(device.getName());
                    Date upDateStore = this.parse(requestData.getDate(), requestData.getTime());
                    if (upDateStore.before(upDate)) {
                        this.storage.getUpDevcList().put(device.getName(), this.requestData);
                    }
                } else {
                    this.storage.getUpDevcList().put(device.getName(), this.requestData);
                }
            }
        }
    }
}
