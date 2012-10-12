package ru.blackart.dsi.infopanel.commands.device;

import com.myjavatools.xml.BasicXmlData;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.services.DevcapsuleService;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CheckStatusDevice extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String devs = this.getRequest().getParameter("devs");
        String[] ids = devs.split("\\|");

        DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();
        List<Devcapsule> devc_list = new ArrayList<Devcapsule>();

        synchronized (devcapsuleService) {
            for (int i = 0; i < ids.length; i++) {
                int id = -1;
                try {
                    id = Integer.valueOf(ids[i]);
                } catch (Exception e) {
                    continue;
                }
                Devcapsule devcapsule = devcapsuleService.getDevcapsule(id);
                devc_list.add(devcapsule);
            }
        }

        BasicXmlData xml = new BasicXmlData("device_message");

        for (Devcapsule devc : devc_list) {
            Device d = devc.getDevice();
            if (d.getHoststatus() == null) {
                BasicXmlData xml_level_1 = new BasicXmlData("device");
                xml_level_1.addKid(new BasicXmlData("id", String.valueOf(d.getId())));
                xml_level_1.addKid(new BasicXmlData("name", d.getName()));
                xml_level_1.addKid(new BasicXmlData("desc", d.getDescription()));
                xml_level_1.addKid(new BasicXmlData("group_id", String.valueOf(d.getHostgroup().getId())));
                xml_level_1.addKid(new BasicXmlData("region_id", String.valueOf(d.getRegion().getId())));
                xml.addKid(xml_level_1);
            }
        }

        OutputStream out = getResponse().getOutputStream();
        xml.save(out);

        return null;
    }
}
