# MarkDownToHTMLConverter

# Overview
This program converts Markdown text to HTML format

Example:

```

# Sample Document

Hello!

This is sample markdown for the [Mailchimp](https://www.mailchimp.com) homework assignment.


We would expect this to convert to the following HTML:

<h1>Sample Document</h1>

<p>Hello!</p>

<p>This is sample markdown for the <a href="https://www.mailchimp.com">Mailchimp</a> homework assignment</p>

```

# Guidelines

1. Clone this repository. More instructions on How-to : https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository

2. MarkupText is the input file, you may test any markdown text within the specified scope of the project.

3. MarkDownToHTMLConverter conatains  main() to execute the project.

4. Output.txt will be generated and will contain the processed input -> HTML output

5. mailchimp.markuphtmlconverter.test.TestHtmlConverter contains testNg tests

# Source Code Review

1. Input file is converted to String for easier line traversal

2. Creates a StringBuilder to capture the result

3. Breaks down the markDownText line by line.
     
4. Checks if current line a new line (\n)
     a. If the current line is a new line which is empty,
        verify that a multiline paragraph (if applicable) has the correctly closed the tag </p>.
     b. If the previous line is not empty, and the current line is empty,
        check the stack for any unclosed <p> and close accordingly.
     
     *   else
     
5. Checks if current line is a Header using regex, maintain a paragraph stack to indicate beginning or ending of a multi-line paragraph
        a. Verifies if previous line was a  paragraph, if yes, close </p>
        b. Additionally, check if the header contains URL Regex and convert to HTML accordingly.
     
     *   else
     
6. Assumes the current line is a paragraph within the scope of the Homework assignment 
        a. Check if the header contains URL Regex and convert  to HTML accordingly.
        b. Append the paragraph to the StringBuilder prepending the line with <p>
                push this "p" to a stack to keep track of new line or track closing of paragraph
        c. Check if this is the last line to processed, if yes, close the paragraph tag accordingly.
 
7. As I traverse through the markdown text and append the result to the HTML stringBuilder,
        we append a "\n" after each match. However, if we do not address this addition of "\n",
        we risk adding an unnecessary new line at the end of the string. Hence, we remove "\n" once all lines are traversed.

8. Return the HTML stringBuilder to  executeConvertToHTML()
