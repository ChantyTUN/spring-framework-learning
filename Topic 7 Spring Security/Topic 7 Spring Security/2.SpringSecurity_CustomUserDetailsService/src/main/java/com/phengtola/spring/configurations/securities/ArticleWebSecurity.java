package com.phengtola.spring.configurations.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class ArticleWebSecurity extends WebSecurityConfigurerAdapter{
	
	
	// Use customUserDetailsService
	private UserDetailsService userDetailsService;
	
	// AccessDenied Handler
	private AccessDeniedHandler accessDeniedHandler;
	
	//Success Handler
	private CustomSuccessHandler customSuccessHandler;

	//Failure Handler
	private CustomFailureHandler customFailureHandler;
	
	@Autowired
	public ArticleWebSecurity(
			@Qualifier("customSuccessHandler") 
			CustomSuccessHandler customSuccessHandler,
			@Qualifier("customUserDetailsService") 
			UserDetailsService userDetailsService,
			@Qualifier("articleAccesDeniedHandler")
			AccessDeniedHandler accessDeniedHandler,
			@Qualifier("customFailureHandler")
			CustomFailureHandler customFailureHandler
		) {
		this.customSuccessHandler = customSuccessHandler;
		this.userDetailsService = userDetailsService;
		this.accessDeniedHandler = accessDeniedHandler;
		this.customFailureHandler = customFailureHandler;
	}
	
	/***
	 * Role admin allow to access /admin/**
	 * Role user allow to access /article/**
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/","/home","/about","/article","/user").permitAll()
				.antMatchers("/article/**").hasAnyRole("USER","ADMIN")
				.antMatchers("/admin/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.failureUrl("/login?error")
				//.successForwardUrl("/")
				.successHandler(customSuccessHandler)
				//.failureHandler(customFailureHandler)
				.permitAll()
			.and()
				.logout()
				.permitAll()
			.and()
			//.exceptionHandling().accessDeniedPage("/error/403")
			.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
	}
	
	
	/**
	 * Create in memory users:
	 * + User 1: 
	 * 	- Username: user
	 * 	- Password: 123
     * 	- Roles: USER
	 * + User 2:
	 * 	- Username: admin
	 * 	- Password: 123
	 *  - Roles: USER and ADMIN
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	/*	auth
			.inMemoryAuthentication()
				.withUser("user").password("123").roles("USER")
				.and()
				.withUser("admin").password("123").roles("ADMIN");
				
	*/
		
		auth.userDetailsService(userDetailsService);
	
	
		
		
	}
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
		web.ignoring().antMatchers("/static/**");
	}
	

}
