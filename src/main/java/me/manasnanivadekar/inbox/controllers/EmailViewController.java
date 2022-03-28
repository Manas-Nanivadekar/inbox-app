package me.manasnanivadekar.inbox.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import me.manasnanivadekar.inbox.email.Email;
import me.manasnanivadekar.inbox.email.EmailRepository;
import me.manasnanivadekar.inbox.emaillist.EmailListItem;
import me.manasnanivadekar.inbox.emaillist.EmailListItemKey;
import me.manasnanivadekar.inbox.emaillist.EmailListItemRepository;
import me.manasnanivadekar.inbox.folders.Folder;
import me.manasnanivadekar.inbox.folders.FolderRepository;
import me.manasnanivadekar.inbox.folders.FolderService;
import me.manasnanivadekar.inbox.folders.UnreadEmailStatsRepository;

@Controller
public class EmailViewController {
    // Folders
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private FolderService folderService;

    // Email
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private EmailListItemRepository emailListItemRepository;
    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping(value = "/emails/{id}")
    public String emailView(
            @RequestParam String folder,
            @PathVariable UUID id,
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }

        // Fetch Folders
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (optionalEmail.isEmpty()) {
            return "inbox-page";
        }

        Email email = optionalEmail.get();
        String toIds = String.join(", ", email.getTo());
        model.addAttribute("email", email);
        model.addAttribute("toIds", toIds);

        EmailListItemKey key = new EmailListItemKey();
        key.setId(userId);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);
        if (optionalEmailListItem.isPresent()) {
            EmailListItem emailListItem = optionalEmailListItem.get();
            if (emailListItem.isUnread()) {
                emailListItem.setUnread(false);
                emailListItemRepository.save(emailListItem);
                unreadEmailStatsRepository.decrementUnreadCount(userId, folder);
            }
        }
        model.addAttribute("stats", folderService.mapCountToLabels(userId));

        return "email-page";
    }
}
