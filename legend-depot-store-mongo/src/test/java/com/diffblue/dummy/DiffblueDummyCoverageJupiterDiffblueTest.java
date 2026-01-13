package com.diffblue.dummy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DiffblueDummyCoverageJupiterDiffblueTest {
    @Test
    public void testDoDummy() {
        DiffblueDummyCoverageClass dummy = new DiffblueDummyCoverageClass();
        assertEquals(26, dummy.doDummy(false));
    }
}