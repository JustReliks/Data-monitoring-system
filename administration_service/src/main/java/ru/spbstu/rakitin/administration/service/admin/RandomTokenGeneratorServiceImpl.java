package ru.spbstu.rakitin.administration.service.admin;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class RandomTokenGeneratorServiceImpl implements RandomTokenGeneratorService {
    @Override
    public String generateRandomAdminToken() {
        return RandomStringUtils.secure().nextAlphabetic(10, 20);
    }
}
