package ru.blackart.dsi.infopanel.temp;

import antlr.collections.Enumerator;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Region;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: blackart
 * Date: 11.07.11
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class TestRegionDevice {
    public static void main(String args[]) {
//        List<Device> devices = new ArrayList<Device>();

        String[] sw = new String[3];
        sw[0] = "sw1";
        sw[1] = "sw1-ang";
        sw[2] = "sw1-usolie";

        List<Region> regions = new ArrayList<Region>();
        Region r1 = new Region(0,"Иркутск","");
        Region r2 = new Region(0,"Ангарск","-ang");
        Region r3 = new Region(0,"Усолье","-usolie");

        regions.add(r1);
        regions.add(r2);
        regions.add(r3);

        Properties regions_all = new Properties();
        for (Region r : regions) {
            regions_all.setProperty(r.getName(),".*(" + r.getPrefix() + ")" );
        }

        for (int i=0; i < sw.length; i++) {
            Boolean irk = true;
            for (Enumeration en = regions_all.keys(); en.hasMoreElements();) {
                String region_ = en.nextElement().toString();
                if (Pattern.matches(regions_all.getProperty(region_), sw[i]) && (!region_.equals("Иркутск"))) {
                    System.out.println(region_ + " - " + regions_all.getProperty(region_) + " - " + sw[i]);
                    irk = false;
                }
            }
            if (irk) System.out.println("Иркутск" + " - " + regions_all.getProperty("Иркутск") + " - " + sw[i]);
        }
    }
}
