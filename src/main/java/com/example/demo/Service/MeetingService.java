package com.example.demo.Service;

import com.example.demo.Entity.Meeting;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MeetingRepository;
import com.example.demo.Repository.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MyAppUserRepository userRepository;

    public List<Meeting> getAllMeetingsForUser(Long userId) {
        return meetingRepository.findAllByParticipantId(userId);
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public Meeting createMeeting(Meeting meeting, Set<Long> participantIds) {
        Set<MyAppUser> participants = new HashSet<>(userRepository.findAllById(participantIds));
        meeting.setParticipants(participants);
        return meetingRepository.save(meeting);
    }

    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id).orElse(null);
    }

    // Các phương thức update, delete nếu cần
}

