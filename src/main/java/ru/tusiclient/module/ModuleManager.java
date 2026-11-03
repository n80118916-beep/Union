package ru.tusiclient.module;

import ru.tusiclient.module.impl.AutoSprint;
import ru.tusiclient.module.impl.ESPModule;
import ru.tusiclient.module.impl.HUDModule;
import ru.tusiclient.module.impl.NeuroAura;
import ru.tusiclient.module.impl.SpookyAura;
import ru.tusiclient.module.impl.WingsModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    public static void init() {
        // Combat
        modules.add(new NeuroAura());
        modules.add(new SpookyAura()); // Наша новая аура для обходов
        
        // Movement
        modules.add(new AutoSprint());
        
        // Render
        modules.add(new ESPModule());
        modules.add(new HUDModule());
        modules.add(new WingsModule());
    }

    public static void onTick() {
        for (Module m : modules) {
            if (m.enabled) {
                m.onTick();
            }
        }
    }

    public static Module getModule(String name) {
        for (Module m : modules) {
            if (m.name.equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
}
