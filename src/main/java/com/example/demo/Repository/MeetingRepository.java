package com.example.demo.Repository;

import com.example.demo.Entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // Lấy danh sách meeting mà user tham gia
    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id = :userId")
    List<Meeting> findAllByParticipantId(Long userId);

    // Cập nhật query để sử dụng đúng tên thuộc tính 'participants'
    List<Meeting> findByParticipants_Id(Long userId);
}
