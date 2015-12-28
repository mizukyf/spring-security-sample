package org.unclazz.sample;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.unclazz.sample.entity.User;

/**
 * Spring Securityの認証メカニズムで利用されるVO.
 * <p>{@link org.springframework.security.core.userdetails.User}を継承（拡張）している点が重要。
 * 継承元のオブジェクトではユーザIDという（DBのリレーション由来の）概念がないのでサブクラスでこれを補っている。
 * また継承元オブジェクトでは権限をコレクションとして保持しているが、サンプル・アプリケーションでは
 * オペレータかアドミニストレータかの区別が重要でそれ以外の権限を用意する想定がないため、
 * 2値を識別するためのフラグを追加している。</p>
 */
public class SampleUserDetails extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 2213449577870703888L;
	
	/**
	 * 空っぽの権限リストを返す.
	 * @return 権限リスト
	 */
	private static List<GrantedAuthority> emptyAuthorities() {
		return Collections.emptyList();
	}
	
	/**
	 * オペレータ権限のみを要素とするリストを返す.
	 * @return 権限リスト
	 */
	private static List<GrantedAuthority> operatorAuthorities() {
		return Arrays.asList(new GrantedAuthority[]{
				SampleGrantedAuthority.OPERATOR});
	}
	
	/**
	 * オペレータおよびアドミニストレータ権限を要素とするリストを返す.
	 * @return 権限リスト
	 */
	private static List<GrantedAuthority> administratorAuthorities() {
		return Arrays.asList(new GrantedAuthority[]{
				SampleGrantedAuthority.OPERATOR,
				SampleGrantedAuthority.ADMINISTRATOR});
	}
	
	/**
	 * リクエスト・パラメータおよびリレーションのためのVOから
	 * Spring Securityの認証メカニズムのためのVOを生成して返す.
	 * @param user リクエスト・パラメータおよびリレーションのためのVO
	 * @return Spring Securityの認証メカニズムのためのVO
	 */
	public static SampleUserDetails of(final User user) {
		return new SampleUserDetails(user.getId(), user.getName(), user.getPassword(), user.isAdmin());
	}
	
	/**
	 * {@link Principal}オブジェクトから{@link SampleUserDetails}を取り出して返す.
	 * @param principal ユーザ・プリンシパル
	 * @return Spring Securityの認証メカニズムのためのVO
	 */
	public static SampleUserDetails of(final Principal principal) {
		return (SampleUserDetails) ((Authentication) principal).getPrincipal();
	}
	
	/**
	 * 無効なユーザを生成するコンストラクタ.
	 */
	public SampleUserDetails() {
        super("INVALID", "INVALID", false, false, false, false, emptyAuthorities());
    }
	
	/**
	 * 有効なユーザを生成するコンストラクタ.
	 * @param id ユーザID
	 * @param username ユーザ名
	 * @param passeord エンコード済みパスワード
	 * @param admin アドミニストレータ権限を持つかどうか
	 */
	private SampleUserDetails(final int id, final String username, final String passeord, final boolean admin) {
		super(username, passeord, true, true, true, true, admin 
				? administratorAuthorities() : operatorAuthorities());
		this.admin = admin;
	}
	
	/**
	 * ユーザID.
	 * 
	 */
	private int id;
	private boolean admin;
	
	public int getId() {
		return id;
	}
	public boolean isAdmin() {
		return admin;
	}
	public User toUser() {
		final User user = new User();
		user.setId(id);
		user.setName(super.getUsername());
		user.setPassword(super.getPassword());
		user.setAdmin(admin);
		return user;
	}
}
