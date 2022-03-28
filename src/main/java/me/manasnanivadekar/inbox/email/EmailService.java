package me.manasnanivadekar.inbox.email;

import java.util.List;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.manasnanivadekar.inbox.emaillist.EmailListItem;
import me.manasnanivadekar.inbox.emaillist.EmailListItemKey;
import me.manasnanivadekar.inbox.emaillist.EmailListItemRepository;
import me.manasnanivadekar.inbox.folders.UnreadEmailStatsRepository;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private EmailListItemRepository emailListItemRepository;
    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from, List<String> to, String subject, String body) {
        Email email = new Email();
        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setId(Uuids.timeBased());
        emailRepository.save(email);

        to.forEach(toId -> {
            EmailListItem item = createEmailListItem(to, subject, email, toId, "Inbox");
            emailListItemRepository.save(item);
            unreadEmailStatsRepository.incrementUnreadCount(toId, "Inbox");
        });

        EmailListItem sentItemEntry = createEmailListItem(to, subject, email, from, "Sent Items");
        sentItemEntry.setUnread(false);
        emailListItemRepository.save(sentItemEntry);

    }

    private EmailListItem createEmailListItem(List<String> to, String subject, Email email, String itemOwner,
            String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setId(itemOwner);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        EmailListItem item = new EmailListItem();
        item.setKey(key);
        item.setTo(to);
        item.setSubject(subject);
        item.setUnread(true);

        emailListItemRepository.save(item);
        return item;
    }
}
