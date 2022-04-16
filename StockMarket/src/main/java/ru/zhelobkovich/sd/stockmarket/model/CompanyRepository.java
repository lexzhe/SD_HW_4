package ru.zhelobkovich.sd.stockmarket.model;

import java.util.Optional;

public interface CompanyRepository {
    boolean addCompany(CompanyObject newCompany);

    Optional<CompanyObject> getCompany(String name);
}
