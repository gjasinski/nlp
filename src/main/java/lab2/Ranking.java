package lab2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ranking {
    private Map<String, String> wordToBaseForm = new HashMap<>();
    private Map<String, Long> wordCounter = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new Ranking();
    }

    public Ranking() throws IOException {
        System.out.println("yolo");
        readFileAndMapping("lab2/odm.txt");
        readFileAndCount("lab2/potop.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/lab2/potopWords.txt"));
        sort().stream().forEach(s -> {
            try {
                writer.write(s.getKey() + " " + s.getValue() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

    private void readFileAndMapping(String fileName) {
        try {
            readFile(fileName).forEach(this::createWordToBaseFormMapping);
        } catch (Exception ex) {
            System.out.println(fileName + " " + ex.toString());
            throw new IllegalArgumentException(ex);
        }
    }

    private void readFileAndCount(String fileName) {
        try {
            readFile(fileName)
                    .map(line -> line.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", ""))
                    .map(line -> line.split(" "))
                    .flatMap(Arrays::stream)
                    .filter(w -> w.length() > 0)
                    .map(String::toLowerCase)
//                    .peek(s -> System.out.print(s + " "))
                    .forEach(word -> wordCounter.put(word, wordCounter.getOrDefault(word, 0L) + 1));
        } catch (Exception ex) {
            System.out.println(fileName + " " + ex.toString());
            throw new IllegalArgumentException(ex);
        }
    }

    private Map<String, Integer> readFileAndBuildNgram(){

    }
    private void buildNgram(int ngramSize, Map<String, Integer> ngramMap, Stream<String> lines) {
        lines.map(String::toLowerCase)
                .map(line -> line.replaceAll("[^a-zA-Z ]", ""))
                .map(line -> line.split(" "))
                .flatMap(Arrays::stream)
                .map(word -> splitWord(word, ngramSize))
                .flatMap(Collection::stream)
                .forEach(ngram -> {
                    if (ngramMap.containsKey(ngram)) {
                        ngramMap.put(ngram, ngramMap.get(ngram) + 1);
                    } else {
                        ngramMap.put(ngram, 1);
                    }
                });
    }

    private List<Map.Entry<String, Long>> sort(){
        return wordCounter.entrySet().stream()
//                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .sorted((s1, s2) -> Long.compare(s2.getValue(), s1.getValue()))
                .collect(Collectors.toList());
    }


    private void createWordToBaseFormMapping(String line) {
        String[] split = line.split(", ");
        for (String aSplit : split) {
            wordToBaseForm.put(aSplit.toLowerCase(), split[0].toLowerCase());
        }
    }

    private Stream<String> readFile(String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            return Files.lines(FileSystems.getDefault().getPath(file.getAbsolutePath()), Charset.forName("UTF-8"));
        } catch (Exception ex) {
            System.out.println(fileName + " " + ex.toString());
            throw new IllegalArgumentException(ex);
        }
    }

}
