package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.services.ServiceService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.DateStr;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.util.ArrayList;
import java.util.List;

public class EditTroubleOfCurrentTroubleList extends AbstractCommand {
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    ServiceService serviceService = ServiceService.getInstance();

    public String execute() throws Exception {
        /*-------------------------------------------------------------------------------------------*/
        String[] services = null;
        String services_str = this.getRequest().getParameter("service").trim().replace(" ", "");
        if ((services_str != null) && (!services_str.equals(""))) {
            services = this.getRequest().getParameter("service").trim().replace(" ", "").split(";");
        }

        int id = Integer.valueOf(this.getRequest().getParameter("id"));
        String title = this.getRequest().getParameter("title").trim();
        String actual_problem = this.getRequest().getParameter("actual_problem").replace("&nbsp;","").trim();
        String timeout_str = this.getRequest().getParameter("timeout");
        /*-------------------------------------------------------------------------------------------*/

        synchronized (dataModelConstructor) {
            Trouble trouble = dataModelConstructor.getTroubleForId(id);

            String timeout = ((trouble.getTimeout() == null) || (trouble.getTimeout().trim().equals(""))) ? null : trouble.getTimeout() ;
            if ((timeout_str != null) && (!timeout_str.equals(""))) {
                String[] timeout_arr = timeout_str.split(" ");
                timeout = String.valueOf(DateStr.parse(timeout_arr[0], timeout_arr[1]).getTime());
            }

            trouble.setTitle(title);
            trouble.setActualProblem(actual_problem);
            trouble.setTimeout(timeout);
            trouble.setAuthor((User) this.getSession().getAttribute("info"));
            trouble.setCrm(false);

            if ((services != null) && (services.length > 0)) {
                synchronized (serviceService) {
                    List<Service> service_ = new ArrayList<Service>();
                    for (int i = 0; i < services.length; i++) {
                        if (!services[i].equals("")) {
                            Service service = serviceService.getService(Integer.valueOf(services[i]));
                            service_.add(service);
                        }
                    }
                    trouble.setServices(service_);
                }
            }
            synchronized (troubleService) {
                troubleService.update(trouble);
            }
        }

        return null;
    }
}
