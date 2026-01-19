package com.ajex.invoice.staging.service;

import com.ajex.invoice.staging.dto.RowEvent;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AggregationState {

    private final List<String> rowIds = new ArrayList<>();
    private Instant firstSeen;

    public AggregationState add(RowEvent event) {
        if (firstSeen == null) {
            firstSeen = event.createdAt();
        }
        rowIds.add(event.waybillNo());
        return this;
    }

    public boolean sizeReached() {
        return rowIds.size() >= 1;
    }

    public boolean timeExceeded(Instant now) {
        return firstSeen != null &&
                Duration.between(firstSeen, now).toMinutes() >= 2;
    }

    public void clear() {
        rowIds.clear();
        firstSeen = null;
    }
}