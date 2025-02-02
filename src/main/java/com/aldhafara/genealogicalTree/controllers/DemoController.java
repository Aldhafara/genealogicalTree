package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.services.DemoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping
    public String index() {
        return "demo";
    }

    @GetMapping("/loadDuckTales")
    public String loadDuckTalesDemo() {
        UUID id = demoService.loadDuckTalesDemo();
        return "redirect:/family-tree/" + id;
    }

}
