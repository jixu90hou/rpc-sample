package learn;

public class SchoolTest {
    public static void main(String[] args) {
        //Student一个类，student1一个对象引用
        Student student1=new Student(222,19,"zhangsan",98.99);
  /*      Student student2=new Student(223,18,"lisi",90);
        Student student3=new Student(224,18,"wangwu",94);
        Student student4=new Student(225,20,"shenliu",93);*/

        student1.printStudentInfo();
    }
}
