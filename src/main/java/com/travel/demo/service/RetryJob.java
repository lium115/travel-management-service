package com.travel.demo.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class RetryJob {
	public static <T, U> void execute(BiConsumer<T, U> consumer, T t, U u, int maxRetryTime){
		AtomicInteger retry = new AtomicInteger(0);
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
		pool.scheduleAtFixedRate(() -> {
			int retryTimes = retry.get();
			if (retryTimes >= maxRetryTime) {
				pool.shutdown();
			}
			try {
				retry.getAndIncrement();
				consumer.accept(t, u);
				retry.set(maxRetryTime);
			} catch (Exception e) {
				retry.getAndIncrement();
			}
		}, 5, 5, TimeUnit.MINUTES);
	}
}
