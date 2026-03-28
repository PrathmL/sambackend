package com.esspbackend.service;

import com.esspbackend.entity.Communication;
import com.esspbackend.repository.CommunicationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunicationService {

    private final CommunicationRepository communicationRepository;

    public CommunicationService(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
    }

    public Communication sendMessage(Communication communication) {
        return communicationRepository.save(communication);
    }

    public List<Communication> getSentMessages(Long senderId) {
        return communicationRepository.findBySenderIdOrderByCreatedAtDesc(senderId);
    }

    public List<Communication> getSchoolMessages(Long schoolId, Long talukaId) {
        List<Communication> messages = new ArrayList<>();
        
        // 1. Messages sent specifically to this school
        if (schoolId != null && schoolId != 0) {
            messages.addAll(communicationRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId));
        }
        
        // 2. Bulk messages for this taluka
        if (talukaId != null && talukaId != 0) {
            messages.addAll(communicationRepository.findByTalukaIdAndIsBulkTrueOrderByCreatedAtDesc(talukaId));
        }
        
        // 3. District-wide bulk messages (talukaId is null and isBulk is true)
        messages.addAll(communicationRepository.findByTalukaIdIsNullAndIsBulkTrueOrderByCreatedAtDesc());
        
        // Use a LinkedHashSet to maintain order while removing duplicates
        return messages.stream()
            .collect(Collectors.toMap(
                Communication::getId,
                msg -> msg,
                (existing, replacement) -> existing
            ))
            .values()
            .stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public void deleteMessage(Long id) {
        communicationRepository.deleteById(id);
    }
}
