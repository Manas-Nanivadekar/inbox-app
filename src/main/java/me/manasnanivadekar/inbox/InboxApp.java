package me.manasnanivadekar.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.manasnanivadekar.inbox.email.EmailService;
import me.manasnanivadekar.inbox.folders.Folder;
import me.manasnanivadekar.inbox.folders.FolderRepository;

@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@RequestMapping("/user")
	public String user(@AuthenticationPrincipal OAuth2User principal) {
		System.out.println(principal);
		return principal.getAttribute("name");
	}

	@PostConstruct
	public void init() {
		folderRepository.save(new Folder("Manas-Nanivadekar", "Work", "blue"));
		folderRepository.save(new Folder("Manas-Nanivadekar", "Home", "green"));
		folderRepository.save(new Folder("Manas-Nanivadekar", "StartUp", "yellow"));

		for (int i = 0; i < 10; i++) {
			emailService.sendEmail("Manas-Nanivadekar", Arrays.asList("Manas-Nanivadekar", "abc"), "Hello" + i, "Body");
		}
	}

}
