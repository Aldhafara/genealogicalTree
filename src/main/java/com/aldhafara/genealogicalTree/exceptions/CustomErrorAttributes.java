package com.aldhafara.genealogicalTree.exceptions;

import com.aldhafara.genealogicalTree.configuration.CorrelationIdFilter;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest,
                                                  ErrorAttributeOptions options) {
        Map<String, Object> attributes = super.getErrorAttributes(webRequest, options);

        Object correlationId = webRequest.getAttribute(CorrelationIdFilter.CORRELATION_ID_REQUEST_ATTRIBUTE, WebRequest.SCOPE_REQUEST);
        if (correlationId != null) {
            attributes.put("requestId", correlationId);
        }

        return attributes;
    }
}
