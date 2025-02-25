package com.example.newquiz.common.util;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;

public class ResourceLoader {

    public static String getResourceContent(String resourcePath) {
        try {
            var resource = new ClassPathResource(resourcePath);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.NOT_FOUND);
        }
    }
}

