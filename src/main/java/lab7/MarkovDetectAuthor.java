package lab7;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarkovDetectAuthor {
    //  public static int SIZE = 1;

    public static void main(String[] args) throws IOException {
//        Map<String, Map<String, Map<String, Integer>>> authorToMarkovModel = new HashMap<>();
        //    for (int i = 1; i < 2; i++) {
        AtomicInteger hit = new AtomicInteger(0);
        AtomicInteger miss = new AtomicInteger(0);
        final int SIZE = 8;
        Map<String, Integer> counter = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get("/home/grzegorz/git/nlp/src/main/resources/lab7_validation/"))) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains("-"))
                    .peek(path -> {
                        try {
                            Map<String, Map<String, Integer>> markovModel = new HashMap<>();
                            markovForFile(markovModel, path.toAbsolutePath().toString(), SIZE);
                            Map<String, String> mostUsedWords = markovOnlyMostUsedWords(markovModel);
                            AtomicReference<Double> atomicDouble = new AtomicReference<>(Double.MAX_VALUE);
                            AtomicReference<String> atomicPath = new AtomicReference<>();
                            try (Stream<Path> pathsWithMarkovs = Files.walk(Paths.get("/home/grzegorz/git/nlp/src/main/resources/lab7_out/" + SIZE))) {
                                pathsWithMarkovs.forEach(p -> {
                                    try {
                                        //System.out.println(p);
                                        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(p.toAbsolutePath().toString()));
                                        Map<String, Map<String, Integer>> calculatedMarkovModel = (Map) oin.readObject();
                                        double compared = compareTwoMarkovs(mostUsedWords, calculatedMarkovModel);
                                        if (compared < atomicDouble.get()) {
                                            atomicDouble.set(compared);
                                            atomicPath.set(p.getFileName().toString());
                                            System.out.println(compared + " < " + atomicDouble.get() + " " + p.getFileName());
                                        }
                                        else {
                                            System.out.println(compared + " > " + atomicDouble.get() + " " + p.getFileName());
                                        }
                                    } catch (Exception ex) {
                                        System.out.println("here should be file which is serialized" + ex.toString());
                                    }
                                });
                            }
                            System.out.println("WYBIERAM: " + atomicDouble.get() + " " + atomicPath.get());

                            String file = path.getFileName().toString();
                            String author = file.substring(0, file.indexOf("-")).replaceAll("_", "");

                            String selectedAuthor = atomicPath.get();
                            if (selectedAuthor.contains(author)) {
                                hit.incrementAndGet();
                                System.out.println("HIT " + author + " " + selectedAuthor);
                            } else {
                                miss.incrementAndGet();
                                System.out.println("MISS " + author + " " + selectedAuthor);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .forEach(System.out::println);
        }
        //}

        System.out.println("################RESULTS FOR: " + SIZE);
        System.out.println(String.format("%d, %d", hit.get(), miss.get() + hit.get()));

    }

    private static void markovForFile(Map<String, Map<String, Integer>> markovModel, String filePath, int keySize) throws IOException {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        List<String> collect = Arrays.stream(new String(bytes).trim().split(" "))
                .map(s -> s.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", ""))
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());
        String[] words = new String[collect.size()];
        words = collect.toArray(words);

        for (int i = 0; i < (words.length - keySize); ++i) {
            StringBuilder key = new StringBuilder(words[i]);
            for (int j = i + 1; j < i + keySize; ++j) {
                key.append(' ').append(words[j]);
            }
            String value = (i + keySize < words.length) ? words[i + keySize] : "";
            if (!markovModel.containsKey(key.toString())) {
                Map<String, Integer> map = new HashMap<>();
                map.put(value, 1);
                markovModel.put(key.toString(), map);
            } else {
                Map<String, Integer> stringIntegerMap = markovModel.get(key.toString());
                if (stringIntegerMap.containsKey(value)) {
                    stringIntegerMap.put(value, stringIntegerMap.get(value) + 1);
                } else {
                    stringIntegerMap.put(value, 1);
                }
            }
        }
    }

    private static Map<String, String> markovOnlyMostUsedWords(Map<String, Map<String, Integer>> markovModel) {
        Map<String, String> map = new HashMap<>();
        markovModel.entrySet()
                .forEach(e -> {
                    final AtomicInteger max = new AtomicInteger(0);
                    final AtomicReference<String> str = new AtomicReference<>();
                    e.getValue().entrySet().forEach(ei -> {
                        if (ei.getValue() > max.get()) {
                            max.set(ei.getValue());
                            str.set(ei.getKey());
                        }
                    });
                    map.put(e.getKey(), str.get());
                });
        return map;
    }

    private static double compareTwoMarkovs(Map<String, String> markovModelA, Map<String, Map<String, Integer>> markovModelB) throws IOException {
        return markovModelA.entrySet().stream()
                .map(e -> calcDist(e.getValue(), markovModelB.get(e.getKey())))
                .reduce(Double::sum)
                .map(Math::sqrt)
                .get();
    }

    private static double calcDist(String value, Map<String, Integer> values) {
        if (values == null) {
            return 1;
        }
        final AtomicInteger max = new AtomicInteger(0);
        final AtomicInteger count = new AtomicInteger(0);
        Integer searched = values.get(value);
        if (searched == null) {
            return 1;
        } else {
            values.forEach((key, currVal) -> {
                if (currVal > max.get()) {
                    max.addAndGet(currVal);
                }
                count.addAndGet(currVal);
            });
        }
        if (searched == max.get()) {
            return 0;
        } else {
            return 1 - searched / max.get();
        }
    }
}
