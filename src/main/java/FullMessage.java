import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;
import com.google.api.services.gmail.model.MessagePartHeader;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 6/3/16.
 */
public class FullMessage {

    private static final String DATE = "Date";
    private static final String DELIVERED_TO = "Delivered-To";
    private static final String FROM = "From";
    private static final String TO = "To";
    private static final String REPLY_TO = "Reply-To";
    private static final String SUBJECT = "Subject";
    private static final String LIST_UNSUBSCRIBE = "List-Unsubscribe";
    private static final String MAILING_LIST = "Mailing-List";

    private Message m;
    private Authenticator auth;


    // TODO: surround long returns with try/catch(NPE e)

    public FullMessage(Authenticator auth, Message message) throws IOException {

        this.auth = auth;
        this.m = getFullMessageInstance(message);
    }

    public String getSnippet() {

        m.getSnippet();
        return null;
    }
    public FullMessage(Inbox inbox, Message message) throws IOException {
        this(inbox.getAuth(),message);
    }
    /**
     * @return the message body in HTML
     * @throws IOException
     */
    public String getMessageAsHTML() throws IOException {
        String html = Inbox.decodeString(m.getPayload().getParts().get(1)
                                                        .getBody().getData());

        if(html == null)
            html = getRawVersion(m).getRaw();

        return html;
    }

    /**
     * gets the message with its full payload
     * @return a full instance version of the message passed
     * @throws IOException
     */
    public Message getFullMessageInstance() throws IOException {
        return auth.service.users().messages().get(auth.userId, m.getId()).execute();
    }
    public Message getFullMessageInstance(Message message) throws IOException {
        return auth.service.users().messages().get(auth.userId, message.getId()).execute();
    }
    /**
     * helper method that gets the message formatted with format=RAW
     * @param message doesn't assume this is a full instance method
     * @return returns the entire raw version of the message
     * @throws IOException
     */
    private Message getRawVersion(Message message) throws IOException {
        return auth.service.users().messages().get(auth.userId, message.getId()).set("format","RAW").execute();
    }
    /**
     * helper method to get certain pieces of the message
     * @param part the name of the header part
     * @return returns the value of the header designated by part
     * @throws IOException
     */
    public String getHeaderPart(String part) throws IOException {

        List<MessagePartHeader> headers = m.getPayload().getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            if(headers.get(i).getName().equals(part)) {
                System.out.println(headers.get(i).getValue());
                return headers.get(i).getValue();
            }
        }
        // at this point, we know message is not found
        // so return empty string
        return "";
    }
    /**
     *
     * @return the text in the body of message
     * @throws IOException
     */
    public String getMessageBody()
            throws IOException {

        // print message body
        return Inbox.decodeString(m.getPayload().getParts().get(0).getBody().getData());
    }
    private String getMessageBody(Message message) {
        // print message body
        return Inbox.decodeString(message.getPayload().getParts().get(0).getBody().getData());
    }
    /**
     *
     * @param thread thread to take the message from
     * @param whichMessage number of the message in the thread, 0 = first message
     *                     1 = second, etc
     * @return the body of the specified message in the thread, or the body
     * of the last message if whichMessage greater than thread.getMessages().size()
     * @throws IOException
     */
    public String getBodyOfMessageInThread(Thread thread, int whichMessage)
            throws IOException {

        if(thread == null) throw new NullPointerException("message is null");

        Thread t = getFullThreadInstance(thread);

        // if user requests a message that doesn't exist in thread (ex: only 2 messages
        // in the thread, user requests the third), then return the last message
        if(t.getMessages().size() <= whichMessage) {
            // return the last message in the thread
            return getMessageBody(t.getMessages().get(t.getMessages().size()-1));
        }
        // return the message queried for
        return getMessageBody(t.getMessages().get(whichMessage));
    }

    /**
     * @return the person who sent this message to the user
     * @throws IOException
     * @throws MessagingException
     */
    public String getFrom()
            throws IOException, MessagingException {

        return getHeaderPart(FROM);
    }
    public String getDate() throws IOException {
        return getHeaderPart(DATE);
    }
    public String getTo() throws IOException {
        return getHeaderPart(TO);
    }
    public String getMailingList() throws IOException {
        return getHeaderPart(MAILING_LIST);
    }

    /**
     * @return email address that can be used to reply to message
     * @throws IOException
     * @throws MessagingException
     */
    public String getReplyToAddress()
            throws IOException, MessagingException {

        return getHeaderPart(REPLY_TO);
    }

    /**
     * @return Subject of the message
     * @throws IOException
     * @throws MessagingException
     */
    public String getSubject()
            throws IOException, MessagingException {

        return getHeaderPart(SUBJECT);
    }

    /**
     * @return the person the message was sent to
     * @throws IOException
     * @throws MessagingException
     */
    public String getDeliveredTo()
            throws IOException, MessagingException {

        return getHeaderPart(DELIVERED_TO);
    }

    /**
     * get the unsubscribe link from the mailing list
     * @return the link the user can go to to unsubscribe from this mailing list, if it exists
     * @throws IOException
     * @throws MessagingException
     */
    public String getUnsubscribeLink()
            throws IOException, MessagingException {

        return getHeaderPart(LIST_UNSUBSCRIBE);
    }
    /**
     *
     * @param thread any thread
     * @return a thread with full payload
     * @throws IOException
     */
    public Thread getFullThreadInstance(Thread thread) throws IOException {
        return auth.service.users().threads().get(auth.userId, thread.getId()).execute();
    }
}
