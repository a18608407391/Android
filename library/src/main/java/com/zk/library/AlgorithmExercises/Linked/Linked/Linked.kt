package com.zk.library.AlgorithmExercises.Linked.Linked

import android.util.Log


class Linked {

    var head: Node? = null


    fun addNode(d: Int) {

        //创建一个新链表
        var newNode = Node(d)
        if (head == null) {
            //如果头是空，就赋值
            head = newNode
            return
        }
        //遍历集合找到最后的节点
        var tmp = head
        while (tmp!!.next != null) {
            //找到最后一个next为null
            tmp = tmp.next
        }
        tmp.next = newNode
    }


    fun lenth(): Int {
        var i = 0
        var node = head
        while (node!!.next != null) {
            node = node.next
            i++
        }
        return i
    }

    fun deleteNode(index: Int): Boolean {
        if (index < 1 || index > lenth()) {
            return false
        }
        if (index == 1) {
            head = head!!.next
            return true
        }
        var i = 1
        var preNode = head   //当前头
        var curNode = preNode!!.next    //当前对象引用
        while (curNode!!.next != null) {
            if (i == index) {
                //找到当前索引
                preNode!!.next = curNode.next
            }
            //未找到索引 下一个
            preNode = curNode
            curNode = curNode.next
            i++
        }
        return false
    }


    fun deleteNode(node: Node): Boolean {
        if (node?.next == null) {
            return false
        }
        node.data = node.next!!.data
        node.next = node.next!!.next
        return true
    }


    //链表反转
    //正向
    fun ReverseIteratively(n: Node): Node {
        if (n?.next == null) {
            return n
        }
//  (1,2)  -> (2,3)  -> (3,4) ->(4,5) ->(5,6) ->(6,null)
        //

        var lastNode: Node? = null      //上一个
        var tmp: Node? = null     //下一个

        while (head != null) {
            // head往右移动   1 2 3 4 5 6    //
            tmp = head!!.next     //  存储当前head.next值

            head!!.next = lastNode    // 赋值curNode.next = null

            lastNode = head       //lastNode =  (1,null)   (2,1)

            head = tmp      // (2,3)  3
        }
        return lastNode!!
    }

    //反向
    fun Reverse(n: Node): Node {
        //  (1,2)  -> (2,3)  -> (3,4) ->(4,5) ->(5,6) ->(6,null)
        if (n?.next == null) {
            Log.e("result", n.data.toString() + "末端节点")
            return n
        }
        // 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8


        //最后 n =7,next = 8,return 8,n.next.next = 7,n.next = null  8 ->7
        //     n =6,next = 7,return 8,n.next.next = 6,n.next = null  8 ->7->6
        //     n =5,next = 6,return 8,n.next.next = 5,n.next = null  8 ->7->6->5
        //     n =4,next = 5,return 8,n.next.next = 4,n.next = null  8 ->7->6->5->4
        //     n =3,next = 4,return 8,n.next.next = 3,n.next = null  8 ->7->6->5->4->3
        //     n =2,next = 3,return 8,n.next.next = 2,n.next = null  8 ->7->6->5->4->3->2
        //     n =1,next = 2,return 8,n.next.next = 1,n.next = null  8 ->7->6->5->4->3->2->1
        var newHead = Reverse(n.next!!)
        n.next!!.next = n
        n.next = null
        return newHead
    }

    //链表查找中间数

    fun FindMide(head: Node): Node {
        var mid = head
        var midlast = head
        while (head?.next != null && head.next!!.next != null) {
            midlast = midlast.next!!.next!!
            mid = mid.next!!
        }
        return mid
    }


    //查找倒数第K个值
    fun SearchElement(head: Node, index: Int): Node? {
        if (head == null || index < 1) {
            return null
        }
        //   假如长度为n    n -k  就是第几位
        var p = head
        var q = head
        var i = 1
        while (i < index) {
            p = p.next!!
            i++
        }
        while (p.next != null) {
            p = p.next!!
            q = q.next!!
        }
        return q
    }


    fun LinkedSort(node: Node) {
        //链表排序
    }


}