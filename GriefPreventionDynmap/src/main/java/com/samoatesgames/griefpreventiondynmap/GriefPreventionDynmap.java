package com.samoatesgames.griefpreventiondynmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Messages;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * The main plugin class
 *
 * @author Sam Oates <sam@samoatesgames.com>
 */
public final class GriefPreventionDynmap extends JavaPlugin {

    /**
     * The dynmap marker api
     */
    private MarkerAPI m_dynmapMarkerAPI = null;

    /**
     * The grief prevention plugin
     */
    private GriefPrevention m_griefPreventionPlugin = null;

    /**
     * The marker set used for the grief prevention layer
     */
    private MarkerSet m_griefPreventionMarkerSet = null;

    /**
     * All claims
     */
    private Map<String, AreaMarker> m_claims = new HashMap<String, AreaMarker>();

    /**
     * The ID of the scheduler update task
     */
    private int m_updateTaskID = -1;

    /**
     * The configuration of the plugin
     */
    private Config m_config;

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        super.onEnable();

        m_config = Config.loadFromFolder(getDataFolder());

        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin dynmapPlugin = pluginManager.getPlugin("dynmap");

        // Dynmap isn't installed, disable this plugin
        if (dynmapPlugin == null) {
            getLogger().warning("The dynmap plugin was not found on this server...");
            pluginManager.disablePlugin(this);
            return;
        }

        DynmapAPI m_dynmapAPI = (DynmapAPI) dynmapPlugin;
        m_dynmapMarkerAPI = m_dynmapAPI.getMarkerAPI();

        Plugin griefPreventionPlugin = pluginManager.getPlugin("GriefPrevention");

        // GriefPrevention isn't installed, disable this plugin
        if (griefPreventionPlugin == null) {
            getLogger().warning("The grief prevention plugin was not found on this server...");
            pluginManager.disablePlugin(this);
            return;
        }

        m_griefPreventionPlugin = (GriefPrevention) griefPreventionPlugin;

        // If either dynmap or grief prevention are disabled, disable this plugin
        if (!(dynmapPlugin.isEnabled() && griefPreventionPlugin.isEnabled())) {
            getLogger().warning("Either dynmap or grief prevention is disabled...");
            pluginManager.disablePlugin(this);
            return;
        }

        if (!setupMarkerSet()) {
            getLogger().warning("Failed to setup a marker set...");
            pluginManager.disablePlugin(this);
            return;
        }

        BukkitScheduler scheduler = getServer().getScheduler();
        m_updateTaskID = scheduler.scheduleSyncRepeatingTask(
                this,
                new Runnable() {
                    @Override
                    public void run() {
                        updateClaims();
                    }
                },
                20L,
                20L * m_config.marker.refreshRateInSeconds
        );

        getLogger().info("Succesfully enabled.");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        if (m_updateTaskID != -1) {
            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.cancelTask(m_updateTaskID);
            m_updateTaskID = -1;
        }

        for (AreaMarker marker : m_claims.values()) {
            marker.deleteMarker();
        }
        m_claims.clear();

        m_griefPreventionMarkerSet.deleteMarkerSet();
    }

    /**
     * Setup the marker set in Dynmap
     */
    private boolean setupMarkerSet() {
        m_griefPreventionMarkerSet = m_dynmapMarkerAPI.getMarkerSet("griefprevention.markerset");

        if (m_griefPreventionMarkerSet == null) {
            m_griefPreventionMarkerSet = m_dynmapMarkerAPI.createMarkerSet("griefprevention.markerset", m_config.layer.name, null, false);
        } else {
            m_griefPreventionMarkerSet.setMarkerSetLabel(m_config.layer.name);
        }

        if (m_griefPreventionMarkerSet == null) {
            getLogger().warning("Failed to create a marker set with the name 'griefprevention.markerset'.");
            return false;
        }

        m_griefPreventionMarkerSet.setLayerPriority(m_config.layer.priority);
        m_griefPreventionMarkerSet.setHideByDefault(m_config.layer.hiddenByDefault);

        return true;
    }

    /**
     * Update all claims
     */
    private void updateClaims() {
        Map<String, AreaMarker> newClaims = new HashMap<String, AreaMarker>();

        Collection<Claim> claims = m_griefPreventionPlugin.dataStore.getClaims();

        // We have found claims! Create markers for them all
        if (claims != null) {
            for (Claim claim : claims) {
                createClaimMarker(claim, newClaims);
                if (claim.children != null && m_config.marker.claim.showChildren) {
                    for (Claim children : claim.children) {
                        createClaimMarker(children, newClaims);
                    }
                }
            }
        }

        // Remove any markers for claims which no longer exist
        for (AreaMarker oldm : m_claims.values()) {
            oldm.deleteMarker();
        }

        // And replace with new map
        m_claims.clear();
        m_claims = newClaims;
    }

    /**
     * Create a new claim marker
     *
     * @param claim     The claim to create a marker for
     * @param claimsMap The map of new claims
     */
    private void createClaimMarker(Claim claim, Map<String, AreaMarker> claimsMap) {

        Location lowerBounds = claim.getLesserBoundaryCorner();
        Location higherBounds = claim.getGreaterBoundaryCorner();
        if (lowerBounds == null || higherBounds == null) {
            return;
        }

        String worldname = lowerBounds.getWorld().getName();
        String owner = claim.getOwnerName();

        // Make outline
        double[] x = new double[4];
        double[] z = new double[4];
        x[0] = lowerBounds.getX();
        z[0] = lowerBounds.getZ();
        x[1] = lowerBounds.getX();
        z[1] = higherBounds.getZ() + 1.0;
        x[2] = higherBounds.getX() + 1.0;
        z[2] = higherBounds.getZ() + 1.0;
        x[3] = higherBounds.getX() + 1.0;
        z[3] = lowerBounds.getZ();

        final String markerid = "Claim_" + claim.getID();
        AreaMarker marker = m_claims.remove(markerid);
        if (marker == null) {
            marker = m_griefPreventionMarkerSet.createAreaMarker(markerid, owner, false, worldname, x, z, false);
            if (marker == null) {
                return;
            }
        } else {
            marker.setCornerLocations(x, z);
            marker.setLabel(owner);
        }

        // Set line and fill properties
        setMarkerStyle(marker, claim.isAdminClaim());

        // Build popup
        String desc = formatInfoWindow(claim);
        marker.setDescription(desc);

        // Add to map
        claimsMap.put(markerid, marker);
    }

    /**
     * Setup the markers styling
     */
    private void setMarkerStyle(AreaMarker marker, boolean isAdmin) {

        // Get the style settings
        int lineColor = 0xFF0000;
        int fillColor = 0xFF0000;

        try {
            lineColor = Integer.parseInt(isAdmin ? m_config.marker.style.border.color : m_config.marker.admin.style.border.color, 16);
            fillColor = Integer.parseInt(isAdmin ? m_config.marker.style.fill.color : m_config.marker.admin.style.fill.color, 16);
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Invalid syle color specified. Defaulting to red.", ex);
        }

        int lineWeight = isAdmin ? m_config.marker.style.border.weight : m_config.marker.admin.style.border.weight;
        double lineOpacity = isAdmin ? m_config.marker.style.border.opacity : m_config.marker.admin.style.border.opacity;
        double fillOpacity = isAdmin ? m_config.marker.style.fill.opacity : m_config.marker.admin.style.fill.opacity;

        // Set the style of the marker
        marker.setLineStyle(lineWeight, lineOpacity, lineColor);
        marker.setFillStyle(fillOpacity, fillColor);
    }

    /**
     * Setup the markers format window
     *
     * @param claim The claim to setup the window for
     * @return Html representation of the information window
     */
    private String formatInfoWindow(Claim claim) {
        final boolean isAdmin = claim.isAdminClaim();
        String owner = claim.getOwnerName();
        if (owner.equals(GriefPrevention.instance.dataStore.getMessage(Messages.OwnerNameForAdminClaims))) {
            owner = m_config.marker.admin.nameOverride;
        }

        String areaText = "";
        if (m_config.marker.claim.showArea) {
            areaText += "Claim area: " + claim.getArea();
            if (m_config.marker.claim.showDimensions) {
                areaText += "<br/>";
            }
        }
        if (m_config.marker.claim.showDimensions) {
            areaText += "Claim dimensions: " + claim.getWidth() + " x " + claim.getHeight();
        }

        return "<div class=\"regioninfo\">" +
                "<center>" +
                "<div class=\"infowindow\">" +
                "<span style=\"font-weight:bold;\">" + owner + "'s claim</span><br/>" +
                (isAdmin ? "" : "<img src='https://minotar.net/helm/" + owner + "/20' /><br/>") +
                areaText +
                getClaimPermissionsText(claim) +
                "</div>" +
                "</center>" +
                "</div>";
    }

    private String getClaimPermissionsText(Claim claim) {
        String claimPermissions = "";
        ArrayList<String> builders = new ArrayList<String>();
        ArrayList<String> containers = new ArrayList<String>();
        ArrayList<String> accessors = new ArrayList<String>();
        ArrayList<String> managers = new ArrayList<String>();
        claim.getPermissions(builders, containers, accessors, managers);

        if (m_config.marker.claim.showBuilders) {
            claimPermissions += getPermissionsFromList(builders, "Builders");
        }
        if (m_config.marker.claim.showContainers) {
            claimPermissions += getPermissionsFromList(containers, "Containers");
        }
        if (m_config.marker.claim.showAccessors) {
            claimPermissions += getPermissionsFromList(accessors, "Accessors");
        }
        if (m_config.marker.claim.showManagers) {
            claimPermissions += getPermissionsFromList(managers, "Managers");
        }
        return claimPermissions;
    }

    private String getPermissionsFromList(ArrayList<String> list, String listName) {
        String permissionText = "";
        if (!list.isEmpty()) {
            permissionText += "<br/>" + listName + ": ";
            for (String item : list) {
                permissionText += item + ", ";
            }
            permissionText = permissionText.substring(0, permissionText.lastIndexOf(", "));
        }
        return permissionText;
    }
}
