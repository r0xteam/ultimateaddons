package dev.c0rex64.ultimateaddons.mechanics.weather;

import org.bukkit.event.Listener;

public class WeatherListener implements Listener {
    private final WeatherMechanic weatherMechanic;

    public WeatherListener(WeatherMechanic weatherMechanic) {
        this.weatherMechanic = weatherMechanic;
    }
} 