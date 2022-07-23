package mailchimp.markuphtmlconverter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Helper {

    public static StringBuilder closeCurrentParagraph(StringBuilder htmlStringBuilder, List<String> paragraphTagStack) {
        int last = htmlStringBuilder.lastIndexOf("\n");
        if (last >= 0) {
            htmlStringBuilder.delete(last, htmlStringBuilder.length());
        }
        return htmlStringBuilder.append(closeCurrentParagraph(paragraphTagStack)).append("\n");
    }

    /**
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
     * @param line
     * @return
     */
    public static String computeParagraph(String line, List<String> paragraphTagStack) {
        int baliseIndex = getWeakBaliseIndex(Regex.HtmlRegexConstruct.PARAGRAPH_HTML_TAG.getValue(), paragraphTagStack);
        if (baliseIndex == -1) {
            paragraphTagStack.add(Regex.HtmlRegexConstruct.PARAGRAPH_HTML_TAG.getValue());
            return String.format("<%s>%s", Regex.HtmlRegexConstruct.PARAGRAPH_HTML_TAG.getValue(), line);
        }
        return line;
    }

    /**
     * @param balise
     * @return
     */
    public static int getWeakBaliseIndex(String balise, List<String> paragraphTagStack) {
        return IntStream.range(0, paragraphTagStack.size())
                .filter(index -> paragraphTagStack.get(index).equals(balise))
                .findFirst()
                .orElse(-1);
    }


    /**
     * @return
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
