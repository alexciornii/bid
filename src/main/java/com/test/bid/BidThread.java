package com.test.bid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BidThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(BidThread.class);

    private static final Map<String, Queue<BidDto>> QUEUE_MAP = new HashMap<>();

    private BidDto bidDto;

    BidThread(BidDto bidDto) {
        this.bidDto = bidDto;
    }

    @Override
    public void run() {
        String bidType = this.bidDto.getType();
        if (QUEUE_MAP.containsKey(bidType)) {
            QUEUE_MAP.get(bidType).add(bidDto);
        } else {
            QUEUE_MAP.put(bidType, new LinkedList());
        }
        LOGGER.info("Queued bid: {}", bidDto);
    }
}
