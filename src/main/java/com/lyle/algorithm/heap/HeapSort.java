package com.lyle.algorithm.heap;

import java.util.Arrays;

public class HeapSort {


    /**
     * 大顶堆：arr[i] >= arr[2i+1] && arr[i] >= arr[2i+2]
     * <p>
     * 小顶堆：arr[i] <= arr[2i+1] && arr[i] <= arr[2i+2]
     *
     * <a href="https://www.cnblogs.com/chengxiao/p/6129630.html">参考地址</a>
     */
    public static void main(String[] args) {
        int[] arr = {9, 98, 7, 6, 99, 4, 3, 32, 1};
        sort(arr);
        System.out.println(Arrays.toString(arr));

    }

    private static void sort(int[] arr) {
        /**
         * 1.构建大顶堆
         * arr.length / 2 即非叶子节点的个数
         * arr.length / 2 - 1 即最下层最靠右的一个非叶子节点的下标
         */
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            System.out.println("index: " + i);
            //从第一个非叶子节点开始，从下至上，从右至左调整结构
            adjustHeap(arr, i, arr.length);
        }
        //2.调整堆结构+交换堆顶元素与末尾元素
        for (int j = arr.length - 1; j > 0; j--) {
            swap(arr, 0, j);//将堆顶元素与末尾元素进行交换
            adjustHeap(arr, 0, j);//重新对堆进行调整
        }
    }

    /**
     * 交换元素
     *
     * @param arr
     * @param a
     * @param b
     */
    public static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    /**
     * 调整大顶堆（仅是调整过程，建立在大顶堆已构建的基础上）
     *
     * @param arr
     * @param index
     * @param length 元素个数
     */
    public static void adjustHeap(int[] arr, int index, int length) {
        int temp = arr[index];//先取出当前元素i
        //k = i * 2 +1 这一步的操作是为了获取该节点的左子节点
        //k = k * 2 + 1,这个操作是当结束循环时，将指针指向K节点的左字节点
        for (int k = index * 2 + 1; k < length; k = k * 2 + 1) {//从i结点的左子结点开始，也就是2i+1处开始
            if (k + 1 < length && arr[k] < arr[k + 1]) {//如果左子结点小于右子结点，k指向右子结点
                k++;
            }
            if (arr[k] > temp) {//如果子节点大于父节点，将子节点值赋给父节点（不用进行交换）
                arr[index] = arr[k];
                index = k;
            } else {
                break;
            }
        }
        arr[index] = temp;//将temp值放到最终的位置
    }
}
