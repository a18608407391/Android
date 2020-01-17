package com.zk.library.Bus

import org.cs.tec.library.Bus.WeakAction
import org.cs.tec.library.binding.command.BindingAction
import org.cs.tec.library.binding.command.BindingConsumer
import java.lang.reflect.Type
class Messenger {

    companion object {
        var defaultInstance: Messenger? = null
            get() {
                if (field == null) {
                    field = Messenger()
                }
                return field
            }

        @Synchronized
        fun get(): Messenger? {
            return defaultInstance
        }

        fun overrideDefault(newWeakMessenger: Messenger) {
            defaultInstance = newWeakMessenger
        }

        fun reset() {
            defaultInstance = null
        }

        class NotMsgType {

        }
    }

    private var recipientsOfSubclassesAction = HashMap<Type, ArrayList<WeakActionAndToken>>()
    private var recipientsStrictAction = HashMap<Type, ArrayList<WeakActionAndToken>>()


    fun register(recipient: Any, action: BindingAction) {
        register(recipient, null, false, action)
    }

    fun register(recipient: Any, token: Any, action: BindingAction) {
        register(recipient, token, false, action)
    }

    fun register(recipient: Any, receiveDerivedMessagesToo: Boolean, action: BindingAction) {
        register(recipient, null, receiveDerivedMessagesToo, action)
    }

    fun register(recipient: Any, nothing: Nothing?, receiveDerivedMessagesToo: Boolean, action: BindingAction) {

    }

    fun register(recipient: Any, token: Any, receiveDerivedMessagesToo: Boolean, action: BindingAction) {
        var messageType = NotMsgType::class.java
        var recipients: HashMap<Type, ArrayList<WeakActionAndToken>>

        if (receiveDerivedMessagesToo) {
            if (recipientsOfSubclassesAction == null) {
                recipientsOfSubclassesAction = HashMap()
            }
            recipients = recipientsOfSubclassesAction
        } else {
            if (recipientsStrictAction == null) {
                recipientsStrictAction = HashMap()
            }
            recipients = recipientsStrictAction
        }
        var list: ArrayList<WeakActionAndToken>

        if (!recipients.containsKey(messageType)) {
            list = ArrayList()
            recipients[messageType] = list
        } else {
            list = recipients[messageType]!!
        }
        var weakAction = WeakAction(recipient, action)
        var item = WeakActionAndToken(weakAction, token)
        list.add(item)
        cleanup()
    }


    fun register(recipient: Any, tClass: Class<*>, action: BindingConsumer<Any>) {
        register(recipient, null, false, action, tClass)
    }

    fun register(recipient: Any, receiveDerivedMessagesToo: Boolean, tClass: Class<*>, action: BindingConsumer<Any>) {
        register(recipient, null, receiveDerivedMessagesToo, action, tClass)
    }

    fun register(recipient: Any, token: Any, tClass: Class<*>, action: BindingConsumer<Any>) {
        register(recipient, token, false, action, tClass)
    }
    fun register(recipient: Any, token: Any?, receiveDerivedMessagesToo: Boolean, action: BindingConsumer<Any>, tClass: Class<*>) {

        var recipients: HashMap<Type, ArrayList<WeakActionAndToken>>

        if (receiveDerivedMessagesToo) {
            if (recipientsOfSubclassesAction == null) {
                recipientsOfSubclassesAction = HashMap()
            }
            recipients = recipientsOfSubclassesAction
        } else {
            if (recipientsStrictAction == null) {
                recipientsStrictAction = HashMap()
            }

            recipients = recipientsStrictAction
        }

        val list: ArrayList<WeakActionAndToken>

        if (!recipients.containsKey(tClass)) {
            list = ArrayList()
            recipients[tClass] = list
        } else {
            list = recipients[tClass]!!
        }
        var weakAction = WeakAction(recipient, action)
        var item = WeakActionAndToken(weakAction, token!!)
        list.add(item)
        cleanup()
    }

    private fun cleanup() {
        cleanupList(recipientsOfSubclassesAction)
        cleanupList(recipientsStrictAction)
    }

    fun sendNoMsg(token: Any) {
        sendToTargetOrType(null, token)
    }

    fun sendNoMsgToTarget(target: Any) {
        sendToTargetOrType(target.javaClass, null)
    }

    fun sendNoMsgToTargetWithToken(token: Any, target: Any) {
        sendToTargetOrType(target.javaClass, token)

    }

    fun <T : Any> send(message: T) {
        sendToTargetOrType(message, null, null)
    }

    fun <T : Any> send(message: T, token: Any) {
        sendToTargetOrType(message, null, token)
    }

    fun <T : Any, R> sendToTarget(message: T, target: Any) {
        sendToTargetOrType(message, target.javaClass, null)
    }

    fun unregister(recipient: Any) {
        unregisterFromLists(recipient, recipientsOfSubclassesAction)
        unregisterFromLists(recipient, recipientsStrictAction)
        cleanup()
    }


    fun <T> unregister(recipient: Any, token: Any) {
        unregisterFromLists(recipient, token, null, recipientsStrictAction)
        unregisterFromLists(recipient, token, null, recipientsOfSubclassesAction)
        cleanup()
    }

    private fun sendToTargetOrType(nothing: Any?, token: Any?) {

    }

    private fun <T> sendToList(
            message: T,
            list: Collection<WeakActionAndToken>?,
            messageTargetType: Type?,
            token: Any?) {
        if (list != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0004.007
            var listClone = ArrayList<WeakActionAndToken>()
            listClone.addAll(list)


            for (item in listClone) {
                var executeAction = item.getAction()
                if (executeAction != null
                        && item.getAction()!!.isLive()
                        && item.getAction()!!.getTarget() != null
                        && (messageTargetType == null
                                || item.getAction()!!.getTarget()!!.javaClass === messageTargetType
                                || classImplements(item.getAction()!!.getTarget()!!.javaClass, messageTargetType))
                        && (item.getToken() == null && token == null || item.getToken() != null && item.getToken() == token)) {
                    executeAction!!.execute(message)
                }
            }
        }
    }

    private fun unregisterFromLists(recipient: Any?, lists: HashMap<Type, ArrayList<WeakActionAndToken>>?) {
        if (recipient == null
                || lists == null
                || lists.size === 0) {
            return
        }
        synchronized(lists) {
            for (messageType in lists.keys) {
                for (item in lists[messageType]!!) {
                    val weakAction = item.getAction()
                    if (weakAction != null && recipient === weakAction!!.getTarget()) {
                        weakAction!!.markForDeletion()
                    }
                }
            }
        }
        cleanupList(lists)
    }

    private fun <T> unregisterFromLists(
            recipient: Any?,
            action: BindingConsumer<T>?,
            lists: HashMap<Type, ArrayList<WeakActionAndToken>>?,
            tClass: Class<T>) {

        if (recipient == null
                || lists == null
                || lists.size === 0
                || !lists.containsKey(tClass)) {
            return
        }

        synchronized(lists) {
            for (item in lists[tClass]!!) {
                val weakActionCasted = item.getAction()
                if (weakActionCasted != null
                        && recipient === weakActionCasted!!.getTarget()
                        && (action == null || action === weakActionCasted!!.getBindingConsumer())) {
                    item.getAction()!!.markForDeletion()
                }
            }
        }
    }

    private fun unregisterFromLists(
            recipient: Any?,
            action: BindingAction?,
            lists: HashMap<Type, List<WeakActionAndToken>>?
    ) {
        val messageType = NotMsgType::class.java

        if (recipient == null
                || lists == null
                || lists.size === 0
                || !lists.containsKey(messageType)) {
            return
        }

        synchronized(lists) {
            for (item in lists[messageType]!!) {
                val weakActionCasted = item.getAction() as WeakAction

                if (weakActionCasted != null
                        && recipient === weakActionCasted.getTarget()
                        && (action == null || action === weakActionCasted.getBindingAction())) {
                    item.getAction()!!.markForDeletion()
                }
            }
        }
    }


    private fun <T> unregisterFromLists(
            recipient: Any?,
            token: Any?,
            action: BindingConsumer<T>?,
            lists: HashMap<Type, List<WeakActionAndToken>>?, tClass: Class<T>) {

        if (recipient == null
                || lists == null
                || lists.size === 0
                || !lists.containsKey(tClass)) {
            return
        }

        synchronized(lists) {
            for (item in lists[tClass]!!) {
                var weakActionCasted = item.getAction()

                if (weakActionCasted != null
                        && recipient === weakActionCasted!!.getTarget()
                        && (action == null || action === weakActionCasted!!.getBindingConsumer())
                        && (token == null || token == item.getToken())) {
                    item.getAction()!!.markForDeletion()
                }
            }
        }
    }

    private fun unregisterFromLists(
            recipient: Any?,
            token: Any?,
            action: BindingAction?,
            lists: HashMap<Type, ArrayList<WeakActionAndToken>>?) {
        val messageType = NotMsgType::class.java

        if (recipient == null
                || lists == null
                || lists.size === 0
                || !lists.containsKey(messageType)) {
            return
        }

        synchronized(lists) {
            for (item in lists[messageType]!!) {
                val weakActionCasted = item.getAction() as WeakAction

                if (weakActionCasted != null
                        && recipient === weakActionCasted.getTarget()
                        && (action == null || action === weakActionCasted.getBindingAction())
                        && (token == null || token == item.getToken())) {
                    item.getAction()!!.markForDeletion()
                }
            }
        }
    }

    private fun classImplements(instanceType: Type?, interfaceType: Type?): Boolean {
        if (interfaceType == null || instanceType == null) {
            return false
        }
        val interfaces = (instanceType as Class<*>).interfaces
        for (currentInterface in interfaces) {
            if (currentInterface == interfaceType) {
                return true
            }
        }

        return false
    }

    private fun cleanupList(lists: HashMap<Type, ArrayList<WeakActionAndToken>>?) {
        if (lists == null) {
            return
        }
        val it = lists.keys.iterator()
        while (it.hasNext()) {
            val key = it.next()
            val itemList = lists[key]
            if (itemList != null) {
                for (item in itemList) {
                    if (item.getAction() == null || !item.getAction()!!.isLive()) {
                        itemList.remove(item)
                    }
                }
                if (itemList.size == 0) {
                    lists.remove(key)
                }
            }
        }
    }

    private fun sendToTargetOrType(messageTargetType: Type, token: Any) {
        val messageType = NotMsgType::class.java
        if (recipientsOfSubclassesAction != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0008.002
            //            var listClone = recipientsOfSubclassesAction.Keys.Take(_recipientsOfSubclassesAction.Count()).ToList();
            var listClone = ArrayList<Type>()
            listClone.addAll(recipientsOfSubclassesAction.keys)
            for (type in listClone) {
                var list: List<WeakActionAndToken>? = null

                if (messageType == type
                        || (type as Class<*>).isAssignableFrom(messageType)
                        || classImplements(messageType, type)) {
                    list = recipientsOfSubclassesAction[type]
                }

                sendToList(list, messageTargetType, token)
            }
        }

        if (recipientsStrictAction != null) {
            if (recipientsStrictAction.containsKey(messageType)) {
                val list = recipientsStrictAction[messageType]
                sendToList(list, messageTargetType, token)
            }
        }

        cleanup()
    }

    private fun sendToList(
            list: Collection<WeakActionAndToken>?,
            messageTargetType: Type?,
            token: Any?) {
        if (list != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0004.007
            var listClone = ArrayList<WeakActionAndToken>()
            listClone.addAll(list)

            for (item in listClone) {
                val executeAction = item.getAction()
                if (executeAction != null
                        && item.getAction()!!.isLive()
                        && item.getAction()!!.getTarget() != null
                        && (messageTargetType == null
                                || item.getAction()!!.getTarget()!!.javaClass === messageTargetType
                                || classImplements(item.getAction()!!.getTarget()!!.javaClass, messageTargetType))
                        && (item.getToken() == null && token == null || item.getToken() != null && item.getToken() == token)) {
                    executeAction!!.execute()
                }
            }
        }
    }

    private fun <T : Any> sendToTargetOrType(message: T, messageTargetType: Type?, token: Any?) {
        val messageType = message.javaClass
        if (recipientsOfSubclassesAction != null) {
            // Clone to protect from people registering in a "receive message" method
            // Bug correction Messaging BL0008.002
            //            var listClone = recipientsOfSubclassesAction.Keys.Take(_recipientsOfSubclassesAction.Count()).ToList();
            var listClone = ArrayList<Type>()
            listClone.addAll(recipientsOfSubclassesAction.keys)
            for (type in listClone) {
                var list: List<WeakActionAndToken>? = null

                if (messageType == type
                        || (type as Class<*>).isAssignableFrom(messageType)
                        || classImplements(messageType, type)) {
                    list = recipientsOfSubclassesAction[type]
                }

                sendToList(message, list, messageTargetType, token)
            }
        }

        if (recipientsStrictAction != null) {
            if (recipientsStrictAction.containsKey(messageType)) {
                val list = recipientsStrictAction[messageType]
                sendToList(message, list, messageTargetType, token)
            }
        }

        cleanup()
    }

    class WeakActionAndToken {
        lateinit var mAction: WeakAction
        lateinit var mToken: Any

        constructor(action: WeakAction, token: Any) {
            this.mAction = action
            this.mToken = token
        }

        fun getAction(): WeakAction? {
            return mAction
        }

        fun setAction(action: WeakAction) {
            this.mAction = action
        }

        fun getToken(): Any {
            return mToken
        }

        fun setToken(token: Any) {
            this.mToken = token
        }
    }

}