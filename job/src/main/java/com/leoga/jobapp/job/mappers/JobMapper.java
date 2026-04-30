package com.leoga.jobapp.job.mappers;

import com.leoga.jobapp.job.clients.CompanyServiceClient;
import com.leoga.jobapp.job.clients.ReviewServiceClient;
import com.leoga.jobapp.job.configuration.MapperConfigGlobal;
import com.leoga.jobapp.job.dto.CompanyResponse;
import com.leoga.jobapp.job.dto.JobRequest;
import com.leoga.jobapp.job.dto.JobResponse;
import com.leoga.jobapp.job.dto.ReviewResponse;
import com.leoga.jobapp.job.model.Job;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapperConfigGlobal.class)
public interface JobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Job toEntity(JobRequest jobRequest);

    JobResponse toJobResponse(Job job,
                              @Context CompanyServiceClient companyClient,
                              @Context ReviewServiceClient reviewClient);

    List<JobResponse> toJobResponseList(List<Job> jobs,
                                        @Context CompanyServiceClient companyClient,
                                        @Context ReviewServiceClient reviewClient);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchJob(JobRequest jobRequest, @MappingTarget Job job);

    @AfterMapping
    default void enrichWithRemoteData(Job job,
                                      @MappingTarget JobResponse jobResponse,
                                      @Context CompanyServiceClient companyClient,
                                      @Context ReviewServiceClient reviewClient) {

        if (null != job.getCompanyId()) {
            CompanyResponse companyResponse = companyClient.getCompanyById(job.getCompanyId());
            List<ReviewResponse> reviews = reviewClient.getReviews(companyResponse.getId());
            companyResponse.setReviews(reviews);
            jobResponse.setCompany(companyResponse);
        }
    }
}
