package persistence;

import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {

    private static SqlSessionFactory sqlSessionFactory;

    private static SqlSessionFactory buildFactory() {
        try {
            Reader reader =
                    Resources.getResourceAsReader(
                            "mybatis-config.xml");

            Properties properties = new Properties();
            properties.setProperty(
                    "database.url",
                    System.getProperty(
                            "uno.database.url",
                            "jdbc:sqlite:uno.db"));

            return new SqlSessionFactoryBuilder()
                    .build(reader, properties);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized SqlSessionFactory getFactory() {
        if (sqlSessionFactory == null) {
            sqlSessionFactory = buildFactory();
        }
        return sqlSessionFactory;
    }

    public static synchronized void resetFactory() {
        sqlSessionFactory = null;
    }
}
