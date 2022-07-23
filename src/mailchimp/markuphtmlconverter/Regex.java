package mailchimp.markuphtmlconverter;

/**
 * Created by rkishore on Jul, 2022
 */
public class Regex {

    public enum MarkDownRegexConstruct {
        // Regex for Titles pattern matching
        TITLE_MARKDOWN_MATCHER("(^[#]{1,6})[\\s](.*)([\\s\\S]*)"),
        TITLE_MARKDOWN("(^[#]{1,6})[\\s].+"),
        TITLE_NUMBER("^([#]){1,6}[\\s]"),

        // Regex for URL pattern matching
        URL_MARKDOWN("(.*)\\[([^]]*)\\]\\(([^\\s^\\)]*)[\\s\\)](.*)"),

        // Regex for Dot all new line
        DOT_ALL("([\\s\\S]*)");

        private final String stringValue;

        MarkDownRegexConstruct(final String stringValue) {
            this.stringValue = stringValue;
        }

        public String getValue() {
            return this.stringValue;
        }
    }

    public enum HtmlRegexConstruct {
        // Regex for Titles pattern matching
        PARAGRAPH_HTML_TAG("p"),

        // Regex for URL pattern matching
        TITLE_HTML_TAG("<h%d>%s</h%d>"),

        // Regex for Dot all new line
        URL_HTML_TAG("%s<a href=\"%s\">%s</a>%s");

        private final String stringValue;

        HtmlRegexConstruct(final String stringValue) {
            this.stringValue = stringValue;
        }

        public String getValue() {
            return this.stringValue;
        }
    }
}
