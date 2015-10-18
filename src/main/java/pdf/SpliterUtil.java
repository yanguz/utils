package pdf;

import java.io.File;
import java.io.FileOutputStream;
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
public class SpliterUtil {

	// 分页码
	private static List<Integer> pages = null;
	// 标题
	private static List<String> titles = null;
	private static PdfReader reader = null;
	// 输入pdf全路径文件名
	private static String pdfFile = null;
	// 生成目录
	private static String dir = null;
	// pdf总页码
	private static int count = 0;
	// 书签深度序号
	private static int bookmarkDeepIdx = 1;
	// 书签层级
	private static int bookmarkLevel = 1;

	public static void main(String[] args) throws Exception {
		splitMultiFile("F:\\Book", 2);
	}

	public static void splitMultiFile(String dir, int level) {
		File file = new File(dir);
		if (file.isDirectory()) {
			String[] fileNames = file.list();
			if (fileNames != null && fileNames.length > 0) {
				for (String fileName : fileNames) {
					try {
						String path = dir + File.separator + fileName;
						File pdfFile = new File(path);
						if (pdfFile.isFile()) {
							System.out.println(path);
							splitSingle(path, level);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void splitSingle(String pdf, int level) throws Exception {
		pdfFile = pdf;
		bookmarkLevel = level;
		dir = pdfFile.substring(0, pdfFile.lastIndexOf(".")).trim();
		// 创建输出目录
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		reader = new PdfReader(pdfFile);
		count = reader.getNumberOfPages();
		// 获取书签
		List<Map<String, Object>> list = SimpleBookmark.getBookmark(reader);
		
		pages = new ArrayList<Integer>();
		titles = new ArrayList<String>();
		bookmarkDeepIdx = 1;
		// 从书签中获取分页信息
		getPageInfo(list);
		pages.add(count);
		// 拆分
		for (int i = 0; i < titles.size(); i++) {
			pdfSplit(titles.get(i) + ".pdf", pages.get(i), pages.get(i + 1));
		}
		reader.close();
	}

	/**
	 * 初始化pdf分割信息
	 * 
	 * @param pdfFile
	 * @throws Exception
	 */
	public static void getPageInfo(List<Map<String, Object>> list) throws Exception {
		for (int i = 0, size = list.size(); i < size; i++) {
			Map<String, Object> bookmark = list.get(i);
			List<Map<String, Object>> kids = (List<Map<String, Object>>) bookmark.get("Kids");
			// 根据需要解析书签层级进行递归
			if (kids != null && bookmarkDeepIdx < bookmarkLevel) {
				bookmarkDeepIdx++;
				getPageInfo(kids);
				bookmarkDeepIdx--;
			} else {
				String page = String.valueOf(bookmark.get("Page"));
				String title = String.valueOf(bookmark.get("Title"));
				if (page != null && !page.equalsIgnoreCase("null") && title != null && !title.equalsIgnoreCase("null")) {
					page = page.split("[ ]")[0];
					int start = Integer.valueOf(page);
					title = title.replaceAll("\r", "");
					title = title.replaceAll("\n", "");
					title = title.replaceAll("\t", "");
					title = title.replaceAll("/", "");
					title = title.replaceAll("\\\\", "");
					title = title.replaceAll("\"", "");
					title = title.replaceAll(":", "");
					title = title.replaceAll("[<]", "");
					title = title.replaceAll("[>]", "");
					title = title.replaceAll("[|]", "");
					title = title.replaceAll("[*]", "");
					title = title.replaceAll("[?]", "");
					title = title.replaceAll("？", "");
					title = title.trim();
					pages.add(start);
					titles.add(title);
				}
			}
		}
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

}
