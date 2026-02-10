package com.ajex.invoice.staging.service;

import com.ajex.tmscommonservice.util.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ContextAwareExecutor {

    private final ScheduledExecutorService scheduledExecutorService;

    public ContextAwareExecutor(@Value("${context.aware.executor.thread-count:5}") int threadCount) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(threadCount);
    }

    public void scheduleSubmitAfterCommit(Runnable runnableTask, long delaySeconds) {
        final String userName = AuthUtil.getUserName();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                scheduledExecutorService.schedule(() -> {
                    try {
                        AuthUtil.setUserName(userName);
                        runnableTask.run();
                    } finally {
                        AuthUtil.removeUserName();
                    }
                }, delaySeconds, TimeUnit.SECONDS);
            }
        });
    }

    public void scheduleSubmitAfterCommitNoTrx(Runnable runnableTask, long delaySeconds) {
        scheduledExecutorService.schedule(runnableTask, delaySeconds, TimeUnit.SECONDS);
    }
    
}