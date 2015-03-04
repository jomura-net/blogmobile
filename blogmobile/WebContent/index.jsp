<!DOCTYPE html>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Map"%>
<%@page import="net.jomura.blog.mobile.service.PostService"%>
<%@page import="net.jomura.blog.StringUtil"%>
<%@page import="net.jomura.StringTruncator"%>
<%
// config
int contentExcerptBytes = 200;

// main
PostService postServ = new PostService();
String blogname = postServ.getBlogname();
%>
<html xmlns:og="http://ogp.me/ns#" xmlns:fb="http://www.facebook.com/2008/fbml">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title><%= blogname %></title>
  <link rel="stylesheet" href="//code.jquery.com/mobile/1.1.1/jquery.mobile-1.1.1.min.css" />
  <style>
    .meta {
      color: #808081;
      font-size: 80%;
      font-weight: normal;
      text-align: right;
    }
    .cite {
      font-size: 70%;
	  line-height: 110%;
	  width: 70%;
      text-align: center;
    }
  </style>
  <script src="//code.jquery.com/jquery-1.7.1.min.js"></script>
  <script src="//code.jquery.com/mobile/1.1.1/jquery.mobile-1.1.1.min.js"></script>
</head>
<body>

<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/ja_JP/all.js#xfbml=1&appId=149667201791132";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>

<%
DateFormat dt_yyyyMMdd = new SimpleDateFormat("yyyy.MM.dd (E)", Locale.US);
DateFormat dt_HHmm = new SimpleDateFormat("HH:mm");

if (StringUtil.isNullOrEmpty(request.getParameter("p"))) {
	// 一覧表示
    List<Map<String, Object>> postList = postServ.index(request.getParameterMap());
    String prePostDate = "";
%>
<div id="index" data-role="page" data-title="<%= blogname %>" data-add-back-btn="true" data-back-btn-text="Back">
  <div data-role="header" data-position="fixed" data-theme="b" style="text-align:center;" >
    <h1><%= blogname %></h1>
   <%-- div style="font-size:70%;text-align:right;">電脳硬化症気味な日記</div --%>
  </div>

  <div data-role="content" data-theme="c">
    <ul data-role="listview" data-filter="true">
<%
	for(Map<String, Object> post : postList) {
	    String postDate = dt_yyyyMMdd.format((Date) post.get("post_date"));
	    if (!postDate.equals(prePostDate)) {
	    	prePostDate = postDate;
%>
      <li data-role="list-divider"><%=postDate%></li> 
<%
 	}
 %>
      <li><a href="?p=<%=post.get("ID")%>">
        <h3><%=post.get("post_title")%> &nbsp; <span class="meta">[<%=post.get("cat_name")%>]</span></h3>
        <p><%=StringTruncator.trunc(StringUtil.escapeHtml(post.get("post_content")), contentExcerptBytes, "UTF-8")%></p>
        <p class="ui-li-aside">at <%=dt_HHmm.format((Date) post.get("post_date"))%></p>
      </a></li>
<%
	} // end of for
%>
    </ul>
  </div>

  <div data-role="footer" style="text-align:center;" class="footer-docs ui-bar" data-theme="b">
    <form method="get" action="index.jsp" style="float:right; margin-right:5px;">
      <input type="search" name="s" value="<%= request.getParameter("s") == null ? "" : StringUtil.convertUTF8(request.getParameter("s")) %>" placeholder="keywords..." />
    </form>
    <cite class="cite">リンクはご自由に。ご意見・お問い合わせ・フィードバックはお気軽に <a href="&#109;&#97;&#105;l&#116;&#111;&#58;&#107;az&#64;&#106;omur&#97;&#46;n&#101;&#116;">メール</a> ください。</cite><br />
    <table style="width:90%; margin-left:auto; margin-right:auto;" ><tr><td class="cite" style="text-align:left;">
           当サイトにおいて提供される情報は、真実性、合法性、安全性、適切性、有用性、完全性、正確性について何ら保証するものではありません。利用者はその旨を了承の上、自己の責任において利用するものとし、当該情報に基づく損害、損失についても、当サイトは一切責任を負いません。
    <div style="text-align:right;">Copyright Jomura ( <a href="//jomura.net">jomura.net</a> ) All Rights Reserved.</div>
    </td></tr></table>
  </div>
</div>
<%
	} else {
    // 詳細表示
    Map<String, Object> post = postServ.show(Integer.valueOf(request.getParameter("p")));
%>
<div id="detail" data-role="page" data-title="<%= blogname %> :: <%= post.get("post_title") %>"
     data-add-back-btn="true" data-back-btn-text="Back">
  <div data-role="header" data-position="fixed" data-theme="b">
    <h1><%= blogname %></h1>
  </div>

  <div data-role="content" data-theme="c">
    <ul data-role="listview">
      <li data-role="list-divider"><%= dt_yyyyMMdd.format((Date) post.get("post_date")) %>
        <p class="ui-li-aside">at <%= dt_HHmm.format((Date) post.get("post_date")) %></p>
      </li>
    </ul>
    <h3><%= post.get("post_title") %> &nbsp; <span class="meta">[<%= post.get("cat_name") %>]</span></h3>
    <p><%= StringUtil.unescapeSql(post.get("post_content")) %></p>

<table class="comment" style="width:100%;"><tr>

<td style="width:30%;">
</td><td style="width:10px;">
</td><td style="vertical-align:top;">
<g:plusone size="medium" count="false"></g:plusone>
</td><td style="vertical-align:top;">
<a href="https://twitter.com/share" class="twitter-share-button" data-count="none">Tweet</a><script type="text/javascript" charset="utf-8" src="https://platform.twitter.com/widgets.js"></script>
</td><td style="vertical-align:top;">
<fb:like width="1" show_faces="false"></fb:like>
</td><td style="width:10px;">
</td>
<td align="right" style="width:30%;"></td>
</tr></table>

    <%-- p><fb:comments href="http://jomura.net/blog" num_posts="2" style="width:100%; margin-left:auto; margin-right:auto; text-align:center;"></fb:comments --%>
    
  </div>

  <div data-role="footer" style="text-align:center;" class="footer-docs ui-bar" data-theme="b">
    <cite class="cite">リンクはご自由に。ご意見・お問い合わせ・フィードバックはお気軽に <a href="&#109;&#97;&#105;l&#116;&#111;&#58;&#107;az&#64;&#106;omur&#97;&#46;n&#101;&#116;">メール</a> ください。</cite><br />
    <table style="width:70%; margin-left:auto; margin-right:auto;" ><tr><td class="cite" style="text-align:left;">
           当サイトにおいて提供される情報は、真実性、合法性、安全性、適切性、有用性、完全性、正確性について何ら保証するものではありません。利用者はその旨を了承の上、自己の責任において利用するものとし、当該情報に基づく損害、損失についても、当サイトは一切責任を負いません。
    <div style="text-align:right;">Copyright Jomura ( <a href="//jomura.net">jomura.net</a> ) All Rights Reserved.</div>
    </td></tr></table>
    <%-- This page is created in 0.058 sec. --%>
  </div>
</div>
<%
}
%>

<script type="text/javascript" src="https://apis.google.com/js/plusone.js" charset="utf-8"></script>

</body>
</html>
