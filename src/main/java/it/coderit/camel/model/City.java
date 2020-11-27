package it.coderit.camel.model;


import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", skipFirstLine = true)
@Data
public class City {

    @DataField(pos = 1)
    private String name;
    @DataField(pos = 2)
    private String country;
    @DataField(pos = 3)
    private String id;
}
