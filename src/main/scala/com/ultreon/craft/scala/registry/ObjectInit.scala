package com.ultreon.craft.scala.registry

import com.ultreon.craft.registry.{DeferRegistry, DeferredElement, Registry}

abstract class ObjectInit[T](namespace: String, registry: Registry[T]) {
  private val deferRegister = DeferRegistry.of[T](namespace, registry)

  protected final def register[C <: T](name: String, getter: () => C): DeferredElement[C] = {
    deferRegister.defer(name, () => getter())
  }

  def register(): Unit = {
    deferRegister.register()
  }
}
