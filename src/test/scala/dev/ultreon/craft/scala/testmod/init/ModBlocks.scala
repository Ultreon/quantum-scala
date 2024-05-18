package dev.ultreon.craft.scala.testmod.init

import com.ultreon.craft.block.Block
import com.ultreon.craft.item.ItemStack
import com.ultreon.craft.registry.{DeferRegistry, DeferredElement, Registries}
import dev.ultreon.craft.scala.registry.ObjectInit
import dev.ultreon.craft.scala.testmod.Constants

import java.util.function.Supplier
import scala.language.postfixOps

object ModBlocks extends ObjectInit[Block](Constants.MOD_ID, Registries.BLOCK) {
  final val TEST_BLOCK: DeferredElement[Block] = register("test_block", { () =>
    new Block(new Block.Properties().dropsItems(new ItemStack(ModItems.TEST_ITEM.get())))
  })
}
