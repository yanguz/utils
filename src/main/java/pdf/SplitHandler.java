package pdf;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SplitHandler {
	public static void main(String[] args) throws Exception {
		File file = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String sourcePath = br.readLine();
		List<PDFSplitInfo> pdfSplitInfos = new ArrayList();
		String line = null;
		while (((line = br.readLine()) != null) && (!"".equals(line))) {
			String[] array = line.split("[|]");
			pdfSplitInfos.add(new PDFSplitInfo(array[0], Integer.parseInt(array[1]), Integer.parseInt(array[2])));
		}
		splitPDF(sourcePath, pdfSplitInfos);
	}

	public static void splitPDF(String fromPath, List<PDFSplitInfo> splitInfos) throws Exception {
		Document document = null;
		PdfCopy copy = null;
		PdfReader reader = new PdfReader(fromPath);
		int totalPages = reader.getNumberOfPages();
		for (PDFSplitInfo pdfSplitInfo : splitInfos) {
			document = new Document(reader.getPageSize(1));
			copy = new PdfCopy(document, new FileOutputStream(pdfSplitInfo.getToPath()));
			document.open();
			int start = pdfSplitInfo.getFrom();
			int to = pdfSplitInfo.getTo() > totalPages ? totalPages : pdfSplitInfo.getTo();
			for (int i = start; i <= to; i++) {
				document.newPage();
				PdfImportedPage page = copy.getImportedPage(reader, i);
				copy.addPage(page);
			}
			document.close();
		}
	}
}

class PDFSplitInfo {
	private String toPath;
	private int from;
	private int to;

	public String getToPath() {
		return this.toPath;
	}

	public void setToPath(String toPath) {
		this.toPath = toPath;
	}

	public int getFrom() {
		return this.from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return this.to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public PDFSplitInfo(String toPath, int from, int to) {
		this.toPath = toPath;
		this.from = from;
		this.to = to;
	}
}
