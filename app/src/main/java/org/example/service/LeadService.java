package org.example.service;

import org.example.entity.*;
import org.example.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private UserService userService;
    
    // Create lead
    public Lead createLead(CrimeCase crimeCase, User submittedBy, String content, 
                          String title, LeadType type, LeadVisibility visibility) {
        // Validate user can submit leads
        if (submittedBy.getRole() != UserRole.SOLVER && submittedBy.getRole() != UserRole.ORGANIZATION) {
            throw new IllegalArgumentException("Only solvers and organizations can submit leads");
        }
        
        // Create lead
        Lead lead = new Lead();
        lead.setCrimeCase(crimeCase);
        lead.setSubmittedBy(submittedBy);
        lead.setContent(content);
        lead.setTitle(title);
        lead.setType(type);
        lead.setVisibility(visibility);
        lead.setStatus(LeadStatus.PENDING);
        lead.setSubmittedAt(LocalDateTime.now());
        
        return leadRepository.save(lead);
    }
    
    // Update lead
    public Lead updateLead(Lead lead) {
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    // Validate lead
    public Lead validateLead(Long leadId, User validator, boolean isValid) {
        Optional<Lead> leadOpt = leadRepository.findById(leadId);
        if (leadOpt.isPresent()) {
            Lead lead = leadOpt.get();
            
            if (validator.getRole() != UserRole.ORGANIZATION) {
                throw new IllegalArgumentException("Only organizations can validate leads");
            }
            
            lead.setValidatedBy(validator);
            lead.setValidatedAt(LocalDateTime.now());
            lead.setStatus(isValid ? LeadStatus.VALIDATED : LeadStatus.REJECTED);
            lead.setUpdatedAt(LocalDateTime.now());
            
            return leadRepository.save(lead);
        }
        throw new IllegalArgumentException("Lead not found");
    }
    
    // Update lead status
    public Lead updateLeadStatus(Long leadId, LeadStatus status) {
        Optional<Lead> leadOpt = leadRepository.findById(leadId);
        if (leadOpt.isPresent()) {
            Lead lead = leadOpt.get();
            
            lead.setStatus(status);
            lead.setUpdatedAt(LocalDateTime.now());
            
            return leadRepository.save(lead);
        }
        throw new IllegalArgumentException("Lead not found");
    }
    
    // Add tag to lead
    public Lead addTag(Long leadId, String tag) {
        Optional<Lead> leadOpt = leadRepository.findById(leadId);
        if (leadOpt.isPresent()) {
            Lead lead = leadOpt.get();
            
            List<String> tags = lead.getTags();
            if (!tags.contains(tag)) {
                tags.add(tag);
                lead.setTags(tags);
                lead.setUpdatedAt(LocalDateTime.now());
                return leadRepository.save(lead);
            }
            return lead;
        }
        throw new IllegalArgumentException("Lead not found");
    }
    
    // Find operations
    public Optional<Lead> findById(Long id) {
        return leadRepository.findById(id);
    }
    
    public List<Lead> findByCrimeCase(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCase(crimeCase);
    }
    
    public List<Lead> findBySubmittedBy(User submittedBy) {
        return leadRepository.findBySubmittedBy(submittedBy);
    }
    
    public List<Lead> findByValidatedBy(User validatedBy) {
        return leadRepository.findByValidatedBy(validatedBy);
    }
    
    public List<Lead> findByStatus(LeadStatus status) {
        return leadRepository.findByStatus(status);
    }
    
    public List<Lead> findByType(LeadType type) {
        return leadRepository.findByType(type);
    }
    
    public List<Lead> findByVisibility(LeadVisibility visibility) {
        return leadRepository.findByVisibility(visibility);
    }
    
    // Combined queries
    public List<Lead> findByCrimeCaseAndStatus(CrimeCase crimeCase, LeadStatus status) {
        return leadRepository.findByCrimeCaseAndStatus(crimeCase, status);
    }
    
    public List<Lead> findByCrimeCaseAndType(CrimeCase crimeCase, LeadType type) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, type);
    }
    
    public List<Lead> findBySubmittedByAndStatus(User submittedBy, LeadStatus status) {
        return leadRepository.findBySubmittedByAndStatus(submittedBy, status);
    }
    
    // Get leads for a case
    public List<Lead> getPendingLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndStatus(crimeCase, LeadStatus.PENDING);
    }
    
    public List<Lead> getValidatedLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndStatus(crimeCase, LeadStatus.VALIDATED);
    }
    
    public List<Lead> getRejectedLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndStatus(crimeCase, LeadStatus.REJECTED);
    }
    
    public List<Lead> getInvestigatingLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndStatus(crimeCase, LeadStatus.INVESTIGATING);
    }
    
    // Get leads by type
    public List<Lead> getTextLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, LeadType.TEXT);
    }
    
    public List<Lead> getDocumentLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, LeadType.DOCUMENT);
    }
    
    public List<Lead> getImageLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, LeadType.IMAGE);
    }
    
    public List<Lead> getVideoLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, LeadType.VIDEO);
    }
    
    public List<Lead> getAudioLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, LeadType.AUDIO);
    }
    
    public List<Lead> getLocationLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, LeadType.LOCATION);
    }
    
    public List<Lead> getWitnessLeads(CrimeCase crimeCase) {
        return leadRepository.findByCrimeCaseAndType(crimeCase, LeadType.WITNESS);
    }
    
    // Get user's leads
    public List<Lead> getUserSubmittedLeads(User user) {
        return leadRepository.findBySubmittedBy(user);
    }
    
    public List<Lead> getUserValidatedLeads(User user) {
        return leadRepository.findByValidatedBy(user);
    }
    
    public List<Lead> getUserPendingLeads(User user) {
        return leadRepository.findBySubmittedByAndStatus(user, LeadStatus.PENDING);
    }
    
    // Date-based queries
    public List<Lead> findBySubmittedDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return leadRepository.findBySubmittedAtBetween(startDate, endDate);
    }
    
    public List<Lead> findByValidatedDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return leadRepository.findByValidatedAtBetween(startDate, endDate);
    }
    
    // Recent leads
    public List<Lead> getRecentLeads() {
        return leadRepository.findRecentLeads();
    }
    
    public List<Lead> getPublicLeadsForCase(CrimeCase crimeCase) {
        return leadRepository.findPublicLeadsForCase(crimeCase);
    }
    
    public List<Lead> getPrivateLeadsForUser(User user) {
        return leadRepository.findPrivateLeadsForUser(user);
    }
    
    // Statistics
    public long getLeadCount(CrimeCase crimeCase) {
        return leadRepository.countByCrimeCase(crimeCase);
    }
    
    public long getLeadCountByStatus(CrimeCase crimeCase, LeadStatus status) {
        return leadRepository.countByStatus(status);
    }
    
    public long getLeadCountByType(CrimeCase crimeCase, LeadType type) {
        return leadRepository.countByType(type);
    }
    
    public long getValidatedLeadCount(CrimeCase crimeCase) {
        return leadRepository.countByStatus(LeadStatus.VALIDATED);
    }
    
    public long getPendingLeadCount(CrimeCase crimeCase) {
        return leadRepository.countByStatus(LeadStatus.PENDING);
    }
    
    public List<Object[]> getLeadStatisticsByStatus(CrimeCase crimeCase) {
        return leadRepository.countLeadsByStatusForCase(crimeCase);
    }
    
    public List<Object[]> getLeadStatisticsByType(CrimeCase crimeCase) {
        return leadRepository.countLeadsByTypeForCase(crimeCase);
    }
    
    // Search leads
    public List<Lead> searchLeadsByContent(String searchTerm) {
        return leadRepository.searchByContent(searchTerm);
    }
    
    // Get leads with comments
    public List<Lead> getLeadsWithComments() {
        return leadRepository.findLeadsWithComments();
    }
    
    // Get leads with reactions
    public List<Lead> getLeadsWithReactions() {
        return leadRepository.findLeadsWithReactions();
    }
    
    // Get leads by tag
    public List<Lead> getLeadsByTag(String tag) {
        return leadRepository.findByTag(tag);
    }
    
    // Get leads with files
    public List<Lead> getLeadsWithFiles() {
        return leadRepository.findLeadsWithFiles();
    }
    
    // Get leads by file type
    public List<Lead> getLeadsByFileType(String fileType) {
        return leadRepository.findByFileType(fileType);
    }
    
    // Lead progress tracking
    public LeadProgress getLeadProgress(CrimeCase crimeCase) {
        long totalLeads = getLeadCount(crimeCase);
        long pendingLeads = getLeadCountByStatus(crimeCase, LeadStatus.PENDING);
        long validatedLeads = getLeadCountByStatus(crimeCase, LeadStatus.VALIDATED);
        long rejectedLeads = getLeadCountByStatus(crimeCase, LeadStatus.REJECTED);
        long investigatingLeads = getLeadCountByStatus(crimeCase, LeadStatus.INVESTIGATING);
        
        double validationRate = totalLeads > 0 ? (double) validatedLeads / totalLeads * 100 : 0;
        double rejectionRate = totalLeads > 0 ? (double) rejectedLeads / totalLeads * 100 : 0;
        
        return new LeadProgress(totalLeads, pendingLeads, validatedLeads, rejectedLeads, 
                              investigatingLeads, validationRate, rejectionRate);
    }
    
    // Inner class for lead progress
    public static class LeadProgress {
        private final long totalLeads;
        private final long pendingLeads;
        private final long validatedLeads;
        private final long rejectedLeads;
        private final long investigatingLeads;
        private final double validationRate;
        private final double rejectionRate;
        
        public LeadProgress(long totalLeads, long pendingLeads, long validatedLeads, 
                          long rejectedLeads, long investigatingLeads,
                          double validationRate, double rejectionRate) {
            this.totalLeads = totalLeads;
            this.pendingLeads = pendingLeads;
            this.validatedLeads = validatedLeads;
            this.rejectedLeads = rejectedLeads;
            this.investigatingLeads = investigatingLeads;
            this.validationRate = validationRate;
            this.rejectionRate = rejectionRate;
        }
        
        // Getters
        public long getTotalLeads() { return totalLeads; }
        public long getPendingLeads() { return pendingLeads; }
        public long getValidatedLeads() { return validatedLeads; }
        public long getRejectedLeads() { return rejectedLeads; }
        public long getInvestigatingLeads() { return investigatingLeads; }
        public double getValidationRate() { return validationRate; }
        public double getRejectionRate() { return rejectionRate; }
    }
} 