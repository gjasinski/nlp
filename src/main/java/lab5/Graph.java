package lab5;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
    private final static int K = 2;
    private final String path;
    private final Map<Integer, String> articles = new HashMap<>();
    private final Map<Integer, Map<String, Map<String, Integer>>> graphs = new HashMap<>();

    public Graph(String path) {
        this.path = path;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        readFile(path)
                .forEach(line -> {
                    if (line.startsWith("#")) {
                        int i = atomicInteger.incrementAndGet();
                        articles.put(i, "");
                    } else {
                        String cleanLine = line.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", "")
                                .toLowerCase();
                        articles.put(atomicInteger.get(), articles.get(atomicInteger.get()) + cleanLine);
                    }
                });
        articles.entrySet()
                .forEach(e -> {
                    graphs.put(e.getKey(), new HashMap<>());
                    Map<String, Map<String, Integer>> graph = graphs.get(e.getKey());
                    String[] splited1 = e.getValue().split(" ");
                    List<String> splited = Arrays.stream(splited1).filter(s -> s.length() > 1).collect(Collectors.toList());
                    for (int i = 0; i < splited.size(); i++) {
                        Map<String, Integer> stringIntegerMap = graph.get(splited.get(i));
                        if (stringIntegerMap == null) {
                            stringIntegerMap = new HashMap<>();
                            graph.put(splited.get(i), stringIntegerMap);
                        }
                        for (int j = i + 1; j < splited.size() && j < i + K; j++) {
                            Integer integer = stringIntegerMap.get(splited.get(j));
                            if (integer == null) {
                                stringIntegerMap.put(splited.get(j), 1);
                            } else {
                                stringIntegerMap.put(splited.get(j), integer + 1);
                            }
                        }
                    }
                });
    }


    private void findSimmilar(Integer articleNumber, int numberOfArticles, List<Integer> simillarArticles1, List<Integer> simillarArticles2) {
        Map<String, Map<String, Integer>> article = graphs.get(articleNumber);
        List<Tuple<Double, Integer>> distanceList = new LinkedList<>();
        graphs.entrySet().forEach(e -> {
            if (!articleNumber.equals(e.getKey())) {
                double distance = calcDistance(article, e.getValue());
                distanceList.add(new Tuple<>(distance, e.getKey()));
            }
        });
        distanceList.sort((s1, s2) -> Double.compare(s1.getKey(), s2.getKey()));
        int true_positive = 0;
        int false_positive = 0;
        int false_negative = 0;
        for (int i = 0; i < numberOfArticles; i++) {
            Tuple<Double, Integer> doubleIntegerTuple = distanceList.get(i);
            System.out.println("distance" + doubleIntegerTuple.getKey() + " article: " + doubleIntegerTuple.getValue());
            if(simillarArticles1.contains(doubleIntegerTuple.getValue())){
                true_positive++;
            }
            else {
                false_positive++;
            }
            if(simillarArticles2.contains(doubleIntegerTuple.getValue())){
                false_negative++;
            }
        }
        double precision = (double) true_positive / (double) (true_positive + false_positive);
        double recall = (double) true_positive / (double) (true_positive + false_negative);
        double f1 = 2.0 * recall * precision / (precision + recall);
        System.out.println("Precision: " +precision + " recall: " + recall + " F1: " + f1);
    }

    private double calcDistance(Map<String, Map<String, Integer>> articleA, Map<String, Map<String, Integer>> articleB) {
        return articleA.entrySet()
                .stream()
                .map(e -> {
                    Map<String, Integer> wordsB = articleB.get(e.getKey());
                    if (wordsB == null) {
                        return 1.0;
                    } else {
                        Map<String, Integer> wordsA = e.getValue();
                        Integer up = wordsA.entrySet().stream()
                                .map(ee -> {
                                    Integer integer = wordsB.get(ee.getKey());
                                    if (integer == null) {
                                        return 0;
                                    } else {
                                        return integer * ee.getValue();
                                    }
                                })
                                .reduce(Integer::sum)
                                .orElseGet(() -> 0);
                        int down = wordsB.size() * wordsA.size();
                        return 1.0 - (double) up / (double) down;
                    }
                })
                .reduce(Double::sum)
                .get();
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

    public static void main(String[] args) {
        List<Integer> similarArticles1 = new LinkedList<>();
        similarArticles1.add(2354);
        similarArticles1.add(25597);
        similarArticles1.add(24736);
        similarArticles1.add(33968);
        similarArticles1.add(891);
        similarArticles1.add(34281);
        similarArticles1.add(26240);
        similarArticles1.add(34296);
        similarArticles1.add(27710);
        similarArticles1.add(28344);
        similarArticles1.add(34281);
        similarArticles1.add(26123);
        similarArticles1.add(24736);
        similarArticles1.add(25598);
        similarArticles1.add(28800);
        List<Integer> similarArticles2 = new LinkedList<>();
        similarArticles2.add(26655);
        similarArticles2.add(1424);
        similarArticles2.add(42111);
        similarArticles2.add(14940);
        similarArticles2.add(32717);
        similarArticles2.add(30034);


        Graph graph = new Graph("pap.txt");
        //graph.findSimmilar(27708, 10, similarArticles1, similarArticles2);
        graph.findSimmilar(23473, 10, similarArticles2, similarArticles1);
    }
}



