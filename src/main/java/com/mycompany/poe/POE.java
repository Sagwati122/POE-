/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poe;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Student
 */
public class POE {

    public static void main(String[] args) {

        boolean running = true;
        while (running) {
            System.out.println(" QuickChat Menu ");
            System.out.println("1) Send Message");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> sendMessage();
                case "2" -> showSentMessages();
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                case "4" -> storedMessagesMenu();
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    // ----- Option 1 -----
    private static void sendMessage() {
        System.out.print("Enter recipient cell number (e.g. +27834557896): ");
        String recipient = scanner.nextLine().trim();
        if (!recipient.matches("^\\+\\d{10,12}$")) {
            System.out.println("Cell number is incorrectly formatted or does not "
                    + "contain an international code. Please correct the number and try again.");
            return;
        }
        String MAX_MESSAGE_LENGTH = null;

        System.out.print("Enter your message (max " + MAX_MESSAGE_LENGTH + " characters): ");
        String text = scanner.nextLine().trim();
        if (text.isEmpty()) {
            System.out.println("A message cannot be empty.");
            return;
        }
        if (text.length() > MAX_MESSAGE_LENGTH) {
            System.out.println("Message exceeds " + MAX_MESSAGE_LENGTH + " characters by "
                    + (text.length() - MAX_MESSAGE_LENGTH) + ", please reduce size.");
            return;
        }

        System.out.println("1) Send   2) Disregard   3) Store");
        System.out.print("What would you like to do with this message? ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                manager.addMessage(recipient, text, MessageManager.FLAG_SENT);
                System.out.println("Message successfully sent.");
                break;
            case "2":
                manager.addMessage(recipient, text, MessageManager.FLAG_DISREGARD);
                System.out.println("Message disregarded.");
                break;
            case "3":
                manager.addMessage(recipient, text, MessageManager.FLAG_STORED);
                saveStoredMessages();
                System.out.println("Message successfully stored.");
                break;
            default:
                System.out.println("Invalid choice, message was not saved.");
        }
    }

    // ----- Option 2 -----
    private static void showSentMessages() {
        List<String> sent = manager.getSentMessages();
        if (sent.isEmpty()) {
            System.out.println("No messages have been sent yet.");
            return;
        }
        System.out.println("--- Sent messages ---");
        for (String message : sent) {
            System.out.println("- " + message);
        }
    }

    // ----- Option 4 -----
    private static void storedMessagesMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search all messages sent or stored for a recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display message report");
            System.out.println("g) Read JSON file and display stored messages");
            System.out.println("x) Back to main menu");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim().toLowerCase()) {
                case "a" -> printList(manager.displaySenderAndRecipientOfStored(),
                            "There are no stored messages.");
                case "b" -> {
                    String longest = manager.getLongestStoredMessage();
                    System.out.println(longest.isEmpty()
                            ? "There are no stored messages."
                            : "Longest stored message: " + longest);
                }
                case "c" -> {
                    System.out.print("Enter message ID: ");
                    String id = scanner.nextLine().trim();
                    String found = manager.searchByMessageId(id);
                    if (found.equals("Message ID not found.")) {
                        System.out.println(found);
                    } else {
                        System.out.println("Recipient: " + manager.getRecipientByMessageId(id));
                        System.out.println("Message: " + found);
                    }
                }
                case "d" -> {
                    System.out.print("Enter recipient: ");
                    String recipient = scanner.nextLine().trim();
                    printList(manager.searchByRecipient(recipient),
                            "No messages found for that recipient.");
                }
                case "e" -> {
                    System.out.print("Enter message hash: ");
                    String hash = scanner.nextLine().trim();
                    System.out.println(manager.deleteByHash(hash));
                    saveStoredMessages();
                }
                case "f" -> printList(manager.getMessageReport(), "No messages have been sent yet.");
                case "g" -> {
                    loadStoredMessages();
                    printList(manager.getStoredMessages(), "There are no stored messages.");
                }
                case "x" -> inMenu = false;
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void saveStoredMessages() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private static void loadStoredMessages() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    }

