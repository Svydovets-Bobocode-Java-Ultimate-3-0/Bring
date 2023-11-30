package com.bobocode.svydovets.source.web;

import svydovets.web.annotation.GetMapping;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestMapping;
import svydovets.web.annotation.RequestParam;
import svydovets.web.annotation.RestController;

@RestController("simpleContr")
@RequestMapping("/simple")
public class SimpleRestController {

    @GetMapping("/hello/{name}")
    public void helloPath(@PathVariable String name) {
    System.out.println(name);
    }

    @GetMapping("/goodbye/{name}")
    public void goodbyePath(@PathVariable("bye") String goodbye) {
        System.out.println(goodbye);
    }

    @GetMapping("/hello")
    public void helloRequest(@RequestParam String name) {
        System.out.println(name);
    }

    @GetMapping("/goodbye")
    public void goodbyeRequest(@RequestParam("bye") String goodbye) {
        System.out.println(goodbye);
    }

}
