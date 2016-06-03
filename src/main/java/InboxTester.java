import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.List;

/**
 * Created by d\aniel on 6/2/16.
 */
public class InboxTester {

    public static void main(String[] args) throws IOException {

        Inbox inbox = new Inbox(new Authenticator("evansdb0@sewanee.edu"));

        System.out.println(inbox.getAuth());

        List<Message> messages = inbox.getDefaultInbox();

        FullMessage m = new FullMessage(inbox, messages.get(0));

        System.out.println(m.get);
    }
}
