package ru.blackart.dsi.infopanel.filters;

import javax.servlet.*;
import java.io.IOException;

public class AdminAccess implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
