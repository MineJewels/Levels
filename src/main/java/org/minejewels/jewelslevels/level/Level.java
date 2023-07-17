package org.minejewels.jewelslevels.level;

import lombok.Data;

import java.util.List;

@Data
public class Level {

    private final int level;
    private final String reward;
    private final List<String> commands;

}
