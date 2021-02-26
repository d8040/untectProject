package com.example.untact.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.untact.dto.Member;
import com.example.untact.dto.ResultData;
import com.example.untact.service.MemberService;

@Controller
public class UsrMemberController {

	@Autowired
	private MemberService memberService;
	//	게시물 추가
	@RequestMapping("/usr/member/doJoin")
	@ResponseBody
	public ResultData doAdd(@RequestParam Map<String, Object> param) {
		
		if (param.get("loginId") == null) {
			return new ResultData("F-1", "loginId을 입력해주세요");
		}
		
		Member existingMember = memberService.getMemberByLoginId((String) param.get("loginId"));
		
		if (existingMember != null) {
		    return new ResultData ("F-2", String.format("%s (은)는 이미 사용중인 아이디 입니다.", param.get("loginId")));
		}
		
		if (param.get("loginPw") == null) {
			return new ResultData("F-1", "loginPw를 입력해주세요");
		}

		if (param.get("name") == null) {
			return new ResultData("F-1", "name을 입력해주세요");
		}

		if (param.get("nickname") == null) {
			return new ResultData("F-1", "nickname를 입력해주세요");
		}

		if (param.get("cellphoneNo") == null) {
			return new ResultData("F-1", "cellphoneNo를 입력해주세요");
		}
		
		if (param.get("email") == null) {
			return new ResultData("F-1", "email을 입력해주세요");
		}

		return memberService.join(param);
	}
	
	@RequestMapping("usr/member/doLogin")
	@ResponseBody
	public ResultData doLogin(String loginId, String loginPw, HttpSession session) {
	    
	    if (session.getAttribute("loginedMemberId") != null) {
		return new ResultData("F-4", "이미 로그인되어 있습니다.");
	    }
	    
	    if (loginId == null) {
		return new ResultData("F-1", "아이디를 입력해주세요.");
	    }
	    
	    Member exsistingMember = memberService.getMemberByLoginId(loginId);
	    
	    if (exsistingMember == null) {
		return new ResultData("F-2", "일치하는 아이디가 존재하지 않습니다.", "loginId", loginId);
	    }
	    
	    if (loginPw == null) {
		return new ResultData("F-1", "비밀번호를 입력해주세요.");
	    }
	    
	    if (exsistingMember.getLoginPw().equals(loginPw)== false) {
		return new ResultData("F-3", "비밀번호가 일치하지 않습니다.");
	    }
	    
	    session.setAttribute("loginedMemberId", exsistingMember.getId());
	    
	    return new ResultData("S-1", String.format("%s님 환영합니다.", exsistingMember.getNickname()));	    
	}
	
	@RequestMapping("usr/member/doLogout")
	@ResponseBody
	public ResultData doLogout(String loginId, String loginPw, HttpSession session) {
	    
	    if (session.getAttribute("loginedMemberId") == null) {
		return new ResultData("F-1", "이미 로그아웃 되었습니다.");
	    }
	    
	    session.removeAttribute("loginedMemberId");	    
	    
	    return new ResultData("S-1", "로그아웃 되었습니다.");
	}
	
	@RequestMapping("usr/member/doModify")
	@ResponseBody
	public ResultData doModify(@RequestParam Map<String, Object> param, HttpSession session) {
	    if (session.getAttribute("loginedMemberId") == null) {
		return new ResultData("F-1", "로그인 후 이용해 주세요");
	    }
	    
	    if (param.isEmpty()) {
		return new ResultData("F-2", "수정할 정보를 입력해 주세요.");
	    }
	    
	    int loginedMemberId = (int) session.getAttribute("loginedMemberId");
	    param.put("id", loginedMemberId);
	    
	    return memberService.modifyMember(param);
	}
}
