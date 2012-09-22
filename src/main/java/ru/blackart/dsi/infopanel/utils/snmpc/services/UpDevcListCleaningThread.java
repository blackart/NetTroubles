package ru.blackart.dsi.infopanel.utils.snmpc.services;

import ru.blackart.dsi.infopanel.utils.snmpc.storage.Storage;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataImpl;

import java.util.*;

public class UpDevcListCleaningThread implements Runnable {

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
            Storage storage = ManagerUpDevcListCleaningThread.getInstance().getStorage();
            HashMap<String, RequestDataImpl> upDevcList = storage.getUpDevcList();

            Date now_date = new Date();

            Set<String> keySet = new HashSet<String>();
            keySet.addAll(upDevcList.keySet());
            for (String aKeySet : keySet) {
                String nextKey = aKeySet;
                RequestDataImpl requestData = upDevcList.get(nextKey);
                Date seletUpTime = this.parse(requestData.getDate(), requestData.getTime());
                if (((now_date.getTime() - seletUpTime.getTime()) / (60 * 1000)) > 30) {
                    upDevcList.remove(nextKey);
                }
            }

            try {
                Thread.currentThread().sleep(20000);    //900000 == 15 минут
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
