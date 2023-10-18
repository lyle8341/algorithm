package com.lyle.algorithm.skipList;

import java.util.Random;
import java.util.Stack;

/**
 * <a href="https://zhuanlan.zhihu.com/p/339750543">参考地址</a>
 */
class SkipNode<T> {
    int key;
    T value;
    SkipNode<T> right, down;//左右上下四个方向的指针

    /**
     * @param key   key
     * @param value value
     */
    public SkipNode(int key, T value) {
        this.key = key;
        this.value = value;
    }
}

public class SkipList<T> {
    SkipNode<T> entrypoint;//头节点，入口
    int layer;//层数
    Random random;// 用于投掷硬币
    final int MAX_LEVEL = 32;//最大的层

    SkipList() {
        random = new Random();
        entrypoint = new SkipNode<>(Integer.MIN_VALUE, null);
        layer = 0;
    }

    public SkipNode<T> search(int key) {
        SkipNode<T> team = entrypoint;
        while (team != null) {
            if (team.key == key) {
                return team;
            } else if (team.right == null) {//右侧没有了，只能下降
                team = team.down;
            } else if (team.right.key > key) {//需要下降去寻找
                team = team.down;
            } else {//右侧比较小向右
                team = team.right;
            }
        }
        return null;
    }

    public void delete(int key) {//删除不需要考虑层数
        SkipNode<T> team = entrypoint;
        while (team != null) {
            if (team.right == null) {//右侧没有了，说明这一层找到，没有只能下降
                team = team.down;
            } else if (team.right.key == key) {//找到节点，右侧即为待删除节点
                team.right = team.right.right;//删除右侧节点
                team = team.down;//向下继续查找删除
            } else if (team.right.key > key) {//右侧已经不可能了，向下
                team = team.down;
            } else { //节点还在右侧
                team = team.right;
            }
        }
    }

    public void add(SkipNode<T> node) {
        int key = node.key;
        SkipNode<T> findNode = search(key);
        if (findNode != null) {//如果存在这个key的节点
            findNode.value = node.value;
            return;
        }
        Stack<SkipNode<T>> stack = new Stack<>();//存储向下的节点，这些节点可能在右侧插入节点
        SkipNode<T> temp = entrypoint;//查找待插入的节点   找到最底层的哪个节点。
        while (temp != null) {//进行查找操作
            if (temp.right == null) {//右侧没有了，只能下降
                stack.add(temp);//将曾经向下的节点记录一下
                temp = temp.down;
            } else if (temp.right.key > key) {//需要下降去寻找
                stack.add(temp);//将曾经向下的节点记录一下
                temp = temp.down;
            } else {//向右
                temp = temp.right;
            }
        }
        int currentLayer = 1;//当前层数，从第一层添加(第一层必须添加，先添加再判断)
        SkipNode<T> downNode = null;//保持前驱节点(即down的指向，初始为null)
        while (!stack.isEmpty()) {
            //在该层插入node
            temp = stack.pop();//抛出待插入的左侧节点
            SkipNode<T> insertNode = new SkipNode<>(node.key, node.value);//节点需要重新创建，因为在循环中
            insertNode.down = downNode;//处理竖方向
            downNode = insertNode;//标记新的节点下次使用
            if (temp.right == null) {//右侧为null 说明插入在末尾
                temp.right = insertNode;
            } else {//右侧还有节点，插入在两者之间
                insertNode.right = temp.right;
                temp.right = insertNode;
            }
            //考虑是否需要向上
            if (currentLayer > MAX_LEVEL)//已经到达最高级的节点啦
                break;
            double num = random.nextDouble();//[0-1]随机数
            if (num > 0.5)//运气不好结束
                break;
            currentLayer++;
            if (currentLayer > layer){
                layer = currentLayer;
                //需要创建一个新的节点
                SkipNode<T> newEntryPoint = new SkipNode<>(Integer.MIN_VALUE, null);
                newEntryPoint.down = entrypoint;
                entrypoint = newEntryPoint;//改变head
                stack.add(entrypoint);//下次抛出head
            }
        }
    }

    public void printList() {
        SkipNode<T> temp = entrypoint;
        SkipNode<T> last = temp;
        //last 指向最后一层的head
        while (last.down != null) {
            last = last.down;
        }
        while (temp != null) {
            SkipNode<T> tr = temp.right;
            SkipNode<T> lr = last.right;
            System.out.printf("%-8s", "head->");
            while (lr != null && tr != null) {//先是第一层和最后一层处理，而后第二层和最后一层处理...
                if (lr.key == tr.key) {
                    System.out.printf("%-5s", tr.key + "->");
                    lr = lr.right;
                    tr = tr.right;
                } else {//两个不在一个纵向时
                    lr = lr.right;
                    System.out.printf("%-5s", "");
                }
            }
            temp = temp.down;
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SkipList<Integer> list = new SkipList<>();
        for (int i = 1; i < 5; i++) {
            list.add(new SkipNode<>(i, 666));
        }
        list.printList();
        //list.delete(4);
        //list.delete(8);
        //list.printList();
    }
}