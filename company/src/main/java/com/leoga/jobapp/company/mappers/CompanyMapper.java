package com.leoga.jobapp.company.mappers;

import com.leoga.jobapp.company.configuration.MapperConfigGlobal;
import com.leoga.jobapp.company.dto.CompanyRequest;
import com.leoga.jobapp.company.dto.CompanyResponse;
import com.leoga.jobapp.company.model.Company;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapperConfigGlobal.class)
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Company toEntity(CompanyRequest companyRequest);

    CompanyResponse toCompanyResponse(Company company);

    List<CompanyResponse> toCompanyResponseList(List<Company> companies);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchCompany(CompanyRequest jobRequest, @MappingTarget Company company);
}
