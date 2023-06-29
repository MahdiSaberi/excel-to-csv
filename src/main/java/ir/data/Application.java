package ir.data;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {

        Converter converter = new Converter(args[0]);
        converter.convert();
    }
}
