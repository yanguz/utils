package pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * pdf分割
 * 
 * @author 80274996
 * 
 */
public class PdfSpliter {
	private static List<Integer> pages = new ArrayList<Integer>();
	// 标题
	private static List<String> titles = new ArrayList<String>();
	private static PdfReader reader = null;
	// pdf总页码
	private static int count = 0;
	// 生成目录
	private static String dir = null;

	public static void main(String[] args) throws Exception {
		//getTitles("E:\\MSTR_HELP\\Report Services 文档分析指南_通过文档和仪表盘进行数据分析.pdf");
		splitSingle("E:\\2015年下半年系统集成项目管理工程师考试葵花宝典之金色考点暨历年真题解析（项管必过神系列）.pdf","E:\\2015年下半年系统集成项目管理工程师考试葵花宝典之金色考点暨历年真题解析（项管必过神系列）.txt");
	}

	public static void splitSingle(String pdf,String bookmark) throws Exception {
		dir = pdf.substring(0, pdf.lastIndexOf(".")).trim();
		// 创建输出目录
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		reader = new PdfReader(pdf);
		count = reader.getNumberOfPages();
		// 从书签中获取分页信息
		getPageInfo(bookmark);
		pages.add(count);
		// 拆分
		for (int i = 0; i < titles.size(); i++) {
			pdfSplit(titles.get(i) + ".pdf", pages.get(i), pages.get(i + 1));
		}
		reader.close();
	}

	/**
	 * pdf分割
	 * 
	 * @param newFile
	 * @param from
	 * @param end
	 */
	public static void pdfSplit(String newFile, int from, int end) {
		try {
			Document document = null;
			PdfCopy copy = null;
			if (end == 0) {
				end = count;
			}
			document = new Document(reader.getPageSize(1));
			String newPdf = dir + File.separator + newFile;
			copy = new PdfCopy(document, new FileOutputStream(newPdf));
			document.open();
			for (int j = from; j <= end; j++) {
				document.newPage();
				PdfImportedPage page = copy.getImportedPage(reader, j);
				copy.addPage(page);
			}
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public static void getPageInfo(String pdf) throws Exception {
		File file = new File(pdf);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while (((line = br.readLine()) != null) && (!"".equals(line))) {
			String[] array = line.split("[|]");
			titles.add(array[0]);
			pages.add(Integer.parseInt(array[1]));
		}
	}

	/**
	 * 初始化pdf分割信息
	 * 
	 * @param pdfFile
	 * @throws Exception
	 */
	public static void getTitles(String pdfFile) throws Exception {
		PdfReader reader = new PdfReader(pdfFile);
		// 获取书签
		List<Map<String, Object>> list = SimpleBookmark.getBookmark(reader);
		for (int i = 0, size = list.size(); i < size; i++) {
			Map<String, Object> bookmark = list.get(i);
			String title = String.valueOf(bookmark.get("Title"));
			System.out.println(title);
		}
		reader.close();
	}

}
