package simplenem12;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class SimpleNem12ParserTest {

    private SimpleNem12ParserImpl parser;

    @Before
    public void setUp() throws Exception {
        this.parser = new SimpleNem12ParserImpl();
    }

    @Test
    public void testParseSimpleNem12Success() {
        File f = new File("./src/test/resources/simplenem12.csv");
        assertTrue("Cannot find " + f, f.exists());

        Collection<MeterRead> list = parser.parseSimpleNem12(f);

        assertEquals(2, list.size());
        Iterator<MeterRead> i = list.iterator();
        MeterRead nmi1 = i.next();
        assertEquals("6123456789", nmi1.getNmi());
        assertEquals(new BigDecimal("-36.84"), nmi1.getTotalVolume());

        MeterRead nmi2 = i.next();
        assertEquals("6987654321", nmi2.getNmi());
        assertEquals(new BigDecimal("14.33"), nmi2.getTotalVolume());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseSimpleNem12Missing100() {
        File f = new File("./src/test/resources/simplenem12_100.csv");

        parser.parseSimpleNem12(f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseSimpleNem12Missing900() {
        File f = new File("./src/test/resources/simplenem12_900.csv");

        parser.parseSimpleNem12(f);
    }
}
