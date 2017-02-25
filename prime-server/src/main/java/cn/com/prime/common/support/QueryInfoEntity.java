package cn.com.prime.common.support;


/**
 * @author Perlin
 * @date 2016年5月10日
 * @since 6.0
 * @version 6.0
 */
@SuppressWarnings("serial")
public class QueryInfoEntity extends QueryInfo {

	/**
	 * the current page number
	 */
	private Integer curPage;
	
	/**
	 * the page limit size limit
	 */
	private Integer pageLimit;
	
	/**
	 * 构造函数
	 */
	public QueryInfoEntity() {
		
	}
	
	/**
	 * 构造函数
	 * @param curPage
	 * @param pageLimit
	 */
	public QueryInfoEntity(Integer curPage, Integer pageLimit) {
		this.curPage = curPage;
		this.pageLimit = pageLimit;
	}

	@Override
	public Integer getLimit() {
		return getPageLimit();
	}

	@Override
	public Integer getOffset() {
		int offset = getCurPage();
		if (offset > 0) {
			offset = (offset - 1) * getPageLimit();
		}
		return offset;
	}

	/**
	 * @return the curPage
	 */
	public Integer getCurPage() {
		return curPage;
	}

	/**
	 * @param curPage the curPage to set
	 */
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}

	/**
	 * @return the pageLimit
	 */
	public Integer getPageLimit() {
		return pageLimit;
	}

	/**
	 * @param pageLimit the pageLimit to set
	 */
	public void setPageLimit(Integer pageLimit) {
		this.pageLimit = pageLimit;
	}
	
}
