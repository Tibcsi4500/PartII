package Console;

import java.io.*;

public class ConsoleInteractions {
    private static BufferedReader reader;
    private static PrintWriter writer;

    public static void initalize(){
        reader = new BufferedReader(new InputStreamReader(System.in));
        writer = new PrintWriter(new OutputStreamWriter(System.out));
    }
    public static String prompt(String promptText){
        display(promptText);
        String line = "";

        try{
            line = reader.readLine();
        } catch (IOException e){
            error(e.toString());
        }

        return line;
    }

    public static void display(String text){
        writer.write(text + "\n");
        writer.flush();
    }

    public static void error(String errorText){
        System.out.println("An error has occured: \n" + errorText);
    }
}
