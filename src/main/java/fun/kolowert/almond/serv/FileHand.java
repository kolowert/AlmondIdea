package fun.kolowert.almond.serv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO logger
public class FileHand {

    final String path;

    public FileHand(String path) {
        this.path = path;
    }

    public List<String> read() {
        try (Stream<String> fileChannelLinesStream = Files.lines(Paths.get(path));) {
            return fileChannelLinesStream.filter(n -> n.length() > 0).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("incorrect text path: " + path + " @read#FileHand");
            e.printStackTrace();
            List<String> stub = new ArrayList<>();
            stub.add("error-at-reading-from-file");
            return stub;
        }
    }

    public void write(List<?> items) {
        List<String> toStore = new ArrayList<>();

        for (Object item : items) {
            toStore.add(item.toString());
        }

        try {
            Files.write(Paths.get(path), toStore);
        } catch (IOException e) {
            System.out.println("incorrect path");
        }
    }

    public void write(String infa) {
        List<String> toStore = new ArrayList<>();
        toStore.add(infa);
        try {
            Files.write(Paths.get(path), toStore);
        } catch (IOException e) {
            System.out.println("incorrect path");
        }
    }

    public void write(String heading, List<?> lines) {

        List<String> result = new ArrayList<>();

        if (heading.length() > 0)
            result.add(heading);

        for (Object line : lines) {
            result.add(line.toString());
        }
        try {
            Files.write(Paths.get(path), result);
        } catch (IOException e) {
            System.out.println("incorrect path");
        }
    }

    public void write(String heading, int[][] pack) {

        List<String> linesToWrite = new ArrayList<>();

        if (heading.length() > 0)
            linesToWrite.add(heading);

        for (int[] a : pack) {
            String line = reportArrow(a);
            linesToWrite.add(line);
        }

        try {
            Files.write(Paths.get(path), linesToWrite);
            System.out.println("written to " + path);
        } catch (IOException e) {
            System.out.println("incorrect path");
        }
    }

    private String reportArrow(int[] arr) {
        if (arr == null) {
            return "input is null";
        }
        StringBuilder report = new StringBuilder();
        String punctuationMark = "";
        for (int e : arr) {
            report.append(punctuationMark).append(e);
            punctuationMark = ",";
        }
        return report.toString();
    }
}
