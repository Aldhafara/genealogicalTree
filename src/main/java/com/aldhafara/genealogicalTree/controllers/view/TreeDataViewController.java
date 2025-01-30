package com.aldhafara.genealogicalTree.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping(value = "/family-tree")
public class TreeDataViewController {

    @GetMapping()
    public String getInitialTreeStructure() {
        return "tree";
    }

    @GetMapping("/{id}")
    public String getTreeStructure(@PathVariable UUID id, Model model) {
        model.addAttribute("initialUuid", id);
        return "tree";
    }
}
