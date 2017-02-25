package cn.com.prime.common.support;

import javax.servlet.http.HttpServletRequest;

import cn.com.prime.common.util.WebUtil;

/**
 * 分页信息
 * @author Jason
 * @date 2016-3-21
 */
public class PageInfo {

	/**
	 * 每一页大小
	 */
	private Integer pageLimit = 100;

	/**
	 * 当前页码
	 */
	private Integer curPage = 1;

	public PageInfo() {

	}

	public PageInfo(HttpServletRequest request) {
		Integer reqPageLimit = WebUtil.getInteger(request, "pageLimit");
		if (reqPageLimit != null && reqPageLimit > 0) {
			pageLimit = reqPageLimit;
		}
		Integer reqCurPage = WebUtil.getInteger(request, "curPage");
		if (reqCurPage != null && reqCurPage > 0) {
			curPage = reqCurPage;
		}
		request.setAttribute("pageInfo", this);
	}

	public Integer getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(Integer pageLimit) {
		this.pageLimit = pageLimit;
	}

	public Integer getCurPage() {
		return curPage;
	}

	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
}
