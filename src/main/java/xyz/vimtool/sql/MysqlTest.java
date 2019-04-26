package xyz.vimtool.sql;

import java.sql.*;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @since   jdk1.8
 * @date    2018/8/3
 */
public class MysqlTest {

    private static String driver = "com.mysql.jdbc.Driver";

    private static String url = "jdbc:mysql://127.0.0.1:3306/jdbc_test?useSSL=true";

    private static String username = "root";

    private static String password = "admin";

    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int insert(Student student) {
        String sql = "insert into student (name,sex,age) values(?,?,?)";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getSex());
            ps.setInt(3, student.getAge());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int update(Student student) {
        String sql = "update student set age=" + student.getAge() + " where name='" + student.getName() + "'";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static Integer getAll() {
        String sql = "select * from student";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt("id"));
                student.setName(resultSet.getString("name"));
                student.setSex(resultSet.getString("sex"));
                student.setAge(resultSet.getInt("age"));
                System.out.println(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int delete(String name) {
        String sql = "delete from student where name='" + name + "'";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        Student student = new Student("Achilles", "Male", 14);

        MysqlTest.insert(student);
        MysqlTest.getAll();

        student.setName("Achilles");
        student.setAge(7);
        MysqlTest.update(student);
        MysqlTest.getAll();

//        MysqlTest.delete("Achilles");
    }
}

class Student {

    private Integer id;

    private String name;

    private String sex;

    private Integer age;

    Student() {
    }

    Student(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                '}';
    }
}
