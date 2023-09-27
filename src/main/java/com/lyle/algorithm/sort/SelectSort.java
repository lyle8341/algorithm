package com.lyle.algorithm.sort;

import com.lyle.algorithm.util.ArrayUtils;

import java.util.Arrays;

/**
 *
 */
public class SelectSort {

    public static void main(String[] args) {
        int[] arr = {99,345,23,0,12,45,32,43,5,6,9};
        int[] compare = {99,345,23,0,12,45,32,43,5,6,9};
        selectSort(arr);
        ArrayUtils.printArr(arr);

        Arrays.sort(compare);
        ArrayUtils.printArr(compare);
    }

    /**
     * 选择排序:
     * 1. 0~N-1位置选择最小值放到0位置
     * 2. 1~N-1位置选择最小值放到1位置
     * 3. 2~N-1位置选择最小值放到2位置
     * ..........................
     * N. N-1~N-1
     */
    public static void selectSort(int[] arr){
        //从上述得知需要N-1次即可
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                minIndex = arr[minIndex] > arr[j] ? j : minIndex;
            }
            swap(arr, i, minIndex);
        }
    }

    private static void swap(int[] arr, int i, int j){
        if(i == j){
            return;
        }
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }



}
