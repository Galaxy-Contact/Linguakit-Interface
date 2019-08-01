import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class KeywordTask extends Thread {

    private String description;
    private int id, line;

    public KeywordTask(int line, String description) {
        this.line = line;
        this.id = line % 4;
        this.description = description;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        System.out.println("Line " + line + ": processing \n" + description);
        writeToFile(Main.INPUT_FILE_DIR.replaceAll(".txt", "_" + id + ".txt"), description);
        String returnKeyword = getKeywords();
        Main.keywordResults.get(id).add(returnKeyword);
        System.out.println("Time: " + (float) (System.currentTimeMillis() - start) / 1000);
    }

    private void writeToFile(String dir, String text) {
        try {
            FileWriter writeInput = new FileWriter(dir);
            writeInput.write(text);
            writeInput.flush();
            writeInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getKeywords() {

        ProcessBuilder processBuilder = new ProcessBuilder();

//        System.out.println("Excecuting: " + LINGUAKIT_DIR + " key en " + INPUT_FILE_DIR);

        // Run a shell command
        processBuilder.command("bash", "-c", Main.LINGUAKIT_DIR + " key en " + Main.INPUT_FILE_DIR.replaceAll(".txt", "_" + id + ".txt"));


        try {
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line + " => " + line.split("[0-9]")[0]);
                output.append(line.split("[0-9]")[0]).append("\n");
            }

            int exitVal = process.waitFor();

            if (exitVal == 0) {
//                System.out.println("Success!");
                return output.toString();
            } else {
                System.out.println("Fuck!");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
