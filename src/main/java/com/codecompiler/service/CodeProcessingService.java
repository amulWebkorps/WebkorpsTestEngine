package com.codecompiler.service;

import com.codecompiler.entity.ResponseToFE;

import java.io.IOException;
import java.util.Map;

public interface CodeProcessingService {

    public ResponseToFE compileCode(Map<String, Object> data) throws IOException;


}
