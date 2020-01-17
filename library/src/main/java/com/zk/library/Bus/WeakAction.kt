package org.cs.tec.library.Bus

import org.cs.tec.library.binding.command.BindingAction
import org.cs.tec.library.binding.command.BindingConsumer
import java.lang.ref.WeakReference


class WeakAction {


    private var action: BindingAction? = null
    private var consumer: BindingConsumer<Any>? = null
    private val isLive: Boolean = false
    private val target: Any? = null
    private var reference: WeakReference<*>? = null

    constructor(target: Any, action: BindingAction) {
        reference = WeakReference(target)
        this.action = action

    }

    constructor(target: Any, consumer: BindingConsumer<Any>) {
        reference = WeakReference(target)
        this.consumer = consumer
    }

    fun execute() {
        if (action != null && isLive()) {
            action!!.call()
        }
    }

    fun  <T>execute(t: T) {
        if (consumer != null && isLive()) {
            consumer!!.call(t!!)
        }
    }


    fun markForDeletion() {
        reference!!.clear()
        reference = null
        action = null
        consumer = null
    }

    fun getBindingAction(): BindingAction? {
        return action
    }

    fun getBindingConsumer(): BindingConsumer<*>? {
        return consumer
    }

    fun isLive(): Boolean {
        if (reference == null) {
            return false
        }
        if (reference!!.get() == null) {
            return false
        }
        return true
    }


    fun getTarget(): Any? {
        return if (reference != null) {
            reference!!.get()
        } else null
    }
}
