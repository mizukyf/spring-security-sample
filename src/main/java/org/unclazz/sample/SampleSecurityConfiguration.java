package org.unclazz.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Securityのための設定情報を初期化するためのオブジェクト.
 * <p>{@link WebSecurityConfigurerAdapter}を継承（拡張）している点が重要。</p>
 * <p>このオブジェクトにより、認証せずにアクセスできるURL、認証せずにはアクセスできないURL、
 * 認証が必要なURLにアクセスしたときにリダイレクトされるURL、
 * 認証成功時にリダイレクトされるURL、ログアウトのトリガーとなるURLなど、認証−URL間の紐付けがなされる。</p>
 * <p>また同時に{@link GrantedAuthority}オブジェクトで表されるユーザの権限とURLの間の紐付けもなされる。</p>
 */
@Configuration
@EnableWebMvcSecurity
public class SampleSecurityConfiguration extends WebSecurityConfigurerAdapter {
	/**
	 * パスワードのエンコードを行うオブジェクト.
	 */
    private final PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
    
    /**
     * Spring Securityの認証メカニズムのためのVOを処理するサービス.
     */
	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * ユーザ認証とユーザ権限をユーザのアクセスURLと紐付けるための設定を行う.
	 * <p>引数として渡される{@link HttpSecurity}は一種のビルダーである。
	 * 開発者はこのオブジェクトを通じてユーザのアクセスするURLと認証状態・保有権限の紐付けや
	 * ログイン画面やログアウト画面の定義を行う。</p>
	 */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        
    	http.authorizeRequests()
    		// "/admin"というURLへのアクセスにはアドミニストレータ権限を必要とするよう設定
        	// ＊メソッド名から推測ができるようにAntっぽいワイルドカード表記が可能
        	.antMatchers("/admin").hasAuthority(SampleGrantedAuthority.NAME_ADMINISTRATOR)
        	// その他のURLについてはいずれにせよ認証をパスすることがアクセスの条件であると設定
            .anyRequest().authenticated()
            .and()
            // フォーム・ログインについての設定
            .formLogin()
            // 認証が必要なURLアクセスが行われた時は"/login"を表示するよう設定
            .loginPage("/login")
            // ログイン失敗時は"/login"を再表示するよう設定
            .failureUrl("/login")
            // ログイン成功後は"/index"に遷移するよう設定
            // ＊第2引数を省略するかfalseにすると、ログイン成功時
            // ログイン画面が表示される前そもそもユーザがアクセスを要求したURLへ遷移する
            .defaultSuccessUrl("/index", true)
            .permitAll()
            .and()
            // ログアウトについての設定
            .logout()
            // ログアウトのトリガーとなるURLを設定
        	// ＊オブジェクト名から推測ができるようにAntっぽいワイルドカード表記が可能
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            // ログアウト成功後のリダイレクト先を設定
            .logoutSuccessUrl("/login")
            .permitAll();
    }
    
    /**
     * このアプリケーションの認証メカニズムの設定を行う.
     * @param auth Spring Securityフレームワークから渡される認証管理ビルダー
     * @throws Exception {@link AuthenticationManagerBuilder#userDetailsService(UserDetailsService)}が例外をスローした場合
     */
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }
    
    /**
     * パスワード・エンコーダを返す.
     * @return パスワード・エンコーダ
     */
    @Bean
    public PasswordEncoder passwordEncorder() {
    	return passwordEncoder;
    }
}
