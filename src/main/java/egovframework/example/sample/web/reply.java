package egovframework.example.sample.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
public class reply {
	@Resource(name="sampleDAO")
	private SampleDAO sampleDAO;
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@RequestMapping(value = "/paging.do")
	public String paging5(HttpServletRequest request, ModelMap model) throws Exception {
		//페이징
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(10);
		paginationInfo.setPageSize(7);
		//인자생성
		EgovMap in = new EgovMap();
		EgovMap in2 = new EgovMap();
		in.put("firstindex", "" + paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", "" + paginationInfo.getRecordCountPerPage());
		String idx = request.getParameter("idx");
		in.put("bno", idx);
		List<EgovMap> list = (List<EgovMap>) sampleDAO.list("selectList", in);
		for( EgovMap item:list ){
		     String idx1 = ""+item.get("idx");
		     in2.put("bno", idx1);
		     List<EgovMap> list2 = (List<EgovMap>) sampleDAO.list("reply", in2);
		     item.put("list2", list2 );           
		}
		model.addAttribute("resultList", list );
		EgovMap count = (EgovMap) sampleDAO.select("selectTotal");
		String total = "" + count.get("num");
		paginationInfo.setTotalRecordCount(Integer.parseInt(total));
		model.addAttribute("paginationInfo", paginationInfo);
		return "user/paging";
	}
	@RequestMapping(value = "/replyProcess.do")
	public String replyProcess2(HttpServletRequest request, ModelMap model) throws Exception {
		String bno = request.getParameter("bno");
		String replyid = request.getParameter("replyid");
		String replytext = request.getParameter("replytext");
		
		EgovMap in = new EgovMap();
		in.put("bno", bno);
		in.put("replyid", replyid);
		in.put("replytext", replytext);
		sampleDAO.insert("replyProcess",in);
		System.out.println("g1");
		return "redirect:/paging.do?idx="+bno;
	}
	@RequestMapping(value = "/insert.do")
	public String insert(HttpServletRequest request, ModelMap model) throws Exception {
		return "user/insert";
	}
	@ResponseBody
	@RequestMapping(value = "/insertProcess.do" , method = RequestMethod.POST, produces = "application/json; charset=utf8")
	public String insertProcess5(HttpServletRequest request, ModelMap model) throws Exception {
		String result = "fail";
		HttpSession session = request.getSession();
		
		String idx = request.getParameter("idx");
		String name = request.getParameter("name");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		
		try{
			EgovMap in = new EgovMap();
			in.put("idx", idx);
			in.put("name", name);
			in.put("title", title);
			in.put("content", content);
			sampleDAO.insert("insertProcess",in);
			
			result="success";
			
		}catch(Exception e){
			System.out.println("insertProcess에러"+e);
			result="가입 실패";
		}
		return result;
	}
}
