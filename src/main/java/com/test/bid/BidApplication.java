package com.test.bid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class BidApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(BidApplication.class);

    private static final String FILE_NAME = "/bids.json";

    private static final int DELAY = 5000; // 5 sec.

    private static final int PERIOD = 60000; // 1 min.

    public static void main(String[] args) {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                readBids();
            }
        }, DELAY, PERIOD);
    }

    private static void readBids() {

        String filePath = "";
        URL resource = BidApplication.class.getResource(FILE_NAME);

        if (resource == null) {
            LOGGER.error("Not found resource file {}", FILE_NAME);
            throw new RuntimeException("No resource found");
        }

        try {
            filePath = Paths.get(resource.toURI()).toFile().getAbsolutePath();
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse resource file {}; {}", FILE_NAME, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error during get resource file path: {}", e.getMessage(), e);
        }

        File file = new File(filePath);
        JsonParser parser = null;

        try {
            parser = Json.createParser(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found {}; {}", filePath, e.getMessage(), e);
        }

        while (parser.hasNext()) {
            Event event = parser.next();
            if (event == Event.KEY_NAME) {
                BidDto bidDto = new BidDto();
                switch (parser.getString()) {
                    case "id":
                        parser.next();
                        bidDto.setId(parser.getString());
                        break;
                    case "ts":
                        parser.next();
                        bidDto.setTimestamp(parser.getString());
                        break;
                    case "ty":
                        parser.next();
                        bidDto.setType(parser.getString());
                        break;
                    case "pl":
                        parser.next();
                        byte[] decodedBytes = Base64.getDecoder().decode(parser.getString());
                        String decodedString = new String(decodedBytes);
                        bidDto.setPayload(decodedString);
                        break;
                }
                Thread thread = new BidThread(bidDto);
                thread.start();
            }
        }
    }
}
