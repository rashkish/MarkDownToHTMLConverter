package mailchimp.markuphtmlconverter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import mailchimp.markuphtmlconverter.Regex.*;

/**
 * MarkDownToHTMLConverter converts a Markdown text to HTML
 * For example:
 *
 INPUT
 ------

 ```
 # Sample Document

 Hello!

 This is sample markdown for the [Mailchimp](https://www.mailchimp.com) homework assignment.
 ```

 OUTOUT
 ------

 ```
 <h1>Sample Document</h1>

 <p>Hello</p>

 <p>This is sample markdown for the <a href="https://www.mailchimp.com">Mailchimp</a> homework assignment</p>
 ```
 */
public class MarkDownToHTMLConverter {

    private static List<String> paragraphTagStack = new ArrayList<>();
    static int lastIndex, currIndex;

    public static void main(String... args) throws IOException {
        // Read the markdown text from MarkupText and convert that
        // into a string to enable easier traversal and conversion.
        // executeConvertToHTML function traverses the string line by line
        // and converts markdown text to HTML format.
        String input =
                Files.readString(Paths.get("MarkupText"));
        String result = executeConvertToHTML(input);

        // Create a new Output.txt if it doesn't exist,
        // and write the resultant HTML string to it.
        String currentDirectory = new File("").getAbsolutePath();
        File output = new File(currentDirectory, "Output.txt");
        FileWriter fw = new FileWriter(output);
        fw.write(input + "\n ************ MARKDOWN -> HTML ************* \n" + result);
        fw.close();

        System.out.println(" See "+ currentDirectory +"/Output.txt for the processed markdown converted to HTML");

    }


    /**
     * executeConvertToHTML function traverses the string line by line
     * and converts markdown text to HTML format.
     *
     * lastIndex tracks end of String to verify if we have reached end of
     * original file.
     *
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

    /**
     * convert has the following functionality
     *
     * 1. Creates a StringBuilder to capture the result
     *
     * 2. Breaks down the markDownText line by line.
     *
     * 3. Checks if current line a new line (\n)
     *      a. If the current line is a new line which is empty,
     *      verify that a multiline paragraph (if applicable) has the correctly closed the tag </p>.
     *      b. If the previous line is not empty, and the current line is empty,
     *          check the stack for any unclosed <p> and close accordingly.
     *
     *   else
     *
     * 4. Checks if current line is a Header using regex
     *      a. Verifies if previous line was a  paragraph, if yes, close </p>
     *      b. Additionally, check if the header contains URL Regex and convert to HTML accordingly.
     *
     *   else
     *
     * 5. Assumes the current line is a paragraph within the scope of the Homework assignment
     *
     *       a. Check if the header contains URL Regex and convert  to HTML accordingly.
     *       b. Append the paragraph to the StringBuilder prepending the line with <p>
     *          push this "p" to a stack to keep track of new line or track closing of paragraph
     *       c. Check if this is the last line to processed, if yes, close the paragraph tag
     *       accordingly.
     *
     *  6. As I traverse through the markdown text and append the result to the HTML stringBuilder,
     *     we append a "\n" after each match. However, if we do not address this addition of "\n",
     *     we risk adding an unnecessary new line at the end of the string. Hence, we remove "\n"
     *     once all lines are traversed.
     *
     *  7. Return the HTML stringBuilder to  executeConvertToHTML()
     *
     * @param markDownText
     * @return
     */
    public static String convert(String markDownText) {

        // previousLine keeps track of multi-line paragraphs
        String previousLine = "";
        // removeLastLine keeps track if "\n" needs to be removed
        // at the end of the final line tracersal.
        boolean removeLastLine = true;
        // 1. htmlSb appends the processed HTML line.
        StringBuilder htmlSb = new StringBuilder();

        //2. Breaks down the markDownText line by line.
        for (String line : markDownText.split("\n")) {

            // currIndex tracks last line of the markDownText
            currIndex += (line.length());

            // 3. Checks if current line a new line (\n)
            if (line.trim().isEmpty()) {


                //   3 a. If the current line is a new line which is empty,
                //      verify that a multiline paragraph (if applicable)
                //      has the correctly closed the tag </p>.
                if (!previousLine.isEmpty() && Helper.getParagraphStackIndex("p", paragraphTagStack) != -1) {

                    // 3 b. If the previous line is not empty, and the current line is empty,
                    //      check the stack for any unclosed <p> and close accordingly.
                    htmlSb = Helper.closeCurrentParagraph(htmlSb, paragraphTagStack);

                    // 3 c. Reset previousLine to empty
                    previousLine = "";
                }
                htmlSb.append("\n");
                removeLastLine = false;

            }
            // 4. Checks if current line is a Header using regex
            else if (line.matches(MarkDownRegexConstruct.TITLE_MARKDOWN_MATCHER.getValue())) {

                // 4 a. Verifies if previous line was a  paragraph, if yes, close </p>
                if (!previousLine.isEmpty() &&
                        Helper.getParagraphStackIndex(HtmlRegexConstruct.PARAGRAPH_HTML_TAG.getValue(), paragraphTagStack) != -1) {
                    htmlSb = Helper.closeCurrentParagraph(htmlSb, paragraphTagStack);
                    previousLine = "";
                }
                String paragraph = line;

                // 4 b. Additionally, check if the header contains URL Regex and convert accordingly.
                if (line.matches(MarkDownRegexConstruct.URL_MARKDOWN.getValue()
                        + MarkDownRegexConstruct.DOT_ALL.getValue())) {
                    paragraph = Helper.computeLink(paragraph);

                }
                htmlSb.append(Helper.computeTitle(paragraph)).append("\n");
            }
            // 5. Assumes the current line is a paragraph within the scope of the Homework assignment
            else {

                String paragraph = line;

                // 5 a. Check if the header contains URL Regex and convert  to HTML accordingly.
                if (line.matches(MarkDownRegexConstruct.URL_MARKDOWN.getValue()
                        + MarkDownRegexConstruct.DOT_ALL.getValue())) {
                    paragraph = Helper.computeLink(paragraph);

                }

                // 5 b. Append the paragraph to the StringBuilder prepending the line with <p>
                // push this "p" to a stack to keep track of new line or track closing of paragraph
                htmlSb.append(Helper.computeParagraph(paragraph, paragraphTagStack))
                        .append("\n");
                previousLine = line;

                // 5 c. Check if this is the last line to processed, if yes, close the paragraph tag accordingly.
                if (lastIndex == currIndex) {
                    //paragraph="";
                    int begin = htmlSb.lastIndexOf(paragraph);
                    int lastLineEndIndex = begin + paragraph.length();
                    htmlSb.insert(lastLineEndIndex, "</p>");
                }

            }

        }

        //  6. As I traverse through the markdown text and append the result to the HTML stringBuilder,
        //     we append a "\n" after each match. However, if we do not address this addition of "\n",
        //     we risk adding an unnecessary new line at the end of the string. Hence, we remove "\n"
        //     once all lines are traversed.
        if (removeLastLine)
            htmlSb.setLength(htmlSb.length() - 1);

        // 7. Return the HTML stringBuilder to  executeConvertToHTML()
        return htmlSb.toString();
    }

}


