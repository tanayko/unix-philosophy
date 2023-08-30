package com.acadiasoft.unixphilosophy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WordCount {
    private final ByteArrayOutputStream currentBuffer = new ByteArrayOutputStream();
    private final ByteArrayOutputStream dataToBeProcessed = new ByteArrayOutputStream();
    private int totalLines = 0;
    private int totalBytes = 0;
    private static final Logger logger
            = LoggerFactory.getLogger(WordCount.class);

    public void run(InputStream in) {
        Scanner scanner = new Scanner(in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            // add 16 bytes to currentBuffer, and rest to dataToBeProcessed
            char[] lineAsCharArray = line.toCharArray();
            for (int i = 0; i < lineAsCharArray.length; i++) {
                if (i < 16) {
                    currentBuffer.write((byte) lineAsCharArray[i]);
                } else {
                    dataToBeProcessed.write((byte) lineAsCharArray[i]);
                }
            }

            // add to new line because we read next line
            // add a byte for the new line (\n) character
            totalBytes++;
            totalLines++;

            // process data in current buffers
            processCurrentBuffer();

            // print current statistics
            if (totalLines % 10 == 0) {
                System.err.printf("%d lines so far, %d bytes processed\n", totalLines, totalBytes);
            }

            // if there were more than 16 bytes of data from the line that was just read,
            // either read the next 16 bytes (if block)
            // or read the rest of the bytes if there are less than 16 (else block)
            while (dataToBeProcessed.toString().length() != 0) {
                if (dataToBeProcessed.toString().length() > 16) {
                    byte[] byteArray = dataToBeProcessed.toByteArray();
                    for (int i = 0; i < 16; i++) {
                        currentBuffer.write(byteArray[i]);
                    }
                    byte[] newDataToBeProcessed = dataToBeProcessed.toString().substring(16).getBytes();
                    dataToBeProcessed.reset();
                    try {
                        dataToBeProcessed.write(newDataToBeProcessed);
                    } catch (IOException e) {
                        logger.error("Trouble reading data", e);
                    }
                    processCurrentBuffer();
                } else {
                    try {
                        currentBuffer.write(dataToBeProcessed.toByteArray());
                    } catch (IOException e) {
                        logger.error("Trouble reading data", e);
                    }
                    dataToBeProcessed.reset();
                    processCurrentBuffer();
                }
            }
        }

        // if there are 0 or 1 lines
        if (totalLines <= 1) {
            System.err.println("No newlines found");
            System.exit(1);
        }

        // final statistic line
        System.out.printf("%d lines total in %d bytes", totalLines, totalBytes);
        System.exit(0);
    }

    // adds all bytes to total byte count
    // resets current buffer
    private void processCurrentBuffer() {
        String bufferAsString = currentBuffer.toString(StandardCharsets.UTF_8);
        totalBytes += bufferAsString.length();
        currentBuffer.reset();
    }
}
