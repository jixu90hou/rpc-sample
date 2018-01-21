package learn;

public class Test {
    public static void main(String[] args) {
        //定义一个字符串数组
        String[] strings={"zhangsan","lisi","wangwu","shenliu"};
        //循环输出数组里面的内容 i表示下标，strings表示数组 string[i]表示数组里面的元素
        for (int i=0;i<strings.length;i++){
            System.out.println(i+"--->"+strings[i]);
        }
        //print(strings);
    }
}
