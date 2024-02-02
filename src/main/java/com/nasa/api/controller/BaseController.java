package com.nasa.api.controller;

import com.nasa.api.service.NasaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
    @Autowired
    protected NasaService nasaService;
}
