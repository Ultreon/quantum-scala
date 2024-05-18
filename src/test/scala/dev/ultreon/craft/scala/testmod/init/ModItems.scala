package dev.ultreon.craft.scala.testmod.init

import com.ultreon.craft.item.Item
import com.ultreon.craft.registry.{DeferRegistry, DeferredElement, Registries}
import dev.ultreon.craft.scala.registry.ObjectInit
import dev.ultreon.craft.scala.testmod.Constants

object ModItems extends ObjectInit[Item](Constants.MOD_ID, Registries.ITEM) {
  final val TEST_ITEM: DeferredElement[Item] = register("test_item", { () =>
    new Item(new Item.Properties)
  })
}
