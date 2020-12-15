package uk.org.lidalia.kotlinlangext.notifiers

open class Notifier<T> {

  private val listeners = mutableListOf<T>()
  fun addListener(listener: T) = listeners.add(listener)
  fun clear() = listeners.clear()

  protected fun notify(task: T.() -> Unit) = listeners.forEach { it.task() }
}
