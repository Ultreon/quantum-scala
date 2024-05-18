package dev.ultreon.craft.scala.testmod

import net.fabricmc.loader.api.{FabricLoader, ModContainer}

object Constants {
  final val MOD_ID: String = "testmod"

  private val modContainer: ModContainer = FabricLoader.getInstance().getModContainer(MOD_ID).get()
  final val MOD_NAME: String = modContainer.getMetadata.getName
  final val MOD_VERSION: String = modContainer.getMetadata.getVersion.getFriendlyString
  final val MOD_DESCRIPTION: String = modContainer.getMetadata.getDescription
}
