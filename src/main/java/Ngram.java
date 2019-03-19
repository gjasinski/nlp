

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class Ngram {
    private static List<String> enlishInput = new ArrayList<>(Arrays.asList("english1.txt", "english2.txt", "english3.txt", "english4.txt"));
    private static List<String> finnishInput = new ArrayList<>(Arrays.asList("finnish1.txt", "finnish2.txt"));
    private static List<String> germanInput = new ArrayList<>(Arrays.asList("german1.txt", "german2.txt", "german3.txt", "german4.txt"));
    private static List<String> italianInput = new ArrayList<>(Arrays.asList("italian1.txt", "italian2.txt"));
    private static List<String> polishInput = new ArrayList<>(Arrays.asList("polish1.txt", "polish2.txt", "polish3.txt"));
    private static List<String> spanishInput = new ArrayList<>(Arrays.asList("spanish1.txt", "spanish2.txt"));
    private static Map<String, Map<String, Integer>> languageNgramsStats = new HashMap<>();

//    private static List<String> text = new ArrayList<>(Collections.singletonList("Litwo! Ojczyzno moja! Ty jesteś jak zdrowie. Ile cię stracił. Dziś piękność twą w tylu brzemienna imionami rycerzy, od dzisiaj nie korzystał dworze jak on zająca pochwycił. Asesor zaś Gotem. Dość, że teraz za nim. Sława czynów tylu szlachty, w powiecie. Lubił bardzo myślistwo, już im pokazał wyprutą z Paryża a my razem ja i Obuchowicz Piotrowski, Obolewski, Rożycki, Janowicz, Mirzejewscy, Brochocki i poplątane, w polskiej szacie siedzi jak wiśnie bliźnięta. U tej krucze, długie paznokcie przedstawiając dwa kruki jednym z okien - nowe o nich opis zwycięstwa lub zgonu. Po cóż by przy jego wiernym ludem! Jak mnie dziecko do dworu. Tu Kościuszko w prawo psy głupie a ubiór zwrócił oczy. wszyscy dokoła brali stronę Kusego, albo sam przyjmować i stryjaszkiem jedno puste miejsce za nim. Sława czynów tylu lat kilku dzieje domowe powiatu dawano przez wzgląd na prawo, koziołka, z rzadka ciche szmery a pani ta chwała należy chartu Sokołowi. Pytano zdania bo tak nazywano młodzieńca, który ma dotąd pierwsze zamiary odmienił kazał, aby w pośrodku zamczyska którego widne były czary przeciw czarów. Raz w guberskim rządzie. Wreszcie."));
    private static List<String> text = new ArrayList<>(Collections.singletonList("She still hopes to ultimately get it in front of MPs for a third go, but says even if that happens and they vote in favour of it, the UK will need a short extension to get the necessary legislation through Parliament.\n" +
        "\n" +
        "A cabinet source told the BBC she therefore plans to ask the EU to agree to postpone the UK's departure until 30 June, but with an option of a longer delay as well."));

    public Ngram() {
        for (int i = 2; i < 20; i++) {
            System.out.println("i = " + i);
            final int ngram = i;
            double best = 1;
            String result="";
            Map<String, Integer> testedNgram = new HashMap<>();
            buildNgram(ngram, testedNgram, text.stream());

            double englishMetric = createNgramAndCalcCosMetric(ngram, testedNgram, enlishInput);
            if(englishMetric < best){
                result = "english";
                best = englishMetric;
            }
            double finnishMetric = createNgramAndCalcCosMetric(ngram, testedNgram, finnishInput);
            if(finnishMetric < best){
                result = "finnish";
                best = finnishMetric;
            }
            double germanMetric = createNgramAndCalcCosMetric(ngram, testedNgram, germanInput);
            if(germanMetric < best){
                result = "german";
                best = germanMetric;
            }
            double italianMetric = createNgramAndCalcCosMetric(ngram, testedNgram, italianInput);
            if(italianMetric < best){
                result = "italian";
                best = italianMetric;
            }
            double polishMetric = createNgramAndCalcCosMetric(ngram, testedNgram, polishInput);
            if(polishMetric < best){
                result = "polish";
                best = polishMetric;
            }
            double spanishMetric = createNgramAndCalcCosMetric(ngram, testedNgram, spanishInput);
            if(spanishMetric < best){
                result = "spanish";
                best = spanishMetric;
            }
            System.out.println(result);

        }
    }

    private double createNgramAndCalcCosMetric(int ngram, Map<String, Integer> testedNgram, List<String> input) {
        Map<String, Integer> ngramMap = new HashMap<>();
        input.forEach(file -> readFileAndBuildNgram(file, ngram, ngramMap));
        double metric = calcCosMetric(testedNgram, ngramMap);
        System.out.println(ngramMap.size() + " " + metric);
        return metric;
    }

    public static void main(String[] args) {
        new Ngram();
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


    private double calcCosMetric(Map<String, Integer> mapA, Map<String, Integer> mapB){
        Integer reduceA = mapA.values().stream().reduce(0, (a, b) -> a + b);
        Integer reduceB = mapB.values().stream().reduce(0, (a, b) -> a + b);
        double divider = (double)(reduceA * reduceB);
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
