package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CommentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Comment getCommentSample1() {
        return new Comment().id(1L).body("body1").sentiment(1L).author("author1");
    }

    public static Comment getCommentSample2() {
        return new Comment().id(2L).body("body2").sentiment(2L).author("author2");
    }

    public static Comment getCommentRandomSampleGenerator() {
        return new Comment()
            .id(longCount.incrementAndGet())
            .body(UUID.randomUUID().toString())
            .sentiment(longCount.incrementAndGet())
            .author(UUID.randomUUID().toString());
    }
}
