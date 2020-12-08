package io.flutter.plugins;

import io.flutter.plugin.common.PluginRegistry;
import io.github.ponnamkarthik.flutterrtmppublisher.FlutterRtmpPublisherPlugin;

/**
 * Generated file. Do not edit.
 */
public final class GeneratedPluginRegistrant {
  public static void registerWith(PluginRegistry registry) {
    if (alreadyRegisteredWith(registry)) {
      return;
    }
    FlutterRtmpPublisherPlugin.registerWith(registry.registrarFor("io.github.ponnamkarthik.flutterrtmppublisher.FlutterRtmpPublisherPlugin"));
  }

  private static boolean alreadyRegisteredWith(PluginRegistry registry) {
    final String key = GeneratedPluginRegistrant.class.getCanonicalName();
    if (registry.hasPlugin(key)) {
      return true;
    }
    registry.registrarFor(key);
    return false;
  }
}
