package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Configs {

    Map<String, String> configs = new HashMap<>();

    public Configs() throws FileNotFoundException, SpecialException {
        Scanner in = null;
        in = new Scanner(new File("src/config.txt"));
        while (in.hasNextLine()) {
            String template = in.nextLine();
            String[] temp = template.split("=");
            if (temp.length != 2) {
                throw new SpecialException("Configs line have a '=' is not 2");
            }
            configs.put(temp[0], temp[1]);
        }
    }

    public String get(String x) throws SpecialException {
        if (configs.containsKey(x)) {
            return configs.get(x);
        } else {
            throw new SpecialException("Configs don't have a " + x);
        }
    }
}
