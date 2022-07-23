package mailchimp.markuphtmlconverter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import mailchimp.markuphtmlconverter.Regex.*;

/**
 *
 */
public class MarkDownToHTMLConverter {

    private static List<String> paragraphTagStack = new ArrayList<>();
    static int lastIndex, currIndex;

    public static void main(String... args) throws IOException {
        String input =
                Files.readString(Paths.get("MarkupText"));
        String result1 = executeConvertToHTML(input);

        String currentDirectory = new File("").getAbsolutePath();
        File output = new File(currentDirectory, "Output.txt");
        FileWriter fw = new FileWriter(output);

        fw.write(input + "\n ************ MARKDOWN -> HTML ************* \n" + result1);
        fw.close();

    }


    /**
     * @param markDownText
     * @return
     */
    public static String executeConvertToHTML(String markDownText) {
        String s = markDownText.replace("\n", "");
        lastIndex = s.length();
        StringBuilder html = new StringBuilder();
        html.append(convert(markDownText));
        return html.toString();
    }

    public static String convert(String markDownText) {

        // previousLine keeps track of multi-line paragraphs
        String previousLine = "";
        boolean removeLastLine = true;


        StringBuilder htmlSb = new StringBuilder();

        for (String line : markDownText.split("\n")) {
            currIndex += (line.length());
            if (line.trim().isEmpty()) {
                if (!previousLine.isEmpty() && Helper.getWeakBaliseIndex("p", paragraphTagStack) != -1) {
                    htmlSb = Helper.closeCurrentParagraph(htmlSb, paragraphTagStack);
                    previousLine = "";
                }
                htmlSb.append("\n");
                removeLastLine = false;

            } else if (line.matches(MarkDownRegexConstruct.TITLE_MARKDOWN_MATCHER.getValue())) {
                if (!previousLine.isEmpty() && Helper.getWeakBaliseIndex("p", paragraphTagStack) != -1) {
                    htmlSb = Helper.closeCurrentParagraph(htmlSb, paragraphTagStack);
                    previousLine = "";
                }
                String paragraph = line;

                // If the header contains a link, apply the html format to the link
                if (line.matches(MarkDownRegexConstruct.URL_MARKDOWN.getValue()
                        + MarkDownRegexConstruct.DOT_ALL.getValue())) {
                    paragraph = Helper.computeLink(paragraph);

                }
                htmlSb.append(Helper.computeTitle(paragraph)).append("\n");
            } else {

                String paragraph = line;
                if (line.matches(MarkDownRegexConstruct.URL_MARKDOWN.getValue()
                        + MarkDownRegexConstruct.DOT_ALL.getValue())) {
                    // Link
                    paragraph = Helper.computeLink(paragraph);

                }
                htmlSb.append(Helper.computeParagraph(paragraph, paragraphTagStack))
                        .append("\n");
                previousLine = line;

                if (lastIndex == currIndex) {
                    //paragraph="";
                    int begin = htmlSb.lastIndexOf(paragraph);
                    int lastLineEndIndex = begin + paragraph.length();
                    htmlSb.insert(lastLineEndIndex, "</p>");
                }

            }

        }
        if (removeLastLine)
            htmlSb.setLength(htmlSb.length() - 1);
        return htmlSb.toString();
    }

}


