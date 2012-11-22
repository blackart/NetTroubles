package ru.blackart.dsi.infopanel.filters;

import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

public class AdminHtmlPageAccessRule extends RewriteRule {
    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response){
        String uri = request.getRequestURI();

        boolean htmlAdminPage = Pattern.matches("^/admin/admin.html$", uri);

        if (htmlAdminPage) {
            return new AdminHtmlPageAccessMatch();
        }

        return null;
    }
}
