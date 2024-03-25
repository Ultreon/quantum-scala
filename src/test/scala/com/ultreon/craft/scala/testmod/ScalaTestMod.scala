package com.ultreon.craft.scala.testmod

import com.ultreon.craft.scala.testmod.init.{ModBlocks, ModItems}
import net.fabricmc.api.ModInitializer

class ScalaTestMod extends ModInitializer {
  override def onInitialize(): Unit = {
    ModBlocks.register()
    ModItems.register()
  }
}
