package it.gov.pagopa.tkm.ms.consentmanager.config;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import org.slf4j.MDC;
import org.springframework.core.annotation.*;
import org.springframework.lang.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@Component
@Order(1)
public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String uuid = UUID.randomUUID().toString();
            MDC.put("MDC", "[Request-Id: " + uuid + "]");
            servletResponse.setHeader(ApiParams.REQUEST_ID_HEADER, uuid);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }

}
