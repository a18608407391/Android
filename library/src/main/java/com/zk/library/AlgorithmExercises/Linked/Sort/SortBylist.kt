package com.zk.library.AlgorithmExercises.Linked.Sort


class SortBylist {


    fun exc(array: IntArray, i: Int, j: Int): IntArray {
        var temp = array[i]
        array[i] = array[j]
        array[j] = temp
        return array
    }

    //排序算法

    //1、冒泡排序

    fun BubbleSort(array: IntArray) {
        for (i in 0 until array.size) {
            for (j in 0 until (array.size) - 1 - i) {
                if (array[j] > array[j + 1]) {
                    exc(array, j, j + 1)
                }
            }
        }
    }

    //2  鸡尾酒排序（冒泡排序升级版）

    fun Cocktail_Sort(array: IntArray) {
        var left = 0
        var right = array.size - 1
        while (left < right) {
            var max = right - 1
            var min =  left
            for (j in (left + 1)..right) {
                if (array[j] < array[min]) {

                }
            }
        }
    }
}