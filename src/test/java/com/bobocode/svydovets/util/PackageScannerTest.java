package com.bobocode.svydovets.util;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.util.PackageScanner;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PackageScannerTest {

    // todo: Implement tests for all methods of PackageScanner()
    @Test
    void test() {
        var packageScanner = new PackageScanner();
        packageScanner.findAllBeanByBasePackage("com.bobocode.svydovets.config");
    }
}
