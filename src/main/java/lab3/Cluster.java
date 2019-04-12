package lab3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cluster {
    private String[] stopList = new String[]{
            "limited",
            "str",
            "f",
            "building",
            "international",
            "oo",
            "oy",
            "and",
            "as",
            "tel",
            "ltd",
            "fax",
            "road",
            "no",
            "coltd",
            "ul",
            "sp",
            "zoo",
            "z",
            "zo",
            "o",
            "add",
            "office",
            "co",
            "a",
            "order",
            "room",
            "sa",
            "order",
            "street",
            "industrial",
            "forwarding",
            "branch",
            "to",
            "global",
            "st",
            "rd",
            "shenzhen",
            "telfax",
            "district",
            "city",
            "floor",
            "agent",
            "b",
            "th",
            "company",
            "phone",
            "spzoo",
            "tower",
            "shipping",
            "eori",
            "bldg",
            "on",
            "zoo",
            "trading",
            "trade",
            "east",
            "behalf",
            "spz",
            "the",
            "fi",
            "rm",
            "line",
            "business",
            "inn",
            "south",
            "plaza",
            "group",
            "code",
            "zip",
            "cargo",
            "t",
            "park",
            "centre",
            "for",
            "world",
            "industry",
            "west",
            "d",
            "zone",
            "fmg",
            "dhl",
            "forward",
            "box",
            "center",
            "town",
            "c",
            "ob",
            "attn",
            "pr",
            "h",
            "north",
            "contact",
            "email",
            "unit",
            "new",
            "ocean",
            "province",
            "anchor",
            "mansion",
            "dist",
            "pvt",
            "bld",
            "dsv",
            "pt",
            "spolka",
            "development",
            "india",
            "kowloon",
            "inc",
            "expedition",
            "hk",
            "po",
            "services",
            "kuehnenagel",
            "transport",
            "trans",
            "logistic",
            "import",
            "ext",
            "importexport",
            "cn",
            "fl",
            "telephone",
            "house",
            "region",
            "ph",
            "pantos",
            "block",
            "m",
            "sdn",
            "pobox",
            "export",
            "sea",
            "same",
            "suite"

    };
    //"'.,()

    ///prawdopobienstwo ze napisalem w pod warunkiem ze chciałem napisać c
    //tw bayesa - będą dominowały najbardziej popularne elemtnty, trzeba wprowadzic wagi

    //lewentine i lcs

    public static void main(String[] args) throws IOException {
        new Cluster();
    }

    public Cluster() throws IOException {
        readFileAndRemoveFromStopList("lab3/lines.txt");
    }

    private void readFileAndRemoveFromStopList(String fileName) throws IOException {
        List<String> lines = readFile(fileName)
                .map(line -> line.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", " "))
                .map(String::toLowerCase)
                .map(line -> Arrays.stream(line.split(" "))
                        .filter(this::isNotInStopList)
                        .reduce("", (s1, s2) -> s1 += " " + s2))
                .map(s -> s.replaceAll(" ", ""))
                .sorted(alphabeticalOrderComparator)
                .collect(Collectors.toList());
        clusterWithLevenshtein(lines);
        return;
    }

    private void clusterWithLevenshtein(List<String> lines) throws IOException {
        List<String> clusteredLinesHeaders = new LinkedList<>();
        Map<String, List<String>> mapOfClusters = new HashMap<>();
        createNewCluster(lines.get(0), clusteredLinesHeaders, mapOfClusters);
        lines.stream()
                .skip(1)
                .forEach(s -> {
                    boolean added = false;
                    for (String clusteredLinesHeader : clusteredLinesHeaders) {
                        int distance = computeLevenshteinDistance(s, clusteredLinesHeader);
                        if (distance < 10) {
                            List<String> strings = mapOfClusters.get(clusteredLinesHeader);
                            strings.add(s);
                            added = true;
                            System.out.println(String.format("added %s to %s with distance %d", s, clusteredLinesHeader, distance));
                            break;
                        }
                    }
                    if (!added) {
                        createNewCluster(s, clusteredLinesHeaders, mapOfClusters);
                        System.out.println("Created new cluster: " + s);
                    }
                });

        BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/lab3/levenstainMetric.txt"));
        mapOfClusters.forEach((k, v) -> {
            System.out.println("=======================");
            v.forEach(System.out::println);
        });
        writer.close();
    }

    private void createNewCluster(String line, List<String> clusteredLinesHeaders, Map<String, List<String>> mapOfClusters) {
        clusteredLinesHeaders.add(0, line);
        LinkedList<String> cluster = new LinkedList<>();
        cluster.add(line);
        mapOfClusters.put(line, cluster);
    }

    private Comparator<String> alphabeticalOrderComparator = (str1, str2) -> {
        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
        if (res == 0) {
            res = str1.compareTo(str2);
        }
        return res;
    };

    private boolean isNotInStopList(String s) {
        for (int i = 0; i < stopList.length; i++) {
            if (s.equals(stopList[i])) {
                return false;
            }
        }
        return true;
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

    public int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
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

        return distance[lhs.length()][rhs.length()];
    }
}
