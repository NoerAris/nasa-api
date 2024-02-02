package com.nasa.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nasa")
public class NasaController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(NasaController.class);

}
