package dev.ultreon.craft.scala.testmod

import dev.ultreon.craft.scala.testmod.init.{ModBlocks, ModItems}
import net.fabricmc.api.ModInitializer

class ScalaTestMod extends ModInitializer {
  override def onInitialize(): Unit = {
    ModBlocks.register()
    ModItems.register()
  }
}
