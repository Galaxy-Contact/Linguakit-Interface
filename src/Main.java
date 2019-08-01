import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final int NUM_THREAD = 3;

    static final String LINGUAKIT_DIR = "/home/bangjdev/Linguakit/linguakit";
    private static final String EXCEL_INPUT_DIR = "/home/bangjdev/Projects/Java/Keywords Extractor/Base_nouvelles_images_Jofl_complete_TRAVAIL_marco_fait1_2_BON_NPE_DATE_COULEUR (copy).xls";
    private static final String OUTPUT_FILE_DIR = "./output_keywords.txt";
    static final String INPUT_FILE_DIR = "./input_keywords.txt";

    static ArrayList<HashSet<String>> keywordResults = new ArrayList<>();


    public static void main(String[] args) {

        for (int i = 0; i < NUM_THREAD; i ++) {
            keywordResults.add(new HashSet<>());
        }

        try {

            FileInputStream fis = new FileInputStream(new File(EXCEL_INPUT_DIR));
            Workbook inputWorkbook = new HSSFWorkbook(fis);

            Sheet inputSheet = inputWorkbook.getSheetAt(0);
            String description;

            int length = inputSheet.getLastRowNum();

            ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREAD);

            ArrayList<Future<?>> futures = new ArrayList<>();

            for (int i = 1; i < length; i++) {
                try {
                    description = inputSheet.getRow(i).getCell(11).getStringCellValue();
                } catch (NullPointerException e) {
                    break;
                }
                futures.add(threadPool.submit(new KeywordTask(i, description)));
            }
            inputWorkbook.close();

            for(Future<?> future : futures) {
                try {
                    future.get();
                }catch(Exception e){
                    // do logging and nothing else
                }
            }

            threadPool.shutdown();
            


        } catch (IOException e) {
            e.printStackTrace();
        }
        

        System.out.println("Printing...");

        try {

            FileWriter fw = new FileWriter(OUTPUT_FILE_DIR);

            for (HashSet<String> h : keywordResults) {
                for (String s : h) {
                    fw.write(s);
                }
            }

            fw.flush();

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
