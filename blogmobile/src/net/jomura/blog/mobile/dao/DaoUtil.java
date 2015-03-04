package net.jomura.blog.mobile.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * @author Jomora
 *
 */
public class DaoUtil {

	private static DaoUtil instance = new DaoUtil();
	public static DaoUtil getInstance() {
		return instance;
	}
	
	private SqlSessionFactory sqlSessionFactory;
	private DaoUtil() {
		sqlSessionFactory = getSqlSessionFactory();
	}

	private SqlSessionFactory getSqlSessionFactory() {
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			return new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load mybatis-config.xml", e);
		}
	}

	public <T> T selectOne(String sqlId, Object args) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectOne(sqlId, args);
		} finally {
			session.close();
		}
	}

	public <E> List<E> selectList(String sqlId, Object args) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectList(sqlId, args);
		} finally {
			session.close();
		}
	}

	public String getBlognames() {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectOne("net.jomura.blog.mobile.PostMapper.getBlognames");
		} finally {
			session.close();
		}
	}

}
