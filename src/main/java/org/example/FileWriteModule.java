package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class FileWriteModule {

    public static void writeStringsToFile(Vector<String> strings, String filePath) {
        try {
            // Create a File object
            File file = new File(filePath);

            // Create a BufferedWriter for efficient writing
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

            // Iterate through the strings and write them to the file
            for (String str : strings) {
                writer.write(str);
                writer.newLine(); // Add a new line after each string
            }

            // Close the BufferedWriter to release resources
            writer.close();

            System.out.println("Strings have been written to the file.");

        } catch (IOException e) {
            // Handle IO exceptions
            e.printStackTrace();
        }
    }
}
