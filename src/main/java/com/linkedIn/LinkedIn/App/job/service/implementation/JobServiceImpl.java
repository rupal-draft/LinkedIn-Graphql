package com.linkedIn.LinkedIn.App.job.service.implementation;

import com.linkedIn.LinkedIn.App.job.dto.JobApplicationDto;
import com.linkedIn.LinkedIn.App.job.dto.JobDto;
import com.linkedIn.LinkedIn.App.job.dto.records.JobInput;
import com.linkedIn.LinkedIn.App.job.service.JobService;
import com.linkedIn.LinkedIn.App.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {
    @Override
    public void createJob(JobInput jobInput) {

    }

    @Override
    public void updateJob(JobInput jobInput) {

    }

    @Override
    public void deleteJob(Long jobId) {

    }

    @Override
    public List<JobDto> getAllJobs() {
        return List.of();
    }

    @Override
    public List<JobDto> getJobsByStatus(String status) {
        return List.of();
    }

    @Override
    public List<JobDto> getJobsByTitle(String title) {
        return List.of();
    }

    @Override
    public List<JobDto> getJobsByLocation(String location) {
        return List.of();
    }

    @Override
    public List<JobDto> getJobsByTitleAndLocation(String title, String location) {
        return List.of();
    }

    @Override
    public List<JobDto> getJobsByTitleAndStatus(String title, String status) {
        return List.of();
    }

    @Override
    public List<JobDto> getJobsByLocationAndStatus(String location, String status) {
        return List.of();
    }

    @Override
    public List<JobDto> getJobsByTitleAndLocationAndStatus(String title, String location, String status) {
        return List.of();
    }

    @Override
    public JobDto getJobById(Long jobId) {
        return null;
    }

    @Override
    public List<JobDto> getJobsByPostedBy(User postedBy) {
        return List.of();
    }

    @Override
    public void applyForJob(Long jobId) {

    }

    @Override
    public void withdrawApplication(Long jobId) {

    }

    @Override
    public void saveJob(Long jobId) {

    }

    @Override
    public void unsaveJob(Long jobId) {

    }

    @Override
    public List<JobApplicationDto> getMyApplications() {
        return List.of();
    }

    @Override
    public List<JobDto> getMySavedJobs() {
        return List.of();
    }

    @Override
    public List<JobDto> recommendJobs() {
        return List.of();
    }
}
