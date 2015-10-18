package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileRead {
	public static void main(String[] args) throws Exception {
		File file = new File("D:\\aaa.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		String target = "seeContent";
		while((line = br.readLine()) != null){
			int idx = line.indexOf(target) + target.length() + 1;
			line = line.substring(idx,idx+4);
			sb.append(line).append(",");
		}
		sb.append("]");
		System.out.println(sb.toString());
	}
}
