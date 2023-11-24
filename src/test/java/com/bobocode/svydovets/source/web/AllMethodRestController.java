package com.bobocode.svydovets.source.web;

import svydovets.web.annotation.*;

@RestController
@RequestMapping("/method")
public class AllMethodRestController {

    @GetMapping("/get")
    public void doGetMethod() {
    System.out.println("doGetMethod");
    }

    @PostMapping("/post")
    public void doPostMethod() {
        System.out.println("doPostMethod");
    }

    @PutMapping("/put")
    public void doPutMethod() {
        System.out.println("doPostMethod");
    }

    @DeleteMapping("/delete")
    public void doDeleteMethod() {
        System.out.println("doDeleteMethod");
    }

    @PatchMapping("/patch")
    public void doPatchMethod() {
        System.out.println("doPatchMethod");
    }

}
