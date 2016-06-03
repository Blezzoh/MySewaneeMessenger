
import com.google.api.client.util.store.FileDataStoreFactory;

import javax.mail.MessagingException;
import java.io.IOException;

public class GmailQuickstart {
    /*

    private static final String FROM_ADDRESS = "evansdb0@sewanee.edu";
    private static final String TO_ADDRESS = "iradub0@sewanee.edu";
    private static final String MESSAGE = "HELLO BLAISE";

    public static final int MIME_VERSION = 1;
    public static final int RECEIVED = 2;
    public static final int DATE = 3;
    public static final int DELIVERED_TO = 4;
    public static final int MESSAGE_ID = 5;
    public static final int SUBJECT = 6;
    public static final int FROM = 7;
    public static final int TO = 8;
    public static final int CONTENT_TYPE = 9;

    */
/**
     * Application name.
     *//*

    private static final String APPLICATION_NAME =
            "Gmail API Java Quickstart";

    */
/**
     * Directory to store user credentials for this application.
     *//*

    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");

    */
/**
     * Global instance of the {@link FileDataStoreFactory}.
     *//*

    private static FileDataStoreFactory DATA_STORE_FACTORY;

    */
/**
     * Global instance of the JSON factory.
     *//*

    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    */
/**
     * Global instance of the HTTP transport.
     *//*

    private static HttpTransport HTTP_TRANSPORT;

    */
/**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart.json
     *//*

    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS
                    , GmailScopes.GMAIL_COMPOSE
                    ,GmailScopes.GMAIL_MODIFY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    public static List<String> listLabels(Gmail service, String userId) throws IOException {
        ListLabelsResponse response = service.users().labels().list(userId).execute();
        List<Label> labels = response.getLabels();
        List<String> labelIds = new ArrayList<>();
         for (int i = 0; i < labels.size(); i++) {
            //System.out.println(labels.get(i));
             labelIds.add(labels.get(i).getId());
        }
        return labelIds;
    }
    public static void getMessageDate(Gmail service, String userId, String messageId)
            throws IOException, MessagingException {

        Message message = service.users().messages().get(userId, messageId).execute();
        String value = message.getPayload().getHeaders().get(1).getValue();
        // TODO: regex for Wed, Thurs, etc
        int index = value.lastIndexOf("Wed");
        System.out.println(value.substring(index));
    }
    public static String decodeString(String base64String) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(base64String));
    }
    public static void getMessageBody(Gmail service, String userId, String messageId)
            throws IOException {

        Message m = service.users().messages().get(userId,messageId).setFormat("full").execute();
        //System.out.println(decodeString(m.getPayload().getParts().get(0).getBody().getData()));
        int i =0;
        while(m.getPayload().getHeaders() != null) {
            System.out.println(m.getPayload().getHeaders().get(i));
            i++;
        }
        System.out.println(decodeString(m.getPayload().getParts().get(TO).getBody().getData()));

    }
    public static void main(String[] args) throws IOException, MessagingException {
        // Build a new authorized API client service.
        Gmail service = getGmailService();

        // Print the labels in the user's account.
        String user = "me";
        */
/*ListLabelsResponse listResponse =
                service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }*//*

        listMessagesMatchingQuery(service,"me","barbara banks");
    }
    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
                                       String query) throws IOException, MessagingException {
        ListMessagesResponse response = service.users()
                .messages().list(userId).setQ(query).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        for(int i = 0; i<messages.size(); ++i) {
            getMessageBody(service,"me",messages.get(i).getId());
            System.out.println(i);
            break;
        }

        return messages;
    }
    */
/**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     *//*

*/
/*    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                GmailQuickstart.class.getResourceAsStream("/client_secret.json");
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
    }*//*


    */
/**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     *//*

    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    */
/**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to       email address of the receiver
     * @param from     email address of the sender, the mailbox account
     * @param subject  subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     *//*

    public static MimeMessage createEmail(String to, String from, String subject,
                                          String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static void sendMessage(Gmail service, String userId, MimeMessage email)
            throws MessagingException, IOException {

        Message message = createMessageWithEmail(email);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message Id:" + message.getId());
        System.out.println(message.toPrettyString());
    }

    */
/**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64 encoded email.
     * @throws IOException
     * @throws MessagingException
     *//*

    private static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

*/}
