package ru.tusiclient.module;

import ru.tusiclient.module.impl.AutoSprint;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    public static void init() {
        modules.add(new AutoSprint());
        // Сюда потом будем добавлять другие читы
        modules.add(new Module("TestModule", "Combat"));
    }

    public static void onTick() {
        for (Module m : modules) {
            if (m.enabled) m.onTick();
        }
    }
}
