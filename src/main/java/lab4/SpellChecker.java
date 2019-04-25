package lab4;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpellChecker {
    public static void main(String[] args) throws IOException {
        new SpellChecker();
    }

    public SpellChecker() throws IOException {
        List<String> inputs = new LinkedList<>();
        inputs.add("lab4/dramat.txt");
        inputs.add("lab4/popul.txt");
        inputs.add("lab4/proza.txt");
        inputs.add("lab4/publ.txt");
        inputs.add("lab4/wp.txt");

        List<String> articles = inputs.stream()
                .map(this::readFile)
                .flatMap(Function.identity())
                .map(line -> line.split(" "))
                .map(Arrays::stream)
                .flatMap(Function.identity())
                .map(String::toLowerCase)
                .map(s -> s.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", ""))
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());
        List<String> formy = readFile("lab4/formy.txt").collect(Collectors.toList());
        String word = "kżesło";
        int min = 100;
        int minIndex = -1;
        for (int i = 0; i < formy.size(); i++) {
            int distance = computeLevenshteinDistance(word, formy.get(i));
            if (distance < min) {
                min = distance;
                minIndex = i;
            }
//            System.out.println(word + " " + formy.get(i) + " " + computeLevenshteinDistance(word, formy.get(i)));
        }
        String bestForm = formy.get(minIndex);
        System.out.println(word + " " + bestForm + " " + computeLevenshteinDistance(word, bestForm));
        long bestFormOccurs = articles.stream()
                .filter(s -> s.equals(bestForm))
                .count();
        System.out.println("prawdopodobieństw: " +  bestFormOccurs + " / " + articles.size() + " = " + (bestFormOccurs / articles.size()));
    }


    private Stream<String> readFile(String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            return Files.lines(FileSystems.getDefault().getPath(file.getAbsolutePath()), Charset.forName("UTF-8"));
        } catch (Exception ex) {
//            System.out.println(fileName + " " + ex.toString());
            throw new IllegalArgumentException(ex);
        }
    }

    private int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public int computeLevenshteinDistance(String lhs, String rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        int subtract = 0;
        if(lhs.contains("rz") && rhs.contains("ż")){
            if(lhs.indexOf("rz") == rhs.indexOf("ż")){
                subtract = 1;
            }
        }
        if(rhs.contains("rz") && lhs.contains("ż")){
            if(rhs.indexOf("rz") == lhs.indexOf("ż")){
                subtract = 1;
            }
        }
        return distance[lhs.length()][rhs.length()] - subtract;
    }
}
