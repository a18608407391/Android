package org.cs.tec.library.binding.command

/**
 * A one-argument action.
 *
 * @param <T> the first argument type
</T> */
interface BindingConsumer<T> {
    fun call(t: T)
}
