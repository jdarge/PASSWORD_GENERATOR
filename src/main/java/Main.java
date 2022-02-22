// Classic Jan shit post. Enjoy B)

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static long secret;
    static HashMap<Character, String> rules;

    private static Boolean FLAG = false;
    private static Boolean HELP = false;
    private static Boolean FILE = false;
    private static Boolean INPUT = false;

    public static void main(String[] args) {

        StringBuilder fileName = new StringBuilder();

        if(args.length != 0 ) {

            // HERE THE USER IS TRYING TO SPECIFY SOMETHING, TIME TO FIGURE OUT WHAT

            String type = args[0].toLowerCase(Locale.ROOT);

            // SET BOOLEAN VALUES (FLAG HELP FILE INPUT)
            setTypes(type);

            if(!FLAG) {
                System.out.println("\nArgument not understood. Type \"$ java Main -h\" for help!");
                System.exit(98);
            }

            if(args.length != 2 && !HELP) {
                System.out.println("\nInsufficient arguments given for type {" + ((FILE) ? "FILE" : "INPUT") +"}\nExiting...");
                System.exit(99);
            }
        }

        if(FLAG) {
            if(HELP) {
                // DISPLAY HELP INFORMATION AND EXIT
                System.out.println("\nWithout a specified argument, the default file \"input.txt\" will be read.\n\n" +
                        "-f : Give a specific file, of any location, to be read.\n(EXAMPLE)> $ java Main -f ~/Desktop/test.txt\n\n" +
                        "-i : Give a specific input, aka password, to use directly; instead of reading from a file.\n(EXAMPLE)> $ java Main -i password123");
                System.exit(0);
            } else if (FILE) {
                // TAKE THE GIVEN FILE NAME AND SET IT FOR LATER
                String tmp = args[1];
                if(tmp.charAt(0) == '~') tmp = tmp.replace("~", System.getProperty("user.home"));
                fileName.append(tmp);
            }
        }

        if(!FLAG) {
            fileName.append("input.txt");
        }

        String[] inputList;

        if(!INPUT) {
            // READ FILE AND SPLIT BASED OFF OF NEWLINES
            inputList = PasswordFile.readFile(fileName.toString()).split("\n");
        } else {
            // INPUT WAS SPECIFIED, SO WE JUST ASSUME THERE WAS ONE PASSWORD GIVEN B/C "WE'RE" LAZY
            inputList = new String[1];
            inputList[0] = args[1];
        }

        secret = init(); // SHHH
        rules = JsonFile.jsonInit();

        String[] passwordList = new String[inputList.length];

        for (int i = 0; i < inputList.length; i++) {
            // MAKE VALID PASSWORD OUT OF EACH INPUT AND STORE IT INTO THE PASSWORD LIST
            String n = modifyLetters(inputList[i]);
            passwordList[i] = finalize(n);
        }

        for (int i = 0; i < inputList.length; i++) {
            // PRINT BEFORE AND AFTER
            System.out.println("Before:\t" + inputList[i] + " \nAfter:\t" + passwordList[i] + "\n");
        }
    }

    private static long init() {
        System.out.print("\nEnter personalized number combination (NO SPACES):\n>: ");

        String input = scanner.nextLine();
        long ret = 0;

        try {
            ret = Long.parseLong(input);
        } catch (Exception ignore) {
            System.out.print("\nWARNING: Using generic key! {");
            String base = System.getProperty("user.home");

            for(int i = 0; i < base.length(); i++) {
                ret += (int) base.charAt(i);
            }

            System.out.println(ret + "}\n");
        }

        System.out.println("\nPlease wait...");

        try{
            for (int i = 0; i < 3; i++) {
                // TODO
                TimeUnit.SECONDS.sleep(1); // TODO later ig...
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();

        return ret;
    }

    private static String finalize(String str) {

        StringBuilder sb = new StringBuilder();
        String n = str.toLowerCase(Locale.ROOT);

        // IS THE FIRST CHARACTER A LETTER?
        if (n.charAt(0) >= 'a' && n.charAt(0) <= 'z') {
            sb.append("$"); // IF SO: ADD A DOLLAR SIGN ($)
        }

        // APPEND ORIGINAL (CASED) TEXT
        sb.append(str).append((int)(Main.secret % str.length()));

        // APPEND "rng" EXPLANATION POINTS (!)
        int rng = GeneralUtility.generateNumber(str);
        sb.append("!".repeat(Math.max(0, rng)));

        return sb.toString();
    }

    public static String modifyLetters(String str) {

        StringBuilder sb = new StringBuilder();
        int frac = (int) (Main.secret % str.length());

        for (int i = 0; i < str.length(); i++) {

            char c = str.charAt(i);

            if(GeneralUtility.isLetter(c)) {
                sb.append(rules.get((char) ('a' + i)));
            } else {
                sb.append(c);
            }

            // RANDOM LETTER

            if (frac == 0) frac = 26;
            if (secret == 0) secret = 26;

            char r = (char) ((((c << ((c % 2 == 0) ? 1 : 2)) + Main.secret) % frac) + 'a');

            if (i % Math.min(Math.min(8, frac), Main.secret) == 0) {
                sb.append(r);
            }

        }

        return sb.toString();
    }

    private static void setTypes(String type) {

        switch (type) {
            case "-h":
            case "--h":
            case "-help":
            case "--help":
                HELP = true;
                FLAG = true;
                break;
            case "-f":
            case "--f":
            case "-file":
            case "--file":
                FILE = true;
                FLAG = true;
                break;
            case "-i":
            case "--i":
            case "-input":
            case "--input":
                INPUT = true;
                FLAG = true;
                break;
        }
    }

}

class GeneralUtility {

    public static int generateNumber(String str) {

        // CAP THE MAX TO BE 5
        int ret = (str.length() << 2) % 5;

        // RETURN A NUMBER THAT'S AT LEAST 1
        return Math.max(ret, 1);
    }

    public static Boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}

class PasswordFile {

    public static String readFile(String name) {

        String input;
        File file = new File(name);
        BufferedReader br = null;

        try {
            // CREATE INPUT FILE READER
            br = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exiting...");
            System.exit(1);
        }

        StringBuilder sb = new StringBuilder();

        try {
            // READ EVERY LINE IN THE INPUT FILE (input.txt)
            while ((input = br.readLine()) != null) {
                sb.append(input).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exiting...");
            System.exit(2);
        }

        return sb.toString();
    }
}

class JsonFile {

    private static final HashMap<Character, String> rules = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static HashMap<Character, String> jsonInit() {

        if (new File("rules.json").exists()) {
            loadJson();
        } else {
            try {
                createJson();
                loadJson();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(30);
            }
        }


        return rules;
    }

    private static void createJson() throws FileNotFoundException {

        for (int i = 0; i < 26; i++) {
            System.out.print("Type rule for letter '" + ((char) ('a' + i)) + "' | [EMPTY FOR DEFAULT] : ");
            String str = scanner.nextLine();
            if (str.equals("")) str = String.valueOf((char) ('a' + i));
            rules.put((char) ('a' + i), str);
        }

        System.out.println();

        PrintWriter pw = new PrintWriter("rules.json");

        pw.write("{\n\t\"letter-rules\" : \n\t[\n");

        for (int i = 0; i < 26; i++) {
            pw.write("\t\t{\n\t\t\t");
            pw.write("\"" + ((char) ('a' + i)) + "\" : \"" + rules.get((char) ('a' + i)) + "\"\n");
            if (i != rules.size() - 1) {
                pw.write("\t\t},\n");
            } else {
                pw.write("\t\t}\n");
            }
        }

        pw.write("\t]\n}");

        pw.close();
    }

    private static void loadJson() {

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("rules.json"));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray array = (JSONArray) jsonObject.get("letter-rules");

            int i = 0;
            for (Object o : array) {

                JSONObject item = (JSONObject) o;

                String encrypted = (String) item.get(String.valueOf((char) ('a' + i)));

                rules.put(((char) ('a' + i)), encrypted);
                i++;
            }

        } catch (Exception ignore) {
            System.out.println("Exiting...");
            System.exit(31);
        }
    }
}