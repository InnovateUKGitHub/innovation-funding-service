package com.worth.ifs.filter;

//public class CsrfHeaderFilter extends OncePerRequestFilter {
//	@Override
//	protected void doFilterInternal(HttpServletRequest request,
//			HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
//				.getName());
//		if (csrf != null) {
//			Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
//			String token = csrf.getToken();
//			if (cookie == null || token != null
//					&& !token.equals(cookie.getValue())) {
//				cookie = new Cookie("XSRF-TOKEN", token);
//				cookie.setPath("/");
//				response.addCookie(cookie);
//			}
//		}
//		filterChain.doFilter(request, response);
//	}
//}
