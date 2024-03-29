package lab7;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Markov {
  //  public static int SIZE = 1;

    public static void main(String[] args) throws IOException {
//        Map<String, Map<String, Map<String, Integer>>> authorToMarkovModel = new HashMap<>();
        for (int i = 2; i < 11; i++) {
            final int SIZE = i;
            Map<String, Integer> counter = new HashMap<>();
            try (Stream<Path> paths = Files.walk(Paths.get("/home/grzegorz/git/nlp/src/main/resources/lab7/"))) {
                paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().contains("-"))
                        .peek(path -> {
                            String file = path.getFileName().toString();
                            String author = file.substring(0, file.indexOf("-")).replaceAll("_", "");
                            if (counter.containsKey(author)) {
                                counter.put(author, counter.get(author) + 1);
                            } else {
                                counter.put(author, 1);
                            }
                        })
                        .filter(path -> {
                            String file = path.getFileName().toString();
                            String author = file.substring(0, file.indexOf("-")).replaceAll("_", "");
                            return counter.get(author) > 3;
                        })
                        .peek(path -> {
                            String file = path.getFileName().toString();
                            String author = file.substring(0, file.indexOf("-")).replaceAll("_", "");
                            System.out.println(author);
                        })
                        .peek(path -> {
                            try {
                                Map<String, Map<String, Integer>> markovModel;
                                String file = path.getFileName().toString();
                                String author = file.substring(0, file.indexOf("-")).replaceAll("_", "");
                                try {
                                    ObjectInputStream oin = new ObjectInputStream(new FileInputStream("/home/grzegorz/git/nlp/src/main/resources/lab7_out/" + SIZE  + "/" + author + ".ser"));
                                    markovModel = (Map) oin.readObject();
                                    System.out.println("Map size: " + markovModel.size());
                                } catch (Exception ex) {
                                    //System.out.println(ex.toString());
                                    markovModel = new HashMap<>();
                                    //System.out.println("ERROR - tworze nową mapkę");
                                }
                                markovForFile(markovModel, path.toAbsolutePath().toString(), SIZE);
                                System.out.println(file);
                                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("/home/grzegorz/git/nlp/src/main/resources/lab7_out/" + SIZE  + "/" + author + ".ser"));
                                out.writeObject(markovModel);
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                        .forEach(System.out::println);
            }
        }
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
}
