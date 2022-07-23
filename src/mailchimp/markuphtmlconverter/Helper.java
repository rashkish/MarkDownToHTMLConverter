package mailchimp.markuphtmlconverter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Helper {

    /**
     *
     * closeCurrentParagraph gets the index of the last paragraph,
     * replaces that with the output from closeCurrentParagraph(paragraphTagStack)
     *
     * @param htmlStringBuilder
     * @param paragraphTagStack
     * @return
     */
    public static StringBuilder closeCurrentParagraph(StringBuilder htmlStringBuilder, List<String> paragraphTagStack) {
        int last = htmlStringBuilder.lastIndexOf("\n");
        if (last >= 0) {
            htmlStringBuilder.delete(last, htmlStringBuilder.length());
        }
        return htmlStringBuilder.append(closeCurrentParagraph(paragraphTagStack)).append("\n");
    }

    /**
     * computeLink does a regex match for link ( eg: [with an inline link](http://google.com))
     * and groups the matches to produce the HTML format (eg : <a href="http://google.com">with an inline link</a>)
     *
     * @param line
     * @return
     */
    public static String computeLink(String line) {
        // Link
        Pattern pattern = Pattern.compile(Regex.MarkDownRegexConstruct.URL_MARKDOWN.getValue());
        Matcher matcher = pattern.matcher(line);
        while (matcher.matches()) {
            line = String.format(Regex.HtmlRegexConstruct.URL_HTML_TAG.getValue(),
                    matcher.group(1), matcher.group(3), matcher.group(2), matcher.group(4));
            matcher = pattern.matcher(line);
        }

        return line;
    }

    /**
     * computeLink does a regex match for Title ( until H6) ( eg: ## Another Header)
     * and groups the matches to produce the HTML format (eg :<h2>Another Header</h2>)
     *
     * @param line
     * @return
     */
    public static String computeTitle(String line) {
        if (line.matches(Regex.MarkDownRegexConstruct.TITLE_MARKDOWN.getValue())) {
            long titleNum = line.replaceFirst(Regex.MarkDownRegexConstruct.TITLE_MARKDOWN.getValue(), "$1").chars().count();
            if (titleNum < 7) {
                String temp = line.replaceFirst(Regex.MarkDownRegexConstruct.TITLE_NUMBER.getValue(), "");
                line = String.format(Regex.HtmlRegexConstruct.TITLE_HTML_TAG.getValue(), titleNum, temp, titleNum);
            }
        }
        return line;
    }

    /**
     * computeParagraph computes if the given line is beginning of a new paragraph
     * by getting the index of "p" from the stack.
     *
     * If the index is -1, prepend the paragraph with <p> and add "p" to the paragraph stack
     * @param line
     * @return
     */
    public static String computeParagraph(String line, List<String> paragraphTagStack) {
        int baliseIndex = getParagraphStackIndex(Regex.HtmlRegexConstruct.PARAGRAPH_HTML_TAG.getValue(), paragraphTagStack);
        if (baliseIndex == -1) {
            paragraphTagStack.add(Regex.HtmlRegexConstruct.PARAGRAPH_HTML_TAG.getValue());
            return String.format("<%s>%s", Regex.HtmlRegexConstruct.PARAGRAPH_HTML_TAG.getValue(), line);
        }
        return line;
    }

    /**
     * getParagraphStackIndex gets the index of "p" in the paragraph stack
     *
     * @param balise
     * @return
     */
    public static int getParagraphStackIndex(String balise, List<String> paragraphTagStack) {
        return IntStream.range(0, paragraphTagStack.size())
                .filter(index -> paragraphTagStack.get(index).equals(balise))
                .findFirst()
                .orElse(-1);
    }


    /**
     * closeCurrentParagraph computes if the given line is end of an existing paragraph
     * by getting the index of "p" from the stack.
     *
     * If the index > -1, prepend the paragraph with <p> and remove "p" from the paragraph stack
     *
     * @return String
     */
    public static String closeCurrentParagraph(List<String> paragraphTagStack) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String balise : paragraphTagStack) {
            stringBuilder.append(String.format("</%s>", balise));
        }
        paragraphTagStack.clear();
        return stringBuilder.toString();
    }

}
