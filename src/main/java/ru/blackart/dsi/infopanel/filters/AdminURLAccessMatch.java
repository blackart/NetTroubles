package ru.blackart.dsi.infopanel.filters;

import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminURLAccessMatch extends RewriteMatch {
    @Override
    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Boolean login = (Boolean) session.getAttribute("login");

        if (login != null) {
            if (login) {
                request.getRequestDispatcher("/admin/admin.html").forward(request, response);
            } else {
                response.sendRedirect("/login-new");
            }
        } else {
            response.sendRedirect("/login-new");
        }

        return true;
    }
}
