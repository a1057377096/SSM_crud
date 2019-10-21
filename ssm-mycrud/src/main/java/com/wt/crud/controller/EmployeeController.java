package com.wt.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wt.crud.bean.Employee;
import com.wt.crud.bean.Msg;
import com.wt.crud.service.EmployeeService;
/**
 * 处理员工CRUD请求
 * @author 10573
 *
 */
@Controller
public class EmployeeController {
	@Autowired
	EmployeeService	employeeService;
	
	/**
	 * 单个批量二合一
	 * 批量删除：1-2-3
	 * 单个删除：1
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
	public Msg deleteEmpById(@PathVariable("ids")String ids) {
		//批量删除
		if(ids.contains("-")) {
			List<Integer> del_ids =new ArrayList<Integer>();
			String[] str_ids = ids.split("-");
			//组装id集合
			for (String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		}else {
			int id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		return Msg.success();
	}
	
	/**
	 * 如果直接发送ajax=PUT形式的请求
	 * 封装的数据
	 * Employee 
	 * [empId=1014, empName=null, gender=null, email=null, dId=null]
	 * 
	 * 问题：
	 * 请求体中有数据；
	 * 但是Employee对象封装不上；
	 * update tbl_emp  where emp_id = 1014;
	 * 
	 * 原因：
	 * Tomcat：
	 * 		1、将请求体中的数据，封装一个map。
	 * 		2、request.getParameter("empName")就会从这个map中取值。
	 * 		3、SpringMVC封装POJO对象的时候。
	 * 				会把POJO中每个属性的值，request.getParamter("email");
	 * AJAX发送PUT请求引发的血案：
	 * 		PUT请求，请求体中的数据，request.getParameter("empName")拿不到
	 * 		Tomcat一看是PUT不会封装请求体中的数据为map，只有POST形式的请求才封装请求体为map
	 * org.apache.catalina.connector.Request--parseParameters() (3111);
	 * 
	 * protected String parseBodyMethods = "POST";
	 * if( !getConnector().isParseBodyMethod(getMethod()) ) {
                success = true;
                return;
            }
	 * 解决方案；
	 * 我们要能支持直接发送PUT之类的请求还要封装请求体中的数据
	 * 1、配置上HttpPutFormContentFilter；
	 * 2、他的作用；将请求体中的数据解析包装成一个map。
	 * 3、request被重新包装，request.getParameter()被重写，就会从自己封装的map中取数据
	 * 员工更新方法
	 * @param employee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
	public Msg saveEmp(Employee employee,HttpServletRequest request) {
		System.out.println("请求体中的值："+request.getParameter("gender"));
		System.out.println("将要更新的员工数据"+employee);
		employeeService.updateEmp(employee);
		return Msg.success();
	}
	
	/**
	 * 根据id查询员工
	 * @PathVariable:从路径取出id占位符的值时
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id) {
		Employee employee=employeeService.getEmp(id);
		return Msg.success().add("emp", employee);
	}
	
	/**
	 * 检查用户用户名是否可用
	 */
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkUser(@RequestParam("empName")String empName) {
		//先判断用户名是否是合法的表达式
		String regx="(^[a-z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
		if(!empName.matches(regx)) {
			return Msg.fail().add("va_msg", "用户名是2-5位中文或者6-16位英文和数字的组合");
		}
		//数据库用户名重复校验
		boolean b=employeeService.checkUser(empName);
		if(b) {
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg", "用户名不可用");
		}
	}
	
	/**
	 * 员工保存
	 * 1.支持JSR303校验
	 * 2.导入Hibernate-Validator
	 * 3.javaBean中指定规则
	 * 4.@Valid指定要封装的数据进行校验,BindingResult封装校验的结果
	 * 
	 * saveEmp方法参数为Employee，自动封装，因为页面中提交过来的key、value，这些key和employee中的属性一样
	 */
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee,BindingResult result) {
		if(result.hasErrors()) {
			//校验失败，应该返回失败，在模态框中显示校验失败的错误信息
			Map<String, Object> map = new HashMap<String, Object>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("错误的字段名"+fieldError.getField());
				System.out.println("错误信息"+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
				
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(employee);
			return Msg.success();
		}
		
		
	}
	
	/**
	 * @ResponseBody要能使用需要导入jackson包
	 * @param pn
	 * @return
	 */
	@RequestMapping("/emps")
	//将返回对象转为json
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue = "1")Integer pn) {
		//这不是一个分页查询，会查出1000条数据
		//引入PageHelper分页插件
		//在查询之前，只需要调用,传入页码，以及分页的每一页大小
		PageHelper.startPage(pn,5,"emp_id asc");
		//PageHelper.orderBy("emp_id asc");
		//startPage后面紧跟的查询就是一个分页查询
		List<Employee> emps=employeeService.getAll();
		//使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就可以了
		//pageInfo封装了详细的分页信息，包括我们查询出来的数据，可以传入连续显示的页数
		PageInfo page=new PageInfo(emps,5);
		//对于ajax返回json数据，可以使用一个Msg类，方便浏览器更方便解析
		return Msg.success().add("pageInfo",page);
	}
	
	/**
	 * 查询员工数据(分页查询)
	 * @return
	 */
	//@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn",defaultValue = "1")Integer pn,Model model) {
		//这不是一个分页查询，会查出1000条数据
		//引入PageHelper分页插件
		//在查询之前，只需要调用,传入页码，以及分页的每一页大小
		PageHelper.startPage(pn,5);
		PageHelper.orderBy("emp_id desc");
		//startPage后面紧跟的查询就是一个分页查询
		List<Employee> emps=employeeService.getAll();
		//使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就可以了
		//pageInfo封装了详细的分页信息，包括我们查询出来的数据，可以传入连续显示的页数
		PageInfo page=new PageInfo(emps,5);
		model.addAttribute("pageInfo",page);
		return "list";
	}
}
