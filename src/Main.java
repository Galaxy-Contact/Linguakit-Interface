import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
	static final int NUM_THREAD = 4;

	static final String LINGUAKIT_DIR = "./linguakit.bat";
	private static final String EXCEL_INPUT_DIR = "./test.xls";
	private static final String OUTPUT_FILE_DIR = "./output_keywords.txt";
	static final String INPUT_FILE_DIR = "./input_keywords.txt";

	static HashSet<String> keywordResults = new HashSet<String>();

	public static void putKeywords(int id, String keyword) {
		synchronized (keywordResults) {
			if (!keywordResults.contains(keyword))
				keywordResults.add(keyword);	
		}
	}

	public static void main(String[] args) {

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

			for (Future<?> future : futures) {
				try {
					future.get();
				} catch (Exception e) {
					// do logging and nothing else
				}
			}

			threadPool.shutdown();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Printing...");

		try {

			FileWriter fw = new FileWriter(new File(OUTPUT_FILE_DIR), StandardCharsets.UTF_8);

			for (String s : keywordResults) {
				fw.write(s);
			}

			fw.flush();

			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished");
	}

}
