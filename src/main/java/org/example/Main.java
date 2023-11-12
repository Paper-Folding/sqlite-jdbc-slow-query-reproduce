package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:sample.db");
            Statement s = c.createStatement();
            String sql = """
                        with parent as (
                            with parent_feature as (
                                select id, min(sub_id) as sub_id from sample group by id
                            )
                            select p.* from parent_feature as pf left join sample as p on pf.id = p.id and pf.sub_id = p.sub_id
                        )
                        select a.*, b.id as child_id, b.sub_id as child_sub_id
                        from parent as a left join sample as b
                        on a.id = b.id and a.sub_id != b.sub_id
                        order by a.id, a.sub_id;
                    """;

            System.out.println("start querying");
            ResultSet rs = s.executeQuery(sql);
            System.out.println("finish querying");
            while (rs.next()) {
                System.out.println(rs.getInt("id"));
                System.out.println(rs.getInt("sub_id"));
            }
            s.close();
            c.close();
        } catch (Exception ignored) {

        }
    }
}