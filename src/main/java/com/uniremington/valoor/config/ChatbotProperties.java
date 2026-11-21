package com.uniremington.valoor.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.uniremington.valoor.chatbot.dto.ChatbotType;

@ConfigurationProperties(prefix = "chatbot")
public class ChatbotProperties {

    private Map<ChatbotType, BotSettings> bots = new EnumMap<>(ChatbotType.class);

    public Map<ChatbotType, BotSettings> getBots() {
        return bots;
    }

    public void setBots(Map<ChatbotType, BotSettings> bots) {
        this.bots = bots;
    }

    public static class BotSettings {

        private String promptResource;
        private List<String> enabledTools = new ArrayList<>();
        private double temperature = 0.4;

        public String getPromptResource() {
            return promptResource;
        }

        public void setPromptResource(String promptResource) {
            this.promptResource = promptResource;
        }

        public List<String> getEnabledTools() {
            return enabledTools;
        }

        public void setEnabledTools(List<String> enabledTools) {
            this.enabledTools = enabledTools;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
    }
}
