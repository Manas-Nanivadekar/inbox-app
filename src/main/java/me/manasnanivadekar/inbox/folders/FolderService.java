package me.manasnanivadekar.inbox.folders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    // Unread Email
    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public List<Folder> fetchDefaultFolders(String userId) {
        return Arrays.asList(
                new Folder(userId, "Inbox", "blue"),
                new Folder(userId, "Sent Items", "greeen"),
                new Folder(userId, "Important", "yellow"));
    }

    public Map<String, Integer> mapCountToLabels(String userId) {
        // Fetch Undread Counter
        List<UnreadEmailStats> stats = unreadEmailStatsRepository.findAllById(userId);
        return stats.stream().collect(Collectors.toMap(UnreadEmailStats::getLabel, UnreadEmailStats::getUnreadCount));
    }
}
