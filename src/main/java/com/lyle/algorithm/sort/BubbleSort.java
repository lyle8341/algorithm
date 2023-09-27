package com.lyle.algorithm.sort;

import com.lyle.algorithm.util.ArrayUtils;

/**
 * 冒泡排序
 */
public class BubbleSort {


    public static void main(String[] args) {
        int[] arr = {99,345,23,0,9999,12,45,32,43,5,6,9};
        bubbleSort(arr);
        ArrayUtils.printArr(arr);
    }

    /**
     * 1. 0~N-1 相邻比较，较大的往后移动，第一轮过后最大的移动到最右边了
     * 2. 0~N-2 相邻比较，较大的往后移动，第二轮过后次最大的移动到了倒数第二个
     * ....................................................
     * N. 0~N-N 相邻比较
     *
     */
    public static void bubbleSort(int[] arr){
        for (int i = 0; i < arr.length - 1; i++) { //综上所述， N-1次即可
            for (int j = 0; j < arr.length - i - 1; j++) {
                if(arr[j] > arr[j + 1]){
                    swap(arr, j, j+ 1);
                }
            }
        }
    }

    /**
     * 异或：相同为0，不同为1
     * @param arr
     * @param i
     * @param j
     */
    private static void swap(int[] arr, int i, int j){
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }


}
