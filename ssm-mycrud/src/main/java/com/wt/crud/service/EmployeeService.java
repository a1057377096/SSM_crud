package com.wt.crud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wt.crud.bean.Employee;
import com.wt.crud.bean.EmployeeExample;
import com.wt.crud.bean.EmployeeExample.Criteria;
import com.wt.crud.bean.Msg;
import com.wt.crud.dao.EmployeeMapper;

@Service
public class EmployeeService {
	@Autowired
	EmployeeMapper employeeMapper;
	
	/**
	 * 查询所有员工
	 * @return
	 */
	public List<Employee> getAll() {
		//查询所有的查询条件为null
		return employeeMapper.selectByExampleWithDept(null);
	}
	
	/**
	 * 员工保存
	 * @param employee
	 */
	public void saveEmp(Employee employee) {
		employeeMapper.insertSelective(employee);
	}

	public boolean checkUser(String empName) {
		//查询条件
		EmployeeExample example=new EmployeeExample();
		//创建查询条件
		Criteria criteria=example.createCriteria();
		//给criteria中拼装需要的条件
		criteria.andEmpNameEqualTo(empName);
		//按照条件统计符合条件的记录数
		long count = employeeMapper.countByExample(example);
		return count==0;
	}
	
	/**
	 * 按照员工id查询员工
	 * @param id
	 * @return
	 */
	public Employee getEmp(Integer id) {
		Employee employee = employeeMapper.selectByPrimaryKey(id);
		return employee;
	}
	
	/**
	 * 员工更新
	 * @param employee
	 */
	public void updateEmp(Employee employee) {
		employeeMapper.updateByPrimaryKeySelective(employee);
	}

	public void deleteEmp(Integer id) {
		employeeMapper.deleteByPrimaryKey(id);
	}

	public void deleteBatch(List<Integer> ids) {
		EmployeeExample example=new EmployeeExample();
		Criteria criteria=example.createCriteria();
		//delete from xxx where emp_id in(1,2,3)
		criteria.andEmpIdIn(ids);
		employeeMapper.deleteByExample(example);
	}
	
}
