package ru.blackart.dsi.infopanel.commands.troubles;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.ServiceService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.DateStr;
import ru.blackart.dsi.infopanel.view.TroubleView;

import java.util.ArrayList;
import java.util.List;

public class EditTrouble extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    ServiceService serviceService = ServiceService.getInstance();

    public String execute() throws Exception {
        String troubleJSON = this.getRequest().getParameter("trouble");

        Gson gson = new Gson();
        TroubleView troubleView = gson.fromJson(troubleJSON, TroubleView.class);

        synchronized (dataModel) {
            Trouble trouble = dataModel.getTroubleForId(troubleView.getId());

            String timeout;
            if (!troubleView.getTimeout().equals("")) {
                String[] timeout_arr = troubleView.getTimeout().split(" ");
                timeout = String.valueOf(DateStr.parse(timeout_arr[0], timeout_arr[1]).getTime());
            } else {
                timeout = null;
            }

            trouble.setTitle(troubleView.getTitle());
            trouble.setActualProblem(troubleView.getActualProblem());
            trouble.setTimeout(timeout);
            trouble.setAuthor((User) this.getSession().getAttribute("info"));
            trouble.setCrm(false);

            synchronized (serviceService) {
                List<Service> services_ = new ArrayList<Service>();
                int[] services = troubleView.getServices();

                for (int i=0; i < services.length; i++) {
                    Service service = serviceService.getService(services[i]);
                    services_.add(service);
                }
                trouble.setServices(services_);
            }

            synchronized (troubleService) {
                troubleService.update(trouble);
            }
        }

        return null;
    }
}
