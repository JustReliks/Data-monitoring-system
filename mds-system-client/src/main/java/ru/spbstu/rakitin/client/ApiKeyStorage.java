package ru.spbstu.rakitin.client;

import lombok.Getter;

public class ApiKeyStorage {

    @Getter
    private String apiKey;
    private final ApiKeyStorageProperties apiKeyStorageProperties;
    private long lastConsumed;

    public ApiKeyStorage(ApiKeyStorageProperties apiKeyStorageProperties) {
        this.apiKeyStorageProperties = apiKeyStorageProperties;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.lastConsumed = System.currentTimeMillis();
    }

    public boolean isValid() {
        return apiKey != null &&
                apiKeyStorageProperties.getApiKeyAliveTimeMs() > System.currentTimeMillis() - lastConsumed;
    }

}
