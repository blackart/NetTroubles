package ru.blackart.dsi.infopanel.filters;

import com.google.gson.Gson;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;
import ru.blackart.dsi.infopanel.services.AccessService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CustomRule extends RewriteRule {
    private Gson gson = new Gson();

    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();

        /**
         * Request for access to admin page
         */
        boolean adminPageAccess = Pattern.matches("^/admin.*$", uri);
        /**
         * Any request to controller
         */
        boolean controllerRequest = Pattern.matches("^/controller.*$", uri);


        HttpSession session = request.getSession(true);
        Boolean login = (Boolean) session.getAttribute("login");

        if ((login == null) || !login) {
            if (adminPageAccess) {
                return new AdminPageURLMatch();
            } else if (controllerRequest) {
                String requestCmd = request.getParameter("cmd");
                if (requestCmd == null) return new ControllerURLMatch();
                boolean redirect = true;

                synchronized (AccessService.getInstance()) {
                    AccessService accessService = AccessService.getInstance();
                    ArrayList<String> cmdArr = accessService.getNoLoginCmd();
                    for (String cmd : cmdArr) {
                        if (cmd.equals(requestCmd)) redirect = false;
                    }
                }

                if (redirect) return new ControllerURLMatch();
            }
        }

        return null;
    }
}