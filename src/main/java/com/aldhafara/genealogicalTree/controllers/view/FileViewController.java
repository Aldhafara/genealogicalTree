package com.aldhafara.genealogicalTree.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/file")
public class FileViewController {

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }
}
