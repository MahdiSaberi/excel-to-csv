package ir.data;

import com.opencsv.CSVWriter;
import org.dhatim.fastexcel.reader.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Converter {

    private String filename;

    public Converter(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void convert() throws IOException {
        File file = new File(filename);
        InputStream inputStream = new FileInputStream(file);

        System.out.println("Opening file: " + "[" + filename + "]");
        ReadableWorkbook workbook = new ReadableWorkbook(inputStream);

        Stream<Sheet> sheets = workbook.getSheets();
        AtomicInteger sheetCounter = new AtomicInteger(1);
        if (sheets != null) {
            System.out.println("Converting file: " + "[" + filename + "] to CSV...");
            sheets.forEach(

                    sheet -> {
                        CSVWriter csvWriter;
                        FileWriter outputFile;
                        try {

                            System.out.println("Opening sheet[" + sheetCounter.get() + "]");
                            outputFile = new FileWriter(filename + "-" + sheetCounter + ".csv");
                            csvWriter = new CSVWriter(outputFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try (Stream<Row> rows = sheet.openStream()) {
                            rows.forEach(

                                    row -> {

                                        List<String[]> data = new ArrayList<>();
                                        List<String> cells = new ArrayList<>();
                                        int nullCells = 0;

                                        for (int i = 0; i < row.getCellCount(); i++) {
                                            Cell cell = row.getCell(i);

                                            if (!Objects.nonNull(cell) || cell.getType() == CellType.EMPTY) {
                                                nullCells++;
                                                cells.add(null);

                                            } else {
                                                cells.add(cell.getText().toString());
                                            }

                                        }
                                        if (nullCells != row.getCellCount()) {
                                            data.add(cells.toArray(new String[0]));
                                            csvWriter.writeAll(data);
                                        }

                                    }
                            );

                        } catch (Exception e) {
                            //todo
                        }
                        try {
                            csvWriter.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        sheetCounter.getAndIncrement();
                    }

            );

            new Thread(
                    () -> {
                        try {
                            workbook.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).start();

            System.out.println("Converted successfully.");
        }
    }
}
