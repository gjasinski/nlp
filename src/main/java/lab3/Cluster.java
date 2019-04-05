package lab3;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

public class Cluster {
    private String[] stopList = new String[]{"tel",
            "ltd",
            "fax",
            "road",
            "no",
            "coltd",
            "ul",
            "sp",
            "zoo",
            "z",
            "o",
            "add",
            "office",
            "co",
            "a"
    };
    //"'.,()

    ///prawdopobienstwo ze napisalem w pod warunkiem ze chciałem napisać c
    //tw bayesa - będą dominowały najbardziej popularne elemtnty, trzeba wprowadzic wagi

    //lewentine i lcs

    public static void main(String[] args) {
        new Cluster();
    }

    public Cluster() {
        readFileAndRemoveFromStopList("lab3/lines.txt");
    }

    private void readFileAndRemoveFromStopList(String fileName){
        readFile(fileName)
                .map(line -> line.replaceAll("[^a-zA-Z ąĄćĆęĘłŁńŃóÓśŚźŹżŻ]", " "))
                .map(String::toLowerCase)
                .map(line -> Arrays.stream(line.split(" "))
                        .filter(this::isNotInStopList)
                        .reduce("", (s1, s2) -> s1 += " " + s2)
                ).forEach(s -> System.out.println(s));
    }

    private boolean isNotInStopList(String s) {
        for(int i = 0; i < stopList.length; i++){
            if(s.equals(stopList[i])){
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
            System.out.println(fileName + " " + ex.toString());
            throw new IllegalArgumentException(ex);
        }
    }


}
