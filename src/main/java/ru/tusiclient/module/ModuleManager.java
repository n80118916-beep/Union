package ru.tusiclient.module;

import ru.tusiclient.module.impl.AutoSprint;
import ru.tusiclient.module.impl.NeuroAura;
import ru.tusiclient.module.impl.RenderModules;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    public static void init() {
        modules.add(new NeuroAura());
        modules.add(new AutoSprint());
        modules.add(new RenderModules()); // ESP и HUD
    }

    public static void onTick() {
        for (Module m : modules) if (m.enabled) m.onTick();
    }

    public static Module getModule(String name) {
        for (Module m : modules) if (m.name.equalsIgnoreCase(name)) return m;
        return null;
    }
}
