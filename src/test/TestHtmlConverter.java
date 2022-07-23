import org.testng.Assert;
import org.testng.annotations.Test;

import static mailchimp.markuphtmlconverter.MarkDownToHTMLConverter.executeConvertToHTML;

public class TestHtmlConverter {

    @Test
    public void testEmptyString() {
        Assert.assertEquals(executeConvertToHTML(""), "", " Error : Expected an empty line");
    }

    // Come back to this
    @Test
    public void testURL() {
        String url = executeConvertToHTML("[Link text](https://www.example.com)");
        Assert.assertEquals(url, "<p><a href=\"https://www.example.com\">Link text</a></p>");
    }

    @Test
    public void testHeaderWithURL() {
        String url = executeConvertToHTML("## This is a header [with a link](http://yahoo.com)");
        Assert.assertEquals(url, "<h2>This is a header <a href=\"http://yahoo.com\">with a link</a></h2>");
    }

    @Test
    public void testParagraphURL() {
        String url = executeConvertToHTML("This is a paragraph [with an inline link](http://google.com). Neat, eh?");
        Assert.assertEquals(url, "<p>This is a paragraph <a href=\"http://google.com\">with an inline link</a>. Neat, eh?</p>");
    }

    @Test
    public void testInvalidHeader() {
        String url = executeConvertToHTML("############## Heading 6+\n ##Invalid Header");
        Assert.assertEquals(url, "<p>############## Heading 6+\n ##Invalid Header</p>");
    }

    @Test
    public void testMalformedURL() {
        String url = executeConvertToHTML("[Link text(https://www.example.com)");
        Assert.assertEquals(url, "<p>[Link text(https://www.example.com)</p>");
    }

    @Test
    public void testMalformedString() {
        String url = executeConvertToHTML("12w3e4ruijk hgfASZ≈c vbnkml-!@#$%^&*()     ");
        Assert.assertEquals(url, "<p>12w3e4ruijk hgfASZ≈c vbnkml-!@#$%^&*()     </p>");
    }

    @Test
    public void testHeaderURL() {
        String url = executeConvertToHTML("## This is a header [with a link](http://yahoo.com)");
        Assert.assertEquals(url, "<h2>This is a header <a href=\"http://yahoo.com\">with a link</a></h2>");
    }

    @Test
    public void testEmptySpaces() {
        String emptySpaces = executeConvertToHTML("\n \n \n");
        Assert.assertEquals(emptySpaces, "\n\n\n");

    }

    @Test
    public void testValidHeaders() {
        String actualHtml = executeConvertToHTML("# Header1 \n" +
                "## Header 2 \n" +
                "### Header 3 \n" +
                "#### Header4 \n" +
                "##### Header 5\n" +
                "###### Header 6\n");
        String expectedHtml = "<h1>Header1 </h1>\n" +
                "<h2>Header 2 </h2>\n" +
                "<h3>Header 3 </h3>\n" +
                "<h4>Header4 </h4>\n" +
                "<h5>Header 5</h5>\n" +
                "<h6>Header 6</h6>";
        Assert.assertEquals(actualHtml, expectedHtml);
    }

}
