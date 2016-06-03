import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.model.Thread;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by daniel on 6/2/16.
 */
public class Inbox {

    // Recipient's email ID needs to be mentioned.
    private String to = "evansdb0@sewanee.edu";

    private String me = "evansdb0@sewanee.edu";

    // Assuming you are sending email from localhost
    private String host = "localhost";


        private static final String DATE = "Date";
        private static final String DELIVERED_TO = "Delivered-To";
        private static final String FROM = "From";
        private static final String TO = "To";
        private static final String REPLY_TO = "Reply-To";
        private static final String SUBJECT = "Subject";
        private static final String LIST_UNSUBSCRIBE = "List-Unsubscribe";
        private static final String MAILING_LIST = "Mailing-List";

        /**
         * Application name.
         */
        private static final String APPLICATION_NAME =
                "Gmail API Java Quickstart";

        /**
         * Directory to store user credentials for this application.
         */
        private static final java.io.File DATA_STORE_DIR = new java.io.File(
                System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");

        /**
         * Global instance of the {@link FileDataStoreFactory}.
         */
        private static FileDataStoreFactory DATA_STORE_FACTORY;

        /**
         * Global instance of the JSON factory.
         */
        private static final JsonFactory JSON_FACTORY =
                JacksonFactory.getDefaultInstance();

        /**
         * Global instance of the HTTP transport.
         */
        private static HttpTransport HTTP_TRANSPORT;

        /**
         * Global instance of the scopes required by this quickstart.
         * <p>
         * If modifying these scopes, delete your previously saved credentials
         * at ~/.credentials/gmail-java-quickstart.json
         */
        private static final List<String> SCOPES =
                Arrays.asList(GmailScopes.GMAIL_LABELS
                        , GmailScopes.GMAIL_COMPOSE
                        ,GmailScopes.GMAIL_MODIFY);

       /* static {
            try {
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        }
*/
    private List<Message> inbox;
    private Authenticator auth;

    /**
     * default constructor
     * sets up oAuth authentication and ensures access to all neccessary
     * facilities of the user's account
     * @param auth Authenticator that performs the oauth authentication
     */
    public Inbox(Authenticator auth) {
        userId = "me";
        this.auth = auth;
        try {
            inbox = getInbox();
        } catch (IOException e) {
            System.out.println("Something went wrong on our end");
            e.printStackTrace();
        }
    }
    public Authenticator getAuth() {
        return auth;
    }

    /**
     * Helper method for decoding base64Encoded strings
     * @param base64String a base64 encoded string
     * @return decoded String in plain text
     */
    public static String decodeString(String base64String) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(base64String));
    }

    /**
     *
     * @param message Any Gmail message
     * @return the text in the body of message
     * @throws IOException
     */
    public String getMessageBody(Message message)
            throws IOException {
        //
        if(message == null) throw new NullPointerException("message is null");
        // get a full instance of the message, rather than just the message Id contained in the
        Message m = getFullMessageInstance(message);
        // print message body
        return decodeString(m.getPayload().getParts().get(0).getBody().getData());
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
     *
     * @param message Any Gmail message
     * @return the person who sent this message to the user
     * @throws IOException
     * @throws MessagingException
     */
    public String getFrom(Message message)
            throws IOException, MessagingException {

        return getHeaderPart(message,FROM);
    }

    /**
     *
     * @param message Any Gmail message
     * @return email address that can be used to reply to message
     * @throws IOException
     * @throws MessagingException
     */
    public String getReplyToAddress(Message message)
            throws IOException, MessagingException {

        return getHeaderPart(message,REPLY_TO);
    }

    /**
     *
     * @param message Any Gmail message
     * @return Subject of the message
     * @throws IOException
     * @throws MessagingException
     */
    public String getSubject(Message message)
            throws IOException, MessagingException {

        return getHeaderPart(message,SUBJECT);
    }

    /**
     *
     * @param message Any Gmail message
     * @return the person the message was sent to
     * @throws IOException
     * @throws MessagingException
     */
    public String getDeliveredTo(Message message)
            throws IOException, MessagingException {

        return getHeaderPart(message,DELIVERED_TO);
    }

    /**
     * get the unsubscribe link from the mailing list
     * @param message Any Gmail message
     * @return the link the user can go to to unsubscribe from this mailing list, if it exists
     * @throws IOException
     * @throws MessagingException
     */
    public String getUnsubscribeLink(Message message)
            throws IOException, MessagingException {

        return getHeaderPart(message,LIST_UNSUBSCRIBE);
    }

    /**
     * gets the decode raw version of the email, formatted with format=RAW
     * @param message any Gmail messaage
     * @return decoded raw message
     * @throws IOException
     */
/*    public static String getMessageRaw(Message message) throws IOException {
        Message m = getRawVersion(message);
        return decodeString(m.getRaw());
    }*/

    /**
     * helper method to get certain pieces of the message
     * @param message the message to extract the header value from
     * @param part the name of the header part
     * @return returns the value of the header designated by part
     * @throws IOException
     */
    public String getHeaderPart(Message message, String part) throws IOException {
        Message m = getFullMessageInstance(message);

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
     * helper method that gets the message formatted with format=RAW
     * @param message doesn't assume this is a full instance method
     * @return returns the entire raw version of the message
     * @throws IOException
     */
    private Message getRawVersion(Message message) throws IOException {
        return auth.service.users().messages().get(userId, message.getId()).set("format","RAW").execute();
    }

    /**
     * gets the message with its full payload
     * @param message a list message
     * @return a full instance version of the message passed
     * @throws IOException
     */
    public Message getFullMessageInstance(Message message) throws IOException {
        return auth.service.users().messages().get(userId, message.getId()).execute();
    }

    /**
     * search inbox for messages matching query
     * @param query searches user's inbox for messages using this query
     * @return returns a list of messages matching the query
     * @throws IOException
     * @throws MessagingException
     */
    public List<Message> listMessagesMatchingQuery(String query)
                                                        throws IOException, MessagingException {
        ListMessagesResponse response = auth.service.users()
                .messages().list(userId).setQ(query).setMaxResults(10L).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = auth.service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return messages;
    }

    /**
     *
     * @return returns a Credential either authorized by the user
     * or throws an exception if the user provide access consent
     * @throws IOException
     */
/*    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                Inbox.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     *
     * @return returns the gmail service authorized with the user's credential
     * @throws IOException
     */
 /*   public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
*/
    /**
     * @return list of messages that contain their respective messageId and threadId
     * @throws IOException
     */
    public List<Message> getInbox() throws IOException {
        int  numMessages = 0;
        // list messages in inbox
        ListMessagesResponse response = auth.service.users().messages().list("me")
                .execute();
        // array list to store messages from each page
        List<Message> messages = new ArrayList<>();
        while (response.getMessages() != null) {

            messages.addAll(response.getMessages());
/*            for(Message m : response.getMessages()) {
                // TODO: store messageIds here
                // TODO: store numMessages
                // TODO: need to limit the number of messages returned here
                System.out.println(m.getId());
                System.out.println(++numMessages);
            }

            if (response.getNextPageToken() != null) {

                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
            */
            break;
        }
/*
        int i = 0;
        for (Message message : messages) {
            System.out.println(++i);
            System.out.println(message.getId());
        }
*/
        return messages;
    }

    /**
     *
     * @return the list of messages in the user's inbox. Note that these are not
     * full instance messages
     */
    public List<Message> getDefaultInbox() {
        return inbox;
    }
    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * can be used to indicate the authenticated user.
     * @param email Email to be sent.
     * @throws MessagingException
     * @throws IOException
     */
    public void sendMessage(MimeMessage email)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        message = auth.service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
    }

    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64 encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     *
     * @return Session with the default system properties
     *
     */
    public Session getSessionWithDefaultProps() {
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host",host);
        return Session.getDefaultInstance(props);
    }

    /**
     *
     * @param session most cases use getSessionWithDefaultProps
     * @param to email address of receiver
     * @param subject subject of message
     * @param message body of the message
     * @return composed MimeMessage
     * @throws MessagingException
     */
    public MimeMessage composeMessage(Session session,
                                     String to,
                                     String subject,
                                     String message) throws MessagingException {

        MimeMessage m = new MimeMessage(session);
        m.setFrom(new InternetAddress(me));

        // TODO: if CC/BCC contains an email address, iterate through the
        // TODO: email addresses using this method
        m.addRecipients(javax.mail.Message.RecipientType.CC, to);
        m.setSubject(subject);
        m.setText(message);
        return m;
    }

    /**
     *
     * @param thread useful information about thread
     * @throws IOException
     */
    public void getThreadInfo(Thread thread) throws IOException {
        Thread t = auth.service.users().threads().get(auth.userId, thread.getId()).execute();
        System.out.println("No. of messages in this thread: " + t.getMessages().size());
        System.out.println(t.toPrettyString());
    }

    /**
     *
     * @param query searches user's inbox for threads using this query
     * @return list of threads matching query
     * @throws IOException
     */
    public List<Thread> listThreadsMatchingQuery(String query) throws IOException {
        ListThreadsResponse response = auth.service.users().threads().list(auth.userId).setQ(query).execute();
        List<Thread> threads = new ArrayList<>();
        threads.addAll(response.getThreads());
        return threads;
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
