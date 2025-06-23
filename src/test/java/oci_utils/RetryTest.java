package oci_utils;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class RetryTest {
    @Test
    void test_staticRetry() {
        assertTrue(new WorksOnNthTime(1).worked());
        assertFalse(new WorksOnNthTime(2).worked());
        WorksOnNthTime w;
        w = new WorksOnNthTime(2);
        assertFalse(w.worked());
        assertTrue(w.worked());
        w = new WorksOnNthTime(3);
        assertFalse(w.worked());
        assertFalse(w.worked());
        assertTrue(w.worked());
    }

    @SneakyThrows
    @Test
    void test_libraryRetry() {
        WorksOnNthTime w;
        Callable<Boolean> works;
        boolean result;

        w = new WorksOnNthTime(1);
        works = wrap(w::worked, 1);
        result = works.call();
        assertTrue(result);
        assertEquals(1, w.times);

        w = new WorksOnNthTime(2);
        works = wrap(w::worked, 1);
        result = works.call();
        assertFalse(result);
        assertEquals(1, w.times);

        w = new WorksOnNthTime(2);
        works = wrap(w::worked, 2);
        result = works.call();
        assertTrue(result);
        assertEquals(2, w.times);
    }

    @SneakyThrows
    @Test
    void test_delay() {
        var w = new WorksOnNthTime(2);
        var callable = Retry.of("example", RetryConfig.custom().maxAttempts(2).retryOnResult(Predicate.not(Boolean.TRUE::equals)).waitDuration(Duration.ofSeconds(1)).build())
                .decorateCallable(w::worked);
        long t0 = System.nanoTime();
        var result = callable.call();
        long t1 = System.nanoTime();
        assertTrue(result);
        assertEquals(2, w.times);
        assertEquals(1, Duration.ofNanos(t1 - t0).getSeconds());
    }

    <T> Callable<T> wrap(Callable<T> callable, int maxAttempts) {
        return Retry.of(
                        "example",
                        RetryConfig.custom()
                                .retryOnResult(Predicate.not(Boolean.TRUE::equals))
                                .waitDuration(Duration.ZERO)
                                .maxAttempts(maxAttempts)
                                .build()
                )
                .decorateCallable(callable);
    }

    static class WorksOnNthTime {
        int times;
        int n;

        WorksOnNthTime(int n) {
            this.n = n;
        }

        boolean worked() {
            times++;
            boolean returning = times >= n;
            return returning;
        }
    }
}
