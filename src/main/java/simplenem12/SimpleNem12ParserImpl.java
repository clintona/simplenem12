/**
 * 
 */
package simplenem12;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Parse a simplified NEM12 format file, a type of CSV file.
 * This parser is stateful - do not share instances across multiple threads.
 * 
 * @author Clinton
 *
 */
public class SimpleNem12ParserImpl implements SimpleNem12Parser {

    private int lineNumber = 0;
    DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;

    private static String FIRST_LINE_100_MSG = "The first line must be a 100 record";

    /*
     * (non-Javadoc)
     * 
     * @see simplenem12.SimpleNem12Parser#parseSimpleNem12(java.io.File)
     */
    @Override
    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {

        List<MeterRead> readings = new ArrayList<MeterRead>();
        // note: try-with-resources auto closes Readers
        try (BufferedReader br = new BufferedReader(new FileReader(simpleNem12File))) {
            String line;
            Nem12RecordType type = null;
            while ((line = br.readLine()) != null) {
                type = parseLine(line, readings);
            }
            if (type != Nem12RecordType.NEM12_900) {
                throw new IllegalArgumentException("Expected 900 record on last line");
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        return readings;
    }

    /**
     * Parse a NEM12 line item, adding any new readings.
     * 
     * @param line CSV line from a simplified NEM12 file
     * @param readings Collection<MeterRead>
     * @return NEM12RecordType of parsed line
     * @throws IllegalArgumentException if the record type is unknown
     */
    Nem12RecordType parseLine(String line, List<MeterRead> readings) {
        this.lineNumber++;
        String[] tokens = line.split(",");
        Nem12RecordType type = Nem12RecordType.valueOf("NEM12_" + tokens[0].trim());

        switch (type) {
        case NEM12_100:
            parse100Record(tokens, readings);
            break;
        case NEM12_200:
            parse200Record(tokens, readings);
            break;
        case NEM12_300:
            parse300Record(tokens, readings);
            break;
        case NEM12_900:
            parse900Record(tokens, readings);
            break;
        default:
            throw new IllegalArgumentException("Unknown record type '" + type + "' on line " + lineNumber);
        }

        return type;
    };

    private void parse100Record(String[] tokens, List<MeterRead> readings) {
        if (this.lineNumber != 1) {
            throw new IllegalArgumentException(FIRST_LINE_100_MSG);
        }
    }

    private void parse200Record(String[] tokens, List<MeterRead> readings) {
        if (this.lineNumber < 2) {
            throw new IllegalArgumentException(FIRST_LINE_100_MSG);
        }
        readings.add(new MeterRead(tokens[1], EnergyUnit.valueOf(tokens[2])));
    }

    private void parse300Record(String[] tokens, List<MeterRead> readings) {

        // fetch the current reading from the Stack of readings
        MeterRead nmi = readings.get(readings.size() - 1);
        if (nmi == null) {
            throw new IllegalArgumentException("Expected 200 record on line " + (this.lineNumber - 1));
        }
        LocalDate date = LocalDate.parse(tokens[1], dateFormatter);
        MeterVolume reading = new MeterVolume(new BigDecimal(tokens[2]), Quality.valueOf(tokens[3]));
        nmi.appendVolume(date, reading);
    }

    private void parse900Record(String[] tokens, List<MeterRead> readings) {
        if (this.lineNumber < 2) {
            throw new IllegalArgumentException(FIRST_LINE_100_MSG);
        }
    }

}
