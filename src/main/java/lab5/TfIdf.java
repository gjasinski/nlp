package lab5;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TfIdf {
    private final String path;
    private final Map<Integer, Map<String, Integer>> articlesWordOccurences = new HashMap<>();
    private final Map<String, Integer> globalWordOccurences = new HashMap<>();
    private final Map<Integer, Map<String, Double>> tfIdfMap = new HashMap<>();
    private final Map<Integer, String> articles = new HashMap<>();
    private int numberOdAllDocuments;
    public TfIdf(String path) {
        this.path = path;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        readFile(path)
                .forEach(line -> {
                    if (line.startsWith("#")) {
                        int i = atomicInteger.incrementAndGet();
                        articlesWordOccurences.put(i, new HashMap<>());
                        articles.put(i, "");
                    } else {
                        Map<String, Integer> localMap = articlesWordOccurences.get(atomicInteger.get());
                        String cleanLine = line.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", "")
                                .toLowerCase();
                        articles.put(atomicInteger.get(), articles.get(atomicInteger.get()) + cleanLine);
                        String[] words = cleanLine.split(" ");
                        for (int i = 0; i < words.length; i++) {
                            if (!localMap.containsKey(words[i])) {
                                if (globalWordOccurences.containsKey(words[i])) {
                                    globalWordOccurences.put(words[i], globalWordOccurences.get(words[i]) + 1);
                                } else {
                                    globalWordOccurences.put(words[i], 1);
                                }
                            }
                            if (localMap.containsKey(words[i])) {
                                localMap.put(words[i], localMap.get(words[i]) + 1);
                            } else {
                                localMap.put(words[i], 1);
                            }
                        }
                    }
                });
        numberOdAllDocuments = atomicInteger.get();
        atomicInteger.set(0);
        readFile(path)
                .forEach(line -> {
                    if (line.startsWith("#")) {
                        atomicInteger.incrementAndGet();
                    } else {
                        Map<String, Integer> localMap = articlesWordOccurences.get(atomicInteger.get());
                        Map<String, Double> localTfIdfMap;
                        if(tfIdfMap.containsKey(atomicInteger.get())){
                            localTfIdfMap = tfIdfMap.get(atomicInteger.get());
                        }
                        else {
                             localTfIdfMap = new HashMap<>();
                        }
                        String[] words = line.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", "")
                                .toLowerCase()
                                .split(" ");
                        for (int i = 0; i < words.length; i++) {
                            double tfidf = localMap.get(words[i]) * Math.log(numberOdAllDocuments / globalWordOccurences.get(words[i]));
                            localTfIdfMap.put(words[i], tfidf);
                        }
                        tfIdfMap.put(atomicInteger.get(), localTfIdfMap);
                    }
                });
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

    private double calcDistance(String text, int art1, int art2){
        Map<String, Double> tfIdfMap1 = tfIdfMap.get(art1);
        Map<String, Double> tfIdfMap2 = tfIdfMap.get(art2);
        return Arrays.stream(text.split(" "))
                .map(s -> tfIdfMap1.getOrDefault(s, 0.0) * tfIdfMap2.getOrDefault(s, 0.0))
                .reduce(Double::sum).get();
    }

    public void findSimillar(int articleNumber, int numberOfFindings, List<Integer> simillarArticles1, List<Integer> simillarArticles2) {
        String article = articles.get(articleNumber);
        List<Tuple<Double, Integer>> distanceList = new LinkedList<>();
        for(int i = 1; i < numberOdAllDocuments; i++){
            if(i == articleNumber){
                continue;
            }
            double distance = calcDistance(article, articleNumber, i);
            if(distance > 0) {
                distanceList.add(new Tuple<>(distance, i));
            }
        }
        distanceList.sort((s1, s2) -> Double.compare(s2.getKey(), s1.getKey()));
        int true_positive = 0;
        int false_positive = 0;
        int false_negative = 0;
        for(int i = 0; i < numberOfFindings; i++){
            Tuple<Double, Integer> doubleIntegerTuple = distanceList.get(i);
            System.out.println("distance " + doubleIntegerTuple.getKey() + " article: " + doubleIntegerTuple.getValue());
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
        TfIdf tfIdf = new TfIdf("pap.txt");
        tfIdf.findSimillar(27708, 10, similarArticles1, similarArticles2);
        //tfIdf.findSimillar(23473, 10, similarArticles2, similarArticles1);
    }
}
