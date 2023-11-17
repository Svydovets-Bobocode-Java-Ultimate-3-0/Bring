package com.bobocode.svydovets.source.config;

import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

/**
 * Config class for scanning a specified package and creating a context with additional beans
 */
@Configuration
@ComponentScan("com.bobocode.svydovets.source.base")
public class BasePackageBeansConfig {
}
