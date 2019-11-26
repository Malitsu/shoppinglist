/*package fi.tuni.tiko.objectorientedprogramming;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    List<String> lines;

    public Parser() {
        lines = new LinkedList<>();
        lines.add("{");
        lines.add("}");
    }

    public void addItem(Item item) {
        String newLine = "  \"" +item.getLabel() +"\": \"" +item.getAmount() +"\",";
        lines.add(lines.size()-1, newLine);
    }

    public void writeToFile() {
        try {
            Path file = Paths.get("save.json");
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch(Exception e) { e.printStackTrace(); }
    }
}*/
