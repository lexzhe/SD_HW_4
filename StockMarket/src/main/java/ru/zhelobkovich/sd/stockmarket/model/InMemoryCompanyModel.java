package ru.zhelobkovich.sd.stockmarket.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryCompanyModel implements CompanyRepository {
    private final List<CompanyObject> companies = new ArrayList<>();

    @Override
    public Optional<CompanyObject> getCompany(final String name) {
        return companies.stream().filter(c -> c.getName().equals(name)).findFirst();
    }

    @Override
    public boolean addCompany(final CompanyObject company) {
        getCompany(company.getName())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Company " + company.getName() + " is already registered.");
                });
        return this.companies.add(company);
    }
}
