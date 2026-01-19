package com.ajex.invoice.staging.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

@Slf4j
public class ResilientStreamsExceptionHandler implements StreamsUncaughtExceptionHandler {

    @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {
        log.error("Kafka Streams uncaught exception. Replacing stream thread", exception);
        return StreamThreadExceptionResponse.REPLACE_THREAD;
    }

}
