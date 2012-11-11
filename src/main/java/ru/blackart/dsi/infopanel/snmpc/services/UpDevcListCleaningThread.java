package ru.blackart.dsi.infopanel.snmpc.services;

import ru.blackart.dsi.infopanel.snmpc.storage.Storage;
import ru.blackart.dsi.infopanel.snmpc.transport.RequestDataImpl;

import java.util.*;

public class UpDevcListCleaningThread implements Runnable {
    ManagerUpDevcListCleaningThread managerUpDevcListCleaningThread = ManagerUpDevcListCleaningThread.getInstance();
    Storage storage = managerUpDevcListCleaningThread.getStorage();

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
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (storage) {
                HashMap<String, RequestDataImpl> upDevcList = storage.getUpDevcList();

                Long now_date = System.currentTimeMillis();

                Set<String> keySet = new HashSet<String>();
                keySet.addAll(upDevcList.keySet());
                for (String aKeySet : keySet) {
                    RequestDataImpl requestData = upDevcList.get(aKeySet);
                    Date seletUpTime = this.parse(requestData.getDate(), requestData.getTime());
                    if (((now_date - seletUpTime.getTime()) / (60 * 1000)) > 30) {
                        upDevcList.remove(aKeySet);
                    }
                }

                try {
                    Thread.currentThread().sleep(20000);    //900000 == 15 минут
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
