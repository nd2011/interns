package com.example.demo.Controller;

import com.example.demo.Entity.Meeting;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MeetingRepository;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Service.MeetingService;
import com.example.demo.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/meetings")
public class MeetingController {
    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MyAppUserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

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
        if (currentUser.getRoles() != null) {
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

        // Tạo thông báo cho các user được giao nhiệm vụ
        List<Long> userIds = new ArrayList<>(participantIds);
        String title = "Bạn vừa được thêm vào cuộc họp: " + meeting.getTitle();
        String link = "/meetings/view/" + meeting.getId();
        notificationService.notifyUsers(userIds, title, link);

        return "redirect:/meetings";
    }
    @GetMapping("/view/{id}")
    public String viewMeeting(@PathVariable Long id, Model model) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found!"));
        model.addAttribute("meeting", meeting);
        return "meetings/view";
    }
    @PostMapping(value = "/create-ajax", produces = "application/json")
    @ResponseBody
    public Map<String, Object> createMeetingAjax(
            @ModelAttribute Meeting meeting,
            @RequestParam(value = "participantIds", required = false) Set<Long> participantIds) {

        Map<String, Object> result = new HashMap<>();
        try {
            if (participantIds == null) participantIds = new HashSet<>();
            meetingService.createMeeting(meeting, participantIds);

            // Gửi notification như cũ

            // Tạo meetingDto chỉ trả về trường cần thiết, không trả về object Meeting
            Map<String, Object> meetingDto = new HashMap<>();
            meetingDto.put("id", meeting.getId());
            meetingDto.put("title", meeting.getTitle());
            meetingDto.put("description", meeting.getDescription());
            meetingDto.put("startTime", meeting.getStartTime());
            meetingDto.put("endTime", meeting.getEndTime());

            result.put("success", true);
            result.put("meeting", meetingDto); // CHỈ TRẢ MAP ĐƠN GIẢN!
        } catch (Exception ex) {
            result.put("success", false);
            result.put("message", ex.getMessage());
        }
        return result;
    }

}
