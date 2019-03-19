

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Ngram {
    private static List<String> enlishInput = new ArrayList<>(Arrays.asList("english1.txt", "english2.txt", "english3.txt", "english4.txt"));
    private static List<String> finnishInput = new ArrayList<>(Arrays.asList("finnish1.txt", "finnish2.txt"));
    private static List<String> germanInput = new ArrayList<>(Arrays.asList("german1.txt", "german2.txt", "german3.txt", "german4.txt"));
    private static List<String> italianInput = new ArrayList<>(Arrays.asList("italian1.txt", "italian2.txt"));
    private static List<String> polishInput = new ArrayList<>(Arrays.asList("polish1.txt", "polish2.txt", "polish3.txt"));
    private static List<String> spanishInput = new ArrayList<>(Arrays.asList("spanish1.txt", "spanish2.txt"));
    private static Map<String, Map<String, Integer>> languageNgramsStats = new HashMap<>();


    public Ngram() {
        Map<String, Integer> englishMap = new HashMap<>();
        enlishInput.stream().forEach(file -> buildNgram(file, 2, englishMap));
        Map<String, Integer> finnishMap = new HashMap<>();
        finnishInput.stream().forEach(file -> buildNgram(file, 2, finnishMap));
//        Map<String, Integer> germanMap = new HashMap<>();
//        germanInput.stream().forEach(file -> buildNgram(file, 2, germanMap));
//        Map<String, Integer> italianMap = new HashMap<>();
//        italianInput.stream().forEach(file -> buildNgram(file, 2, italianMap));
//        Map<String, Integer> polishMap = new HashMap<>();
//        polishInput.stream().forEach(file -> buildNgram(file, 2, polishMap));
//        Map<String, Integer> spanishMap = new HashMap<>();
//        spanishInput.stream().forEach(file -> buildNgram(file, 2, spanishMap));
    }

    public static void main(String[] args) {
        new Ngram();
    }

    private void buildNgram(String fileName, int ngramSize, Map<String, Integer> ngramMap) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            Files.lines(FileSystems.getDefault().getPath(file.getAbsolutePath()), Charset.forName("UTF-8"))
                    .peek(s -> System.out.println(s))
                    .map(String::toLowerCase)
                    .map(line -> line.replaceAll("[^a-zA-Z ]", ""))
                    .peek(s -> System.out.println(s))
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
        catch (Exception ex){
            System.out.println(fileName + " " + ex.toString());
            //ex.printStackTrace();
            throw new IllegalArgumentException(ex);
        }
    }

    private List<String> splitWord(String word, int ngramSize){
        List<String> result = new LinkedList<>();
        for(int i = 0; i + ngramSize < word.length(); i++){
            result.add(word.substring(i, i + ngramSize));
        }
        return result;
    }

}
