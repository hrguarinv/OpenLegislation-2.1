package gov.nysenate.openleg.processor.transcript;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class TranscriptLineTest {

    @Test
    public void testPageNumber() {
        TranscriptLine line = new TranscriptLine("                                 1234");
        assertTrue(line.isPageNumber());

        line = new TranscriptLine("4321");
        assertTrue(line.isPageNumber());

        // Sometimes page number starts the second line. e.g. 011299.v1, 020597.v1
        line = new TranscriptLine("55");
        assertTrue(line.isPageNumber());

        // Remove broken pipe character. e.g. 122099.v1
        line = new TranscriptLine("  �                               2301");
        assertTrue(line.isPageNumber());
    }

    @Test
    public void testRemoveLineNumber() {
        TranscriptLine line = new TranscriptLine("        21   NEW YORK STATE SENATE ");
        assertEquals("   NEW YORK STATE SENATE", line.removeLineNumber());

        line = new TranscriptLine("    22");
        assertEquals("", line.removeLineNumber().trim());

        line = new TranscriptLine("    2");
        assertEquals("", line.removeLineNumber().trim());

        line = new TranscriptLine("       THE NEW YORK SENATE");
        assertEquals("       THE NEW YORK SENATE", line.removeLineNumber());

        line = new TranscriptLine("               833");
        assertEquals("               833", line.removeLineNumber());
    }

    @Test
    public void timeTypoNotInterpretedAsLineNumber() {
        TranscriptLine line = new TranscriptLine("           10 00 a.m.");
        assertEquals("1000AM", line.getTimeString());
    }

    @Test
    public void testIsLocation() {
        TranscriptLine line = new TranscriptLine("  10      ALBANY, NEW YORK");
        assertTrue(line.isLocation());

        // e.g. 021197.v1
        line = new TranscriptLine("               ALBANY, NEW   YORK");
        assertTrue(line.isLocation());
    }

    @Test
    public void testDateHandling() {
        TranscriptLine line = new TranscriptLine("    11          March 26, 2011");
        assertTrue(line.isDate());
        assertEquals("March 26 2011", line.getDateString());

        // e.g 031699.v1
        line = new TranscriptLine("   22             Albany NEW YORK");
        assertFalse(line.isDate());

        // e.g 031699.v1
        line = new TranscriptLine("                March 16, 1999.");
        assertTrue(line.isDate());
        assertEquals("March 16 1999", line.getDateString());

        // e.g. 022894.v1
        line = new TranscriptLine("                February 28 , 1994");
        assertTrue(line.isDate());
        assertEquals("February 28 1994", line.getDateString());

        // e.g. 033193.v1
        line = new TranscriptLine("                March 31,1993");
        assertTrue(line.isDate());
        assertEquals("March 31 1993", line.getDateString());
    }

    @Test
    public void testIsTime() {
        TranscriptLine line = new TranscriptLine("   13       26, 2011");
        assertFalse(line.isTime());

        line = new TranscriptLine("   9            9:55 am");
        assertTrue(line.isTime());

        // e.g. 032611v1.TXT
        line = new TranscriptLine("                10 00 a.m.");
        assertTrue(line.isTime());

        line = new TranscriptLine("                2:00 p.m.");
        assertTrue(line.isTime());

        line = new TranscriptLine("   9            12:55 pm");
        assertTrue(line.isTime());

        // e.g. 030393.v1
        line = new TranscriptLine("             12:00 Noon");
        assertTrue(line.isTime());
    }

    @Test
    public void testGetTime() {
        TranscriptLine line = new TranscriptLine(" 10:15 a.m.");
        assertEquals("1015AM", line.getTimeString());
    }

    @Test
    public void testIsSessionType() {
        TranscriptLine line = new TranscriptLine("   12           REGULAR SESSION");
        assertTrue(line.isSession());

        line = new TranscriptLine("               EXTRAORDINARY SESSION");
        assertTrue(line.isSession());
    }

    @Test
    public void testIsEmpty() {
        TranscriptLine line = new TranscriptLine("\t    \n");
        assertTrue(line.isEmpty());

        line = new TranscriptLine("       ");
        assertTrue(line.isEmpty());
    }

    @Test
    public void testIsStenographer() {
        TranscriptLine line = new TranscriptLine("  Candyco Transcription Service, Inc. ");
        assertTrue(line.isStenographer());

        line = new TranscriptLine(" (518) 371-8910 ");
        assertTrue(line.isStenographer());
    }

    @Test
    public void properlyIgnoresInvalidCharacters() {
        TranscriptLine line = new TranscriptLine("  �                               2301");
        assertTrue(line.isPageNumber());
    }

    @Test
    public void dateTests() {
        String date = "NOVEMBER 15 2005";
        String time = "1000am";

        date = WordUtils.capitalizeFully(date);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d yyyy hhmma");
        LocalDateTime dt = LocalDateTime.parse(date + " " + time.toUpperCase(), dtf);
    }
}