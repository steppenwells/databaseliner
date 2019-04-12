package org.databaseliner.output;

import org.junit.Test;

import static org.junit.Assert.*;

public class RawToHexOutputterTest {

    @Test
    public void asSqlString() {
        RawToHexOutputter outputter = new RawToHexOutputter();

        assertEquals("HEXTORAW('05140301')", outputter.asSqlString(new byte[]{5, 20, 3, 1}));
    }

}