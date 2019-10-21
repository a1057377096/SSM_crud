package com.wt.crud.test;

import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wt.crud.bean.Employee;
import com.wt.crud.dao.DepartmentMapper;
import com.wt.crud.dao.EmployeeMapper;

/**
 * 测试dao层工作
 * @author 10573
 *推荐Spring的项目就可以使用Spring的单元测试，可以自动注入我们需要的组件
 *1.导入SpringTest模块
 *2.@ContextConfiguration指定spring配置文件的位置，自动创建出ioc容器
 *3.@RunWith是junit的注解指定运行单元测试的时候，用哪个单元测试来运行，所有的@Test都是SpringJUnit4ClassRunner(Spring单元测试的驱动)来运行
 *4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MapperTest {
	@Autowired
	DepartmentMapper departmentMapper;
	
	@Autowired
	EmployeeMapper employeeMapper;
	
	@Autowired
	//是一个批量的sqlSession
	SqlSession sqlSession;
	
	/** 
	 * 测试DepartmentMapper
	 */
	@Test
	public void TestCRUD() {
		/*
		 * //我们通过整合Spring和Mybatis将mybatis接口的实现加入到ioc容器中，所以要测试Mapper可以分为两步 //1.创建Spring容器
		 * ApplicationContext ioc=new
		 * ClassPathXmlApplicationContext("applicationContext.xml"); //2.从容器中获取Mapper，来使用Spring的单元测试更加方便
		 * ioc.getBean(DepartmentMapper.class);
		 */
		System.out.println(departmentMapper);
		
		//1.插入几个部门
		/**
		 * 有外键约束下的表，想要删除数据后再插入数据，自增列从1开始，可以用以下sql
		 * 
		 * 失效外键约束
		 * SET FOREIGN_KEY_CHECKS=0
		 * Mysql删除数据并自增列从1开始
		 * TRUNCATE tbl_dept;
		 * 生效外键约束
		 * SET FOREIGN_KEY_CHECKS=1
		 */
//		departmentMapper.insertSelective(new Department(null,"开发部"));
//		departmentMapper.insertSelective(new Department(null,"测试部"));
		
		//2.生成员工数据，测试员工插入
		//employeeMapper.insertSelective(new Employee(null,"Jerry", "M", "Jerry@wt.com", 1));
		
		//3.批量插入多个员工，可以定义一个新的方法，传入员工集合，现在使用可以批量操作的sqlSession
		EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
		for (int i = 0; i < 1000; i++) {
			String uid=UUID.randomUUID().toString().substring(0, 5)+i;
			mapper.insertSelective(new Employee(null,uid, "M", uid+"@wt.com", 1));
		}
		System.out.println("批量完成");
	}
}
