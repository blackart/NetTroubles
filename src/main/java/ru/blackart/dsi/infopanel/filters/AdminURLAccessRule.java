package ru.blackart.dsi.infopanel.filters;

import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

public class AdminURLAccessRule extends RewriteRule {
    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();

//        boolean oldAdmin = Pattern.matches("^/admin(/)?$", uri);
        boolean newAdmin = Pattern.matches("^/admin-new(/)?$", uri);

//        if (oldAdmin || newAdmin) {
        if (newAdmin) {
            return new AdminURLAccessMatch();
        }

        return null;
    }
}