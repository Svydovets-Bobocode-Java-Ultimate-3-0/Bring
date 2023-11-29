package com.bobocode.svydovets.source.web;

import svydovets.web.annotation.GetMapping;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestMapping;
import svydovets.web.annotation.RestController;

@RestController("simpleContr")
@RequestMapping("/simple")
public class SimpleRestController {

    @GetMapping("/hello/{name}")
    public void hello(@PathVariable String name) {
    System.out.println(name);
    }

}
