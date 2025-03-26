package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParameterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Parameter getParameterSample1() {
        return new Parameter()
            .id(1L)
            .terms("terms1")
            .webSite("webSite1")
            .instagram("instagram1")
            .facebook("facebook1")
            .linkedin("linkedin1")
            .x("x1");
    }

    public static Parameter getParameterSample2() {
        return new Parameter()
            .id(2L)
            .terms("terms2")
            .webSite("webSite2")
            .instagram("instagram2")
            .facebook("facebook2")
            .linkedin("linkedin2")
            .x("x2");
    }

    public static Parameter getParameterRandomSampleGenerator() {
        return new Parameter()
            .id(longCount.incrementAndGet())
            .terms(UUID.randomUUID().toString())
            .webSite(UUID.randomUUID().toString())
            .instagram(UUID.randomUUID().toString())
            .facebook(UUID.randomUUID().toString())
            .linkedin(UUID.randomUUID().toString())
            .x(UUID.randomUUID().toString());
    }
}
