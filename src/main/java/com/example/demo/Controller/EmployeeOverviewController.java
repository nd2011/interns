package com.example.demo.Controller;

import com.example.demo.Entity.Meeting;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.Task;
import com.example.demo.Repository.MeetingRepository;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class EmployeeOverviewController {

    @Autowired
    private MyAppUserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @GetMapping("/employees/overview")
    public String showEmployeeOverview(Model model) {
        List<MyAppUser> users = userRepository.findAll();
        Map<Long, List<Meeting>> userMeetingsMap = new HashMap<>();
        Map<Long, List<Project>> userProjectsMap = new HashMap<>();
        Map<Long, Map<Long, List<Task>>> userProjectTasksMap = new HashMap<>();
        Map<Long, String> meetingStartTimesMap = new HashMap<>();

        for (MyAppUser user : users) {
            // Get meetings for user
            List<Meeting> meetings = meetingRepository.findByParticipants_Id(user.getId());
            userMeetingsMap.put(user.getId(), meetings);

            // Collect formatted start times for meetings
            for (Meeting meeting : meetings) {
                if (meeting.getStartTime() != null) {
                    String formattedStartTime = meeting.getStartTime()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    meetingStartTimesMap.put(meeting.getId(), formattedStartTime);
                }
            }

            // Get projects for user
            List<Project> projects = projectRepository.findByAssignedUsers(user);
            userProjectsMap.put(user.getId(), projects);

            // For each project, get tasks assigned to user
            Map<Long, List<Task>> projectTasks = new HashMap<>();
            for (Project project : projects) {
                List<Task> tasks = taskRepository.findByAssignedUser_IdAndProject_Id(user.getId(), project.getId());
                projectTasks.put(project.getId(), tasks);
            }
            userProjectTasksMap.put(user.getId(), projectTasks);
        }

        model.addAttribute("users", users);
        model.addAttribute("userMeetingsMap", userMeetingsMap);
        model.addAttribute("userProjectsMap", userProjectsMap);
        model.addAttribute("userProjectTasksMap", userProjectTasksMap);
        model.addAttribute("meetingStartTimesMap", meetingStartTimesMap);

        return "employee/employee-overview";
    }
}
