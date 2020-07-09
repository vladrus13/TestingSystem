package base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;

public class HTMLWriter {
    private final ArrayList <String> result;
    private String compilation = "";
    private long compileTime = -1;

    public HTMLWriter(int size) {
        result = new ArrayList<>(Collections.nCopies(size, ""));
    }

    private synchronized String getRow(int test, String status, int code, long time) {
        return String.format("<tr class=\"%s\">" +
                "<td>%d</td>" +
                "<td>%s</td>" +
                "<td>%d</td>" +
                "<td>%d</td></tr>", status, test, status, code, time);
    }

    public void setCompilation(String compilation) {
        this.compilation = compilation;
    }

    public void setCompileTime(long compileTime) {
        this.compileTime = compileTime;
    }

    public synchronized void setAccepted(int test, long time) {
        result.set(test - 1, getRow(test, "AC", 0, time));
    }

    public synchronized void setWrongAnswer(int test, long time) {
        result.set(test - 1, getRow(test, "WA", 0, time));
    }

    public synchronized void setRuntimeError(int test, int code, long time) {
        result.set(test - 1, getRow(test, "RE", code, time));
    }

    public synchronized void setTimeLimit(int test) {
        result.set(test - 1, getRow(test, "Time Limit", 0, -1));
    }

    public synchronized void writeHTML() throws IOException {
        BufferedWriter out = Files.newBufferedWriter(Paths.get("result.html"), Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING);
        out.write("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "\t<meta charset=\"UTF-8\">\n" +
                "\t<title>Result</title>\n" +
                "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<compileOutput>\n" +
                "\tCompile time: " + compileTime + ".\n\n\t" + compilation + "\n" +
                "</compileOutput>\n" +
                "<table>\n");
        out.write("\t<tr><th>Test</th><th>Status</th><th>Code</th><th>Time</th></tr>\n");
        for (String s : result) {
            out.write("\t" + s + "\n");
        }
        out.write("</table>\n</body></html>");
        out.close();
    }
}
