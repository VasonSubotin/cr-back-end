package com.sm.client.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GUIController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="title", required=false, defaultValue="World") String title,
                           @RequestParam(name="content", required=false, defaultValue="World") String content,
                           Model model) {
        model.addAttribute("title", title);
        return "greeting";
    }

}
