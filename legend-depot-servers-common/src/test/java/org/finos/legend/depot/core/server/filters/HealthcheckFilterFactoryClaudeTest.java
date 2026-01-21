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

package org.finos.legend.depot.core.server.filters;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.when;

class HealthcheckFilterFactoryClaudeTest
{
    @Test
    @DisplayName("Test constructor creates instance successfully")
    void testConstructor()
    {
        // Act
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();

        // Assert
        assertNotNull(factory, "Factory instance should not be null");
    }

    @Test
    @DisplayName("Test build returns non-null filter")
    void testBuildReturnsNonNullFilter()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();

        // Act
        Filter<IAccessEvent> filter = factory.build();

        // Assert
        assertNotNull(filter, "Filter should not be null");
    }

    @Test
    @DisplayName("Test build creates new filter instance on each call")
    void testBuildCreatesNewFilterInstanceEachTime()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();

        // Act
        Filter<IAccessEvent> filter1 = factory.build();
        Filter<IAccessEvent> filter2 = factory.build();
        Filter<IAccessEvent> filter3 = factory.build();

        // Assert
        assertNotNull(filter1, "First filter should not be null");
        assertNotNull(filter2, "Second filter should not be null");
        assertNotNull(filter3, "Third filter should not be null");
        assertNotSame(filter1, filter2, "Each call to build should create a new filter instance");
        assertNotSame(filter2, filter3, "Each call to build should create a new filter instance");
        assertNotSame(filter1, filter3, "Each call to build should create a new filter instance");
    }

    @Test
    @DisplayName("Test filter returns NEUTRAL for non-healthcheck requests")
    void testFilterReturnsNeutralForNonHealthcheckRequests()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("/api/projects");

        // Act
        FilterReply reply = filter.decide(event);

        // Assert
        assertEquals(FilterReply.NEUTRAL, reply, "Filter should return NEUTRAL for non-healthcheck requests");
    }

    @Test
    @DisplayName("Test filter returns NEUTRAL for first 11 healthcheck requests")
    void testFilterReturnsNeutralForFirstElevenHealthcheckRequests()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("/admin/healthcheck");

        // Act & Assert - First 11 requests should return NEUTRAL (counter increments from 0 to 11)
        for (int i = 1; i <= 11; i++)
        {
            FilterReply reply = filter.decide(event);
            assertEquals(FilterReply.NEUTRAL, reply,
                    "Filter should return NEUTRAL for healthcheck request #" + i);
        }
    }

    @Test
    @DisplayName("Test filter returns DENY for healthcheck requests after the 11th")
    void testFilterReturnsDenyForHealthcheckRequestsAfterEleventh()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("/admin/healthcheck");

        // Act - Make 11 requests first (counter reaches 11)
        for (int i = 1; i <= 11; i++)
        {
            filter.decide(event);
        }

        // Assert - 12th and subsequent requests should return DENY (counter > 10)
        FilterReply reply12 = filter.decide(event);
        FilterReply reply13 = filter.decide(event);
        FilterReply reply14 = filter.decide(event);

        assertEquals(FilterReply.DENY, reply12, "Filter should return DENY for 12th healthcheck request");
        assertEquals(FilterReply.DENY, reply13, "Filter should return DENY for 13th healthcheck request");
        assertEquals(FilterReply.DENY, reply14, "Filter should return DENY for 14th healthcheck request");
    }

    @Test
    @DisplayName("Test filter increments counter for all requests")
    void testFilterIncrementsCounterForAllRequests()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent healthcheckEvent = Mockito.mock(IAccessEvent.class);
        when(healthcheckEvent.getRequestURI()).thenReturn("/admin/healthcheck");

        IAccessEvent otherEvent = Mockito.mock(IAccessEvent.class);
        when(otherEvent.getRequestURI()).thenReturn("/api/projects");

        // Act - Mix of healthcheck and non-healthcheck requests
        filter.decide(otherEvent);  // 1
        filter.decide(healthcheckEvent);  // 2
        filter.decide(otherEvent);  // 3
        filter.decide(otherEvent);  // 4
        filter.decide(healthcheckEvent);  // 5
        filter.decide(otherEvent);  // 6
        filter.decide(otherEvent);  // 7
        filter.decide(otherEvent);  // 8
        filter.decide(otherEvent);  // 9
        filter.decide(otherEvent);  // 10
        filter.decide(otherEvent);  // 11 - counter now > 10

        // Assert - Next healthcheck request should be DENIED (counter > 10)
        FilterReply reply = filter.decide(healthcheckEvent);
        assertEquals(FilterReply.DENY, reply,
                "Filter should return DENY for healthcheck when counter exceeds 10");
    }

    @Test
    @DisplayName("Test filter handles URI ending with healthcheck path")
    void testFilterHandlesUriEndingWithHealthcheckPath()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();

        // Test various URIs that end with /admin/healthcheck
        IAccessEvent event1 = Mockito.mock(IAccessEvent.class);
        when(event1.getRequestURI()).thenReturn("/admin/healthcheck");

        IAccessEvent event2 = Mockito.mock(IAccessEvent.class);
        when(event2.getRequestURI()).thenReturn("http://localhost:8080/admin/healthcheck");

        // Act
        FilterReply reply1 = filter.decide(event1);
        FilterReply reply2 = filter.decide(event2);

        // Assert
        assertEquals(FilterReply.NEUTRAL, reply1, "Should match exact path");
        assertEquals(FilterReply.NEUTRAL, reply2, "Should match path ending with /admin/healthcheck");
    }

    @Test
    @DisplayName("Test filter does not match URI containing healthcheck but not ending with it")
    void testFilterDoesNotMatchUriContainingHealthcheckButNotEndingWithIt()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("/admin/healthcheck/details");

        // Act
        FilterReply reply = filter.decide(event);

        // Assert
        assertEquals(FilterReply.NEUTRAL, reply,
                "Filter should return NEUTRAL when URI contains but doesn't end with healthcheck path");
    }

    @Test
    @DisplayName("Test each filter instance has independent counter")
    void testEachFilterInstanceHasIndependentCounter()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter1 = factory.build();
        Filter<IAccessEvent> filter2 = factory.build();

        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("/admin/healthcheck");

        // Act - Increment filter1's counter to > 10
        for (int i = 1; i <= 11; i++)
        {
            filter1.decide(event);
        }

        // Assert - filter1 should DENY, but filter2 should still NEUTRAL
        FilterReply reply1 = filter1.decide(event);
        FilterReply reply2 = filter2.decide(event);

        assertEquals(FilterReply.DENY, reply1, "Filter1 should DENY after 11 requests");
        assertEquals(FilterReply.NEUTRAL, reply2, "Filter2 should NEUTRAL on first request");
    }

    @Test
    @DisplayName("Test filter at exact threshold boundary (11th request)")
    void testFilterAtExactThresholdBoundary()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("/admin/healthcheck");

        // Act - Make exactly 11 requests (counter reaches 11)
        FilterReply reply11 = null;
        for (int i = 1; i <= 11; i++)
        {
            reply11 = filter.decide(event);
        }

        // Assert
        assertEquals(FilterReply.NEUTRAL, reply11, "11th request should still return NEUTRAL (counter is now 11)");

        // Act - 12th request (counter is 11, which is > 10, so DENY)
        FilterReply reply12 = filter.decide(event);

        // Assert
        assertEquals(FilterReply.DENY, reply12, "12th request should return DENY (counter > 10)");
    }

    @Test
    @DisplayName("Test filter with null URI throws NullPointerException")
    void testFilterWithNullUriThrowsException()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn(null);

        // Act & Assert - The filter does not handle null URIs, so NPE is expected
        try
        {
            filter.decide(event);
            // If we get here, the test should fail
            assertEquals(true, false, "Expected NullPointerException but none was thrown");
        }
        catch (NullPointerException e)
        {
            // Expected - the filter does not handle null URIs
            assertNotNull(e, "NullPointerException should be thrown for null URI");
        }
    }

    @Test
    @DisplayName("Test filter with empty URI returns NEUTRAL")
    void testFilterWithEmptyUriReturnsNeutral()
    {
        // Arrange
        HealthcheckFilterFactory factory = new HealthcheckFilterFactory();
        Filter<IAccessEvent> filter = factory.build();
        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("");

        // Act
        FilterReply reply = filter.decide(event);

        // Assert
        assertEquals(FilterReply.NEUTRAL, reply, "Filter should return NEUTRAL for empty URI");
    }

    @Test
    @DisplayName("Test multiple factories create independent filters")
    void testMultipleFactoriesCreateIndependentFilters()
    {
        // Arrange
        HealthcheckFilterFactory factory1 = new HealthcheckFilterFactory();
        HealthcheckFilterFactory factory2 = new HealthcheckFilterFactory();

        Filter<IAccessEvent> filter1 = factory1.build();
        Filter<IAccessEvent> filter2 = factory2.build();

        IAccessEvent event = Mockito.mock(IAccessEvent.class);
        when(event.getRequestURI()).thenReturn("/admin/healthcheck");

        // Act - Exhaust filter1's counter
        for (int i = 0; i < 11; i++)
        {
            filter1.decide(event);
        }

        // Assert - filter1 should DENY, filter2 should NEUTRAL
        FilterReply reply1 = filter1.decide(event);
        FilterReply reply2 = filter2.decide(event);

        assertEquals(FilterReply.DENY, reply1, "Filter1 should DENY");
        assertEquals(FilterReply.NEUTRAL, reply2, "Filter2 should NEUTRAL on first request");
    }
}
