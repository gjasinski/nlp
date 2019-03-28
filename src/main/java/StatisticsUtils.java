import java.util.HashMap;
import java.util.Map;

public class StatisticsUtils {
    private static Map<Key, Integer> truePositive = new HashMap<>();
    private static Map<Key, Integer> falsePositive = new HashMap<>();
    private static Map<Key, Integer> trueNegative = new HashMap<>();
    private static Map<Key, Integer> falseNegative = new HashMap<>();
    
    static void printStatisForPrecision(String language) {
        System.out.println(language + " precision:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falsePositives, precision;
            if(truePositive.containsKey(key)) {
                truePositives = truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falsePositive.containsKey(key)){
                falsePositives = falsePositive.get(key);
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
    static void printStatisForRecall(String language) {
        System.out.println(language + " Recall:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falseNegatives, recall;
            if(truePositive.containsKey(key)) {
                truePositives = truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falseNegative.containsKey(key)) {
                falseNegatives = falseNegative.get(key);
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

    static void printStatisForHarmonicMean(String language) {
        System.out.println(language + " HarmonicMean:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falsePositives, falseNegatives,precision, recall, sredniaHarmoniczna, accuracy;
            if(truePositive.containsKey(key)) {
                truePositives = truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falsePositive.containsKey(key)){
                falsePositives = falsePositive.get(key);
            }
            else {
                falsePositives = 0;
            }
            if(falseNegative.containsKey(key)) {
                falseNegatives = falseNegative.get(key);
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

    static void printStatisForAccuracy(String language) {
        System.out.println(language + " Accuracy:");
        for (int i = 2; i < 11; i++) {
            Key key = new Key(i, language);
            double truePositives, falsePositives, falseNegatives, accuracy;
            if(truePositive.containsKey(key)) {
                truePositives = truePositive.get(key);
            }
            else {
                truePositives = 0;
            }
            if(falsePositive.containsKey(key)){
                falsePositives = falsePositive.get(key);
            }
            else {
                falsePositives = 0;
            }
            if(falseNegative.containsKey(key)) {
                falseNegatives = falseNegative.get(key);
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

    static void updateStatistics(String language, int ngram, String detectedLanguage, boolean detectedCorretly) {
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
    }
}
