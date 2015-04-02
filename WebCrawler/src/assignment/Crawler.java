package assignment;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class upon which WebCrawler can be built. This uses TagSoup
 * and the SAX API. For more information on the SAX check out
 * http://www.saxproject.org/
 */
public abstract class Crawler extends DefaultHandler {
    protected Parser parser = new Parser();

    /**
     * Returns the WebIndex object built by this Crawler.
     *
     * @return    a WebIndex object covering all pages crawled by this Crawler.
     */
    public abstract WebIndex getWebIndex();

    public abstract List<URL> parse(URL url) throws IOException, SAXException;

    /**
     * Called when the parser first starts reading a document.
     */
    public abstract void startDocument();

    /**
     * Called when the parser finishes reading a document.
     */
    public abstract void endDocument();

    /**
     * Called at the start of any tag.
     */
    public abstract void startElement(String uri, String name, String qName, Attributes atts);

    /**
     * Called at the end of any tag.
     */
    public abstract void endElement(String uri, String name, String qName);

    /**
     * Called whenever characters are found inside a tag. Note that the parser is not
     * required to return all characters in the tag in a single chunk. Whitespace is
     * also returned as characters.
     */
    public abstract void characters(char ch[], int start, int length);
}
