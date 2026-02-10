package com.ajex.invoice.staging.service;

import com.ajex.invoice.staging.dto.RowEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class AggregationState {

    private final List<String> rowIds = new ArrayList<>();
    private Instant firstSeen;

    @Value("${aggregation.size-limit:1}")
    private int aggregationSize;

    @Value("${aggregation.time-limit:5}")
    private int timeLimitMinutes;

    public AggregationState add(RowEvent event) {
        if (firstSeen == null) {
            firstSeen = event.createdAt();
        }
        rowIds.add(event.waybillNo());
        return this;
    }

    public boolean sizeReached() {
        return rowIds.size() >= aggregationSize;
    }

    public boolean timeExceeded(Instant now) {
        return firstSeen != null &&
                Duration.between(firstSeen, now).toMinutes() >= timeLimitMinutes;
    }

    public void clear() {
        rowIds.clear();
        firstSeen = null;
    }
}