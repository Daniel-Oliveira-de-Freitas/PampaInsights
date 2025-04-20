package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.SentimentAnalysisType;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FilterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Filter getFilterSample1() {
        return new Filter().id(1L).name(SentimentAnalysisType.valueOf("name1"));
    }

    public static Filter getFilterSample2() {
        return new Filter().id(2L).name(SentimentAnalysisType.valueOf("name2"));
    }

    public static Filter getFilterRandomSampleGenerator() {
        return new Filter().id(longCount.incrementAndGet()).name(SentimentAnalysisType.valueOf(UUID.randomUUID().toString()));
    }
}
