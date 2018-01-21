package learn;

public class Student {
    //学生编号 成员变量
    private int studentNo;
    //学生年龄
    private int age;
    //学生姓名
    private String name;
    //学生分数
    private double score;
    //构造方法和普通方法(run)差不多，但是他能够初始化属性参数
    public Student(int studentNo1,int age1,String name1,double score1){
        this.studentNo=studentNo1;
        this.age=age1;
        this.name=name1;
        this.score=score1;
    }
    public void printStudentInfo(){
        System.out.println("name:"+this.name+"\t"+"age:"+age);
    }

}
