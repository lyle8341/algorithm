package com.lyle.algorithm.xor;

/**
 * 异或： Exclusive OR，中exclusive表示“独家的”、“独有的”
 *
 * 异或：相同为0，不同为1
 *
 * 异或运算就是在两个的信息中比较，寻找“差异”的过程
 */
public class ExclusiveOr {

    public static void main(String[] args) {
         int a = 0b1010111100;
        //1010111100 (700)
        //0101000011
        //0101000100
        //0000000100
        System.out.println(rightmost1(a));

    }

    /**
     * 0 ^ N = N
     * N ^ N = 0
     * 异或运算（Operation）满足交换律和结合律
     */
    private static void xor(){

    }

    /**
     * 原理分析：
     * a = 甲
     * b = 乙
     * a = a ^ b; =======> a = 甲 ^ 乙; b = 乙;
     * b = a ^ b; =======> b = (甲 ^ 乙) ^ 乙; a = 甲 ^ 乙; =======> b = 甲; a = 甲 ^ 乙;
     * a = a ^ b; =======> a = (甲 ^ 乙) ^ 甲; b = 甲; ========> a = 乙; b = 甲;
     * 前提：a 和 b 在内存中是两块区域
     *
     * 数组中交换元素时候 i 和 j 千万不能相等。否则数据抹零
     */
    private static void swap(int[] arr, int i, int j){
        if(i == j){
            return;
        }
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }

    /**
     * 提取出最右侧的1
     *
     * int a = 0b1010111100;
     *         //1010111100 (700)
     *         //0101000011
     *         //0101000100
     *         //0000000100
     */
    public static int rightmost1(int source){
        return source & (~source + 1);
    }
}
