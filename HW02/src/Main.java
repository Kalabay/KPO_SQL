import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/shopme";
        String username = "kalab";
        String password = "qwertyhse179";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            String sql1 = "INSERT INTO KPO (name, surname, is_visited, mark, id)"
                + " VALUES ('Максим', 'Максимов', NULL, NULL, 1)";
            String sql2 = "INSERT INTO KPO (name, surname, is_visited, mark, id)"
                    + " VALUES ('Иван', 'Иванов', NULL, NULL, 2)";
            String sql3 = "INSERT INTO KPO (name, surname, is_visited, mark, id)"
                    + " VALUES ('Пётр', 'Петров', NULL, NULL, 3)";

            Statement statement = connection.createStatement();

            statement.executeUpdate(sql1);
            statement.executeUpdate(sql2);
            statement.executeUpdate(sql3);

            Scanner in = new Scanner(System.in);
            System.out.println("/r - выбрать случайного студента.");
            System.out.println("/l - список студентов с оценками.");
            System.out.println("/e - закончить.");
            String input = in.nextLine();
            boolean[] used = new boolean[3];
            while (!Objects.equals(input, "/e")) {
                if (Objects.equals(input, "/r")) {
                    int count = 0;
                    for (int i = 0; i < used.length; ++i) {
                        if (!used[i]) {
                            count += 1;
                        }
                    }
                    if (count == 0) {
                        System.out.println("Никого не осталось.");
                    } else {
                        int indexStudent = ThreadLocalRandom.current().nextInt(0, count + 1);
                        count = 0;
                        for (int i = 0; i < used.length; ++i) {
                            if (!used[i]) {
                                if (count == indexStudent) {
                                    used[i] = true;
                                    String select = "SELECT * FROM KPO WHERE id == " + (i + 1);
                                    ResultSet student = statement.executeQuery(select);
                                    student.next();
                                    String name = student.getString("name");
                                    String surname = student.getString("surname");
                                    Integer mark;
                                    System.out.println("Отвечает Студент " + name + " " + surname);
                                    System.out.println("Присутствует ли на паре? (y/n)");
                                    String ans = in.nextLine();
                                    while (!Objects.equals(ans, "y") && !Objects.equals(ans, "n")) {
                                        System.out.println("Некорректный ввод.");
                                        ans = in.nextLine();
                                    }
                                    if (ans.equals("y")) {
                                        System.out.println("Оценка?");
                                        int markInput = in.nextInt();
                                        while (markInput < 0 || markInput > 10) {
                                            System.out.println("Некорректный ввод.");
                                            markInput = in.nextInt();
                                        }
                                        mark = markInput;

                                        String update = "UPDATE KPO SET is_visited = 1, mark == "
                                                + mark + " WHERE id == " + (i + 1);
                                        statement.executeQuery(update);
                                    } else {
                                        mark = 0;
                                        String update = "UPDATE KPO SET is_visited = 0, mark == "
                                                + mark + " WHERE id == " + (i + 1);
                                        statement.executeQuery(update);
                                    }
                                    break;
                                }
                                count += 1;
                            }
                        }
                    }
                } else if (Objects.equals(input, "/l")) {
                    String printall = "SELECT * FROM KPO";
                    ResultSet students = statement.executeQuery(printall);
                    while (students.next()) {
                        String name = students.getString("name");
                        String surname = students.getString("surname");
                        Integer is_visited = students.getInt("is_visited");
                        Integer mark = students.getInt("mark");
                        System.out.println(name + " " + surname + " " + is_visited + " " + mark);
                    }
                } else {
                    System.out.println("Некорректный ввод.");
                }
                input = in.nextLine();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}