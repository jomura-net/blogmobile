package net.jomura.blog.mobile.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jomura.blog.StringUtil;
import net.jomura.blog.mobile.dao.DaoUtil;

/**
 * @author Jomora
 */
public class PostService {

	public List<Map<String, Object>> index(Map<String, String[]> params) {
		Map<String, String[]> newParams = new HashMap<String, String[]>(params);
		// パラメータの整形
		if (newParams.containsKey("s")) {
			String[] newSParams = StringUtil.convertUTF8(newParams.get("s")[0]).split(" ");
			for (int i = 0, max = newSParams.length; i < max; i++) {
				newSParams[i] = "%" + newSParams[i] + "%";
			}
			newParams.put("s", newSParams);
		}
		newParams.put("limit", new String[]{String.valueOf(15)});
		
		return DaoUtil.getInstance().
				selectList("net.jomura.blog.mobile.PostMapper.selectList", newParams);
	}

	public Map<String, Object> show(int id) {
		return DaoUtil.getInstance().
				selectOne("net.jomura.blog.mobile.PostMapper.selectOne", id);
	}

	public String getBlogname() {
		String[] blognames = DaoUtil.getInstance().getBlognames().split(",");
		return blognames[(Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1) % blognames.length];
	}

}
