package com.samoatesgames.griefpreventiondynmap;

import org.cubeengine.reflect.Reflector;
import org.cubeengine.reflect.Section;
import org.cubeengine.reflect.annotations.Comment;
import org.cubeengine.reflect.codec.yaml.ReflectedYaml;

import java.io.File;

@SuppressWarnings("CanBeFinal")
public class Config extends ReflectedYaml {

    LayerSection layer = new LayerSection();
    MarkerSection marker = new MarkerSection();

    public static class LayerSection implements Section {
        @Comment("The name of the claims layer shown on dynmap")
        String name = "Claims";
        @Comment("The render priority of the claims layer shown on dynmap")
        int priority = 10;
        @Comment("Should the claims layer be hidden by default on the dynmap")
        boolean hiddenByDefault = false;
    }

    public static class MarkerSection implements Section {
        @Comment("How many seconds should we wait between each time we refresh the dynmap layer")
        int refreshRateInSeconds = 30;

        MarkerClaimSection claim = new MarkerClaimSection();
        MarkerStyleSection style = new MarkerStyleSection();
        MarkerAdminSection admin = new MarkerAdminSection();
    }

    public static class MarkerClaimSection implements Section {
        @Comment("Should child claims be shown on the dynmap")
        boolean showChildren = false;
        @Comment("Show claim blocks count?")
        boolean showArea = true;
        @Comment("Show claim dimensions?")
        boolean showDimensions = false;
        @Comment("Show users with full trust?")
        boolean showBuilders = true;
        @Comment("Show users with container trust?")
        boolean showContainers = false;
        @Comment("Show users with access trust?")
        boolean showAccessors = false;
        @Comment("Show users with permission trust?")
        boolean showManagers = false;
    }

    public static class MarkerStyleSection implements Section {
        MarkerStyleBorderSection border = new MarkerStyleBorderSection();
        MarkerStyleFillSection fill = new MarkerStyleFillSection();
    }

    public static class MarkerAdminSection implements Section {
        @Comment("The name to use for administrator claims (default 'an administrator'")
        String nameOverride = "an administrator";
        MarkerStyleSection style = new MarkerStyleSection();
    }

    public static class MarkerStyleBorderSection implements Section {
        @Comment("The color of the border of the marker (in hex)")
        String color = "FF0000";
        @Comment("The thickness of the border of the marker")
        int weight = 2;
        @Comment("The alpha transparency level of the border for the marker")
        double opacity = 0.8;
    }

    public static class MarkerStyleFillSection implements Section {
        @Comment("The fill color of the marker (in hex)")
        String color = "FF0000";
        @Comment("The alpha transparency level of the fill for the marker")
        double opacity = 0.35;

    }

    static Config loadFromFolder(File folder) {
        if (folder.exists() && !folder.isDirectory()) {
            throw new IllegalStateException("Config directory was not a directory!");
        } else if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("Could not create config directory!");
        }
        File file = new File(folder, "config.yml");

        Reflector reflector = new Reflector();
        return reflector.load(Config.class, file);
    }

}
