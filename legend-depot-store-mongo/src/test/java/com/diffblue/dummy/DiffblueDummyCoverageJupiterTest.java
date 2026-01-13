package com.diffblue.dummy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DiffblueDummyCoverageJupiterTest {
    @Test
    public void testDoDummy() {
        DiffblueDummyCoverageClass dummy = new DiffblueDummyCoverageClass();
        assertEquals(22, dummy.doDummy(true));
    }
}