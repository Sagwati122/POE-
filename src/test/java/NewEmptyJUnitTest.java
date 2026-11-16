/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author Student
 */
public class NewEmptyJUnitTest {
    
  public class MessageManagerTest {

    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time!";
    private static final String MSG5 = "Ok, I am leaving without you.";

    private static final String ID1 = "1000000001";
    private static final String ID2 = "2000000002";
    private static final String ID3 = "3000000003";
    private static final String ID4 = "4000000004";
    private static final String ID5 = "5000000005";

    private MessageManager manager;

    /** Populates the arrays with Test Data Messages 1 to 5. */
    @BeforeEach
    void setUp() {
        manager = new MessageManager();
        manager.addMessage("+27834557896", MSG1, MessageManager.FLAG_SENT, ID1);
        manager.addMessage("+27838884567", MSG2, MessageManager.FLAG_STORED, ID2);
        manager.addMessage("+27834484567", MSG3, MessageManager.FLAG_DISREGARD, ID3);
        manager.addMessage("0838884567", MSG4, MessageManager.FLAG_SENT, ID4);
        manager.addMessage("+27838884567", MSG5, MessageManager.FLAG_STORED, ID5);
    }

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        List<String> expected = Arrays.asList("Did you get the cake?", "It is dinner time!");
        assertEquals(expected, manager.getSentMessages());
    }

    @Test
    void disregardedMessagesArrayCorrectlyPopulated() {
        assertEquals(Arrays.asList(MSG3), manager.getDisregardedMessages());
    }

    @Test
    void storedMessagesArrayCorrectlyPopulated() {
        assertEquals(Arrays.asList(MSG2, MSG5), manager.getStoredMessages());
    }

    @Test
    void hashAndIdArraysCorrectlyPopulated() {
        assertEquals(5, manager.getMessageIDs().size());
        assertEquals(5, manager.getMessageHashes().size());
        assertEquals(Arrays.asList(ID1, ID2, ID3, ID4, ID5), manager.getMessageIDs());
    }

    @Test
    void displayTheLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                manager.getLongestStoredMessage());
    }

    @Test
    void searchForMessageId() {
        assertEquals("It is dinner time!", manager.searchByMessageId(ID4));
        assertEquals("0838884567", manager.getRecipientByMessageId(ID4));
    }

    @Test
    void searchAllMessagesForAParticularRecipient() {
        List<String> expected = Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you.");
        assertEquals(expected, manager.searchByRecipient("+27838884567"));
    }

    @Test
    void deleteMessageUsingMessageHash() {
        String hash = MessageManager.createHash(ID2, 2, MSG2);
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\""
                + " successfully deleted.", manager.deleteByHash(hash));
        assertEquals(Arrays.asList(MSG5), manager.getStoredMessages());
        assertEquals(4, manager.getMessageIDs().size());
    }

    @Test
    void deleteWithUnknownHashDoesNothing() {
        assertEquals("Message hash not found.", manager.deleteByHash("00:0:NOPE"));
        assertEquals(5, manager.getMessageHashes().size());
    }

    @Test
    void messageReportListsSentMessages() {
        List<String> report = manager.getMessageReport();
        assertEquals(2, report.size());
        assertEquals("Message Hash: 10:1:DIDCAKE? | Message ID: 1000000001 | Recipient: "
                + "+27834557896 | Message: Did you get the cake?", report.get(0));
    }

    @Test
    void storedMessagesWrittenToAndReadFromJson(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("storedMessages.json");
        manager.writeStoredToJson(file);

        MessageManager reader = new MessageManager();
        reader.loadStoredFromJson(file);

        assertEquals(Arrays.asList(MSG2, MSG5), reader.getStoredMessages());
        assertEquals(MSG5, reader.searchByMessageId(ID5));
    }
}
}
