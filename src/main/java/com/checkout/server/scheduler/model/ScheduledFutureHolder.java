package com.checkout.server.scheduler.model;

import lombok.Data;

import java.util.concurrent.ScheduledFuture;

@Data
public class ScheduledFutureHolder {
    private ScheduledFuture<?> scheduledFuture;

    public void cancel() {
        scheduledFuture.cancel(false);
    }
}
