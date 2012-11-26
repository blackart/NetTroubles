package ru.blackart.dsi.infopanel.filters;

import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

public class AdminURLAccessRule extends RewriteRule {
    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();

        boolean admin = Pattern.matches("^/admin.*$", uri);

        HttpSession session = request.getSession(true);
        Boolean login = (Boolean) session.getAttribute("login");

        if (admin && ((login == null) || !login)) {
            return new AdminURLAccessMatch();
        }

        return null;
    }
}