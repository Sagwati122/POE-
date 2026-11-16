/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poe;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Student
 */
class MessageManager {
    public static final String FLAG_SENT = "Sent";
    public static final String FLAG_STORED = "Stored";
    public static final String FLAG_DISREGARD = "Disregard";
    public static final String SENDER = "Developer";

    // ----- Required arrays -----
    private final List<String> sentMessages = new ArrayList<>();
    private final List<String> disregardedMessages = new ArrayList<>();
    private final List<String> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();

    // ----- Parallel arrays that go with messageIDs / messageHashes -----
    private final List<String> recipients = new ArrayList<>();
    private final List<String> messageTexts = new ArrayList<>();
    private final List<String> messageFlags = new ArrayList<>();

    // ----- Parallel arrays that go with storedMessages -----
    private final List<String> storedRecipients = new ArrayList<>();
    private final List<String> storedIDs = new ArrayList<>();
    private final List<String> storedHashes = new ArrayList<>();

    private final Random random = new Random();

   
    // Creating IDs and hashes
    
    public String generateMessageId() {
        long number = (long) (random.nextDouble() * 10_000_000_000L);
        return String.format("%010d", number);
    }

   
    public static String createHash(String id, int messageNumber, String text) {
        String[] words = text.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        return (id.substring(0, 2) + ":" + messageNumber + ":" + firstWord + lastWord)
                .toUpperCase();
    }

  
    // Populating the arrays (no hard-coding: values come from the caller)
    
    public void addMessage(String recipient, String text, String flag) {
        addMessage(recipient, text, flag, generateMessageId());
    }

    /** Adds a message with a known ID (used by the unit tests and JSON loading). */
    public void addMessage(String recipient, String text, String flag, String id) {
        int messageNumber = messageIDs.size() + 1;
        String hash = createHash(id, messageNumber, text);
        addToAllArrays(recipient, text, flag, id, hash);
    }

    private void addToAllArrays(String recipient, String text, String flag,
                                String id, String hash) {
        messageIDs.add(id);
        messageHashes.add(hash);
        recipients.add(recipient);
        messageTexts.add(text);
        messageFlags.add(flag);

        if (flag.equalsIgnoreCase(FLAG_SENT)) {
            sentMessages.add(text);
        } else if (flag.equalsIgnoreCase(FLAG_DISREGARD)) {
            disregardedMessages.add(text);
        } else if (flag.equalsIgnoreCase(FLAG_STORED)) {
            storedMessages.add(text);
            storedRecipients.add(recipient);
            storedIDs.add(id);
            storedHashes.add(hash);
        }
    }

   
    // JSON: write stored messages and read them back into the array
 
    public void writeStoredToJson(Path file) throws IOException {
        JsonArray array = new JsonArray();
        for (int i = 0; i < storedMessages.size(); i++) {
            JsonObject obj = new JsonObject();
            obj.addProperty("messageID", storedIDs.get(i));
            obj.addProperty("messageHash", storedHashes.get(i));
            obj.addProperty("recipient", storedRecipients.get(i));
            obj.addProperty("message", storedMessages.get(i));
            obj.addProperty("flag", FLAG_STORED);
            array.add(obj);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(array, writer);
        }
    }

   
    public void loadStoredFromJson(Path file) throws IOException {
        storedMessages.clear();
        storedRecipients.clear();
        storedIDs.clear();
        storedHashes.clear();

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                String id = obj.get("messageID").getAsString();
                String hash = obj.get("messageHash").getAsString();
                String recipient = obj.get("recipient").getAsString();
                String text = obj.get("message").getAsString();

                storedMessages.add(text);
                storedRecipients.add(recipient);
                storedIDs.add(id);
                storedHashes.add(hash);

                // Make sure the message is also in the "all messages" arrays
                if (!messageHashes.contains(hash)) {
                    messageIDs.add(id);
                    messageHashes.add(hash);
                    recipients.add(recipient);
                    messageTexts.add(text);
                    messageFlags.add(FLAG_STORED);
                }
            }
        }
    }

    
    public List<String> displaySenderAndRecipientOfStored() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < storedMessages.size(); i++) {
            result.add("Sender: " + SENDER + " | Recipient: " + storedRecipients.get(i));
        }
        return result;
    }

    /** b. The longest stored message ("" if there are none). */
    public String getLongestStoredMessage() {
        String longest = "";
        for (String message : storedMessages) {
            if (message.length() > longest.length()) {
                longest = message;
            }
        }
        return longest;
    }

    /** c. Search for a message ID and return the message text. */
    public String searchByMessageId(String id) {
        int index = messageIDs.indexOf(id);
        if (index < 0) {
            return "Message ID not found.";
        }
        return messageTexts.get(index);
    }

    /** c. Search for a message ID and return the recipient. */
    public String getRecipientByMessageId(String id) {
        int index = messageIDs.indexOf(id);
        if (index < 0) {
            return "Message ID not found.";
        }
        return recipients.get(index);
    }

    /** d. All sent or stored messages for one recipient. */
    public List<String> searchByRecipient(String recipient) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < messageTexts.size(); i++) {
            boolean sameRecipient = recipients.get(i).equals(recipient);
            boolean sentOrStored = messageFlags.get(i).equalsIgnoreCase(FLAG_SENT)
                    || messageFlags.get(i).equalsIgnoreCase(FLAG_STORED);
            if (sameRecipient && sentOrStored) {
                result.add(messageTexts.get(i));
            }
        }
        return result;
    }

    /** e. Delete a message using its hash. */
    public String deleteByHash(String hash) {
        int index = messageHashes.indexOf(hash);
        if (index < 0) {
            return "Message hash not found.";
        }

        String text = messageTexts.get(index);
        String flag = messageFlags.get(index);

        // Remove from the flag-specific array
        if (flag.equalsIgnoreCase(FLAG_SENT)) {
            sentMessages.remove(text);
        } else if (flag.equalsIgnoreCase(FLAG_DISREGARD)) {
            disregardedMessages.remove(text);
        }

        // Remove from the stored arrays
        int storedIndex = storedHashes.indexOf(hash);
        if (storedIndex >= 0) {
            storedMessages.remove(storedIndex);
            storedRecipients.remove(storedIndex);
            storedIDs.remove(storedIndex);
            storedHashes.remove(storedIndex);
        }

        // Remove from the parallel "all messages" arrays
        messageIDs.remove(index);
        messageHashes.remove(index);
        recipients.remove(index);
        messageTexts.remove(index);
        messageFlags.remove(index);

        return "Message: \"" + text + "\" successfully deleted.";
    }

    /** f. Report of every sent message: hash, ID, recipient and message. */
    public List<String> getMessageReport() {
        List<String> report = new ArrayList<>();
        for (int i = 0; i < messageTexts.size(); i++) {
            if (messageFlags.get(i).equalsIgnoreCase(FLAG_SENT)) {
                report.add("Message Hash: " + messageHashes.get(i)
                        + " | Message ID: " + messageIDs.get(i)
                        + " | Recipient: " + recipients.get(i)
                        + " | Message: " + messageTexts.get(i));
            }
        }
        return report;
    }

    // Getters (return copies so the arrays cannot be changed from outside)
    

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public List<String> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public List<String> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }

    public List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }
}

