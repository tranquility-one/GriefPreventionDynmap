/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.griefpreventiondynmap;

/**
 *
 * @author Sam Oates <sam@samoatesgames.com>
 */
 class Setting {
    
    // Marker Settings
     static final String DynmapUpdateRate = "marker.refreshRateInSeconds";
     static final String ShowChildClaims = "marker.claim.showchildren";
    
    // Marker Style Settings
     static final String MarkerLineColor = "marker.style.border.color";
     static final String MarkerLineWeight = "marker.style.border.weight";
     static final String MarkerLineOpacity = "marker.style.border.opacity";
     static final String MarkerFillColor = "marker.style.fill.color";
     static final String MarkerFillOpacity = "marker.style.fill.opacity";
    
    // Admin Marker Style Settings
     static final String AdminMarkerLineColor = "marker.admin.style.border.color";
     static final String AdminMarkerLineWeight = "marker.admin.style.border.weight";
     static final String AdminMarkerLineOpacity = "marker.admin.style.border.opacity";
     static final String AdminMarkerFillColor = "marker.admin.style.fill.color";
     static final String AdminMarkerFillOpacity = "marker.admin.style.fill.opacity";
    
    // Layer Settings
     static final String ClaimsLayerName = "layer.name";
     static final String ClaimsLayerPriority = "layer.priority";
     static final String ClaimsLayerHiddenByDefault = "layer.hiddenByDefault";

    static int getSetting(String setting, int def) {
        return setting != null && !setting.isEmpty() ? Integer.parseInt(setting) : def;
    }

    static String getSetting(String setting, String def) {
        return setting != null && !setting.isEmpty() ? setting : def;
    }

    static boolean getSetting(String setting, boolean def) {
        return setting != null && !setting.isEmpty() ? Boolean.parseBoolean(setting) : def;
    }

    static double getSetting(String setting, double def) {
        return setting != null && !setting.isEmpty() ? Double.parseDouble(setting) : def;
    }
}
