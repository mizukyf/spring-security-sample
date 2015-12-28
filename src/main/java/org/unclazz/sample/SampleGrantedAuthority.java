package org.unclazz.sample;

import org.springframework.security.core.GrantedAuthority;

/**
 * Spring Securityによる権限管理メカニズムで利用されるオブジェクト.
 * <p>{@link GrantedAuthority}インターフェースを実装している点が重要。</p>
 * <p>{@link SampleSecurityConfiguration}でユーザがアクセスするURLと権限との紐付けに利用するため、
 * 権限名を文字列型の{@code static final}フィールドとして公開している。
 * Spring Securityのメカニズムに即して言えばこの権現名こそ権限情報の本体である。</p>
 */
public class SampleGrantedAuthority implements GrantedAuthority {
	private static final long serialVersionUID = -4297213634794564411L;
	
	/**
	 * オペレータ権限の名前.
	 * 前述のとおりURLと権限の紐付けではこの名前を利用するため重要な定数である。
	 */
	public static final String NAME_OPERATOR = "OPERATOR";
	
	/**
	 * アドミニストレータ権限の名前.
	 * 前述のとおりURLと権限の紐付けではこの名前を利用するため重要な定数である。
	 */
	public static final String NAME_ADMINISTRATOR = "ADMINISTRATOR";
	
	/**
	 * オペレータの権限オブジェクト.
	 */
	public static final SampleGrantedAuthority OPERATOR = new SampleGrantedAuthority(NAME_OPERATOR); 
	
	/**
	 * アドミニストレータの権限オブジェクト.
	 */
	public static final SampleGrantedAuthority ADMINISTRATOR = new SampleGrantedAuthority(NAME_ADMINISTRATOR); 
	
	/**
	 * 内的に利用する権限オブジェクトの一覧.
	 */
	private static final SampleGrantedAuthority[] knownAuthorities = {OPERATOR, ADMINISTRATOR};
	
	/**
	 * 権限オブジェクトを得るためのファクトリ・メソッド.
	 * @param name 権限名
	 * @return 権限オブジェクト
	 */
	public static SampleGrantedAuthority of(final String name) {
		// 既知の権限一覧から該当するものを探す
		for (final SampleGrantedAuthority auth : knownAuthorities) {
			if (auth.equals(name)) {
				// 見つかったらその場で処理を終える
				return auth;
			}
		}
		// 見つからなかった場合は実行時例外をスロー
		throw new IllegalArgumentException(String.format("Unknown authority \"%s\"", name));
	}
	
	/**
	 * 権限名.
	 */
	private final String name;
	
	/**
	 * アドミニストレータであるかどうか.
	 */
	private final boolean admin;
	
	/**
	 * 非公開のコンストラクタ.
	 * @param name 権限名
	 */
	private SampleGrantedAuthority(String name) {
		this.name = name;
		this.admin = name.equals(NAME_ADMINISTRATOR);
	}
	
	public boolean isAdmin() {
		return admin;
	}
	public String getName() {
		return name;
	}
	
	@Override
	public String getAuthority() {
		return getName();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (admin ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleGrantedAuthority other = (SampleGrantedAuthority) obj;
		if (admin != other.admin)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
