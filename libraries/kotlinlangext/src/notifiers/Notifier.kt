package uk.org.lidalia.kotlinlangext.notifiers

import java.util.concurrent.CopyOnWriteArrayList

open class Notifier<T> {

  private val listeners = CopyOnWriteArrayList<T>()
  fun addListener(listener: T) = listeners.add(listener)
  fun clear() = listeners.clear()

  protected fun notify(task: T.() -> Unit) = listeners.forEach { it.task() }
}
