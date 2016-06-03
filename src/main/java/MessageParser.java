import org.jetbrains.annotations.Contract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by daniel on 6/2/16.
 */
// TODO: detect links so we can send the user to that page
public class MessageParser {

    /**
     *
     * @param message Any gmail message
     * @return all img tags in message
     * @throws IOException
     */
    public static Elements getImgs(FullMessage message) throws IOException {
        Document doc = generateDocument(message);
        return doc.select("img");
    }

    /**
     *
     * @param imgElement expects an element of type img
     * @return the src attribute of the element or nothing if the element has no src attribute
     */
    public static String getImgSrc(Element imgElement) {

        return imgElement.attr("src");
    }

    /**
     *
     * @param message Any gmail message
     * @return the src attribute of the first image in the email
     * @throws IOException
     */
    public static String getFirstImgSrc(FullMessage message) throws IOException {

        Document doc = generateDocument(message);
        return doc.select("img").first().attr("src");
    }

    /**
     *
     * @param message Ay gmail message
     * @return All the a links in the email
     * @throws IOException
     */
    public static Elements getLinks(FullMessage message) throws IOException {

        Document doc = generateDocument(message);
        return doc.select("a");
    }

    /**
     *
     * @param linkElement expects an element of type a
     * @return the href attribute of the element or nothing if the element has no href attribute
     */
    public static String getHref(Element linkElement) {

        return linkElement.attr("href");
    }

    /**
     * helper method for getting a parsable version of the email message
     * @param message Ay gmail message
     * @return Document representing the jsoup parsed version of the email message
     * @throws IOException
     */
    @Contract("null -> fail")
    private static Document generateDocument(FullMessage message)
            throws IOException {
        if(message == null) throw new NullPointerException("message can't be null");
        return Jsoup.parse(message.getMessageAsHTML());
    }
}
