package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.ServiceService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.util.ArrayList;
import java.util.List;

public class EditTroubleOfCompleteTroubleList extends AbstractCommand {
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    ServiceService serviceService = ServiceService.getInstance();

    public String execute() throws Exception {
        //Parse incoming date. Format "DD/MM/YYYY hh/mm/ss" to array [0]DD/MM/YYYY and [1]hh/mm/ss/
        /*String date_time_in_str = this.getRequest().getParameter("date_in");
        String date_in = null;
        if ((date_time_in_str != null) && (!date_time_in_str.trim().equals("")) ) {
            String[] date_time_in = date_time_in_str.split(" ");
            date_in = String.valueOf(logEngine.parse(date_time_in[0],date_time_in[1]).getTime());
        }

        String date_time_out_str = this.getRequest().getParameter("date_out");
        String date_out = null;
        if ((date_time_out_str != null) && (!date_time_out_str.trim().equals(""))) {
            String[] date_time_out = date_time_out_str.split(" ");
            date_out = String.valueOf(logEngine.parse(date_time_out[0],date_time_out[1]).getTime());
        }

        String timeout_str = this.getRequest().getParameter("timeout");
        String timeout = null;
        if ((timeout_str != null) && (!timeout_str.equals("")) ) {
            String[] timeout_arr = timeout_str.split(" ");
            timeout = String.valueOf(logEngine.parse(timeout_arr[0],timeout_arr[1]).getTime());
        }*/

        //parse incoming value of service. incoming format " xxx ; yyy ; zzz ; " parse to array
        String[] services = null;
        String services_str = this.getRequest().getParameter("service").trim().replace(" ","");
        if ((services_str != null) && (!services_str.equals(""))) {
            services = this.getRequest().getParameter("service").trim().replace(" ","").split(";");
        }

        int id = Integer.valueOf(this.getRequest().getParameter("id"));
        String title = this.getRequest().getParameter("title").trim();
        String actual_problem = this.getRequest().getParameter("actual_problem").replace("&nbsp;","").trim();

        synchronized (dataModelConstructor) {
            Trouble trouble = dataModelConstructor.getTroubleForId(id);

            trouble.setTitle(title);
            trouble.setActualProblem(actual_problem);
            trouble.setAuthor((User) this.getSession().getAttribute("info"));

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