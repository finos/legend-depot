//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.domain.notifications;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetadataNotificationResponseClaudeTest 

{

    @Test
    @DisplayName("Test default constructor initializes empty lists")
    void testConstructor()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        assertNotNull(response.getMessages());
        assertNotNull(response.getErrors());
        assertTrue(response.getMessages().isEmpty());
        assertTrue(response.getErrors().isEmpty());
        assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
    }

    @Test
    @DisplayName("Test getStatus returns SUCCESS when no errors")
    void testGetStatusSuccess()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Operation completed");

        assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
    }

    @Test
    @DisplayName("Test getStatus returns FAILED when errors exist")
    void testGetStatusFailed()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Error occurred");

        assertEquals(MetadataNotificationStatus.FAILED, response.getStatus());
    }

    @Test
    @DisplayName("Test getStatus returns FAILED even with messages when errors exist")
    void testGetStatusFailedWithMessages()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Some message");
        response.addError("Error occurred");

        assertEquals(MetadataNotificationStatus.FAILED, response.getStatus());
    }

    @Test
    @DisplayName("Test toString with empty response")
    void testToStringEmpty()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        String result = response.toString();

        assertTrue(result.contains("messages=[]"));
        assertTrue(result.contains("errors=[]"));
        assertEquals("MetadataEventResponse{messages=[], errors=[]}", result);
    }

    @Test
    @DisplayName("Test toString with messages")
    void testToStringWithMessages()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");
        response.addMessage("Message 2");

        String result = response.toString();

        assertTrue(result.contains("Message 1"));
        assertTrue(result.contains("Message 2"));
        assertTrue(result.contains("messages="));
        assertTrue(result.contains("errors=[]"));
    }

    @Test
    @DisplayName("Test toString with errors")
    void testToStringWithErrors()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Error 1");
        response.addError("Error 2");

        String result = response.toString();

        assertTrue(result.contains("Error 1"));
        assertTrue(result.contains("Error 2"));
        assertTrue(result.contains("errors="));
    }

    @Test
    @DisplayName("Test getErrors returns the errors list")
    void testGetErrors()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Error 1");
        response.addError("Error 2");

        List<String> errors = response.getErrors();

        assertEquals(2, errors.size());
        assertTrue(errors.contains("Error 1"));
        assertTrue(errors.contains("Error 2"));
    }

    @Test
    @DisplayName("Test getMessages returns the messages list")
    void testGetMessages()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");
        response.addMessage("Message 2");

        List<String> messages = response.getMessages();

        assertEquals(2, messages.size());
        assertTrue(messages.contains("Message 1"));
        assertTrue(messages.contains("Message 2"));
    }

    @Test
    @DisplayName("Test addError adds error and returns this")
    void testAddError()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        MetadataNotificationResponse result = response.addError("Test error");

        assertSame(response, result);
        assertEquals(1, response.getErrors().size());
        assertEquals("Test error", response.getErrors().get(0));
    }

    @Test
    @DisplayName("Test addError can be chained")
    void testAddErrorChaining()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        response.addError("Error 1").addError("Error 2").addError("Error 3");

        assertEquals(3, response.getErrors().size());
        assertEquals("Error 1", response.getErrors().get(0));
        assertEquals("Error 2", response.getErrors().get(1));
        assertEquals("Error 3", response.getErrors().get(2));
    }

    @Test
    @DisplayName("Test addMessage adds message and returns this")
    void testAddMessage()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        MetadataNotificationResponse result = response.addMessage("Test message");

        assertSame(response, result);
        assertEquals(1, response.getMessages().size());
        assertEquals("Test message", response.getMessages().get(0));
    }

    @Test
    @DisplayName("Test addMessage can be chained")
    void testAddMessageChaining()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        response.addMessage("Message 1").addMessage("Message 2").addMessage("Message 3");

        assertEquals(3, response.getMessages().size());
        assertEquals("Message 1", response.getMessages().get(0));
        assertEquals("Message 2", response.getMessages().get(1));
        assertEquals("Message 3", response.getMessages().get(2));
    }

    @Test
    @DisplayName("Test addMessages with empty list")
    void testAddMessagesEmpty()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        MetadataNotificationResponse result = response.addMessages(new ArrayList<>());

        assertSame(response, result);
        assertTrue(response.getMessages().isEmpty());
    }

    @Test
    @DisplayName("Test addMessages with single message")
    void testAddMessagesSingle()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        List<String> messages = Arrays.asList("Message 1");

        MetadataNotificationResponse result = response.addMessages(messages);

        assertSame(response, result);
        assertEquals(1, response.getMessages().size());
        assertEquals("Message 1", response.getMessages().get(0));
    }

    @Test
    @DisplayName("Test addMessages with multiple messages")
    void testAddMessagesMultiple()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        List<String> messages = Arrays.asList("Message 1", "Message 2", "Message 3");

        response.addMessages(messages);

        assertEquals(3, response.getMessages().size());
        assertEquals("Message 1", response.getMessages().get(0));
        assertEquals("Message 2", response.getMessages().get(1));
        assertEquals("Message 3", response.getMessages().get(2));
    }

    @Test
    @DisplayName("Test addMessages appends to existing messages")
    void testAddMessagesAppends()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Existing message");

        List<String> newMessages = Arrays.asList("New message 1", "New message 2");
        response.addMessages(newMessages);

        assertEquals(3, response.getMessages().size());
        assertEquals("Existing message", response.getMessages().get(0));
        assertEquals("New message 1", response.getMessages().get(1));
        assertEquals("New message 2", response.getMessages().get(2));
    }

    @Test
    @DisplayName("Test logError adds error to list")
    void testLogError()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        response.logError("Error message");

        assertEquals(1, response.getErrors().size());
        assertEquals("Error message", response.getErrors().get(0));
        assertTrue(response.hasErrors());
    }

    @Test
    @DisplayName("Test logError can be called multiple times")
    void testLogErrorMultiple()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        response.logError("Error 1");
        response.logError("Error 2");
        response.logError("Error 3");

        assertEquals(3, response.getErrors().size());
        assertEquals("Error 1", response.getErrors().get(0));
        assertEquals("Error 2", response.getErrors().get(1));
        assertEquals("Error 3", response.getErrors().get(2));
    }

    @Test
    @DisplayName("Test hasErrors returns false when no errors")
    void testHasErrorsFalse()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        assertFalse(response.hasErrors());
    }

    @Test
    @DisplayName("Test hasErrors returns true when errors exist")
    void testHasErrorsTrue()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Error");

        assertTrue(response.hasErrors());
    }

    @Test
    @DisplayName("Test hasErrors after adding and having multiple errors")
    void testHasErrorsMultiple()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Error 1");
        response.addError("Error 2");

        assertTrue(response.hasErrors());
    }

    @Test
    @DisplayName("Test combine with null response")
    void testCombineNull()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");
        response.addError("Error 1");

        MetadataNotificationResponse result = response.combine(null);

        assertSame(response, result);
        assertEquals(1, response.getMessages().size());
        assertEquals(1, response.getErrors().size());
    }

    @Test
    @DisplayName("Test combine with empty response")
    void testCombineEmpty()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");
        response.addError("Error 1");

        MetadataNotificationResponse other = new MetadataNotificationResponse();
        MetadataNotificationResponse result = response.combine(other);

        assertSame(response, result);
        assertEquals(1, response.getMessages().size());
        assertEquals(1, response.getErrors().size());
    }

    @Test
    @DisplayName("Test combine merges messages from other response")
    void testCombineMessages()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");

        MetadataNotificationResponse other = new MetadataNotificationResponse();
        other.addMessage("Message 2");
        other.addMessage("Message 3");

        response.combine(other);

        assertEquals(3, response.getMessages().size());
        assertEquals("Message 1", response.getMessages().get(0));
        assertEquals("Message 2", response.getMessages().get(1));
        assertEquals("Message 3", response.getMessages().get(2));
    }

    @Test
    @DisplayName("Test combine merges errors from other response")
    void testCombineErrors()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Error 1");

        MetadataNotificationResponse other = new MetadataNotificationResponse();
        other.addError("Error 2");
        other.addError("Error 3");

        response.combine(other);

        assertEquals(3, response.getErrors().size());
        assertEquals("Error 1", response.getErrors().get(0));
        assertEquals("Error 2", response.getErrors().get(1));
        assertEquals("Error 3", response.getErrors().get(2));
    }

    @Test
    @DisplayName("Test combine merges both messages and errors")
    void testCombineBoth()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");
        response.addError("Error 1");

        MetadataNotificationResponse other = new MetadataNotificationResponse();
        other.addMessage("Message 2");
        other.addError("Error 2");

        response.combine(other);

        assertEquals(2, response.getMessages().size());
        assertEquals(2, response.getErrors().size());
        assertEquals("Message 1", response.getMessages().get(0));
        assertEquals("Message 2", response.getMessages().get(1));
        assertEquals("Error 1", response.getErrors().get(0));
        assertEquals("Error 2", response.getErrors().get(1));
    }

    @Test
    @DisplayName("Test combine can be chained")
    void testCombineChaining()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");

        MetadataNotificationResponse other1 = new MetadataNotificationResponse();
        other1.addMessage("Message 2");

        MetadataNotificationResponse other2 = new MetadataNotificationResponse();
        other2.addMessage("Message 3");

        MetadataNotificationResponse result = response.combine(other1).combine(other2);

        assertSame(response, result);
        assertEquals(3, response.getMessages().size());
    }

    @Test
    @DisplayName("Test combine affects status when errors are merged")
    void testCombineAffectsStatus()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Message 1");
        assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());

        MetadataNotificationResponse other = new MetadataNotificationResponse();
        other.addError("Error from other");

        response.combine(other);

        assertEquals(MetadataNotificationStatus.FAILED, response.getStatus());
        assertTrue(response.hasErrors());
    }

    @Test
    @DisplayName("Test multiple operations together")
    void testComplexScenario()
  {
        MetadataNotificationResponse response = new MetadataNotificationResponse();

        response.addMessage("Starting operation")
                .addMessage("Processing step 1")
                .addError("Failed step 2")
                .addMessage("Attempting recovery");

        response.logError("Recovery failed");

        List<String> additionalMessages = Arrays.asList("Step 3 skipped", "Operation ended");
        response.addMessages(additionalMessages);

        assertEquals(5, response.getMessages().size());
        assertEquals(2, response.getErrors().size());
        assertTrue(response.hasErrors());
        assertEquals(MetadataNotificationStatus.FAILED, response.getStatus());

        String toString = response.toString();
        assertTrue(toString.contains("Failed step 2"));
        assertTrue(toString.contains("Recovery failed"));
    }
}
