package com.example.demo.Controller;

import com.example.demo.Entity.Meeting;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MyAppUserRepository userRepository;

    // Hiển thị danh sách cuộc họp của người dùng hiện tại
    @GetMapping
    public String listUserMeetings(Model model, @AuthenticationPrincipal MyAppUser currentUser) {
        List<Meeting> meetings = meetingService.getAllMeetingsForUser(currentUser.getId());
        model.addAttribute("meetings", meetings);
        model.addAttribute("meeting", new Meeting());
        model.addAttribute("users", userRepository.findAll().stream()
                .filter(u -> !u.getRoles().contains("ADMIN") && !u.getRoles().contains("ROLE_ADMIN"))
                .collect(Collectors.toList()));

        // Sửa đoạn này cho chuẩn (phòng trường hợp roles là "ROLE_ADMIN")
        boolean isAdmin = false;
        if (currentUser != null && currentUser.getRoles() != null) {
            for (String role : currentUser.getRoles()) {
                if ("ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)) {
                    isAdmin = true;
                    break;
                }
            }
        }
        model.addAttribute("isAdmin", isAdmin);

        return "meetings/list";
    }


    // Trang tạo cuộc họp (chỉ Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("meeting", new Meeting());
        model.addAttribute("users", userRepository.findAll().stream()
                .filter(u -> !u.getRoles().contains("ADMIN"))
                .collect(Collectors.toList()));
        return "meetings/create";
    }

    // Xử lý tạo cuộc họp (chỉ Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createMeeting(@ModelAttribute Meeting meeting,
                                @RequestParam(value = "participantIds", required = false) Set<Long> participantIds) {
        if (participantIds == null) participantIds = new HashSet<>();
        meetingService.createMeeting(meeting, participantIds);
        return "redirect:/meetings";
    }
}
