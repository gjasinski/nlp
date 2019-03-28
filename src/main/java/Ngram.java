import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ngram {
    private static Map<Key, Integer> truePositive = new HashMap<>();
    private static Map<Key, Integer> falsePositive = new HashMap<>();
    private static Map<Key, Integer> trueNegative = new HashMap<>();
    private static Map<Key, Integer> falseNegative = new HashMap<>();
    private static List<String> enlishInput = new ArrayList<>(Arrays.asList("english1.txt", "english2.txt", "english3.txt", "english4.txt"));
    private static List<String> finnishInput = new ArrayList<>(Arrays.asList("finnish1.txt", "finnish2.txt"));
    private static List<String> germanInput = new ArrayList<>(Arrays.asList("german1.txt", "german2.txt", "german3.txt", "german4.txt"));
    private static List<String> italianInput = new ArrayList<>(Arrays.asList("italian1.txt", "italian2.txt"));
    private static List<String> polishInput = new ArrayList<>(Arrays.asList("polish1.txt", "polish2.txt", "polish3.txt"));
    private static List<String> spanishInput = new ArrayList<>(Arrays.asList("spanish1.txt", "spanish2.txt"));

    public Ngram(List<String> texts, String language) {
        for (int i = 2; i < 11; i++) {
            System.out.println("i = " + i);
            final int ngram = i;
            Map<String, Map<String, Integer>> ngramCache = new HashMap<>();
            ngramCache.put("english", calculateNgramMap(enlishInput, i));
            ngramCache.put("finnish", calculateNgramMap(finnishInput, i));
            ngramCache.put("german", calculateNgramMap(germanInput, i));
            ngramCache.put("italian", calculateNgramMap(italianInput, i));
            ngramCache.put("polish", calculateNgramMap(polishInput, i));
            ngramCache.put("spanish", calculateNgramMap(spanishInput, i));


            long count = texts.stream()
                    .map(text -> {
                        String detectedLanguage = calcAndPrintResult(ngram, text, ngramCache);
                        boolean detectedCorretly = detectedLanguage.equals(language);
                        Key expectedLanguageKey = new Key(ngram, language);
                        Key detectedLanguageKey = new Key(ngram, detectedLanguage);
                        if (detectedCorretly) {
                            if (truePositive.containsKey(expectedLanguageKey)) {
                                truePositive.put(expectedLanguageKey, truePositive.get(expectedLanguageKey) + 1);
                            } else {
                                truePositive.put(expectedLanguageKey, 1);
                            }
                        } else {
                            if (falsePositive.containsKey(detectedLanguageKey)) {
                                falsePositive.put(detectedLanguageKey, falsePositive.get(detectedLanguageKey) + 1);
                            } else {
                                falsePositive.put(detectedLanguageKey, 1);
                            }
                            if (falseNegative.containsKey(expectedLanguageKey)) {
                                falseNegative.put(expectedLanguageKey, falseNegative.get(expectedLanguageKey) + 1);
                            } else {
                                falseNegative.put(expectedLanguageKey, 1);
                            }
                        }
                        return detectedCorretly;
                    }).filter(v -> v).

                            count();
            System.out.println(count + " / " + texts.size());
            new

                    Key(ngram, language);
        }

    }

    private Map<String, Integer> calculateNgramMap(List<String> input, int ngramSize) {
        Map<String, Integer> ngramMap = new HashMap<>();
        input.forEach(file -> readFileAndBuildNgram(file, ngramSize, ngramMap));
        return ngramMap;
    }

    private String calcAndPrintResult(int ngramSize, String text, Map<String, Map<String, Integer>> cacheNgramMap) {
        Map<String, Integer> calculatedNgramMap = new HashMap<>();
        buildNgram(ngramSize, calculatedNgramMap, Stream.of(text));

        double best = 1;
        String result = "";

        double englishMetric = calcCosMetric(calculatedNgramMap, cacheNgramMap.get("english"));
        if (englishMetric < best) {
            result = "english";
            best = englishMetric;
        }
        double finnishMetric = calcCosMetric(calculatedNgramMap, cacheNgramMap.get("finnish"));
        if (finnishMetric < best) {
            result = "finnish";
            best = finnishMetric;
        }
        double germanMetric = calcCosMetric(calculatedNgramMap, cacheNgramMap.get("german"));
        if (germanMetric < best) {
            result = "german";
            best = germanMetric;
        }
        double italianMetric = calcCosMetric(calculatedNgramMap, cacheNgramMap.get("italian"));
        if (italianMetric < best) {
            result = "italian";
            best = italianMetric;
        }
        double polishMetric = calcCosMetric(calculatedNgramMap, cacheNgramMap.get("polish"));
        if (polishMetric < best) {
            result = "polish";
            best = polishMetric;
        }
        double spanishMetric = calcCosMetric(calculatedNgramMap, cacheNgramMap.get("spanish"));
        if (spanishMetric < best) {
            result = "spanish";
            best = spanishMetric;
        }


        //System.out.println(result + " best: " + best);
        return result;
    }

    public static void main(String[] args) {
        if (false) {
            List<String> texts = Arrays.stream(TestedTexts.ENGLISH_TEXT.split("\\.")).filter(s -> s.length() > 1).collect(Collectors.toList());
            System.out.println("Now english");
            new Ngram(texts, "english");
            texts = Arrays.stream(TestedTexts.FINNISH_TEXT.split("\\.")).filter(s -> s.length() > 1).collect(Collectors.toList());
            System.out.println("Now finnish");
            new Ngram(texts, "finnish");
            texts = Arrays.stream(TestedTexts.GERMAN_TEXT.split("\\.")).filter(s -> s.length() > 1).collect(Collectors.toList());
            System.out.println("Now german");
            new Ngram(texts, "german");
            texts = Arrays.stream(TestedTexts.ITALIAN_TEXT.split("\\.")).filter(s -> s.length() > 1).collect(Collectors.toList());
            System.out.println("Now italian");
            new Ngram(texts, "italian");
            texts = Arrays.stream(TestedTexts.POLISH_TEXT.split("\\.")).filter(s -> s.length() > 1).collect(Collectors.toList());
            System.out.println("Now polish");
            new Ngram(texts, "polish");
            texts = Arrays.stream(TestedTexts.SPANISH_TEXT.split("\\.")).filter(s -> s.length() > 1).collect(Collectors.toList());
            System.out.println("Now spanish");
            new Ngram(texts, "spanish");
            String[] languages = new String[]{"english", "finnish", "german", "italian", "polish", "spanish"};
            for(int i = 0; i < languages.length; i++) {
                printStatisForPrecision(languages[i]);
                printStatisForRecall(languages[i]);
                printStatisForHarmonicMean(languages[i]);
                printStatisForAccuracy(languages[i]);
            }
        } else {
            String myText = "Przysiągłem smyk teraźniejsza chce Podniesionemi Zazdroszczono nikt Dąbrowskiego razu. Żytem leżą gonił Białopiotrowiczowi zginą";
            new Ngram(Collections.singletonList(myText), "");
        }
    }

    private static void printStatisForPrecision(String language) {
        System.out.println(language + " precision:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falsePositives, precision;
            if(truePositive.containsKey(key)) {
                truePositives = Ngram.truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falsePositive.containsKey(key)){
                falsePositives = Ngram.falsePositive.get(key);
            }
            else {
                falsePositives = 0;
            }
            if(truePositives + falsePositives == 0){
                precision = 0;
            }
            else {
                precision = truePositives / (truePositives + falsePositives);
            }
            System.out.println(String.format("%f", precision));
        }
    }
    private static void printStatisForRecall(String language) {
        System.out.println(language + " Recall:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falseNegatives, recall;
            if(truePositive.containsKey(key)) {
                truePositives = Ngram.truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falseNegative.containsKey(key)) {
                falseNegatives = Ngram.falseNegative.get(key);
            }
            else {
                falseNegatives = 0;
            }
            if(truePositives + falseNegatives == 0){
                recall = 0;
            }else {
                recall = truePositives / (truePositives + falseNegatives);
            }
            System.out.println(String.format("%f", recall));
        }
    }

    private static void printStatisForHarmonicMean(String language) {
        System.out.println(language + " HarmonicMean:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falsePositives, falseNegatives,precision, recall, sredniaHarmoniczna, accuracy;
            if(truePositive.containsKey(key)) {
                truePositives = Ngram.truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falsePositive.containsKey(key)){
                falsePositives = Ngram.falsePositive.get(key);
            }
            else {
                falsePositives = 0;
            }
            if(falseNegative.containsKey(key)) {
                falseNegatives = Ngram.falseNegative.get(key);
            }
            else {
                falseNegatives = 0;
            }
            if(truePositives + falseNegatives == 0){
                precision = 0;
            }
            else {
                precision = truePositives / (truePositives + falsePositives);
            }
            if(truePositives + falseNegatives == 0){
                recall = 0;
            }else {
                recall = truePositives / (truePositives + falseNegatives);
            }
            if(precision + recall == 0){
                sredniaHarmoniczna = 0;
            }
            else {
                sredniaHarmoniczna = 2 * precision * recall / (precision + recall);
            }
            System.out.println(String.format("%f", sredniaHarmoniczna));
        }
    }

    private static void printStatisForAccuracy(String language) {
        System.out.println(language + " Accuracy:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falsePositives, falseNegatives, accuracy;
            if(truePositive.containsKey(key)) {
                truePositives = Ngram.truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falsePositive.containsKey(key)){
                falsePositives = Ngram.falsePositive.get(key);
            }
            else {
                falsePositives = 0;
            }
            if(falseNegative.containsKey(key)) {
                falseNegatives = Ngram.falseNegative.get(key);
            }
            else {
                falseNegatives = 0;
            }
            if(truePositives + falsePositives + falseNegatives == 0){
                accuracy = 0;
            }
            else {
                accuracy = truePositives / (truePositives + falsePositives + falseNegatives);
            }
            System.out.println(String.format("%f", accuracy));
        }
    }

    private void readFileAndBuildNgram(String fileName, int ngramSize, Map<String, Integer> ngramMap) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            Stream<String> lines = Files.lines(FileSystems.getDefault().getPath(file.getAbsolutePath()), Charset.forName("UTF-8"));
            buildNgram(ngramSize, ngramMap, lines);
        } catch (Exception ex) {
            System.out.println(fileName + " " + ex.toString());
            throw new IllegalArgumentException(ex);
        }
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

    private List<String> splitWord(String word, int ngramSize) {
        List<String> result = new LinkedList<>();
        for (int i = 0; i + ngramSize < word.length(); i++) {
            result.add(word.substring(i, i + ngramSize));
        }
        return result;
    }


    private double calcCosMetric(Map<String, Integer> mapA, Map<String, Integer> mapB) {
        Integer reduceA = mapA.values().stream().reduce(0, (a, b) -> a + b);
        Integer reduceB = mapB.values().stream().reduce(0, (a, b) -> a + b);
        double divider = (double) (reduceA * reduceB);
        return 1 - mapA.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    Integer aValue = entry.getValue();
                    Integer bValue = mapB.get(key);
                    if (bValue != null) {
                        return ((double) aValue * (double) bValue) / divider;
                    } else {
                        return 0.0;
                    }
                })
                .reduce(0.0, (a, b) -> a + b);
    }

}
