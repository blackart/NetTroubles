package ru.blackart.dsi.infopanel.filters;

import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminURLAccessMatch extends RewriteMatch {
    @Override
    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("/login-new");
        return true;
    }
}
