package me.manasnanivadekar.inbox.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import me.manasnanivadekar.inbox.folders.Folder;
import me.manasnanivadekar.inbox.folders.FolderRepository;

@Controller
public class InboxControllers {

    @Autowired
    private FolderRepository folderRepository;

    @GetMapping(value = "/")
    public String homePage(
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }

        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);

        return "inbox-page";
    }
}
